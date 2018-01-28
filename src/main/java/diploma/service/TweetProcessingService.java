package diploma.service;

import diploma.model.TweetData;
import twitter4j.Status;

public interface TweetProcessingService {

    boolean hasGeoLocation(Status status);

    boolean isSpecifiedLaguage(TweetData tweetData);

    boolean isEnglishLanguage(TweetData tweetData);
}
