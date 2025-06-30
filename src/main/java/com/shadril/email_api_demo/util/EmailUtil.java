package com.shadril.email_api_demo.util;

import com.shadril.email_api_demo.dto.FileDetails;
import com.shadril.email_api_demo.dto.request.EmailDto;
import com.shadril.email_api_demo.dto.request.FileAttachmentDto;
import com.shadril.email_api_demo.dto.request.InlineContentDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailUtil {

    private final JavaMailSender mailSender;

    private static final Set<String> VALID_IMAGE_TYPES = Set.of("image/png", "image/jpg", "image/jpeg");
    private static final Set<String> VALID_ATTACHMENT_TYPES =
            Set.of("image/png", "image/jpg", "image/jpeg", "application/pdf", "application/xml"
                    , "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "text/xml", "text/csv", "application/vnd.ms-excel", "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");


    public void sendMimeMail(EmailDto emailDto, String fromEmail) throws MailSendException {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            // Set multipart to true to support both inline content and file attachments
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setSubject(emailDto.getSubject());
            helper.setTo(emailDto.getRecipients().toArray(new String[0]));
            helper.setFrom(fromEmail);

            if (emailDto.getCcRecipients() != null && !emailDto.getCcRecipients().isEmpty()) {
                helper.setCc(emailDto.getCcRecipients().toArray(new String[0]));
            }

            if (emailDto.getBccRecipients() != null && !emailDto.getBccRecipients().isEmpty()) {
                helper.setBcc(emailDto.getBccRecipients().toArray(new String[0]));
            }

            helper.setText(emailDto.getContent(), true); // true for HTML

            // Handle inline content if it exists
            List<InlineContentDto> inlineContent = emailDto.getInlineContent();
            if (inlineContent != null && !inlineContent.isEmpty()) {
                for (InlineContentDto inline : inlineContent) {
                    FileDetails fileDetails = FileUtil.decodeBase64ToFileDetails(inline);
                    String inlineContentType = fileDetails.getMimeType();
                    if (!VALID_IMAGE_TYPES.contains(inlineContentType)) {
                        throw new MessagingException(
                                "Invalid inline content type: " + inlineContentType +
                                        ". Only image/png and image/jpeg are accepted."
                        );
                    }
                    helper.addInline(inline.getContentId(), fileDetails.getResource());
                }
            }

            // Handle file attachments if they exist
            List<FileAttachmentDto> fileAttachments = emailDto.getFileAttachments();
            if (fileAttachments != null && !fileAttachments.isEmpty()) {
                for (FileAttachmentDto attachment : fileAttachments) {
                    FileDetails fileDetails = FileUtil.decodeBase64ToFileDetails(attachment);
                    String attachmentContentType = fileDetails.getMimeType();
                    if (!VALID_ATTACHMENT_TYPES.contains(attachmentContentType)) {
                        throw new MessagingException(
                                "Invalid File attachment content type: " + attachmentContentType +
                                        ". Only " + VALID_ATTACHMENT_TYPES + " are accepted."
                        );
                    }
                    helper.addAttachment(fileDetails.getFileName(), fileDetails.getResource());
                }
            }

            log.debug("Send message :: {}", message);
            mailSender.send(message);

        } catch (MailSendException | MessagingException e) {
            throw new MailSendException("Failed to send email", e);
        }
    }
}
