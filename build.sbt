import play.Play.autoImport.cache

name := """akg"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  cache,
  "com.softwaremill.macwire" %% "macros" % "0.7", // injecting class Secured to controllers in Global
  "org.mongodb" %% "casbah" % "2.7.2",    // Mongodb scala driver
  "com.novus" %% "salat" % "1.9.9",       // Serialization library for case classes to database
  "org.scalatestplus" % "play_2.10" % "1.0.1" % "test",
  "org.scalatest" % "scalatest_2.10" % "2.1.5",
  "org.json4s" %% "json4s-jackson" % "3.2.11"
)
