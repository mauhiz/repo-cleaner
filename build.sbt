name := "repo-cleaner"

version := "1.0"

scalaVersion := "2.12.8"

scalacOptions ++= Seq("-Xlint", "-feature", "-deprecation")

libraryDependencies += "commons-io" % "commons-io" % "2.6"

libraryDependencies += "commons-codec" % "commons-codec" % "1.12"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.9"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.2.0"
