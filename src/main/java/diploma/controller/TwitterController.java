package diploma.controller;

import diploma.service.HashTagProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TwitterController {

    @Autowired
    private HashTagProcessingService hashTagProcessingService;

    @GetMapping("/")
    public String getSparkStream() {
        hashTagProcessingService.hashTagAnalysis();
        return "spark";
    }
}
