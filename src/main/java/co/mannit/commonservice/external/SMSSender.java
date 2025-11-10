package co.mannit.commonservice.external;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import co.mannit.commonservice.po.MsgDetails;


public class SMSSender implements MsgSender{

	@Override
	public boolean sendMessage(MsgDetails msgDetails) {
		// TODO Auto-generated method stub
		//Write sms sending code here //whatsapp 
		String url ="https://rabbitmq.mannit.co/publish/queue2";
		String user = "9551530140";
		List<Map<String, Object>> arr = new ArrayList<>();
		List<String[]> b = new ArrayList<>();
		Map<String, Object> req = new HashMap<String, Object>();
		String[] arr2 = { "name", msgDetails.getToAddress()};
		b.add(arr2);
		// System.out.println(Arrays.toString(arr2));
		req.put("user", user);

	

			String msg = msgDetails.getMsg();
            //System.out.println("message>>>>>>>>>>>>>>>>>>>>"+msg);
			req.put("message",msg);
		

		req.put("selectedData", b);
		arr.add(req);
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(60000); // 20 seconds
		factory.setReadTimeout(60000);
	
		RestTemplate restTemplate = new RestTemplate(factory);
		ResponseEntity<String> response = restTemplate.postForEntity(url, arr, String.class);
		//System.out.println(response);
		return true;
	}

	
	
}
