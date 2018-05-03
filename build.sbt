name := "GenericChecker"

version := "1.0"

scalaVersion := "2.11.4"

resolvers += "Oscar Snapshots" at "http://artifactory.info.ucl.ac.be/artifactory/libs-snapshot/"

updateOptions := updateOptions.value.withLatestSnapshots(false) // to be added because of a bad behaviour in the sbt version

libraryDependencies += "oscar" %% "oscar-cp" % "4.0.0-SNAPSHOT" withSources()
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.12.4"
libraryDependencies += "org.choco-solver" % "choco-solver" % "4.0.5"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"


unmanagedSourceDirectories in Test += baseDirectory.value / "src" / "main" / "examples"