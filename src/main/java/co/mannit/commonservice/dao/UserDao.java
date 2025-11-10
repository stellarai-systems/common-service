package co.mannit.commonservice.dao;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import co.mannit.commonservice.CustomRequestInterceptor;
import co.mannit.commonservice.common.CollectionName;
import co.mannit.commonservice.common.MongokeyvaluePair;
import co.mannit.commonservice.pojo.User;


@Repository
public class UserDao {
	
	@Autowired
	private CommonDao commonDao;
	
	public boolean isUserAlreadyExist(String key, Object value) {
		return commonDao.isUserAlreadyExist(getCollectionName(), key, value);
	}
	
	public boolean isUserAlreadyExist(List<MongokeyvaluePair<? extends Object>> keyValuPairs) {
		return commonDao.isUserAlreadyExist(getCollectionName(), keyValuPairs);
	}
	
	public void insertDocument(String json) {
		commonDao.insertDocument(getCollectionName(), json);
	}
	
	public void insertDocument(String json, List<MongokeyvaluePair<? extends Object>> keyValuPairs) {
		commonDao.insertDocument(getCollectionName(), json, keyValuPairs);
	}
	
	public void saveDocument(String json, List<MongokeyvaluePair<? extends Object>> keyValuPairs) {
		commonDao.saveDocument(getCollectionName(), json, keyValuPairs);
	}
	
	public void saveDocument(Document document) {
		commonDao.saveDocument(getCollectionName(), document);
	}
	
	public void printAllDoc() {
		commonDao.printAllDoc(getCollectionName());
	}
	
	public String findDocAsString(List<MongokeyvaluePair<? extends Object>> pairs) throws Exception {
		return commonDao.findDocAsString(getCollectionName(), pairs);
	}
	
	public Document findDoc(List<MongokeyvaluePair<? extends Object>> pairs) throws Exception {
		return commonDao.findDoc(getCollectionName(), pairs);
	}
	
	public String findDocAsString(MongokeyvaluePair<? extends Object> pair) throws Exception {
		return commonDao.findDocAsString(getCollectionName(), pair);
	}
	
	public void saveDocumentweb(String json, List<MongokeyvaluePair<? extends Object>> keyValuPairs) {
		commonDao.saveDocument(getwebCollectionName(), json, keyValuPairs);
	}
	public long deleteDoc(List<MongokeyvaluePair<? extends Object>> pairs) throws Exception {
		return commonDao.deleteDoc(getCollectionName(), pairs);
	}
	public User loadbyUsrname(String usernamr) {
		 return commonDao.finduserDetails(getCollectionName(), usernamr);
	 }
	
	private String getCollectionName() {
		
		//System.out.println("uniqueid"+CustomRequestInterceptor.getUniqueId());
		if(CustomRequestInterceptor.getUniqueId()!=null) {
			return String.format(CollectionName.COLLECTION_USER, CustomRequestInterceptor.getUniqueId());
		}else {
			return String.format(CollectionName.COLLECTION_USER, "web");
		}
		
	}
	private String getwebCollectionName() {
	
			return String.format(CollectionName.COLLECTION_USER, "web");
		}
		
	
	
}
