package diploma.service;

import java.io.File;

public interface EmailService {

    File generateReportFile(String attachFileUrl);

    void sendEmail( String to, String subject, File file);
}
