package co.mannit.commonservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.bson.types.ObjectId;

import co.mannit.commonservice.ServiceCommonException;
import co.mannit.commonservice.common.MongokeyvaluePair;
import co.mannit.commonservice.common.ValueExtracterFromJSON;
import co.mannit.commonservice.common.util.DateUtil;
import co.mannit.commonservice.crypto.AESSymmetricEncryption;
import co.mannit.commonservice.dao.UserDao;
import co.mannit.commonservice.validator.PasswordValidator;

@Service
public class SignupService {

	private static final Logger logger = LogManager.getLogger(SignupService.class);
			
	@Autowired
	private ValueExtracterFromJSON valueExtracterFromJSON;
	
	@Autowired
	private UserDao logisignupassDao;
	
	@Autowired
	private AESSymmetricEncryption aesSymmetricEncryption;
	
	@Autowired
	private PasswordValidator passwordValidator;
	
	@Autowired
	private PasswordEncoder encoder;
	
	Pattern ptrn = Pattern.compile("^\\d{10}$");
	
	public String signupUser(String registrationDetails, String mobileNoValidation) throws Exception{
		logger.debug("<signupUser> registrationDetails:{}",registrationDetails);
		
		String userName = valueExtracterFromJSON.getValue(registrationDetails, "username", String.class);
		Long mobileno = valueExtracterFromJSON.getValue(registrationDetails, "mobileno", Long.class);
		String password = valueExtracterFromJSON.getValue(registrationDetails, "password", String.class);
		
		String domain = valueExtracterFromJSON.getValue(registrationDetails, "domain", String.class);
		String subDomain = valueExtracterFromJSON.getValue(registrationDetails, "subdomain", String.class);
		String email = valueExtracterFromJSON.getValue(registrationDetails, "email", String.class);
		
		//how to validate the domain name
		if(!StringUtils.hasText(domain) || !StringUtils.hasText(subDomain)) {
			throw new ServiceCommonException("100");
		}
		
        if(!StringUtils.hasText(userName) && (mobileno == null || !StringUtils.hasText(String.valueOf(mobileno)))) {
        	throw new ServiceCommonException("102");
        }
		
        if (!StringUtils.hasText(password)) {
            throw new ServiceCommonException("102");
        }
        
		if(mobileno != null && StringUtils.hasText(String.valueOf(mobileno)) && !ptrn.matcher(String.valueOf(mobileno)).matches()) {
			throw new ServiceCommonException("103", new String[]{"mobile number"});
		}
	
		
		if(!passwordValidator.isValid(password)) {throw new ServiceCommonException("105");}
		
		if(userName != null && StringUtils.hasText(String.valueOf(userName)) && logisignupassDao.isUserAlreadyExist("username", userName)) {
			throw new ServiceCommonException("104", new String[] {"User name"});
		}
		if(email !=null&&StringUtils.hasText(String.valueOf(email))&&logisignupassDao.isUserAlreadyExist("email",email)) {
			throw new ServiceCommonException("104", new String[] {"Email"});
		}
		isMobileNumberExist(mobileno, mobileNoValidation, domain, subDomain);
		
		logger.debug("aesSymmetricEncryption.getSecretKey():{}",aesSymmetricEncryption.getSecretKey());
		List<MongokeyvaluePair<? extends Object>> keyValuePairs = new ArrayList<>();
		String cryptedPassword = aesSymmetricEncryption.encryptAsString(password, aesSymmetricEncryption.getSecretKey());
		keyValuePairs.add(new MongokeyvaluePair<String>("password", cryptedPassword ));
		keyValuePairs.add(new MongokeyvaluePair<String>("creationdate", DateUtil.getCurrentDateTime()));
		keyValuePairs.add(new MongokeyvaluePair<String>("updated_at", DateUtil.getCurrentDateTime()));
		
		logisignupassDao.insertDocument(registrationDetails, keyValuePairs);
		
		List<MongokeyvaluePair<? extends Object>> loginPairs = new ArrayList<>();
		loginPairs.add(new MongokeyvaluePair<String>("password", cryptedPassword));
		loginPairs.add(new MongokeyvaluePair<String>("domain", domain ));
		loginPairs.add(new MongokeyvaluePair<String>("subdomain", subDomain));
		if(StringUtils.hasText(userName)) {
			loginPairs.add(new MongokeyvaluePair<String>("username", userName));
		}else if(StringUtils.hasText(String.valueOf(mobileno))) {
			loginPairs.add(new MongokeyvaluePair<Long>("mobileno", Long.valueOf(mobileno)));
		}
		
		String userDetails = logisignupassDao.findDocAsString(loginPairs);
		
		logger.debug("</signupUser>");
		return userDetails;
	}
	public String AuthsignupUser(String registrationDetails, String mobileNoValidation) throws Exception{
		logger.debug("<signupUser> registrationDetails:{}",registrationDetails);
		
		String userName = valueExtracterFromJSON.getValue(registrationDetails, "username", String.class);
		Long mobileno = valueExtracterFromJSON.getValue(registrationDetails, "mobileno", Long.class);
		String password = valueExtracterFromJSON.getValue(registrationDetails, "password", String.class);
		
		String domain = valueExtracterFromJSON.getValue(registrationDetails, "domain", String.class);
		String subDomain = valueExtracterFromJSON.getValue(registrationDetails, "subdomain", String.class);
		String email = valueExtracterFromJSON.getValue(registrationDetails, "email", String.class);
		
		//how to validate the domain name
		if(!StringUtils.hasText(domain) || !StringUtils.hasText(subDomain)) {
			throw new ServiceCommonException("100");
		}
		
        if(!StringUtils.hasText(userName) && (mobileno == null || !StringUtils.hasText(String.valueOf(mobileno)))) {
        	throw new ServiceCommonException("102");
        }
		
        if (!StringUtils.hasText(password)) {
            throw new ServiceCommonException("102");
        }
        
		if(mobileno != null && StringUtils.hasText(String.valueOf(mobileno)) && !ptrn.matcher(String.valueOf(mobileno)).matches()) {
			throw new ServiceCommonException("103", new String[]{"mobile number"});
		}
	
		
		if(!passwordValidator.isValid(password)) {throw new ServiceCommonException("105");}
		
		if(userName != null && StringUtils.hasText(String.valueOf(userName)) && logisignupassDao.isUserAlreadyExist("username", userName)) {
			throw new ServiceCommonException("104", new String[] {"User name"});
		}
		if(email !=null&&StringUtils.hasText(String.valueOf(email))&&logisignupassDao.isUserAlreadyExist("email",email)) {
			throw new ServiceCommonException("104", new String[] {"Email"});
		}
		isMobileNumberExist(mobileno, mobileNoValidation, domain, subDomain);
		
		logger.debug("aesSymmetricEncryption.getSecretKey():{}",aesSymmetricEncryption.getSecretKey());
		List<MongokeyvaluePair<? extends Object>> keyValuePairs = new ArrayList<>();
		String cryptedPassword = encoder.encode(password);
		keyValuePairs.add(new MongokeyvaluePair<String>("password", cryptedPassword ));
		keyValuePairs.add(new MongokeyvaluePair<String>("creationdate", DateUtil.getCurrentDateTime()));
		keyValuePairs.add(new MongokeyvaluePair<String>("updated_at", DateUtil.getCurrentDateTime()));
		
		logisignupassDao.insertDocument(registrationDetails, keyValuePairs);
		
		List<MongokeyvaluePair<? extends Object>> loginPairs = new ArrayList<>();
		loginPairs.add(new MongokeyvaluePair<String>("password", cryptedPassword));
		loginPairs.add(new MongokeyvaluePair<String>("domain", domain ));
		loginPairs.add(new MongokeyvaluePair<String>("subdomain", subDomain));
		if(StringUtils.hasText(userName)) {
			loginPairs.add(new MongokeyvaluePair<String>("username", userName));
		}else if(StringUtils.hasText(String.valueOf(mobileno))) {
			loginPairs.add(new MongokeyvaluePair<Long>("mobileno", Long.valueOf(mobileno)));
		}
		
		String userDetails = logisignupassDao.findDocAsString(loginPairs);
		
		logger.debug("</signupUser>");
		return userDetails;
	}
	
	
	public String signupUsernew(String registrationDetails, String mobileNoValidation) throws Exception{
		logger.debug("<signupUser> registrationDetails:{}",registrationDetails);
		
		String userName = valueExtracterFromJSON.getValue(registrationDetails, "username", String.class);
		Long mobileno = valueExtracterFromJSON.getValue(registrationDetails, "mobileno", Long.class);
		String password = valueExtracterFromJSON.getValue(registrationDetails, "password", String.class);
		
		String domain = valueExtracterFromJSON.getValue(registrationDetails, "domain", String.class);
		String subDomain = valueExtracterFromJSON.getValue(registrationDetails, "subdomain", String.class);
		String email = valueExtracterFromJSON.getValue(registrationDetails, "email", String.class);
		
		//how to validate the domain name
		if(!StringUtils.hasText(domain) || !StringUtils.hasText(subDomain)) {
			throw new ServiceCommonException("100");
		}
		
        if(!StringUtils.hasText(userName) && (mobileno == null || !StringUtils.hasText(String.valueOf(mobileno)))) {
        	throw new ServiceCommonException("102");
        }
		
        if (!StringUtils.hasText(password)) {
            throw new ServiceCommonException("102");
        }
        
		if(mobileno != null && StringUtils.hasText(String.valueOf(mobileno)) && !ptrn.matcher(String.valueOf(mobileno)).matches()) {
			throw new ServiceCommonException("103", new String[]{"mobile number"});
		}
	
		
		/*
		 * if(!passwordValidator.isValid(password)) {throw new
		 * ServiceCommonException("105");}
		 */
		
		if(userName != null && StringUtils.hasText(String.valueOf(userName)) && logisignupassDao.isUserAlreadyExist("username", userName)) {
			throw new ServiceCommonException("104", new String[] {"User name"});
		}
		if(email !=null&&StringUtils.hasText(String.valueOf(email))&&logisignupassDao.isUserAlreadyExist("email",email)) {
			throw new ServiceCommonException("104", new String[] {"Email"});
		}
		isMobileNumberExist(mobileno, mobileNoValidation, domain, subDomain);
		
		logger.debug("aesSymmetricEncryption.getSecretKey():{}",aesSymmetricEncryption.getSecretKey());
		List<MongokeyvaluePair<? extends Object>> keyValuePairs = new ArrayList<>();
		String cryptedPassword = aesSymmetricEncryption.encryptAsString(password, aesSymmetricEncryption.getSecretKey());
		keyValuePairs.add(new MongokeyvaluePair<String>("password", cryptedPassword ));
		keyValuePairs.add(new MongokeyvaluePair<String>("creationdate", DateUtil.getCurrentDateTime()));
		keyValuePairs.add(new MongokeyvaluePair<String>("updated_at", DateUtil.getCurrentDateTime()));
		
		logisignupassDao.insertDocument(registrationDetails, keyValuePairs);
		
		List<MongokeyvaluePair<? extends Object>> loginPairs = new ArrayList<>();
		loginPairs.add(new MongokeyvaluePair<String>("password", cryptedPassword));
		loginPairs.add(new MongokeyvaluePair<String>("domain", domain ));
		loginPairs.add(new MongokeyvaluePair<String>("subdomain", subDomain));
		if(StringUtils.hasText(userName)) {
			loginPairs.add(new MongokeyvaluePair<String>("username", userName));
		}else if(StringUtils.hasText(String.valueOf(mobileno))) {
			loginPairs.add(new MongokeyvaluePair<Long>("mobileno", Long.valueOf(mobileno)));
		}
		
		String userDetails = logisignupassDao.findDocAsString(loginPairs);
		
		logger.debug("</signupUser>");
		return userDetails;
	}
	
	private void isMobileNumberExist(Long mobileno, String mobileNoValidation, String domain, String subDomain) {
		
		if(mobileno != null && StringUtils.hasText(String.valueOf(mobileno)) && logisignupassDao.isUserAlreadyExist("mobileno", mobileno)) {
			
			if("SINGLEUSER".equals(mobileNoValidation)) {
				throw new ServiceCommonException("104", new String[] {"User mobile number"});
			}else if("MULTIPLEUSER".equals(mobileNoValidation)) {
				List<MongokeyvaluePair<? extends Object>> keyValuePairs = new ArrayList<>();
				keyValuePairs.add(new MongokeyvaluePair<String>("domain", domain ));
				keyValuePairs.add(new MongokeyvaluePair<String>("subdomain", subDomain));
				keyValuePairs.add(new MongokeyvaluePair<Long>("mobileno", mobileno));
				
				if(logisignupassDao.isUserAlreadyExist(keyValuePairs)) {
					throw new ServiceCommonException("104", new String[] {"User mobile number"});
				}
				
			}else {
				throw new ServiceCommonException("Invalid Mobile Number vs Domain and sub domain");
			}
			
		}
	}
	

	
	
	public String signupUserQuiz(String registrationDetails, String mobileNoValidation) throws Exception{
		logger.debug("<signupUser> registrationDetails:{}",registrationDetails);
		
		String userName = valueExtracterFromJSON.getValue(registrationDetails, "username", String.class);
		Long mobileno = valueExtracterFromJSON.getValue(registrationDetails, "mobileno", Long.class);
		String password = valueExtracterFromJSON.getValue(registrationDetails, "password", String.class);
		
		String domain = valueExtracterFromJSON.getValue(registrationDetails, "domain", String.class);
		String subDomain = valueExtracterFromJSON.getValue(registrationDetails, "subdomain", String.class);
//		String email = valueExtracterFromJSON.getValue(registrationDetails, "email", String.class);
		
//		logisignupassDao.printAllDoc();
		
		//how to validate the domain name
		if(!StringUtils.hasText(domain) || !StringUtils.hasText(subDomain)) {
			throw new ServiceCommonException("100");
		}
		
        if(!StringUtils.hasText(userName) && (mobileno == null || !StringUtils.hasText(String.valueOf(mobileno)))) {
        	throw new ServiceCommonException("102");
        }
		
        if (!StringUtils.hasText(password)) {
            throw new ServiceCommonException("102");
        }
        
//		if(!StringUtils.hasText(userName) || !StringUtils.hasText(password)) {
//			throw new ServiceCommonException("102");
//		}
		
		if(mobileno != null && StringUtils.hasText(String.valueOf(mobileno)) && !ptrn.matcher(String.valueOf(mobileno)).matches()) {
			throw new ServiceCommonException("103", new String[]{"mobile number"});
		}
		
	//	if(!passwordValidator.isValid(password)) {throw new ServiceCommonException("105");}
		
		if(userName != null && StringUtils.hasText(String.valueOf(userName)) && logisignupassDao.isUserAlreadyExist("username", userName)) {
			throw new ServiceCommonException("104", new String[] {"User name"});
		}
		
		isMobileNumberExist(mobileno, mobileNoValidation, domain, subDomain);
		
		logger.debug("aesSymmetricEncryption.getSecretKey():{}",aesSymmetricEncryption.getSecretKey());
		List<MongokeyvaluePair<? extends Object>> keyValuePairs = new ArrayList<>();
		String cryptedPassword = aesSymmetricEncryption.encryptAsString(password, aesSymmetricEncryption.getSecretKey());
		keyValuePairs.add(new MongokeyvaluePair<String>("password", cryptedPassword ));
		keyValuePairs.add(new MongokeyvaluePair<String>("creationdate", DateUtil.getCurrentDateTime()));
		keyValuePairs.add(new MongokeyvaluePair<String>("updated_at", DateUtil.getCurrentDateTime()));
		
		logisignupassDao.insertDocument(registrationDetails, keyValuePairs);
		
		List<MongokeyvaluePair<? extends Object>> loginPairs = new ArrayList<>();
		loginPairs.add(new MongokeyvaluePair<String>("password", cryptedPassword));
		loginPairs.add(new MongokeyvaluePair<String>("domain", domain ));
		loginPairs.add(new MongokeyvaluePair<String>("subdomain", subDomain));
		if(StringUtils.hasText(userName)) {
			loginPairs.add(new MongokeyvaluePair<String>("username", userName));
		}else if(StringUtils.hasText(String.valueOf(mobileno))) {
			loginPairs.add(new MongokeyvaluePair<Long>("mobileno", Long.valueOf(mobileno)));
		}
		
		String userDetails = logisignupassDao.findDocAsString(loginPairs);
		
		logger.debug("</signupUser>");
		return userDetails;
	}
	
	public long deleteResource(String resourceId) throws Exception {
		logger.debug("<deleteResource>");
		
		
		
		List<MongokeyvaluePair<? extends Object>> lstKeyValuePairs = new ArrayList<>();

		lstKeyValuePairs.add(new MongokeyvaluePair<ObjectId>("_id", new ObjectId(resourceId)));
		
		
		long deletedCount=logisignupassDao.deleteDoc(lstKeyValuePairs);
		logger.debug("</deleteResource>");
		return deletedCount;
	}
	
}
