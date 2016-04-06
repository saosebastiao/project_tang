package models.partier

import play.api.libs.json._
import scala.concurrent.duration._
import org.joda.time.{DateTime,LocalDate}
import slick.jdbc.GetResult
import slick.driver.JdbcProfile
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

object UserAttributes {
  case class TangRegistration(
      userID: String, 
      email: String,
      name: String, 
      birthdate: String, 
      gender: String,
      accessToken: String)
  case class Login(userID: String, accessToken: String)
  case class Tang(
      userID: String, 
      email: Option[String],
      name: Option[String], 
      birthdate: Option[LocalDate], 
      gender: Option[String],
      userStatus: String) 
  implicit val fmtRegistration = Json.format[TangRegistration]
  implicit val fmtLogin = Json.format[Login]
  implicit val fmtTang = Json.format[Tang]
  
}

object AuthModel {
  import scala.concurrent.Await
  private val timeout = 5.seconds
  import tang.utils.MyPostgresDriver.plainAPI._
  import UserAttributes._
  val db = Database.forConfig("mydb")

  implicit val getPartier = GetResult(r => 
    Tang(r.<<,r.<<,r.<<,r.<<,r.<<,r.<<))
  def createUser(user: TangRegistration) = {
    // TODO: Error Checking : user exists, 
    val q = sqlu"""
       UPDATE partiers SET 
       email = ${user.email},
       name = ${user.name},
       birthdate = ${user.birthdate}::date,
       gender = ${user.gender}
       WHERE user_id = ${user.userID}"""
    val u = sql"""
       SELECT 
       user_id,
       email,
       name,
       birthdate,
       gender,
       CASE WHEN validated THEN 'EXISTING' ELSE 'NEW' END as user_status
       FROM partiers
       WHERE user_id = ${user.userID}
	      """.as[Tang]
      .headOption
    val out = db.run(q)
    val valid = Await.result(out, timeout) match {
      case 1 => true
      case _ => false
    }
	  val res = db.run(u).map {
	    case Some(u) => u
	    case None => Tang(user.userID, None, None, None, None, "NEW")
	  }
	  Await.result(res,timeout)
  }
  def validate(userID: String, token: UUID) = {
     val q = sqlu"""
       UPDATE partiers SET 
       validated = true
       WHERE user_id = $userID
         AND validation_token = $token"""
    val out = db.run(q)
    Await.result(out, timeout) match {
      case 1 => true
      case _ => false
    }
  }
  def login(user: Login) = {
    // TODO: Error Checking : validate with Facebook 
    val q = sql"""
       SELECT 
       user_id,
       email,
       name,
       birthdate,
       gender,
       CASE WHEN validated THEN 'EXISTING' ELSE 'NEW' END as user_status
       FROM partiers
       WHERE user_id = ${user.userID}
	      """.as[Tang]
      .headOption
    val newUser = sqlu"""
       INSERT INTO partiers(user_id) VALUES (${user.userID})
	      """
    val out = db.run(q).map {
      case Some(u) => u
      case None => {
        db.run(newUser)
        Tang(user.userID, None, None, None, None, "NEW")
      }
    }
    Await.result(out, timeout)
  }
}


