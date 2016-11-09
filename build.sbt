name := "repo-cleaner"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-Xlint", "-feature", "-deprecation")

libraryDependencies += "commons-io" % "commons-io" % "2.5"

libraryDependencies += "commons-codec" % "commons-codec" % "1.10"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.5"

libraryDependencies += "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.6"
