package co.mannit.commonservice.common.util;

import java.net.URLEncoder;
import java.util.Base64;
import java.util.UUID;

public class UniqueIdGenerator {

	public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
	
	public static String geberateId(String domain, String subdomain) {
		String base64 = Base64.getEncoder().encodeToString((domain+"-"+subdomain).getBytes());
		return base64.substring(0, base64.length()-1);
	}
	
	public static void main(String[] args) {
		System.out.println(geberateId("IT", "Software"));
		System.out.println(URLEncoder.encode( UUID.randomUUID().toString()));
		 final String uuid = UUID.randomUUID().toString().replace("-", "");
		System.out.println(uuid);
	}
	
	
}
