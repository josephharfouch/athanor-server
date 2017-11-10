packAutoSettings
name := "athanorserver"

//
// Athanor has been modified to be a pretend SBT project so that it is
// a dependency of this project. The dependency will be downloaded by SBT
// to the staging areas, and its jars become accessible to us.
// The implemented project dependency scheme is described in:
// [sbt reference manual-multi project build](http://www.scala-sbt.org/0.13/docs/Multi-Project.html)
//
dependsOn(athanorProject)

// This git branch should contain the athanor jar that we depend on. An athanor dummy sbt 
// project has been created in athanor, and the lib directory of athanor contains a link to the 
// jar that we default to. This jar does not need to be copied to the athanor-server lib directory
// as athanor is a dependency and the jar seems to be picked up. 
lazy val athanorProject = ProjectRef(uri("https://github.com/uts-cic/athanor.git#develop"),"athanor")

// This is the git branch that I tested with. For the final version, we can try specifying 
// the develop branch, or see if we can omit the branch all together. 
// lazy val athanorProject = ProjectRef(uri("https://github.com/josephharfouch/athanor.git#dummy_sbt_project"),"athanor")

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
  // Please see comments below about disabling nlytx dependency
  //"io.nlytx" %% "commons" % nlytxCommonsVersion,
  //  "com.typesafe" % "config" % "1.3.1",
  "org.json4s" %% "json4s-jackson" % json4sVersion,
  "de.heikoseeberger" %% "akka-http-json4s" % akkaHttpJson4sVersion,
  "org.skyscreamer" % "jsonassert" % jsonassertVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion % "test",
  "org.slf4j" % "jcl-over-slf4j" % slf4jVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion % Runtime 
)

scalacOptions in (Compile, doc) ++= Seq("-doc-root-content", baseDirectory.value+"/src/main/scala/root-doc.md")

//
// I disabled nlytx dependency for the time being, until we figure out
// what to do with it. When I delete my private .ivy2 cache, sbt
// tries to download this dependency but cannot find it. I suspect its 
// position in the bintray may have moved, but need to do more investigation 
// on this. I found that when I disable this dependency, the testcases still
// run. so I am not sure if the dependency is still needed or not. 
// resolvers += Resolver.bintrayRepo("nlytx", "nlytx_commons")

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


//Task for downloading an athanor library by typing: sbt downloadAthanor
// I left this in for now, in case we need to get back to it, or someone needs
// to adapt it for their own private use, but it would
// need modifications to work reliably. Not only is the location of the
// file hidden as documented below, but I suspect that the token would
// expire or would not work for other users, so I suspect it has to
// be obtained programmatically from git, which gets us into git
// authentication via api/url. In any case, I prefer the project
// dependency scheme where the user does not have to type any
// extra commands, and that would make this task unnecessary.
val downloadAthanorTask = TaskKey[Unit]("downloadAthanor","copies Athanor library to the lib/ directory")
downloadAthanorTask := {

  import java.io.File
  import java.nio.file.Paths
  import java.nio.file.Files
  import java.nio.file.StandardCopyOption._



  //
  // The difficulty with this is that the real location of the file in github is hidden, and I had to capture what the load command was
  // doing by right clicking on the file and doing a load to see where the load was getting the file from
  // We have to do this in the future if we want to update to a different version of the athanor library. 
  //
  val athanorSourceJarName = "https://raw.githubusercontent.com/uts-cic/athanor/develop/java/versioned_dist/athanor-0.87b-1.0.0.jar?token=AfHf5cx0BAP0hVxlxh7oqvTuYVecPmkyks5aDa47wA%3D%3D"
  val athanorTargetJarName = "lib/athanor.jar"

  val athanorSourceJar = new URL(athanorSourceJarName)
  val in = athanorSourceJar.openStream()
  val out = Paths.get(athanorTargetJarName)
  Files.copy(in, out, REPLACE_EXISTING);
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
