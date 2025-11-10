package co.mannit.commonservice.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import co.mannit.commonservice.CustomRequestInterceptor;
import co.mannit.commonservice.common.CollectionName;
import co.mannit.commonservice.common.MongokeyvaluePair;
import co.mannit.commonservice.pojo.PaginationReqParam;

@Repository
public class ImgDao {

	@Autowired
	private CommonDao commonDao;
	
	public String insertDocument(String json, List<MongokeyvaluePair<? extends Object>> keyValuPairs) {
		return commonDao.insertDocument(getCollectionName(), json, keyValuPairs);
	}
	public String uploadDocumen(String phoneNumber,byte []imageData) throws FileNotFoundException, IOException {
		String folderName=getCollectionName();
		return commonDao.uploadtofolder(folderName, imageData, CustomRequestInterceptor.getUniqueId(), phoneNumber);
	}
	public List<Document> search(List<MongokeyvaluePair<? extends Object>> criteria, Map<String, String> filters, PaginationReqParam paginationReq,List<String>fields)
			throws Exception {
		return commonDao.search(getCollectionName(), criteria, filters, paginationReq,fields);
	}
	public String replaceDoc(String json, List<MongokeyvaluePair<? extends Object>> fields, List<MongokeyvaluePair<? extends Object>> criteria,byte[]file) throws Exception {
		return commonDao.replaceDocImage(getCollectionName(), json, fields, criteria,file).toJson();
	}
	private String getCollectionName() {
		return String.format(CollectionName.COLLECTION_IMG, CustomRequestInterceptor.getUniqueId());
	}
    	
}
