package diploma.service.impl;

import diploma.config.ConfigProperties;
import diploma.converter.TweetDataConverter;
import diploma.model.TweetData;
import diploma.service.HashTagProcessingService;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import twitter4j.Status;

@Service
public class HashTagProcessingServiceImpl implements HashTagProcessingService {

    private static final String TWEET_DATA_TABLE = "tweet_data";

    @Autowired
    private JavaStreamingContext streamingContext;

    @Autowired
    private SQLContext sqlContex;

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
            DataFrame dataFrame = sqlContex.createDataFrame(rdd, TweetData.class);
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
        streamingContext.stop();
    }

    private void checkStreamState() {
        if(this.stream == null) {
            this.stream = TwitterUtils.createStream(streamingContext);
        }
    }
}