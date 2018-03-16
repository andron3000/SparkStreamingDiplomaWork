package diploma.service.impl;

import diploma.config.ConfigProperties;
import diploma.converter.HashTagConverter;
import diploma.converter.TweetDataConverter;
import diploma.dto.HashTagDto;
import diploma.model.HashTag;
import diploma.model.TweetData;
import diploma.service.HashTagProcessingService;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
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
import java.util.Map;
import java.util.stream.Collectors;

import static diploma.utils.Constants.DATABASE_URL;
import static diploma.utils.Constants.HASH_TAG_TABLE;
import static diploma.utils.Constants.TWEET_DATA_TABLE;

@Service
public class HashTagProcessingServiceImpl implements HashTagProcessingService {

    @Autowired
    private JavaStreamingContext streamingContext;

    @Autowired
    private SparkSession sparkSession;

    @Autowired
    private ConfigProperties configProperties;

    @Value("${spark.streaming.timeout.max}")
    private Long maxTimeout;

    @Override
    public void startHashTagAnalysis() {
        JavaReceiverInputDStream<Status> stream = TwitterUtils.createStream(streamingContext);

        JavaDStream<Status> filterStreamData = stream.filter(TweetDataConverter::isUtf8)
                                                     .filter(TweetDataConverter::containsHashTags);

        JavaDStream<TweetData> tweetDataDStream = filterStreamData.map(
                (Function<Status, TweetData>) TweetDataConverter::convert);

        JavaDStream<HashTag> hashTagDataDStream = filterStreamData.flatMap(HashTagConverter::convert);

        tweetDataDStream.print(); // todo print stream

        tweetDataDStream.foreachRDD(rdd -> {
            Dataset<Row> tweetDataFrame = sparkSession.createDataFrame(rdd, TweetData.class);
            tweetDataFrame = tweetDataFrame
                    .withColumnRenamed("createDate", "create_date")
                    .withColumnRenamed("hashTags", "hash_tags");
            tweetDataFrame.write()
                          .mode(SaveMode.Append)
                          .jdbc(configProperties.getProperty(DATABASE_URL), TWEET_DATA_TABLE, configProperties);
        });

        hashTagDataDStream.foreachRDD(rdd -> {
            Dataset<Row> hashTagDataFrame = sparkSession.createDataFrame(rdd, HashTag.class);
            hashTagDataFrame.write()
                            .mode(SaveMode.Append)
                            .jdbc(configProperties.getProperty(DATABASE_URL), HASH_TAG_TABLE, configProperties);
        });

        try {
            streamingContext.start();
            streamingContext.awaitTerminationOrTimeout(maxTimeout);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void stopProcessingHashTags() {
        streamingContext.stop(false);
    }

    @Override
    public void displayAnalyticResultByDate(Model model, int i) {
        Dataset<Row> dataFrame = sparkSession.read()
                                             .jdbc(configProperties.getProperty(DATABASE_URL), HASH_TAG_TABLE, configProperties)
                                             .toDF();
        dataFrame.createOrReplaceTempView("tags");
        Encoder<HashTagDto> tweetEncoder = Encoders.bean(HashTagDto.class);

        model.addAttribute("tweetPeriodDataMap", getDataMapPerPeriod(tweetEncoder, i));
        model.addAttribute("languageDataMap", getDataMapByLanguage(tweetEncoder, i));
    }

    private Map<String, Long> getDataMapPerPeriod(Encoder<HashTagDto> tweetEncoder, Integer index) {
        Dataset<Row> dataPerPeriod = getDataFrameByDate(getTimestamp(index));
        List<HashTagDto> tweetDataListPerPeriod = dataPerPeriod.as(tweetEncoder).collectAsList();
        return tweetDataListPerPeriod
                .stream()
                .collect(Collectors.toMap(HashTagDto::getValue, HashTagDto::getCount));
    }

    private Map<String, Long> getDataMapByLanguage(Encoder<HashTagDto> tweetEncoder, Integer index) {
        Dataset<Row> dataFrameByLanguage = getDataFrameByLanguageAndDate(getTimestamp(index));
        List<HashTagDto> tweetDataListPerPeriod = dataFrameByLanguage.as(tweetEncoder).collectAsList();
        return tweetDataListPerPeriod
                .stream()
                .collect(Collectors.toMap(HashTagDto::getValue, HashTagDto::getCount));
    }

    private Dataset<Row> getDataFrameByDate(Timestamp timestamp) {
        String sqlQuery = String.format("SELECT " +
                                                "value, COUNT(value) AS count " +
                                                "FROM tags " +
                                                "WHERE tags.date > CAST('%s' AS TIMESTAMP) " +
                                                "GROUP BY tags.value " +
                                                "ORDER BY COUNT(value) DESC, value " +
                                                "LIMIT 10", timestamp);

        return sparkSession.sql(sqlQuery);
    }

    private Dataset<Row> getDataFrameByLanguageAndDate(Timestamp timestamp) {
        String sqlQuery = String.format("SELECT " +
                                                "tags.language as value, COUNT(tags.language) AS count " +
                                                "FROM tags " +
                                                "WHERE tags.date > CAST('%s' AS TIMESTAMP) " +
                                                "GROUP BY tags.language " +
                                                "ORDER BY COUNT(tags.language) DESC " +
                                                "LIMIT 10", timestamp);

        return sparkSession.sql(sqlQuery);
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
}