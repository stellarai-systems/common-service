package co.mannit.commonservice.po;

import java.util.List;

import lombok.Data;

@Data
public class EmailMessageRequestBody {

	
	public String msg;
	
	public List<String> toEmail;
	
	public String fromEmail;
	
	public String subject;
}
