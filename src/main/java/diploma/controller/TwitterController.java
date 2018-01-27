package diploma.controller;

import diploma.service.TwitterTemplateCreatorService;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.CursoredList;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import twitter4j.Status;

@Controller
public class TwitterController {

    @Autowired
    private TwitterTemplateCreatorService twitterTemplateCreatorService;

    @Autowired
    private JavaStreamingContext streamingContext;

    @GetMapping("/")
    public String getUserDetails(Model model) {
        Twitter twitterTemplate = twitterTemplateCreatorService.getTwitterTemplate();

        String[] filters={"#test"};
        JavaReceiverInputDStream<Status> stream = TwitterUtils.createStream(streamingContext, filters);

//        final JavaReceiverInputDStream<Status> stream = twitterTemplate.streamingOperations().sample()
        model.addAttribute(twitterTemplate.userOperations().getUserProfile());
        CursoredList<TwitterProfile> friends = twitterTemplate.friendOperations().getFriends();
        model.addAttribute("friends", friends);
        return "userDetails";
    }
}
