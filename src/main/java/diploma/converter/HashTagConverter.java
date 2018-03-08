package diploma.converter;

import diploma.model.HashTag;
import twitter4j.HashtagEntity;
import twitter4j.Status;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HashTagConverter {

    public static List<HashTag> convert(Status status) {
        List<HashTag> hashTagList = new ArrayList<>();
        Timestamp creationDate = new Timestamp(status.getCreatedAt().getTime());

        List<String> tweetHashTags = getTweetHashTags(status.getHashtagEntities());
        tweetHashTags.forEach(message -> hashTagList.add(HashTag.builder().value(message).date(creationDate).build()));

        return hashTagList;
    }

    private static List<String> getTweetHashTags(HashtagEntity[] entities) {
        return Arrays.stream(entities)
                     .map(HashtagEntity::getText)
                     .collect(Collectors.toList());
    }
}
