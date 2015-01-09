name := """akg"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.json4s" %% "json4s-ext" % "3.2.11",
  "org.mongodb" %% "casbah" % "2.7.2",         // Mongodb scala driver
  "org.scalatest" % "scalatest_2.10" % "2.1.5",
  "org.scalatestplus" % "play_2.10" % "1.0.1" % "test",
  "org.mockito" % "mockito-core" % "1.9.5"
)
