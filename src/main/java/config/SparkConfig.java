package config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    private static final String APP_NAME = "Diploma Work";
    private static final String LOCAL = "local[*]";
    private static final String ALLOW_MULTIPLE_CONTEXTS = "spark.driver.allowMultipleContexts";
    private static final long DURATION = 5000L;

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
        return new JavaStreamingContext(sparkContext(), new Duration(DURATION));
    }
}
