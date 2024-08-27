package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.model.EmailRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.repository.S3Repository;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private S3Repository s3Repository;

    @InjectMocks
    private MailService mailService;

    private static EmailRequest mockEmailRequest;

    private static final String emailBody = "Cptdanko wants to share a file with you";

    @BeforeAll
    public static void setup(){
        mockEmailRequest = EmailRequest.builder()
                .to("bhuman@mydaytodo.com")
                .cc(new String[]{"cptdanko@mydaytodo.com"})
                .body("Sharing a file with you")
                .filesToAttach(new String[]{"fil1.pdf", "file2.pdf"})
                .build();
    }

    @Test
    public void testAddRecipents_success() throws Exception {
        MimeMessage mimeMailMessage = new MimeMessage((Session) null);
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMailMessage, true);
        String [] addresses = {"bhuman.soni@gmail.com", "bhuman@mydaytodo.com"};
        mailService.addRecipients(MimeMessage.RecipientType.TO, addresses, mimeMailMessage);
        Assertions.assertEquals(2, mimeMailMessage.getAllRecipients().length);
    }
    @Test
    public void testAddRecipents_fail() throws Exception {
        MimeMessage mimeMailMessage = new MimeMessage((Session) null);
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMailMessage, true);
        String [] addresses = new String[0];
        Assertions.assertThrows(AssertionError.class, () -> {
            mailService.addRecipients(MimeMessage.RecipientType.TO, addresses, mimeMailMessage);
        });
    }

    @Test
    public void testSendEmail_success() throws Exception {
        when(s3Repository.getDataForFile(any())).thenReturn(new byte[256]);
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        ServiceResponse response = mailService.sendEmail(MailServiceTest.mockEmailRequest);
        Assertions.assertTrue(response.
                getData().toString().equalsIgnoreCase("Sharing a file with you"));
    }
}
