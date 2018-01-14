package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import twitter4j.GeoLocation;
import twitter4j.Status;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetData implements Serializable {

    private long id;
    private Date tweeted_at;
    private int favorited_count;
    private double longitude;
    private double latitude;
    private long quote_id;
    private int retweet_count;
    private String text;
    private List<String> links;
    private List<String> hashtags;
    private int sentiment;
    private String location;
    private String lucene;

    private List<String> extractHastags(String status) {

        List<String> hashtags = new ArrayList<>();
        Matcher matcher = Pattern.compile("#\\s*(\\w+)").matcher(status);
        while (matcher.find())
            hashtags.add(matcher.group(1));
        return hashtags;
    }

    private List<String> extractLinks(String status) {

        List<String> links = new ArrayList<>();
        // Pattern for recognizing a URL, based off RFC 3986
        final Pattern urlPattern = Pattern.compile(
                "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                        + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                        + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = urlPattern.matcher(status);
        while (matcher.find()) {
            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();
            links.add(status.substring(matchStart, matchEnd));
        }
        return links;
    }

    public TweetData(Status tweet, int sentiment, String location) {
        this.id = tweet.getId();
        this.tweeted_at = tweet.getCreatedAt();
        this.favorited_count = tweet.getFavoriteCount();
        GeoLocation tweetLocation = tweet.getGeoLocation();
        if (tweetLocation != null) {
            this.longitude = tweet.getGeoLocation().getLongitude();
            this.latitude = tweet.getGeoLocation().getLatitude();
        }
        this.location = location;
        this.quote_id = tweet.getQuotedStatusId();
        this.retweet_count = tweet.getRetweetCount();
        this.text = tweet.getText();
        this.hashtags = this.extractHastags(this.text);
        this.links = this.extractLinks(this.text);
        this.sentiment = sentiment;
    }
}
