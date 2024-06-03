package vn.edu.iuh.fit.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Controller;
import vn.edu.iuh.fit.core.services.EmailService;
import jakarta.jms.*;
import vn.edu.iuh.fit.core.utils.Constants;

@Controller
public class EmailController {
    @Autowired
    private EmailService emailService;


    @JmsListener(destination = Constants.REGISTER_COURSE)
    public void receiveMessage(final Message message) throws Exception {

        if(message instanceof TextMessage) {
            //1. read message data
            String msg = ((TextMessage) message).getText();
            System.out.println("MSG: " + msg);

            String[] parts = msg.split(" ");
            String email = parts[parts.length - 1];
            System.out.println("EmailController: " + email);

            int emailPosition = msg.lastIndexOf(email);
            String msgWithoutEmail = msg.substring(0, emailPosition).trim();
            System.out.println("Message without email: " + msgWithoutEmail);

            emailService.sendEmail(email, "IUH - Success", msgWithoutEmail);
        }
    }
}
