package co.mannit.commonservice.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import co.mannit.commonservice.ServiceCommonException;
import co.mannit.commonservice.common.LoginMethod;
import co.mannit.commonservice.common.MongokeyvaluePair;
import co.mannit.commonservice.common.ValueExtracterFromJSON;
import co.mannit.commonservice.common.util.DateUtil;
import co.mannit.commonservice.common.util.ValidationUtil;
import co.mannit.commonservice.crypto.AESSymmetricEncryption;
import co.mannit.commonservice.dao.UserDao;
import co.mannit.commonservice.external.MsgSender;
import co.mannit.commonservice.external.SMSSender;
import co.mannit.commonservice.po.MsgDetails;
import co.mannit.commonservice.pojo.User;
import co.mannit.commonservice.validator.PasswordValidator;
import jakarta.annotation.PreDestroy;

@Service
public class PasswordService {

	private static final Logger logger = LogManager.getLogger(LoginService.class);

	@Autowired
	private ValueExtracterFromJSON valueExtracterFromJSON;

	@Autowired
	private UserDao logisignupassDao;

	@Autowired
	private AESSymmetricEncryption aesSymmetricEncryption;

	@Autowired
	private PasswordValidator passwordValidator;

	@Autowired
	private Msgserv sendmail;
	
    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    private ExecutorService emailExecutorService;
	
	public String forgetPassword(String loginDetails) throws Exception {
		return null;
	}

	public String resetpwd(String loginDetails) throws Exception {
		logger.debug("<login> loginDetails:{} method:{}", loginDetails);

		String msg = "";
		String userDetails = null;

		String userName = valueExtracterFromJSON.getValue(loginDetails, "username", String.class);
		String mobileNumber = String.valueOf(valueExtracterFromJSON.getValue(loginDetails, "mobileno", Long.class));
		String password = String.valueOf(valueExtracterFromJSON.getValue(loginDetails, "password", String.class));
		String newPassword = String
				.valueOf(valueExtracterFromJSON.getValue(loginDetails, "new_password", String.class));

		/*
		 * if (!passwordValidator.isValid(newPassword)) { throw new
		 * ServiceCommonException("105"); }
		 */

		List<MongokeyvaluePair<? extends Object>> lstPairs = new ArrayList<>();
		lstPairs.add(new MongokeyvaluePair<String>("password",
				aesSymmetricEncryption.encryptAsString(password, aesSymmetricEncryption.getSecretKey())));

		if (StringUtils.hasText(userName)) {
			lstPairs.add(new MongokeyvaluePair<String>("username", userName));
		} else if (StringUtils.hasText(mobileNumber)) {
			lstPairs.add(new MongokeyvaluePair<Long>("mobileno", Long.valueOf(mobileNumber)));
		} else {
			throw new ServiceCommonException("103", new String[] { "login crendential" });
		}

		userDetails = logisignupassDao.findDocAsString(lstPairs);

		if (userDetails == null) {
			throw new ServiceCommonException("103", new String[] { "login crendential" });
		}
		;

		String loggedinTime = valueExtracterFromJSON.getValue(loginDetails, "loggedintime", String.class);

		List<MongokeyvaluePair<? extends Object>> pairs = new ArrayList<>();
		pairs.add(new MongokeyvaluePair<String>("loggedintime", DateUtil.getCurrentDateTime()));
		pairs.add(new MongokeyvaluePair<String>("lastloggedintime", loggedinTime));
		pairs.add(new MongokeyvaluePair<String>("password",
				aesSymmetricEncryption.encryptAsString(newPassword, aesSymmetricEncryption.getSecretKey())));
		logisignupassDao.saveDocument(userDetails, pairs);
		msg = "Password successfully created";
//		lstPairs.add(new MongokeyvaluePair<Integer>("otp", otp));
//		lstPairs.add(new MongokeyvaluePair<Long>("otptime", System.currentTimeMillis()));
//		lstPairs.add(new MongokeyvaluePair<String>("otpchannel", method.toString()));
//		lstPairs.add(new MongokeyvaluePair<String>("otpstatus", "Success"));

		logger.debug("</login>");
		return msg;
	}

	/*
	 * public String forgetPassword(User user) throws Exception {
	 * 
	 * if(!ValidationUtil.validateDomainAndSubDomain(user.getDomain(),
	 * user.getSubdomain())) { throw new ServiceCommonException("100"); }
	 * 
	 * if(ValidationUtil.isNotValidMobileNo(user.getMobileno())) throw new
	 * ServiceCommonException("103", new String[]{"mobile number"});
	 * 
	 * List<MongokeyvaluePair<? extends Object>> keyValuePairs = new ArrayList<>();
	 * 
	 * keyValuePairs.add(new MongokeyvaluePair<String>("domain", user.getDomain()));
	 * keyValuePairs.add(new MongokeyvaluePair<String>("subdomain",
	 * user.getSubdomain())); if(!(user.getMobileno()==null)) {
	 * keyValuePairs.add(new MongokeyvaluePair<Long>("mobileno",
	 * Long.parseLong(user.getMobileno()))); }else { keyValuePairs.add(new
	 * MongokeyvaluePair<Long>("username", Long.parseLong(user.getUsername()))); }
	 * 
	 * 
	 * 
	 * Document doc = logisignupassDao.findDoc(keyValuePairs);
	 * 
	 * if(doc == null )throw new ServiceCommonException("103", new
	 * String[]{"user details"}); else
	 * generateandsendotp(doc,Long.parseLong(user.getMobileno()));
	 * 
	 * 
	 * if(passwordValidator.isNotValid(user.getPassword())) {throw new
	 * ServiceCommonException("105");}
	 * 
	 * doc.put("password",
	 * aesSymmetricEncryption.encryptAsString(user.getPassword(),
	 * aesSymmetricEncryption.getSecretKey()));
	 * 
	 * logisignupassDao.saveDocument(doc);
	 * 
	 * 
	 * //doc.put("password", "");
	 * 
	 * return doc.toJson(); }
	 */
	public String resetpwdweb(String loginDetails) throws Exception {
		logger.debug("<login> loginDetails:{} method:{}", loginDetails);

		String msg = "";
		String userDetails = null;

		String userName = valueExtracterFromJSON.getValue(loginDetails, "username", String.class);
		String mobileNumber = String.valueOf(valueExtracterFromJSON.getValue(loginDetails, "mobileno", Long.class));
		String password = String.valueOf(valueExtracterFromJSON.getValue(loginDetails, "password", String.class));
		String newPassword = String
				.valueOf(valueExtracterFromJSON.getValue(loginDetails, "new_password", String.class));

		if (!passwordValidator.isValid(newPassword)) {
			throw new ServiceCommonException("105");
		}

		List<MongokeyvaluePair<? extends Object>> lstPairs = new ArrayList<>();
		// lstPairs.add(new MongokeyvaluePair<String>("password",
		// aesSymmetricEncryption.encryptAsString(password,
		// aesSymmetricEncryption.getSecretKey())));

		if (StringUtils.hasText(userName)) {
			lstPairs.add(new MongokeyvaluePair<String>("username", userName));
		} else if (StringUtils.hasText(mobileNumber)) {
			lstPairs.add(new MongokeyvaluePair<Long>("mobileno", Long.valueOf(mobileNumber)));
		} else {
			throw new ServiceCommonException("103", new String[] { "login crendential" });
		}

		userDetails = logisignupassDao.findDocAsString(lstPairs);

		if (userDetails == null) {
			throw new ServiceCommonException("103", new String[] { "username" });
		} else {
			lstPairs.add(new MongokeyvaluePair<String>("password",
					aesSymmetricEncryption.encryptAsString(password, aesSymmetricEncryption.getSecretKey())));
			userDetails = logisignupassDao.findDocAsString(lstPairs);
			if (userDetails == null) {
				throw new ServiceCommonException("103", new String[] { "password" });
			}
		}
		;
		String loggedinTime = valueExtracterFromJSON.getValue(loginDetails, "loggedintime", String.class);

		List<MongokeyvaluePair<? extends Object>> pairs = new ArrayList<>();
		pairs.add(new MongokeyvaluePair<String>("loggedintime", DateUtil.getCurrentDateTime()));
		pairs.add(new MongokeyvaluePair<String>("lastloggedintime", loggedinTime));
		pairs.add(new MongokeyvaluePair<String>("password",
				aesSymmetricEncryption.encryptAsString(newPassword, aesSymmetricEncryption.getSecretKey())));
		logisignupassDao.saveDocumentweb(userDetails, pairs);
		msg = "Password successfully created";
//		lstPairs.add(new MongokeyvaluePair<Integer>("otp", otp));
//		lstPairs.add(new MongokeyvaluePair<Long>("otptime", System.currentTimeMillis()));
//		lstPairs.add(new MongokeyvaluePair<String>("otpchannel", method.toString()));
//		lstPairs.add(new MongokeyvaluePair<String>("otpstatus", "Success"));

		logger.debug("</login>");
		return msg;
	};
	public String resetPassword(Map<String,Object>ob) throws Exception {
       
		String email = ob.get("email").toString();
	    String otp = ob.get("otp").toString();
	    String newPassword = ob.get("new_password").toString();

	    // Step 1: Find user doc by email
	    List<MongokeyvaluePair<? extends Object>> keyValuePairs = new ArrayList<>();
	    keyValuePairs.add(new MongokeyvaluePair<String>("email", email));
	    Document doc = logisignupassDao.findDoc(keyValuePairs);

	    if (doc == null) {
	        throw new ServiceCommonException("103", new String[]{"email"});
	    }

	    // Step 2: Check OTP expiry
	    Date expiry = doc.getDate("otp_created_at");
	  //  System.out.println(expiry);
	    long expiryDurationMillis = 15 * 60 * 1000; // 15 minutes
	    long timePassed = System.currentTimeMillis() - expiry.getTime();

	    if (timePassed > expiryDurationMillis) {
	        throw new ServiceCommonException("400",new String[]{"OTP expired. Please request a new one."});
	    }

	    // Step 3: Check attempts
	    Integer attempts = doc.getInteger("otp_attempts", 0);
	    if (attempts >= 5) {
	        throw new ServiceCommonException("400", new String[]{"Too many incorrect attempts. OTP locked."});
	    }

	    // Step 4: Match OTP
	    String storedHash = doc.getString("otp");
	    if (!encoder.matches(otp, storedHash)) {
	        // incrementing attempt counter
	        doc.put("otp_attempts", attempts + 1);
	        logisignupassDao.saveDocument(doc);
	        throw new ServiceCommonException("400" ,new String[]{"Incorrect OTP. Try again."});
	    }

	    doc.put("password", encoder.encode(newPassword));
	    doc.remove("otp");
	    doc.remove("otp_expiry");
	    doc.remove("otp_attempts");
	    doc.put("updated_at", new Date());
	    logisignupassDao.saveDocument(doc);
	    return "reset successfull";
    }
	public String resetpwdauth(String loginDetails) throws Exception {
		logger.debug("<login> loginDetails:{} method:{}", loginDetails);

		String msg = "";
		String userDetails = null;

		String userName = valueExtracterFromJSON.getValue(loginDetails, "username", String.class);
		String mobileNumber = String.valueOf(valueExtracterFromJSON.getValue(loginDetails, "mobileno", Long.class));
	//	String password = String.valueOf(valueExtracterFromJSON.getValue(loginDetails, "password", String.class));
		String newPassword = String
				.valueOf(valueExtracterFromJSON.getValue(loginDetails, "new_password", String.class));

		/*
		 * if (!passwordValidator.isValid(newPassword)) { throw new
		 * ServiceCommonException("105"); }
		 */

		List<MongokeyvaluePair<? extends Object>> lstPairs = new ArrayList<>();
		
		if (StringUtils.hasText(userName)) {
			lstPairs.add(new MongokeyvaluePair<String>("username", userName));
		} else if (StringUtils.hasText(mobileNumber)) {
			lstPairs.add(new MongokeyvaluePair<Long>("mobileno", Long.valueOf(mobileNumber)));
		} else {
			throw new ServiceCommonException("103", new String[] { "login crendential" });
		}

		userDetails = logisignupassDao.findDocAsString(lstPairs);

		if (userDetails == null) {
			throw new ServiceCommonException("103", new String[] { "login crendential" });
		}
		;

		String loggedinTime = valueExtracterFromJSON.getValue(loginDetails, "loggedintime", String.class);

		List<MongokeyvaluePair<? extends Object>> pairs = new ArrayList<>();
		pairs.add(new MongokeyvaluePair<String>("loggedintime", DateUtil.getCurrentDateTime()));
		pairs.add(new MongokeyvaluePair<String>("lastloggedintime", loggedinTime));
		pairs.add(new MongokeyvaluePair<String>("password",
				encoder.encode(newPassword)));
		logisignupassDao.saveDocument(userDetails, pairs);
		msg = "Password successfully created";
//		lstPairs.add(new MongokeyvaluePair<Integer>("otp", otp));
//		lstPairs.add(new MongokeyvaluePair<Long>("otptime", System.currentTimeMillis()));
//		lstPairs.add(new MongokeyvaluePair<String>("otpchannel", method.toString()));
//		lstPairs.add(new MongokeyvaluePair<String>("otpstatus", "Success"));

		logger.debug("</login>");
		return msg;
	}
	public String forgetPassword(User user) throws Exception {
		List<MongokeyvaluePair<? extends Object>> keyValuePairs = new ArrayList<>();

        if(user.getDomain()!=null&&user.getSubdomain()!=null) {
        	if (!ValidationUtil.validateDomainAndSubDomain(user.getDomain(), user.getSubdomain())) {
    			keyValuePairs.add(new MongokeyvaluePair<String>("domain", user.getDomain()));
    			keyValuePairs.add(new MongokeyvaluePair<String>("subdomain", user.getSubdomain()));
    		}

        }
		
		// if(ValidationUtil.isNotValidMobileNo(user.getMobileno())) throw new
		// ServiceCommonException("103", new String[]{"mobile number"});

		
		
		// keyValuePairs.add(new MongokeyvaluePair<Long>("mobile",
		// Long.parseLong(user.getMobileno())));
        if(user.getMobileno()!=null) {
        	keyValuePairs.add(new MongokeyvaluePair<Long>("mobileno",Long.parseLong(user.getMobileno())));
        }else if(user.getEmail()!=null) {
			keyValuePairs.add(new MongokeyvaluePair<String>("email",user.getEmail()));
		}
		
        
		
		Document doc = logisignupassDao.findDoc(keyValuePairs);
		String val = null;
		if (doc == null)
			throw new ServiceCommonException("103", new String[] { "user details" });
		else
			
			if(user.getMobileno()!=null) {
				val = generateandsendotp(doc,Long.parseLong(user.getMobileno()), user.getDomain());	
			}else {
				val = generateandsendotpViaEmail(doc,user.getEmail(), user.getDomain());	
			}
			

		return val;
	}


	private String generateandsendotp(Document doc,Long mobile, String domain) {
		try {
			int min = 100000;
			int max = 999999;
			int randomNumber = (int) (Math.random() * (max - min + 1)) + min;
			doc.put("OTP", randomNumber);
			logisignupassDao.saveDocument(doc);
			MsgDetails de;
			if(domain!=null) {
				 de = MsgDetails.builder().toAddress(String.valueOf(mobile)).fromAddress("")
						.msg("*MANNIT*    " + "<" + domain + ">" + " " + "OTP" + " " + " : " + randomNumber).build();
			}else {
				de = MsgDetails.builder().toAddress(String.valueOf(mobile)).fromAddress("")
						.msg("*MANNIT*    " + "<" + "VERIFY" + ">" + " " + "OTP" + " " + " : " + randomNumber).build();
			}
			
			MsgSender msgSender = MsgSender.createMsgSender(LoginMethod.MOBILENO);
			//System.out.println(de.getMsg());
			msgSender.sendMessage(de);
			return "OTP Sent Successfully";

		} catch (Exception e) {
			return e.getMessage();
		}

	}
	
	private String generateandsendotpViaEmail(Document doc,String email, String domain) {
		try {
			int min = 100000;
			int max = 999999;
			int randomNumber = (int) (Math.random() * (max - min + 1)) + min;
			doc.put("OTP", randomNumber);
			logisignupassDao.saveDocument(doc);
			
			
			StringBuilder builder =new StringBuilder();
			if(domain!=null) {
				 builder.append("*MANNIT*    " + "<" + domain + ">" + " " + "OTP" + " " + " : " + randomNumber);
			}else {
			 builder.append
						("*MANNIT*    " + "<" + "VERIFY" + ">" + " " + "OTP" + " " + " : " + randomNumber);
			}
			
			
			//System.out.println(de.getMsg());
			sendmail.sendMessage(builder.toString(),email);
			return "OTP Sent Successfully";

		} catch (Exception e) {
			return e.getMessage();
		}

	}

	public String getotpandverify(User user, int otp) throws Exception {
		/*
		 * if (!ValidationUtil.validateDomainAndSubDomain(user.getDomain(),
		 * user.getSubdomain())) { throw new ServiceCommonException("100"); }
		 */

		// if(ValidationUtil.isNotValidMobileNo(user.getMobileno())) throw new
		// ServiceCommonException("103", new String[]{"mobile number"});

		List<MongokeyvaluePair<? extends Object>> keyValuePairs = new ArrayList<>();
	    if(user.getDomain()!=null&&user.getSubdomain()!=null) {
		keyValuePairs.add(new MongokeyvaluePair<String>("domain", user.getDomain()));
		keyValuePairs.add(new MongokeyvaluePair<String>("subdomain", user.getSubdomain()));
	}
			
			if (!(user.getMobileno() == null)) {
				keyValuePairs.add(new MongokeyvaluePair<Long>("mobileno", Long.parseLong(user.getMobileno())));
			} else if(!(user.getUsername()==null)) {
				keyValuePairs.add(new MongokeyvaluePair<String>("username", user.getUsername()));
			}else if(!(user.getEmail()==null)) {
				keyValuePairs.add(new MongokeyvaluePair<String>("email", user.getEmail()));
			}

			Document doc = logisignupassDao.findDoc(keyValuePairs);
			MsgDetails de;
			if (doc == null)
				throw new ServiceCommonException("103", new String[] { "user details" });
			String pass = aesSymmetricEncryption.decrypt(doc.get("password").toString(),
					aesSymmetricEncryption.getSecretKey());
			
			
			if (doc.get("OTP").equals(otp)) {
				if(user.getDomain()!=null) {
					de = MsgDetails.builder().toAddress(user.getMobileno()).fromAddress("")
							.msg("*MANNIT*     " + "<" + user.getDomain() + ">" + " " + "PASS" + " " + " : " + pass).build();
				}else {
					de = MsgDetails.builder().toAddress(user.getMobileno()).fromAddress("")
							.msg("*MANNIT*     " + " " + "PASS" + " " + " : " + pass).build();
				}
				if(user.getMobileno()!=null) {
					MsgSender msgSender = MsgSender.createMsgSender(LoginMethod.MOBILENO);
					msgSender.sendMessage(de);
				}else {
					
					StringBuilder builder = new StringBuilder();
					builder.append("*MANNIT*     " + " " + "PASS" + " " + " : " + pass);
					sendmail.sendMessage(builder.toString(),user.getEmail());
				}
				
				return "Verified ,password sent to the whatsapp number";
			} else {
				throw new ServiceCommonException("103", new String[] { "OTP" });
			}

		}
	
 public String forgetPasswordauth(Map<String,Object>payload) throws Exception {
		List<MongokeyvaluePair<? extends Object>> keyValuePairs = new ArrayList<>();
		keyValuePairs.add(new MongokeyvaluePair<String>("email", payload.get("email").toString()));
		Document doc = logisignupassDao.findDoc(keyValuePairs);
		if(doc ==null) {
			throw new ServiceCommonException("103", new String[] { "email" });
		}
		
		Instant now =Instant.now();
		int MAX_REQUEST_PER_HOUR =100;
		
		Date window_startdate =doc.getDate("otp_req_window_start");
		Integer currentcount =doc.getInteger("otp_req_count",0);
		
		if(window_startdate!=null) {
			
		Instant windowstart=window_startdate.toInstant();
		
			if(Duration.between(now, windowstart).toHours()>=1) {
				
				currentcount=0;
				doc.put("otp_req_window_start", Date.from(now));
			}
		}else {
			doc.put("otp_req_window_start", Date.from(now));
		}
		if(currentcount>=MAX_REQUEST_PER_HOUR) {
			throw new ServiceCommonException("429", new String[]{"Too many OTP requests. Please try again later."});
		}
		
		doc.put("otp_req_count", currentcount+1);
		StringBuilder builder = new StringBuilder();
		int min = 100000;
		int max = 999999;
		int otp = (int) (Math.random() * (max - min + 1)) + min;
		
		
		builder.append(
				 "Hello "+ doc.getString("username")+",\r\n"
				+ "\r\n"
				+ "We received a request to reset the password for your account associated with this email.\r\n"
				+ "\r\n"
				+ "To reset your password, please use this One Time Password:\r\n"
				+ "\r\n"
				+ otp
				+ "\r\n"
				+ "This OTP will expire in 15 minutes for security purposes.\r\n"
				+ "\r\n"
				+ "If you did not request this change, please ignore this email. Your password will remain unchanged.\r\n"
				+ "\r\n"
				+ "Thank you,  \r\n"
				+ "Mannit Support Team\r\n"
				+ "");
		
        if(payload.containsKey("mobileno")) {
        	
        }else if(payload.containsKey("email")){
        	emailExecutorService.submit(()->{
        		sendmail.sendMessage(builder.toString(),payload.get("email").toString());
        	});
        	//sendmail.sendMessage(builder.toString(),payload.get("email").toString());
		}
		
        doc.append("otp", encoder.encode(String.valueOf(otp)));
        doc.put("otp_created_at", Date.from(now));
        doc.put("otp_attempts", 0);
        logisignupassDao.saveDocument(doc);
		return "OTP Sent To Email" ;
	}
	@PreDestroy
	public void onShutdown() {
		emailExecutorService.shutdown();
	}
}
