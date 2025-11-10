package co.mannit.commonservice.controller;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.mannit.commonservice.service.WebhookServiceR;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("W")
@Slf4j
@CrossOrigin("*")
public class WebhookRazorpay {

	@Autowired
	private WebhookServiceR service;
	
	public static final String secretkey="#sec&web";
	@PostMapping("/Webhook/web")
	public ResponseEntity<String> RazorpayWebhook(@RequestBody(required=false) String json,@RequestHeader("X-Razorpay-Signature") String signature) {
	log.info("received request");
		try {
			if(service.VerifyWebhooksignature(json, signature,secretkey)) {
				log.info("Webhook verified successfully. Payload: {}",json);
				service.savePayLoad(json,"successfull");
				return ResponseEntity.ok("SUCCESSFULL");
			}else {
				log.warn("Invalid signature for payload: {}",json);
				service.savePayLoad(json,"UNAUTHORIZED");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Signature");
			}
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
			log.error("Error processing payload");
			service.savePayLoad(json,"SERVERERROR");
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Processing Request Body");
			
		}
	}
}
