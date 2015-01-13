name := """akg"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
//  "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23",         // Mongodb scala driver
  "org.mongodb" %% "casbah" % "2.7.2",    // Mongodb scala driver
  "com.novus" %% "salat" % "1.9.9",       // Serialization library for case classes to database
  "org.scalatestplus" % "play_2.10" % "1.0.1" % "test"
)
