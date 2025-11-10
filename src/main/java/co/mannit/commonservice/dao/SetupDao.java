package co.mannit.commonservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import co.mannit.commonservice.common.CollectionName;
import co.mannit.commonservice.common.Constant;
import co.mannit.commonservice.common.MongokeyvaluePair;

@Repository
public class SetupDao {

	@Autowired
	private CommonDao commanDao;
	
	public boolean isRecordExist(List<MongokeyvaluePair<? extends Object>> keyValuePairs) {
		return commanDao.isRecordExist(CollectionName.DOMAINDETAILS, keyValuePairs);
	}
	
	public boolean signup(String domain, String subDomain) {
		
		List<MongokeyvaluePair<? extends Object>> keyValuePairs = new ArrayList<>();
		keyValuePairs.add(new MongokeyvaluePair<String>(Constant.DOMAIN, domain));
		keyValuePairs.add(new MongokeyvaluePair<String>(Constant.SUBDOMAIN, subDomain));
		
		return commanDao.isRecordExist(CollectionName.DOMAINDETAILS, keyValuePairs);
	}
	
	public String insertDocument(List<MongokeyvaluePair<? extends Object>> keyValuePairs) {
		return commanDao.insertDocument(CollectionName.DOMAINDETAILS, "{}", keyValuePairs);
	}
	
	public void createCollection(String uniqueId) {
		commanDao.createCollection(String.format(CollectionName.COLLECTION_USER, uniqueId));
		commanDao.createCollection(String.format(CollectionName.COLLECTION_RESOURCE, uniqueId));
		commanDao.createCollection(String.format(CollectionName.COLLECTION_IMG,uniqueId));
	}
	
	public Document findDocAsString(List<MongokeyvaluePair<? extends Object>> keyValuePairs) throws Exception {
		return commanDao.findDoc(CollectionName.DOMAINDETAILS, keyValuePairs);
	}
}
