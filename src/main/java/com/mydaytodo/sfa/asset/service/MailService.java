package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.model.EmailRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.repository.S3Repository;
import jakarta.activation.DataSource;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailService {
    /**
     * AES details
     */
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private S3Repository s3Repository;

    public void test() {

    }

    public ServiceResponse sendEmail(EmailRequest emailRequest) {
        try {
            MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
            log.info("1");

            StringBuilder builder = new StringBuilder();
            builder.append("<hi> Creative User </h1>");
            builder.append(emailRequest.getBody());
            builder.append("<h2> You are awesome </h2>,<br /> Cheers, <br/> Bhuman</h2>");
            mimeMailMessage.setContent(builder.toString(), "text/html; charset=utf-8");
            // if the email request contains any attachments, then send it
            for (String filename : emailRequest.getFilesToAttach()) {
                log.info("In the filesToAttachLoop");
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMailMessage, true);
                String name = filename.substring(filename.indexOf("/"));
                byte[] fileData = s3Repository.getDataForFile(filename);
                DataSource dataSource = new ByteArrayDataSource(fileData, "application/pdf");
                messageHelper.addAttachment(name, dataSource);
            }
            log.info("5");

            mimeMailMessage.setFrom("bhuman@mydaytodo.com");
            mimeMailMessage.setSubject(emailRequest.getSubject());
            log.info("6");
            // mimeMailMessage.setFrom("mdt@mydaytodo.com");
            addRecipients(Message.RecipientType.TO, new String[]{emailRequest.getTo()}, mimeMailMessage);
            addRecipients(Message.RecipientType.CC, emailRequest.getCc(), mimeMailMessage);
            addRecipients(Message.RecipientType.BCC, emailRequest.getBcc(), mimeMailMessage);
            mimeMailMessage.setHeader("Content-Type", "application/pdf");
            javaMailSender.send(mimeMailMessage);
            log.info("7");

            return ServiceResponse.builder()
                    .data(builder.toString())
                    .status(HttpStatus.SC_NO_CONTENT)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(e.getLocalizedMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private void addRecipients(Message.RecipientType recipientType, String[] addresses, MimeMessage message) throws MessagingException {
        if (addresses.length > 0) {
            InternetAddress[] internetAddresses = new InternetAddress[addresses.length];
            int idx = 0;
            for (String s : addresses) {
                internetAddresses[idx] = new InternetAddress(s);
                idx += 1;
            }
            message.setRecipients(recipientType, internetAddresses);
        }
    }
}
