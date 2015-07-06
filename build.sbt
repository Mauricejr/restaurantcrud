name := """restaurantcrud"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
   jdbc,
  specs2 % Test,
 "com.typesafe.play" %% "anorm" % "2.3.0",
 "mysql" % "mysql-connector-java" % "5.1.18"
 
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.specs2 % Test routesGenerator := InjectedRoutesGenerator
//#"com.wordnik" %% "swagger-play2" % "1.3.11"

routesGenerator := InjectedRoutesGenerator