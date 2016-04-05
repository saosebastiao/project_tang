enablePlugins(JavaAppPackaging)

name         := "akka-http-microservice"
organization := "com.theiterators"
version      := "1.0"
scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
resolvers += Resolver.bintrayRepo("hseeberger", "maven")

libraryDependencies ++= List(

)


libraryDependencies ++= {
  val akkaV       = "2.4.3"
  val scalaTestV  = "2.2.5"
  Seq(
    "joda-time" % "joda-time" % "2.9.1",
	"org.postgresql" % "postgresql" % "9.4-1206-jdbc42",
	"com.github.tminglei" %% "slick-pg" % "0.11.0",
	"com.github.tminglei" %% "slick-pg_joda-time" % "0.11.0",
	"com.github.tminglei" %% "slick-pg_play-json" % "0.11.0",
	"com.github.tminglei" %% "slick-pg_jts" % "0.11.0",
	"com.typesafe.akka" %% "akka-actor" % akkaV,
	"com.typesafe.akka" %% "akka-stream" % akkaV,
	"com.typesafe.akka" %% "akka-http-experimental" % akkaV,
	"de.heikoseeberger" %% "akka-http-upickle" % "1.5.3",
	"com.typesafe.akka" %% "akka-http-testkit" % akkaV,
	"org.scalatest"     %% "scalatest" % scalaTestV % "test"
  )
}


Revolver.settings
