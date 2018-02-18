package diploma.converter;

import diploma.model.TweetData;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import twitter4j.Status;

import java.nio.charset.StandardCharsets;

public class TweetDataConverter {

    public static TweetData convert(Status status) {
        return TweetData.builder()
                .createDate(status.getCreatedAt().toString())
                .country(status.getPlace() != null ? status.getPlace().getCountry() : null)
                .language(status.getLang())
                .text(status.getText())
                .build();
    }

    public static Boolean isUtf8(Status status) {
        CharsetDetector detector = new CharsetDetector();

        detector.setText(status.getText().getBytes());
        CharsetMatch match = detector.detect();

        return StandardCharsets.UTF_8.name().equals(match.getName());
    }
}