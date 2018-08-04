package nicodo.com.myemail;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GMail {

    private final String emailPort = "587";// gmail's smtp port
    private final String smtpAuth = "true";
    private final String starttls = "true";
    private final String emailHost = "smtp.gmail.com";

    private Multipart _multipart = new MimeMultipart();


    private String fromEmail;
    private String fromPassword;
    private List<String> toEmailList;
    private String emailSubject;
    private String emailBody;

    private Properties emailProperties;
    private Session mailSession;
    private MimeMessage emailMessage;

    public GMail() {

    }

    GMail(String fromEmail, String fromPassword,
                 List<String> toEmailList, String emailSubject, String emailBody) {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.toEmailList = toEmailList;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.i("GMail", "Mail server properties set.");
    }

    public MimeMessage createEmailMessage() throws
            MessagingException, UnsupportedEncodingException {

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);

        emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));
        for (String toEmail : toEmailList) {
            Log.i("GMail", "toEmail: " + toEmail);
            emailMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(toEmail));
        }

        emailMessage.setSubject(emailSubject);

//        emailMessage.setContent(emailBody, "text/html");// for a html email
         emailMessage.setText(emailBody);// for a text email

        Log.i("GMail", "Email Message created.");
        return emailMessage;
    }

    public void sendEmail() throws MessagingException {


        Transport transport = mailSession.getTransport("smtp");
        transport.connect(emailHost, fromEmail, fromPassword);
        Log.i("GMail", "allrecipients: " + emailMessage.getAllRecipients());
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        Log.i("GMail", "Email sent successfully.");
    }

    public void addAttachment(List<String> filename) throws Exception {



  /*      DataSource source = new FileDataSource(filename);

        messageBodyPart.setDataHandler(new DataHandler(source));

        String photoTitle = Util.getCurrentTime() + ".jpg";
        messageBodyPart.setFileName(photoTitle);
*/
        // Email Picture(s)
        // Iterate through each picture and add to the mail attachment
        for(String file: filename){
            BodyPart messageBodyPart = createPictureAttachment(file);
            _multipart.addBodyPart(messageBodyPart);
        }

        // Email Body Text
        BodyPart messageBodyPartText = new MimeBodyPart();
        //        messageBodyPartText.setContent(emailBody, "text/html");
        messageBodyPartText.setText(emailBody);// for a text email
        _multipart.addBodyPart(messageBodyPartText);

        emailMessage.setContent(_multipart);
    }

    private BodyPart createPictureAttachment(String filename) throws Exception{

        BodyPart messageBodyPart = new MimeBodyPart();

        DataSource source = new FileDataSource(filename);

        messageBodyPart.setDataHandler(new DataHandler(source));

        String photoTitle = Util.getCurrentTime() + ".jpg";
        messageBodyPart.setFileName(photoTitle);

        return messageBodyPart;

    }


}