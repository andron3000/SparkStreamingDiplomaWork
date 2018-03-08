package diploma.service.impl;

import diploma.config.ConfigProperties;
import diploma.converter.TweetDataConverter;
import diploma.model.TweetData;
import diploma.service.HashTagProcessingService;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.*;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import twitter4j.Status;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.List;

@Service
public class HashTagProcessingServiceImpl implements HashTagProcessingService {

    private static final String TWEET_DATA_TABLE = "tweet_data";

    @Autowired
    private JavaStreamingContext streamingContext;

    @Autowired
    private SQLContext sqlContext;

    @Autowired
    private ConfigProperties configProperties;

    @Value("${spark.streaming.timeout.max}")
    private Long maxTimeout;

    private JavaReceiverInputDStream<Status> stream;

    @Override
    public void startHashTagAnalysis() {
        checkStreamState();

        JavaDStream<TweetData> tweetDataDStream = stream
                .filter(TweetDataConverter::isUtf8)
                .filter(TweetDataConverter::containsHashTags)
                .map((Function<Status, TweetData>) TweetDataConverter::convert);

        tweetDataDStream.print(); // todo print stream

        tweetDataDStream.foreachRDD(rdd -> {
            DataFrame dataFrame = sqlContext.createDataFrame(rdd, TweetData.class);
            dataFrame = dataFrame.withColumnRenamed("createDate", "create_date");
            dataFrame = dataFrame.withColumnRenamed("hashTags", "hash_tags");
            dataFrame.write()
                    .mode(SaveMode.Append)
                    .jdbc(configProperties.getProperty("url"), TWEET_DATA_TABLE, configProperties);
        });
        streamingContext.start();
        streamingContext.awaitTerminationOrTimeout(maxTimeout);
    }

    @Override
    public void stopProcessingHashTags() {
        streamingContext.stop(false);
    }

    @Override
    public void displayAnalyticResultByDate(Model model, int i) {
        DataFrame dataFrame = sqlContext.read()
                                       .jdbc(configProperties.getProperty("url"), TWEET_DATA_TABLE, configProperties)
                                       .withColumnRenamed("create_date","createDate")
                                       .withColumnRenamed("hash_tags","hashTags")
                                       .toDF();
        dataFrame.registerTempTable("tweets");

        Timestamp timestamp = getTimestamp(i);
        DataFrame result = getDataFrameByDate(timestamp);

        Encoder<TweetData> tweetEncoder = Encoders.bean(TweetData.class);
        List<TweetData> tweetDataList = result.as(tweetEncoder).collectAsList();
    }

    private DataFrame getDataFrameByDate(Timestamp timestamp) {
        String sqlQuery = String.format("SELECT * FROM tweets WHERE createDate > CAST('%s' AS TIMESTAMP)", timestamp);

        return sqlContext.sql(sqlQuery);
    }

    private Timestamp getTimestamp(int i) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        switch (i) {
            case 0:
                timestamp = Timestamp.from(timestamp.toInstant().minus(Duration.ofHours(1)));
                break;
            case 1:
                timestamp = Timestamp.from(timestamp.toInstant().minus(Duration.ofDays(1)));
                break;
            case 2:
                timestamp = Timestamp.from(timestamp.toInstant().minus(Duration.ofDays(300)));
                break;
        }
        return timestamp;
    }

    private void checkStreamState() {
        if(this.stream == null) {
            this.stream = TwitterUtils.createStream(streamingContext);
        }
    }
}