# Spark Twitter Sentiment Analysis

Uses Stamford CoreNLP and Spark Streaming to Analyse tweets from Twitters API.

## Running
Expects following system properties:
```
-Dtwitter4j.oauth.consumerKey
-Dtwitter4j.oauth.consumerSecret
-Dtwitter4j.oauth.accessToken
-Dtwitter4j.oauth.accessTokenSecret
```

Which can be obtained by creating a new app on the twitter developer portal.

## Hashtag Filters
Change the line `val twitterStream = TwitterUtils.createStream(ssc, auth, Array("#stormdoris"))` in  `src/main/scala/TwitterSentiment.scala` to include a list of interesting hashtags to process.