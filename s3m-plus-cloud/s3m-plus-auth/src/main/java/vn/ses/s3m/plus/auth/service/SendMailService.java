package vn.ses.s3m.plus.auth.service;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;

/**
 * Class xử lý gửi mail.
 *
 * @author Arius Vietnam JSC.
 * @since 2022-01-01.
 */
public class SendMailService {
    private static String from = "lamn44362@gmail.com";

    @Value ("${url-reset-password}")
    private static String url;

    /**
     * Hàm xử lý login và authen. *
     *
     * @param String body
     * @return void.
     */
    public static void sendMail(final String body, final String to, final String name) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("lamn44362@gmail.com", "cvofwberkrcqcadl");
            }
        };
        Session session = Session.getInstance(properties, auth);
        try {
            MimeMessage message = new MimeMessage(session);
            message.addHeader("Content-type", "text/HTML; charset=UTF-8");
            message.addHeader("format", "flowed");
            message.addHeader("Content-Transfer-Encoding", "8bit");
            message.setFrom(new InternetAddress(from, "ChangePassword-S3M"));
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            message.setSubject("Thay đổi mật khẩu S3M!");
            String href = "http://222.252.20.228:3000/reset-password/" + body;
            message.setContent("Xin chào " + name + "!" + "<br/>" + "Để thay đổi mật khẩu, hãy chọn đường link bên dưới"
                + "<br/>" + "<a href='" + href + "'>Đổi mật khẩu</a>", "text/html; charset=utf-8");
            message.setSentDate(new Date());
            message.setReplyTo(InternetAddress.parse(from, false));
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Sent message errorr....");
        }
    }
}
