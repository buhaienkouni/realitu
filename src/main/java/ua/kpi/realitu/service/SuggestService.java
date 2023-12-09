package ua.kpi.realitu.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ua.kpi.realitu.web.model.StoryDto;

@Service
@Slf4j
public class SuggestService {

    private final JavaMailSender emailSender;

    public SuggestService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendStory(StoryDto storyDto) {
        String subject = "New Story Submission";
        String recipient = "realit-u@gmx.net";

        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Personal Data: ").append(storyDto.getPersonalData()).append("\n");
        emailBody.append("Story: ").append(storyDto.getStory());

        try {
            log.info("Before creating MimeMessage...");
            MimeMessage message = emailSender.createMimeMessage();
            log.info("After creating MimeMessage...");
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("realit-u@gmx.net");
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(emailBody.toString());

            emailSender.send(message);
            log.info("Email sent successfully!");
        } catch (MessagingException e) {
            log.error("Error sending email: ", e); // Log the exception stack trace
            throw new RuntimeException(e);
        }
    }
}
