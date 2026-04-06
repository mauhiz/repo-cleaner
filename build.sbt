name := "repo-cleaner"

version := "1.0"

scalaVersion := "3.8.3"

scalacOptions ++= Seq("-feature", "-deprecation", "-Wunused:all")

libraryDependencies += "commons-io" % "commons-io" % "2.21.0"

libraryDependencies += "commons-codec" % "commons-codec" % "1.21.0"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.20.0"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.4.0"
