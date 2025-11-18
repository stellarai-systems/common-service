package co.mannit.commonservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.mannit.commonservice.common.Response;
import co.mannit.commonservice.po.EmailMessageRequestBody;
import co.mannit.commonservice.service.Msgserv;

@RestController
@RequestMapping("/auth")
public class EmailController {
	
	@Autowired
	private Msgserv msgjd;
	
	@PostMapping("/sendEmail")
	public Response<String> sendEmail(@RequestBody EmailMessageRequestBody body){
		msgjd.sendMessageEmail(body);
		
		return  Response.buildSuccessMsg(200, "Successfully Sent Email", String.format("Successfully sent email to %s recipients", body.toEmail.size()));
	}
}
