package oshrik.shidech_stable_match.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Spring מזריק את ה-JavaMailSender אוטומטית אחרי שנגדיר אותו
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * פונקציה ראשית לשליחת אימייל טקסטואלי פשוט.
     * הערה: בשימוש עם Gmail, כתובת המקור תהיה תמיד הכתובת של השרת שלנו.
     */
    public void sendSimpleEmail(String destination, String subject, String messageText) {
        SimpleMailMessage message = new SimpleMailMessage();
        
        // הגדרת פרטי המייל
        message.setTo(destination);
        message.setSubject(subject);
        message.setText(messageText);
        
        // ביצוע השליחה
        mailSender.send(message);
        System.out.println("Email sent successfully to: " + destination);
    }
}