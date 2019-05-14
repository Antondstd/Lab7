import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public class Mail {
    public static void send(String address, String generatedPassword)

    {

        final String username = "testmailvt7@yandex.ru";
        final String password = "45eA$8795y";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.yandex.ru");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("testmailvt7@yandex.ru"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(address)
            );
            MessageDigest md = MessageDigest.getInstance( "SHA-256" );

            // Change this to UTF-16 if needed
            md.update( generatedPassword.getBytes( StandardCharsets.UTF_8 ) );
            byte[] digest = md.digest();

            String hex = String.format( "%064x", new BigInteger( 1, digest ) );
            System.out.println( hex );
            message.setSubject("Пароль для 7 лабы");
            message.setText("Пароль ---- " + generatedPassword);

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

}
