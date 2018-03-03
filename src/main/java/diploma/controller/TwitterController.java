package diploma.controller;

import diploma.service.HashTagProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TwitterController {

    @Autowired
    private HashTagProcessingService hashTagProcessingService;

    @GetMapping("/")
    public String getHomePage(Model model) {
        model.addAttribute("displayStartProcessing",true);
        return "home";
    }

    @PostMapping("/start")
    public String startSparkStream(Model model) {
        model.addAttribute("displayStartProcessing",false);
        hashTagProcessingService.startHashTagAnalysis();
        return "home";
    }

    @PostMapping("/stop")
    public String stopSparkStream() {
        hashTagProcessingService.stopProcessingHashTags();
        return "result";
    }
}
