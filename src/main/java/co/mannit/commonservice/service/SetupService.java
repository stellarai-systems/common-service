package co.mannit.commonservice.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import co.mannit.commonservice.ServiceCommonException;
import co.mannit.commonservice.common.Constant;
import co.mannit.commonservice.common.MongokeyvaluePair;
import co.mannit.commonservice.common.util.UniqueIdGenerator;
import co.mannit.commonservice.dao.SetupDao;
import co.mannit.commonservice.pojo.Setup;

@Service
public class SetupService {

	private static final Logger logger = LogManager.getLogger(SetupService.class);
	
	@Autowired
	private SetupDao signupDao;
	
	public String setup(Setup setup) throws Exception {
		logger.debug("<Setup>");
		String domain = setup.getDomain();
		String subDomain = setup.getSubdomain();
		
		if(!StringUtils.hasText(domain) || !StringUtils.hasText(subDomain)) {
			throw new ServiceCommonException("100");
		}
		
		List<MongokeyvaluePair<? extends Object>> keyValuePairs = new ArrayList<>();
		keyValuePairs.add(new MongokeyvaluePair<String>(Constant.DOMAIN, domain));
		keyValuePairs.add(new MongokeyvaluePair<String>(Constant.SUBDOMAIN, subDomain));
		
		if(signupDao.isRecordExist(keyValuePairs)) {
			throw new ServiceCommonException("101");
		}
		
		String uniqueId = UniqueIdGenerator.geberateId(domain, subDomain);
		logger.debug("<uniqueId>:{}",uniqueId);
		keyValuePairs.add(new MongokeyvaluePair<String>(Constant.UNIQUEID, uniqueId));
		createfolder(uniqueId);
		signupDao.insertDocument(keyValuePairs);
		signupDao.createCollection(uniqueId);
		
		Document doc =signupDao.findDocAsString(keyValuePairs);
		
		logger.debug("</Setup>doc{}",doc);
		return doc != null ? doc.toJson() : null;
	}
	
	
	public String createfolder(String uniquid) throws Exception{
		
		  String parentDirectory = "D:\\opt\\image"; 
		  String directoryName = uniquid + "_" + "img"; 
		 File newDirectory = new File(parentDirectory+ File.separator + directoryName);
		  
		 if (!newDirectory.exists()) {
		  boolean isCreated = newDirectory.mkdirs(); 
		  if (isCreated) {
		  System.out.println("Directory created successfully: " +
		  newDirectory.getPath()); } else {
		  System.out.println("Failed to create directory."); } 
		  }else

	{
		System.out.println("Directory already exists: " + newDirectory.getPath());
	}
		 
		return null;
	}
}
