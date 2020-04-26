name := "repo-cleaner"

version := "1.0"

scalaVersion := "2.13.2"

scalacOptions ++= Seq("-Xlint", "-feature", "-deprecation")

libraryDependencies += "commons-io" % "commons-io" % "2.6"

libraryDependencies += "commons-codec" % "commons-codec" % "1.14"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.10"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.3.0"
