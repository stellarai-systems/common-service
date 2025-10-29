package co.mannit.commonservice.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.mannit.commonservice.dao.CommonDao;

@Service
public class WebhookServiceR {

	@Autowired
	private CommonDao dao;
	
	public boolean VerifyWebhooksignature(String payload, String signature, String secretkey)
			throws InvalidKeyException, NoSuchAlgorithmException {
		// signature = "5b50d80c7dc7ae8bb1b1433cc0b99ecd2ac8397a555c6f75cb8a619ae35a0c35";
	
	

		String hmacSHA256Algorithm = "HmacSHA256";
		String result = hmacWithJava(hmacSHA256Algorithm, payload, secretkey);
		if (signature.equals(result)) {
			
			//System.out.println("TRUE");
			return true;
		} else {
			//System.out.println("FALSE");
			return false;
		}

	}
	
	public static String hmacWithJava(String algorithm, String data, String key)
			  throws NoSuchAlgorithmException, InvalidKeyException {
			    SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), algorithm);
			    Mac mac = Mac.getInstance(algorithm);
			    mac.init(secretKeySpec);
			    return byteArrayToHex(mac.doFinal(data.getBytes()));
	}
	public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
	public  void savePayLoad(String json,String status) {
		if(json!=null) {
			Document k =Document.parse(json);
			k.append("wStatus", status);
			dao.saveDocument("RazorpayData",k);
		}
	}
	
}
