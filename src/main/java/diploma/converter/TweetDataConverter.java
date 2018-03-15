package diploma.converter;

import diploma.model.TweetData;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import twitter4j.HashtagEntity;
import twitter4j.Status;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class TweetDataConverter {

    public static TweetData convert(Status status) {
        return TweetData.builder()
                .createDate(new Timestamp(status.getCreatedAt().getTime()))
                .country(status.getPlace() != null ? status.getPlace().getCountry() : null)
                .language( new Locale(status.getLang()).getDisplayName())
                .hashTags(getTweetHashTags(status.getHashtagEntities()))
                .build();
    }

    private static String getTweetHashTags(HashtagEntity[] entities) {
        return Arrays.stream(entities)
                .map(HashtagEntity::getText)
                .collect(Collectors.joining(", "));
    }

    public static Boolean isUtf8(Status status) {
        CharsetDetector detector = new CharsetDetector();

        detector.setText(status.getText().getBytes());
        CharsetMatch match = detector.detect();

        return StandardCharsets.UTF_8.name().equals(match.getName());
    }

    public static Boolean containsHashTags(Status status) {
        return status.getHashtagEntities() != null && status.getHashtagEntities().length != 0;
    }
}