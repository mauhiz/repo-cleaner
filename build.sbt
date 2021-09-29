name := "repo-cleaner"

version := "1.0"

scalaVersion := "2.13.6"

scalacOptions ++= Seq("-Xlint", "-feature", "-deprecation")

libraryDependencies += "commons-io" % "commons-io" % "2.11.0"

libraryDependencies += "commons-codec" % "commons-codec" % "1.15"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.12.0"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.0.1"
