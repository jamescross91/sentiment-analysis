import com.databricks.spark.corenlp.functions._
import org.apache.spark._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{current_timestamp, explode}
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter.TwitterUtils
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder

object TwitterSentiment {
  System.setProperty("spark.cassandra.connection.host", "127.0.0.1")

  def main(args:Array[String]): Unit = {
    val conf = new SparkConf().setAppName("TwitterSentiment").setMaster("local[*]")

    val spark = SparkSession.builder.config(conf).getOrCreate()

    val ssc = new StreamingContext(spark.sparkContext, Seconds(1))
    // Create a Twitter Stream for the input source.
    val auth = Some(new OAuthAuthorization(new ConfigurationBuilder().build()))
    val twitterStream = TwitterUtils.createStream(ssc, auth, Array("trump", "donald"))

    val tweets = twitterStream
      .filter(tweet => tweet.getLang.equals("en") || tweet.getLang.equals(""))
      .map(_.getText())
      .map(_.replaceAll("/[^A-Za-z0-9 ]/", ""))
      .map(_.replaceAll("/", ""))
      .map(_.replaceAll("RT.+?(?=\\s)\\s", ""))
      .map(_.replaceAll("https([^\\s]+).*", ""))

    tweets.foreachRDD(rdd => {
      import spark.implicits._
      val df = rdd.toDF()

      val output = df.select(cleanxml('value).as('doc))
        .select(explode(ssplit('doc)).as('sen))
        .select('sen, tokenize('sen).as('words), ner('sen).as('nerTags), sentiment('sen).as('sentiment))


      val formated = output
        .drop("words")
        .drop("nerTags")
        .withColumnRenamed("sen", "tweet")
        .withColumn("processed_time", current_timestamp())

      formated
        .write
        .mode("append")
        .format("org.apache.spark.sql.cassandra")
        .options(Map("table" -> "storm_doris", "keyspace" -> "sentiment"))
        .save()

    })

    ssc.start()

    // Let's await the stream to end - forever
    ssc.awaitTermination()
  }
}


