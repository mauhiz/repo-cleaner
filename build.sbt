name := "repo-cleaner"

version := "1.0"

scalaVersion := "2.12.4"

scalacOptions ++= Seq("-Xlint", "-feature", "-deprecation")

libraryDependencies += "commons-io" % "commons-io" % "2.6"

libraryDependencies += "commons-codec" % "commons-codec" % "1.11"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.7"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.1.0"
