package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.model.EmailRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.repository.S3Repository;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class MailService {
    /**
     * AES details
     */
    @Autowired
    private JavaMailSender javaMailSender;
    // JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

    @Autowired
    private S3Repository s3Repository;

    public void test() {

    }

    public ServiceResponse sendEmail(EmailRequest emailRequest) {
        log.info("In the sendEmail function");
        MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
        StringBuilder builder = new StringBuilder();
        builder.append(emailRequest.getBody());
        try {
            // if the email request contains any attachments, then send it
            for (String filename : emailRequest.getFilesToAttach()) {
                log.info("Filename is {}", filename);
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMailMessage, true);
                byte[] fileData = s3Repository.getDataForFile(filename);
                String name = filename.substring(filename.indexOf("/") + 1);
                String extension = name.substring(name.lastIndexOf('.') + 1);
                log.info("File extension is {}", extension);
                ByteArrayResource fileDataResource = new ByteArrayResource(fileData);
                messageHelper.addAttachment(name, fileDataResource);
                messageHelper.setText(builder.toString(), true);
            }
            mimeMailMessage.setFrom("bhuman@mydaytodo.com");
            mimeMailMessage.setSubject(emailRequest.getSubject());
            addRecipients(Message.RecipientType.TO, new String[]{emailRequest.getTo()}, mimeMailMessage);
            if(emailRequest.getCc() != null)
                addRecipients(Message.RecipientType.CC, emailRequest.getCc(), mimeMailMessage);
            if(emailRequest.getBcc() != null)
                addRecipients(Message.RecipientType.BCC, emailRequest.getBcc(), mimeMailMessage);

            javaMailSender.send(mimeMailMessage);
            return ServiceResponse.builder()
                    .data(builder.toString())
                    .status(HttpStatus.SC_NO_CONTENT)
                    .build();
        } catch (MailException e) {
            log.error(e.getMessage());
            log.error(e.getLocalizedMessage());
            throw new RuntimeException(e.getMessage());
        } catch (MessagingException me) {
            log.error(me.getMessage());
            log.info("Messaging exception occurred with {}", me.getLocalizedMessage());
            throw new RuntimeException(me);
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
            log.info("IO exception occurred while sending mail {}", ioe.getLocalizedMessage());
            throw new RuntimeException(ioe);
        }
    }

    public void addRecipients(Message.RecipientType recipientType, String[] addresses, MimeMessage message) throws MessagingException, AssertionError {
        assert addresses.length > 0;
        InternetAddress[] internetAddresses = new InternetAddress[addresses.length];
        int idx = 0;
        for (String s : addresses) {
            internetAddresses[idx] = new InternetAddress(s);
            idx += 1;
        }
        message.setRecipients(recipientType, internetAddresses);
    }
}
