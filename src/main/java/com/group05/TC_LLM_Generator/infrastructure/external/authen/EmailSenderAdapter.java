package com.group05.TC_LLM_Generator.infrastructure.external.authen;

import com.group05.TC_LLM_Generator.application.port.out.authen.EmailSenderPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSenderAdapter implements EmailSenderPort {

    private final JavaMailSender mailSender;

    @Value("${app.mail-from}")
    private String fromAddress;

    @Override
    public void sendVerificationEmail(String toEmail, String fullName, String verificationUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject("Verify your QA Artifacts account");
            helper.setText(buildEmailHtml(fullName, verificationUrl), true);

            mailSender.send(message);
            log.info("Verification email sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}", toEmail, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private String buildEmailHtml(String fullName, String verificationUrl) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin:0;padding:0;background-color:#f4f4f5;font-family:'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="padding:40px 20px;">
                        <tr>
                            <td align="center">
                                <table width="480" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 1px 3px rgba(0,0,0,0.1);">
                                    <!-- Header -->
                                    <tr>
                                        <td style="background:#18181b;padding:32px 40px;text-align:center;">
                                            <h1 style="margin:0;color:#ffffff;font-size:20px;font-weight:700;letter-spacing:-0.025em;">
                                                ✓ QA Artifacts
                                            </h1>
                                        </td>
                                    </tr>
                                    <!-- Body -->
                                    <tr>
                                        <td style="padding:40px;">
                                            <h2 style="margin:0 0 8px;color:#18181b;font-size:22px;font-weight:700;">
                                                Hi %s,
                                            </h2>
                                            <p style="margin:0 0 24px;color:#71717a;font-size:15px;line-height:1.6;">
                                                Welcome to <strong>QA Artifacts</strong> — an AI-powered platform that transforms your user stories into comprehensive test cases. We're excited to have you on board!
                                            </p>
                                            <p style="margin:0 0 24px;color:#18181b;font-size:15px;font-weight:600;">
                                                Do you confirm creating an account with this email?
                                            </p>
                                            <!-- CTA Button -->
                                            <table width="100%%" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td align="center" style="padding:8px 0 24px;">
                                                        <a href="%s" style="display:inline-block;background:#18181b;color:#ffffff;text-decoration:none;padding:14px 32px;border-radius:8px;font-size:15px;font-weight:600;letter-spacing:-0.01em;">
                                                            Verify My Email →
                                                        </a>
                                                    </td>
                                                </tr>
                                            </table>
                                            <p style="margin:0 0 8px;color:#a1a1aa;font-size:13px;text-align:center;">
                                                This link will expire in <strong>5 minutes</strong>.
                                            </p>
                                        </td>
                                    </tr>
                                    <!-- Footer -->
                                    <tr>
                                        <td style="padding:24px 40px;background:#fafafa;border-top:1px solid #f4f4f5;">
                                            <p style="margin:0;color:#a1a1aa;font-size:12px;text-align:center;line-height:1.5;">
                                                If you didn't request this, you can safely ignore this email.
                                                <br>© 2026 QA Artifacts Inc.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(fullName, verificationUrl);
    }
}
