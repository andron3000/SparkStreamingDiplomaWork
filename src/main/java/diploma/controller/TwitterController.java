package diploma.controller;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import twitter4j.Status;

import java.util.Arrays;

@Controller
public class TwitterController {

    @Autowired
    private JavaStreamingContext streamingContext;

    @GetMapping("/")
    public String getSparkStream() {
//        String[] filters={"#lviv"};
        JavaReceiverInputDStream<Status> stream = TwitterUtils.createStream(streamingContext);
        JavaDStream<String> words = stream.flatMap((FlatMapFunction<Status, String>) s -> Arrays.asList(s.getText().split(" ")));
        JavaDStream<String> hashTags = words.filter((Function<String, Boolean>) word -> word.startsWith("#"));

        hashTags.print();
        streamingContext.start();
        streamingContext.awaitTermination();

        return "spark";
    }
}
