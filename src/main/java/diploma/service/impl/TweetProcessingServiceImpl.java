package diploma.service.impl;

import diploma.converter.HashTagConverter;
import diploma.model.HashTag;
import diploma.model.TweetData;
import diploma.service.TweetProcessingService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import twitter4j.*;

import java.util.*;

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

    @Override
    public void searchTweetsByParameter(String searchParam, Model model) {
        Twitter twitter = new TwitterFactory().getInstance();
//        Set<String> countriesList = new HashSet<>();
        List<HashTag> customHashTags = new ArrayList<>();

        try {
//            ResponseList<Location> availableTrends = twitter.getAvailableTrends();
//            availableTrends.stream()
//                    .filter(x-> StringUtils.isNotBlank(x.getCountryName()))
//                    .map(Location::getCountryName)
//                    .forEach(countriesList::add);

            Query query = new Query(searchParam);
            QueryResult result;
            do {
                result = twitter.search(query);
                result.getTweets()
                        .stream()
                        .filter(x -> Objects.nonNull(x.getHashtagEntities()))
                        .map(HashTagConverter::customConvert)
                        .forEach(x-> x.forEachRemaining(customHashTags::add));
            } while ((query = result.nextQuery()) != null && customHashTags.size() < 50);

//            model.addAttribute("countries", countriesList);
            model.addAttribute("hashTags", customHashTags);

        } catch (TwitterException te) {
            System.out.println("Failed to search tweets: " + te.getMessage());
        }
    }
}
