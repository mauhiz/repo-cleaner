name := "repo-cleaner"

version := "1.0"

scalaVersion := "2.12.3"

scalacOptions ++= Seq("-Xlint", "-feature", "-deprecation")

libraryDependencies += "commons-io" % "commons-io" % "2.5"

libraryDependencies += "commons-codec" % "commons-codec" % "1.10"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.6"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
