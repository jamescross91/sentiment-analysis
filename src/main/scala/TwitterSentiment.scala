import org.apache.spark._
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter.TwitterUtils
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder
import com.databricks.spark.corenlp.functions._
import org.apache.spark.sql.functions.explode

object TwitterSentiment {
  def main(args:Array[String]): Unit = {
    val conf = new SparkConf().setAppName("TwitterSentiment").setMaster("local[*]")

    val spark = SparkSession.builder.config(conf).getOrCreate()

    val ssc = new StreamingContext(spark.sparkContext, Seconds(1))
    // Create a Twitter Stream for the input source.
    val auth = Some(new OAuthAuthorization(new ConfigurationBuilder().build()))
    val twitterStream = TwitterUtils.createStream(ssc, auth, Array("#stormdoris"))

    val tweets = twitterStream
      .map(status => status.getText())
      .map(text => text.replaceAll("/[^A-Za-z0-9 ]/", ""))

    tweets.foreachRDD(rdd => {
      import spark.implicits._
      val df = rdd.toDF()

      val output = df.select(cleanxml('value).as('doc))
        .select(explode(ssplit('doc)).as('sen))
        .select('sen, tokenize('sen).as('words), ner('sen).as('nerTags), sentiment('sen).as('sentiment))

      output.show()
    })

    ssc.start()

    // Let's await the stream to end - forever
    ssc.awaitTermination()
  }
}


