package vn.edu.iuh.fit.core.controller;

import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Controller;
import vn.edu.iuh.fit.core.services.EmailService;
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
            emailService.sendEmail("hieu92145@gmail.com", "IUH - register graduation", msg);
        }
    }
}
