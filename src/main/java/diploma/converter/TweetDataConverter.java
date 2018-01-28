package diploma.converter;

import diploma.model.TweetData;
import twitter4j.Status;

public class TweetDataConverter {

    public static TweetData convert(Status status) {
        return TweetData.builder()
                .createDate(status.getCreatedAt())
                .country(status.getPlace() != null ? status.getPlace().getCountry() : null)
                .language(status.getLang())
                .text(status.getText())
                .build();
    }
}