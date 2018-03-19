package diploma.service.impl;

import diploma.converter.HashTagConverter;
import diploma.model.HashTag;
import diploma.model.TweetData;
import diploma.service.TweetProcessingService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import twitter4j.Location;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        List<HashTag> customHashTags = new ArrayList<>();
        try {
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
            model.addAttribute("hashTags", customHashTags);

        } catch (TwitterException te) {
            System.out.println("Failed to search tweets: " + te.getMessage());
        }
    }

    @Override
    public void calculateTopTweetsMap(Model model) {
        Twitter twitter = new TwitterFactory().getInstance();
        Map<String, Long> locationMap;
        try {
            ResponseList<Location> availableTrends = twitter.getAvailableTrends();
            locationMap = availableTrends.stream()
                                         .filter(x -> StringUtils.isNotBlank(x.getCountryName()))
                                         .collect(Collectors.groupingBy(Location::getCountryName, Collectors.counting()));

            model.addAttribute("countries", locationMap);
        } catch (TwitterException te) {
            System.out.println("Failed to fetch tweets: " + te.getMessage());
        }
    }
}
