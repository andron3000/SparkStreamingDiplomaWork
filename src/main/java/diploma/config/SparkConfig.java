package diploma.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static diploma.utils.Constants.*;

@Configuration
public class SparkConfig {

    @Value("${spark.streaming.duration}")
    private Long duration;

    @Bean
    public SparkConf sparkConf() {
        return new SparkConf()
                .setAppName(APP_NAME)
                .setMaster(LOCAL)
                .set(ALLOW_MULTIPLE_CONTEXTS, String.valueOf(Boolean.TRUE));
    }

    @Bean
    public JavaSparkContext sparkContext() {
        return new JavaSparkContext(sparkConf());
    }

    @Bean
    public JavaStreamingContext streamingContext() {
        return new JavaStreamingContext(sparkContext(), new Duration(duration));
    }

    @Bean
    public SQLContext sqlContex() {
        return new SQLContext(sparkContext());
    }
}
