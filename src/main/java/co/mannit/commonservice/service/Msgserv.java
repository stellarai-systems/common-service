package co.mannit.commonservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import co.mannit.commonservice.po.EmailMessageRequestBody;
import co.mannit.commonservice.po.MsgDetails;

@Service
public class Msgserv {

	@Autowired
	private JavaMailSender emailSender;
	
	
	public boolean sendMessage(String msg,String email) {
		// TODO Auto-generated method stub
		//write email sending code here
		//System.out.println("hi");
		SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("contact@mannit.co");
        message.setTo(email);
        message.setSubject("Password Reset Request – One-Time Password (OTP)");
        message.setText(msg);

        emailSender.send(message);
		return true;
	}
	public void sendMessageEmail(EmailMessageRequestBody body) {
		
		SimpleMailMessage message =new SimpleMailMessage();
		message.setFrom(body.getFromEmail());
		//message.setTo(body.getToEmail());
		message.setTo(body.getToEmail().toArray(new String[0]));

		message.setSubject(body.getSubject());
		message.setText(body.getMsg());
		
	
	
		emailSender.send(message);
	}
}
