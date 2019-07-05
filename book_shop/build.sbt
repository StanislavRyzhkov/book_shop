name := "book_shop"

version := "0.1"

scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.8",
  "com.typesafe.akka" %% "akka-stream" % "2.5.19",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.8",
  "org.postgresql" % "postgresql" % "42.2.6",
  "org.apache.commons" % "commons-dbcp2" % "2.6.0",
  "ch.megard" %% "akka-http-cors" % "0.4.1",
  "com.typesafe.slick" %% "slick" % "3.3.1",
  "org.slf4j" % "slf4j-nop" % "1.7.26",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.1",
  "io.jsonwebtoken" % "jjwt-api" % "0.10.6",
  "io.jsonwebtoken" % "jjwt-impl" % "0.10.6" % "runtime",
  "io.jsonwebtoken" % "jjwt-jackson" % "0.10.6" % "runtime"
)

mainClass in assembly := Some("company.ryzhkov.Main")
