package diploma.service.impl;

import diploma.service.EmailService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    @Qualifier("getJavaMailSender")
    public JavaMailSender emailSender;

    @Override
    public File generateReportFile(String attachFileUrl) {
        File temp = null;
        try {
            String base64Image = attachFileUrl.split(",")[1];
            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);

            temp = File.createTempFile("report", ".png");
            FileUtils.writeByteArrayToFile(temp, imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return temp;
    }

    @Override
    public void sendEmail(String to, String subject, File file) {
        MimeMessage message = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText("Report");

            FileSystemResource report = new FileSystemResource(file);

            helper.addAttachment("Report.png", report);
        } catch (MessagingException ignored) { }

        emailSender.send(message);
    }
}
