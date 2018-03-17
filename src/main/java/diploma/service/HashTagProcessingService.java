package diploma.service;

import diploma.model.HashTag;
import org.springframework.ui.Model;

import java.util.List;

public interface HashTagProcessingService {

    void startHashTagAnalysis();

    void stopProcessingHashTags();

    void displayAnalyticResultByDate(Model model, int i);

    List<HashTag> getRealTimeHashTags();
}