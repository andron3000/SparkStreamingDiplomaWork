package diploma.controller;

import diploma.model.HashTag;
import diploma.service.HashTagProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class TwitterController {

    @Autowired
    private HashTagProcessingService hashTagProcessingService;

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
        model.addAttribute("optionId","0");
        hashTagProcessingService.stopProcessingHashTags();
        hashTagProcessingService.displayAnalyticResultByDate(model, 0);
        return "result";
    }

    @PostMapping("/selector")
    public String selectDateRange(@ModelAttribute("optionId") String optionId, Model model) {
        model.addAttribute("optionId",optionId);
        hashTagProcessingService.displayAnalyticResultByDate(model, Integer.parseInt(optionId));
        return "result";
    }

    @GetMapping(value = "/realTimeData", produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<HashTag> realTimeData() {
        return hashTagProcessingService.getRealTimeHashTags();
    }
}
