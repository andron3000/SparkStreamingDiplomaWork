package diploma.service;

import diploma.model.TweetData;
import org.springframework.ui.Model;
import twitter4j.Status;

public interface TweetProcessingService {

    boolean hasGeoLocation(Status status);

    boolean isSpecifiedLaguage(TweetData tweetData);

    boolean isEnglishLanguage(TweetData tweetData);

    void searchTweetsByParameter(String searchParam, Model model);

    void calculateTopTweetsMap(Model model);
}
