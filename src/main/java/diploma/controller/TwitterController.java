package diploma.controller;

import diploma.dto.EmailDto;
import diploma.model.HashTag;
import diploma.service.EmailService;
import diploma.service.HashTagProcessingService;
import diploma.service.TweetProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.List;

@Controller
public class TwitterController {

    @Autowired
    private HashTagProcessingService hashTagProcessingService;

    @Autowired
    private TweetProcessingService tweetProcessingService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/")
    public String getHomePage() {
        return "home";
    }

    @PostMapping("/start")
    public String startSparkStream() {
        hashTagProcessingService.startHashTagAnalysis();
        return "realTimeData";
    }

    @PostMapping("/stop")
    public String stopSparkStream(Model model) {
        model.addAttribute("optionId", "0");
        hashTagProcessingService.stopProcessingHashTags();
        hashTagProcessingService.displayAnalyticResultByDate(model, 0);
        return "result";
    }

    @GetMapping("/result")
    public String selectDateRange(Model model) {
        model.addAttribute("optionId", "0");
        hashTagProcessingService.displayAnalyticResultByDate(model,0);
        return "result";
    }

    @PostMapping("/selector")
    public String selectDateRange(@ModelAttribute("optionId") String optionId, Model model) {
        model.addAttribute("optionId", optionId);
        hashTagProcessingService.displayAnalyticResultByDate(model, Integer.parseInt(optionId));
        return "result";
    }

    @GetMapping(value = "/realTimeData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<HashTag> realTimeData() {
        return hashTagProcessingService.getRealTimeHashTags();
    }

    @GetMapping("/search")
    public String selectDateRange() {
        return "searchPage";
    }

    @PostMapping("/search-result")
    public String selectDateRange(Model model, @ModelAttribute("searchParam") String searchParam) {
        tweetProcessingService.searchTweetsByParameter(searchParam, model);
        return "searchResult";
    }

    @GetMapping("/tweetMap")
    public String displayTweetMap(Model model) {
        tweetProcessingService.calculateTopTweetsMap(model);
        return "tweetMap";
    }

    @RequestMapping(value = "/sendEmail", method = RequestMethod.POST)
    public void sendEmailToClient(@RequestBody EmailDto emailDto) {
        File reportFile = emailService.generateReportFile(emailDto.attachFileUrl);
        emailService.sendEmail(emailDto.receiver, "Report", reportFile);
    }
}
