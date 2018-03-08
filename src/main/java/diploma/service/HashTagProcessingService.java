package diploma.service;

import org.springframework.ui.Model;

public interface HashTagProcessingService {

    void startHashTagAnalysis();

    void stopProcessingHashTags();

    void displayAnalyticResultByDate(Model model, int i);
}