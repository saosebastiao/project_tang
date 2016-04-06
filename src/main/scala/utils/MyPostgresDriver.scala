package tang.utils

import com.github.tminglei.slickpg._
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import slick.driver.PostgresDriver
import slick.jdbc.PositionedResult
import java.util.UUID

//https://github.com/slick/slick/issues/161
// UUID support is incomplete in Slick
trait UUIDPlainImplicits {
  import java.sql.JDBCType
  import slick.jdbc.SetParameter
  import slick.jdbc.PositionedParameters
  import utils.PlainSQLUtils._
  implicit class PgUUIDPositionedResult(val r: PositionedResult) {
    def nextUUID: UUID = UUID.fromString(r.nextString)
    def nextUUIDOption: Option[UUID] = r.nextStringOption().map(UUID.fromString)
  }

  implicit object SetUUID extends SetParameter[UUID] {
    def apply(v: UUID, pp: PositionedParameters) {
      pp.setObject(v, JDBCType.BINARY.getVendorTypeNumber)
    }
  }
  implicit val getUUID = mkGetResult(_.nextUUID)
  implicit val getUUIDOption = mkGetResult(_.nextUUIDOption)
  //not sure if needed with addition of implicit object above
  implicit val setUUID = mkSetParameter[UUID]("UUID")
  implicit val setUUIDOption = mkOptionSetParameter[UUID]("UUID")
}

trait MyPostgresDriver
  extends ExPostgresDriver
  with PgDateSupportJoda
  with PgPlayJsonSupport
  with PgPostGISSupport  {

  object MyAPI
    extends API
    with PlayJsonImplicits
    with DateTimeImplicits
    with PostGISImplicits
    with PostGISAssistants

  object PlainAPI extends API
    with JodaDateTimePlainImplicits
    with PlayJsonPlainImplicits
    with PostGISPlainImplicits
    with UUIDPlainImplicits {
  }
  
  val pgjson = "jsonb"
  override val api = MyAPI
  val plainAPI = PlainAPI
}

object MyPostgresDriver extends MyPostgresDriver
