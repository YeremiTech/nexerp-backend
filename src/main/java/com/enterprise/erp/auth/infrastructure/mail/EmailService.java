package com.enterprise.erp.auth.infrastructure.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.from-name}")
    private String fromName;

    public void sendPasswordResetCode(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from, fromName);
            helper.setTo(to);
            helper.setSubject("Código de recuperación de contraseña");
            helper.setText(buildHtml(code), true);
            mailSender.send(message);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            throw new IllegalStateException("No se pudo enviar el correo de recuperación de contraseña", e);
        }
    }

    private String buildHtml(String code) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Recuperación de contraseña</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f4f6f8;font-family:Arial,Helvetica,sans-serif;">
                    <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f6f8;padding:24px 0;">
                        <tr>
                            <td align="center">
                                <table role="presentation" width="480" cellpadding="0" cellspacing="0" style="max-width:480px;width:100%%;background-color:#ffffff;border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.08);">
                                    <tr>
                                        <td style="background-color:#2563eb;padding:20px 32px;">
                                            <span style="color:#ffffff;font-size:18px;font-weight:bold;">ERP PYMES</span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:32px;">
                                            <h1 style="margin:0 0 12px;font-size:20px;color:#111827;">Recuperación de contraseña</h1>
                                            <p style="margin:0 0 24px;font-size:14px;line-height:1.6;color:#4b5563;">
                                                Hemos recibido una solicitud para restablecer tu contraseña. Usa el siguiente código para continuar:
                                            </p>
                                            <div style="background-color:#eff6ff;border:1px solid #bfdbfe;border-radius:6px;padding:20px;text-align:center;letter-spacing:16px;font-size:32px;font-weight:bold;color:#1d4ed8;margin:0 0 24px;">
                                                %s
                                            </div>
                                            <p style="margin:0 0 8px;font-size:13px;line-height:1.6;color:#6b7280;">
                                                El código es válido por <strong>1 minuto</strong>.
                                            </p>
                                            <p style="margin:0;font-size:13px;line-height:1.6;color:#6b7280;">
                                                Si no solicitaste este cambio, ignora este correo.
                                            </p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:16px 32px;background-color:#f9fafb;border-top:1px solid #e5e7eb;">
                                            <p style="margin:0;font-size:12px;color:#9ca3af;text-align:center;">
                                                Este es un correo automático, por favor no respondas a este mensaje.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(code);
    }
}
