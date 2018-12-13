import Dependencies._

val resdir: File = file("/home/akemisetti/ctakesresources")
val clspath: File = file("/home/akemisetti/projects/ctakes/hs619/lib")


lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "edu.usfca.hs619",
      scalaVersion := "2.12.7",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "SimpleNLP", 
    scalacOptions in (Compile,doc) ++= Seq("-doc-root-content", "/home/akemisetti/projects/ctakes/hs619/root-doc.txt"),
    resourceDirectory  in Compile := resdir,
    resourceDirectory  in Test  := resdir,
    resourceDirectory  in runMain   := resdir,
    resourceDirectory  in Runtime  := resdir,
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "org.typelevel" %% "cats-core" % "1.1.0",
    libraryDependencies += "org.apache.opennlp" % "opennlp-tools" % "1.8.1",
    libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.9.1",
    libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.9.1"  classifier "models",
    libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25",
    libraryDependencies += "org.apache.uima" % "uimaj-core" % "2.10.1",
    libraryDependencies += "org.apache.uima" % "uimaj-tools" % "2.10.1",
    libraryDependencies += "org.apache.uima" % "uimafit-core" % "2.3.0",
    libraryDependencies += "org.apache.ctakes" % "ctakes-core" % "4.0.0",
    libraryDependencies += "org.apache.ctakes" % "ctakes-chunker" % "4.0.0",
    libraryDependencies += "org.apache.ctakes" % "ctakes-dictionary-lookup" % "4.0.0",
    libraryDependencies += "org.apache.ctakes" % "ctakes-dictionary-lookup-fast" % "4.0.0",
    libraryDependencies += "org.apache.ctakes" % "ctakes-drug-ner" % "4.0.0",
    libraryDependencies += "org.apache.ctakes" % "ctakes-ne-contexts" % "4.0.0",
    libraryDependencies += "org.apache.ctakes" % "ctakes-clinical-pipeline" % "4.0.0",
    libraryDependencies += "org.apache.ctakes" % "ctakes-context-tokenizer" % "4.0.0",
    libraryDependencies += "org.postgresql" % "postgresql" % "42.1.1",
    libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.2"
  )
