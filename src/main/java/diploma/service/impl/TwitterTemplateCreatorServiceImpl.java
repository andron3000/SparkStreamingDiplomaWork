package diploma.service.impl;

import diploma.service.TwitterTemplateCreatorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;

@Service
public class TwitterTemplateCreatorServiceImpl implements TwitterTemplateCreatorService {

    @Value("${spring.social.twitter.appId}")
    private String consumerKey;

    @Value("${spring.social.twitter.appSecret}")
    private String consumerSecret;

    @Value("${spring.social.twitter.accessToken}")
    private String accessToken;

    @Value("${spring.social.twitter.accessTokenSecret}")
    private String accessTokenSecret;

    @Override
    public Twitter getTwitterTemplate() {
        return new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
    }
}