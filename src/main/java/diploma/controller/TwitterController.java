package diploma.controller;

import diploma.service.HashTagProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TwitterController {
    private static final String DISPLAY_START_PROCESSING = "displayStartProcessing";

    @Autowired
    private HashTagProcessingService hashTagProcessingService;

    @GetMapping("/")
    public String getHomePage(Model model) {
        model.addAttribute(DISPLAY_START_PROCESSING,true);
        return "home";
//        return "result"; // todo do not commit
    }

    @PostMapping("/start")
    public String startSparkStream(Model model) {
        model.addAttribute(DISPLAY_START_PROCESSING,false);
        hashTagProcessingService.startHashTagAnalysis();
        return "home";
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
}
