package diploma.service.impl;

import diploma.converter.TweetDataConverter;
import diploma.model.TweetData;
import diploma.repository.TweetDataRepository;
import diploma.service.HashTagProcessingService;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import twitter4j.Status;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Service
public class HashTagProcessingServiceImpl implements HashTagProcessingService {

    @Autowired
    private TweetDataRepository tweetDataRepository;

    @Autowired
    private JavaStreamingContext streamingContext;

    @Value("${spark.streaming.timeout.max}")
    private Long maxTimeout;

    private JavaReceiverInputDStream<Status> stream;

    @Override
    public void hashTagAnalysis() {
        checkStreamState();

        JavaDStream<TweetData> tweetDataJavaDStream = stream
                .flatMap((FlatMapFunction<Status, TweetData>) t -> Collections.singletonList(TweetDataConverter.convert(t)));

        tweetDataJavaDStream.persist().foreachRDD(x -> {
            if (!x.isEmpty() && x.first() != null) {
                if(isCorrectEncoding(x.first())) {
                    TweetData data = tweetDataRepository.save(x.first());
                    System.out.println(data.getText());
                }
            }
        });

        streamingContext.start();
        streamingContext.awaitTerminationOrTimeout(maxTimeout);
    }

    private boolean isCorrectEncoding(TweetData tweetData) {
        boolean isUtf8 = true;
        try {
            tweetData.getText().getBytes(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ignored) {
            isUtf8 = false;
        }
        return isUtf8;
    }

    private void checkStreamState() {
        if(this.stream == null) {
            this.stream = TwitterUtils.createStream(streamingContext);
        }
    }
}