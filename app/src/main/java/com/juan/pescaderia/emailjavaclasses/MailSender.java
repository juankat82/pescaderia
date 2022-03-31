package com.juan.pescaderia.emailjavaclasses;



import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.juan.pescaderia.R;
import com.juan.pescaderia.persistence.sharedpreferencesconfig.SharedPreferencesController;
import java.security.Security;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MailSender extends javax.mail.Authenticator {
    //Hostname of the SMTP mail server which you want to connect for sending emails.
    private final static String mailhost = "smtp.gmail.com";
    //Your SMTP username. In case of GMail SMTP this is going to be your GMail email address.
    private final static String user= SharedPreferencesController.Companion.getCompanyEmail();
    // Your SMTP password. In case of GMail SMTP this is going to be your GMail password.
    private final static String password="Imk230)(\"kl:{\""; //HAS TO BE ERASED IF CODE IS ON GITHUB
    private final Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public MailSender() {

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized boolean sendMail(String subject, String body, String sender, String recipients, Context context) throws Exception {
        boolean success;
        MimeMessage message = new MimeMessage(session);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        message.setDataHandler(handler);

        if (recipients.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));

        try{
            success = true;
            Transport.send(message);
        }
        catch (javax.mail.AuthenticationFailedException ex)
        {
            success = false;
            new Handler(Looper.getMainLooper()).postDelayed(() -> Toast.makeText(context, R.string.fallo_autenticacion, Toast.LENGTH_SHORT).show(),200L);
        }
        return success;
    }
}

