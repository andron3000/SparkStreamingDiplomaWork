package diploma.service.impl;

import diploma.model.TweetData;
import diploma.service.TweetProcessingService;
import org.springframework.stereotype.Service;
import twitter4j.Status;

import static diploma.utils.Constants.ENGLISH_LANGUAGE;

@Service
public class TweetProcessingServiceImpl implements TweetProcessingService {

    @Override
    public boolean hasGeoLocation(Status status) {
        return status.getGeoLocation() != null;
    }

    @Override
    public boolean isSpecifiedLaguage(TweetData tweetData) {
        return tweetData.getLanguage() != null;
    }

    @Override
    public boolean isEnglishLanguage(TweetData tweetData) {
        return ENGLISH_LANGUAGE.equals(tweetData.getLanguage());
    }
}
