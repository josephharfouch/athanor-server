name := "athanorserver"
version := "0.8"
scalaVersion := "2.12.3"
organization := "au.edu.utscic"

//Scala library versions
val akkaVersion = "2.5.6"
val akkaStreamVersion = "2.5.6"
val akkaHttpVersion = "10.0.10"
val akkaHttpJson4sVersion = "1.18.0"
val json4sVersion = "3.5.3"
val slf4jVersion = "1.7.25"
val logbackVersion = "1.2.3"
val scalatestVersion = "3.0.4"
val nlytxCommonsVersion = "0.1.1"

//Java library versions
val coreNlpVersion = "3.8.0"
val jsonassertVersion = "1.5.0"

//Akka
libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-stream_2.12" % akkaStreamVersion,
  "com.typesafe.akka" % "akka-stream-testkit_2.12" % akkaStreamVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
)
//NLP dependencies
libraryDependencies ++= Seq(
  "edu.stanford.nlp" % "stanford-corenlp" % coreNlpVersion,
  "edu.stanford.nlp" % "stanford-corenlp" % coreNlpVersion  classifier "models-english"
)

//General
libraryDependencies ++= Seq(
  "au.edu.utscic" % "athanor" % "0.9.0",
  "org.json4s" %% "json4s-jackson" % json4sVersion,
  "de.heikoseeberger" %% "akka-http-json4s" % akkaHttpJson4sVersion,
  "org.skyscreamer" % "jsonassert" % jsonassertVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion % "test",
  "org.slf4j" % "jcl-over-slf4j" % slf4jVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion //% Runtime
)

scalacOptions in (Compile, doc) ++= Seq("-doc-root-content", baseDirectory.value+"/src/main/scala/root-doc.md")

resolvers += Resolver.bintrayRepo("nlytx", "athanor")

//Documentation - run ;paradox;copyDocs
enablePlugins(ParadoxPlugin) //Generate documentation with Paradox
paradoxTheme := Some(builtinParadoxTheme("generic"))
paradoxProperties in Compile ++= Map(
  "github.base_url" -> s"https://github.com/uts-cic/athanor-server",
  "scaladoc.api.base_url" -> s"https://uts-cic.github.io/athanor-server"
)
//Task for copying to root level docs folder (for GitHub pages)
val copyDocsTask = TaskKey[Unit]("copyDocs","copies paradox docs to /docs directory")
copyDocsTask := {
  import java.io.File
  
  val docSourceFileName = "target/paradox/site"
  if (! new java.io.File(docSourceFileName).exists)
  {
      println("Error: Cannot locate documentation source directory:{}", docSourceFileName)
      System.exit(1)
  }
  val docSource = new File(docSourceFileName)
 
  val docDest = new File("docs")
  IO.copyDirectory(docSource,docDest,overwrite=true,preserveLastModified=true)
}

// RUN sbt dependencyUpdates to check dependency version

// RUN sbt universal:packageZipTarball to create a tar package for upload to server
// ensure that JavaAppPackaging is enabled - disable for Travis CI
//enablePlugins(JavaAppPackaging)

//coverageEnabled := false

//coverageMinimum := 70
//coverageFailOnMinimum := false
//coverageHighlighting := true
//publishArtifact in Test := false
//parallelExecution in Test := false

import com.typesafe.sbt.SbtNativePackager.autoImport.NativePackagerHelper._
//Enable this only for local builds - disabled for Travis
enablePlugins(JavaAppPackaging) // sbt universal:packageZipTarball
dockerExposedPorts := Seq(8083) // sbt docker:publishLocal
mappings in Universal ++= directory("grammar")
javaOptions in Universal ++= Seq(
  // -J params will be added as jvm parameters
  "-J-Xmx2048m",
  "-J-Xms512m"

  // others will be added as app parameters
  //  "-Dproperty=true",
  //  "-port=8080",

  // you can access any build setting/task here
  //s"-version=${version.value}"
)
