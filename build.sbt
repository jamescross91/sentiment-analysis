name := "sentiment"

version := "1.0"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  Resolver.file("Local Maven Repository"),
  "bintray-spark-packages" at "https://dl.bintray.com/spark-packages/maven/"
)


libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "2.1.0",
  "org.apache.spark" % "spark-streaming_2.11" % "2.1.0",
  "org.apache.spark" % "spark-sql_2.11" % "2.1.0",
  "com.google.protobuf" % "protobuf-java" % "2.6.1",
  "org.apache.bahir" % "spark-streaming-twitter_2.11" % "2.0.2",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" classifier "models",
  "databricks" % "spark-corenlp" % "0.2.0-s_2.11",
  "com.datastax.spark" % "spark-cassandra-connector_2.11" % "2.0.0-RC1"
)
    