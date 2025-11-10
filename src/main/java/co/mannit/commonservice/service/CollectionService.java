package co.mannit.commonservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.mannit.commonservice.CustomRequestInterceptor;
import co.mannit.commonservice.ServiceCommonException;
import co.mannit.commonservice.common.CollectionName;
import co.mannit.commonservice.common.MongokeyvaluePair;
import co.mannit.commonservice.common.util.TextUtil;
import co.mannit.commonservice.common.util.ValidationUtil;
import co.mannit.commonservice.dao.CommonDao;
import co.mannit.commonservice.pojo.PaginationReqParam;

@Service
public class CollectionService {

	private static final Logger logger = LogManager.getLogger(CreateResource.class);
	
	@Autowired
	private CommonDao commonDao;
	
	public Document retrieveCollection(String domain, String subDomain, String collName) throws Exception {
		logger.debug("<retrieveCollection>");
		
		if (/* TextUtil.isEmpty(domain) || TextUtil.isEmpty(subDomain) || */TextUtil.isEmpty(collName))throw new ServiceCommonException("111");
		
		List<MongokeyvaluePair<? extends Object>>  keyValuePairs = new ArrayList<>();
		/*
		 * keyValuePairs.add(new MongokeyvaluePair<String>("domain", domain));
		 * keyValuePairs.add(new MongokeyvaluePair<String>("subdomain", subDomain));
		 */
		keyValuePairs.add(new MongokeyvaluePair<String>("collname", collName));
		
		List<Document> lstDoc= commonDao.findDoc(getCollectionName(), keyValuePairs, null);
		
		//System.out.println(lstDoc);
		
		logger.debug("</retrieveCollection>");
		return lstDoc == null || lstDoc.size() == 0? null : lstDoc.get(0);
	}
	
	public List<String> retrieveCollection( PaginationReqParam paginationReq, Map<String, String> param,List<String>fields) throws Exception {
		logger.debug("<retrieveCollection>");
		
		List<MongokeyvaluePair<? extends Object>> lstKeyValuePairs = new ArrayList<>();
		
		/*
		 * if(TextUtil.isNotEmpty(paginationReq.getDomain())) { lstKeyValuePairs.add(new
		 * MongokeyvaluePair<String>("domain", paginationReq.getDomain())); }
		 * if(TextUtil.isNotEmpty(paginationReq.getSubdomain())) {
		 * lstKeyValuePairs.add(new MongokeyvaluePair<String>("subdomain",
		 * paginationReq.getSubdomain())); }
		 */
		if(TextUtil.isNotEmpty(paginationReq.getUserId())) {
			
			
			lstKeyValuePairs.add(new MongokeyvaluePair<ObjectId>("userId", new ObjectId(paginationReq.getUserId())));
		}
		
		/*if(!Optional.of(ValidationUtil.validateDomainAndSubDomain(paginationReq)).get()) {
			throw new ServiceCommonException("106");
		}*/			
		String colName = param.get("ColName");
		List<Document> listDoc = commonDao.search(colName, lstKeyValuePairs, param, paginationReq,fields);
		List<String> lst = listDoc.stream().map(doc->doc.toJson()).collect(Collectors.toCollection(ArrayList::new));
	      //List<String>ist2=listDoc1.stream().map(doc->doc.toString()).collect(Collectors.toCollection(ArrayList::new));
		logger.debug("<searchResource> size {}",lst.size());
		
		
		logger.debug("</retrieveCollection>");
		return lst;
	}
	public List<String> retrieveCollection( PaginationReqParam paginationReq, Map<String, String> param,List<String>fields,String distinct_field) throws Exception {
		logger.debug("<retrieveCollection>");
		
		List<MongokeyvaluePair<? extends Object>> lstKeyValuePairs = new ArrayList<>();
		
		/*
		 * if(TextUtil.isNotEmpty(paginationReq.getDomain())) { lstKeyValuePairs.add(new
		 * MongokeyvaluePair<String>("domain", paginationReq.getDomain())); }
		 * if(TextUtil.isNotEmpty(paginationReq.getSubdomain())) {
		 * lstKeyValuePairs.add(new MongokeyvaluePair<String>("subdomain",
		 * paginationReq.getSubdomain())); }
		 */
		if(TextUtil.isNotEmpty(paginationReq.getUserId())) {
			
			
			lstKeyValuePairs.add(new MongokeyvaluePair<ObjectId>("userId", new ObjectId(paginationReq.getUserId())));
		}
		
		/*if(!Optional.of(ValidationUtil.validateDomainAndSubDomain(paginationReq)).get()) {
			throw new ServiceCommonException("106");
		}*/			
		String colName = param.get("ColName");
		List<Document> listDoc1=	commonDao.searchAndGetDistinct(colName, lstKeyValuePairs, param, paginationReq,fields,distinct_field);
		
		
	    List<String>ist2=listDoc1.stream().map(doc->doc.toJson()).collect(Collectors.toCollection(ArrayList::new));
		logger.debug("<searchResource> size {}",ist2.size());
		
		
		logger.debug("</retrieveCollection>");
		return ist2;
	}
	
	
	public Long retrieveCount( PaginationReqParam paginationReq, Map<String, String> param) throws Exception {
		logger.debug("<retrieveCount>");
		
		List<MongokeyvaluePair<? extends Object>> lstKeyValuePairs = new ArrayList<>();
		
		/*
		 * if(TextUtil.isNotEmpty(paginationReq.getDomain())) { lstKeyValuePairs.add(new
		 * MongokeyvaluePair<String>("domain", paginationReq.getDomain())); }
		 * if(TextUtil.isNotEmpty(paginationReq.getSubdomain())) {
		 * lstKeyValuePairs.add(new MongokeyvaluePair<String>("subdomain",
		 * paginationReq.getSubdomain())); }
		 */
		if(TextUtil.isNotEmpty(paginationReq.getUserId())) {
			lstKeyValuePairs.add(new MongokeyvaluePair<ObjectId>("userId", new ObjectId(paginationReq.getUserId())));
		}
		
		String colName = param.get("ColName");
		
		long count = commonDao.getCount(colName, lstKeyValuePairs, param, paginationReq);
		
		logger.debug("<searchResource> count {}",count);
		
		
		logger.debug("</retrieveCount>");
		return count;
	}
	private String getCollectionName() {
		return String.format(CollectionName.COMBOCOLLECTION, CustomRequestInterceptor.getUniqueId());
	}

	public String updateResource(String userId, String resourceId, String json,String colname) throws Exception {
          logger.debug("<updateResource>");
		
			/*
			 * if(!Optional.of(ValidationUtil.validateUserId(userId)).get()) { throw new
			 * ServiceCommonException("106"); }
			 */
		List<MongokeyvaluePair<? extends Object>> fields = new ArrayList<>();
		if(userId!=null) {
			fields.add(new MongokeyvaluePair<ObjectId>("userId", new ObjectId(userId)));	
		}
		fields.add(new MongokeyvaluePair<ObjectId>("_id", new ObjectId(resourceId)));
		
		String doc = commonDao.replaceDoc(colname,json, fields, fields).toJson();
		
		logger.debug("</updateResource>");
		
		return doc;
	}
	public String findrating(String ColName,String adminId) {
		Map<String,Object>m=commonDao.getAverageRatingAndCount(ColName,adminId);
		 ObjectMapper objectMapper = new ObjectMapper();
		 String jacksonData=null;
		    try {
				 jacksonData = objectMapper.writeValueAsString(m);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return jacksonData;
				
	}
	public List<String> findnearbyLocation(double longitude,double lattitude,double maxdist,String ColName,PaginationReqParam paginationreq,Map<String, String> param) {
		List<Document>doc=commonDao.findNearbyPlaces(longitude, lattitude, maxdist,ColName,paginationreq,param);
		List<String> lst = doc.stream().map(doc1->doc1.toJson()).collect(Collectors.toCollection(ArrayList::new));
		return lst;
	}
	public List<String> retrieveCollectionFuzzy( PaginationReqParam paginationReq, Map<String, String> param,List<String>fields,String fuzzyfield,String fuzzystring) throws Exception {
		logger.debug("<retrieveCollection>");
		
		List<MongokeyvaluePair<? extends Object>> lstKeyValuePairs = new ArrayList<>();
		
		/*
		 * if(TextUtil.isNotEmpty(paginationReq.getDomain())) { lstKeyValuePairs.add(new
		 * MongokeyvaluePair<String>("domain", paginationReq.getDomain())); }
		 * if(TextUtil.isNotEmpty(paginationReq.getSubdomain())) {
		 * lstKeyValuePairs.add(new MongokeyvaluePair<String>("subdomain",
		 * paginationReq.getSubdomain())); }
		 */
		if(TextUtil.isNotEmpty(paginationReq.getUserId())) {
			
			
			lstKeyValuePairs.add(new MongokeyvaluePair<ObjectId>("userId", new ObjectId(paginationReq.getUserId())));
		}
		
		/*if(!Optional.of(ValidationUtil.validateDomainAndSubDomain(paginationReq)).get()) {
			throw new ServiceCommonException("106");
		}*/			
		String colName = param.get("ColName");
		List<Document> listDoc = commonDao.searchfieldsD(colName, lstKeyValuePairs, param, paginationReq,fuzzyfield,fuzzystring);
		List<String> lst = listDoc.stream().map(doc->doc.toJson()).collect(Collectors.toCollection(ArrayList::new));
	      //List<String>ist2=listDoc1.stream().map(doc->doc.toString()).collect(Collectors.toCollection(ArrayList::new));
		logger.debug("<searchResource> size {}",lst.size());		
		logger.debug("</retrieveCollection>");
		return lst;
	}
	public String saveCollection( String domain, String subDomain,String userId,String colname,String resourceJson) throws Exception {
		    logger.debug("<createResource>");
		    List<MongokeyvaluePair<? extends Object>> lstKeyValuePairs = new ArrayList<>();
			if(Optional.of(ValidationUtil.validateDomainAndId(domain, subDomain, userId)).get()) {
				lstKeyValuePairs.add(new MongokeyvaluePair<String>("domain", domain));
				lstKeyValuePairs.add(new MongokeyvaluePair<String>("subdomain", subDomain));
				lstKeyValuePairs.add(new MongokeyvaluePair<ObjectId>("userId", new ObjectId(userId)));
			}			
			
			
			String json = commonDao.insertDocument(colname,resourceJson, lstKeyValuePairs);		
			logger.debug("</createResource>");
			
				return json;
		}

	public Long deletefromCollection( String userId ,String resourceId,String ColName) throws Exception {
		logger.debug("<retrieveCollection>");
		
		List<MongokeyvaluePair<? extends Object>> lstKeyValuePairs = new ArrayList<>();
	
			lstKeyValuePairs.add(new MongokeyvaluePair<ObjectId>("_id", new ObjectId(resourceId)));
			if(userId!=null) {
				lstKeyValuePairs.add(new MongokeyvaluePair<ObjectId>("userId", new ObjectId(userId)));
			}
			

		
		long listDoc = commonDao.deleteDoc(ColName, lstKeyValuePairs);
		
		return listDoc;
	}



	    public List<Document> getRandomQuestions( int count,String colname,String Class, String subject,String chapter) {
	    	List<Document>listDoc=commonDao.getRandomQuestions(count,colname,Class,subject,chapter);
	    	List<String> lst = listDoc.stream().map(doc->doc.toJson()).collect(Collectors.toCollection(ArrayList::new));
	    	return listDoc;
	    	
	    }
	
	    public List<Document> getRandomQuestions2( int count,String colname,Map<String,Object>filters) {
	    	List<Document>listDoc=commonDao.getRandomQuestions2(count,colname,filters);
	    	List<String> lst = listDoc.stream().map(doc->doc.toJson()).collect(Collectors.toCollection(ArrayList::new));
	    	return listDoc;
	    	
	    }
	    public List<Document> getRandomQuestions24( int count,String colname,Map<String,Object>filters) {
	    	List<Document>listDoc=commonDao.getRandomQuestions24(count,colname,filters);
	    	System.out.println(listDoc.size());
	    	//List<String> lst = listDoc.stream().map(doc->doc.toJson()).collect(Collectors.toCollection(ArrayList::new));
	    	return listDoc;
	    	
	    }
		 public List<String> testdetails( PaginationReqParam paginationReq, String testname) throws Exception {
			logger.debug("<retrieveCollection>");
			String colName;
			if (testname.equalsIgnoreCase("NEET-JEE")) {
				colName= "TUFIQUNPTEXFR0UTBWFOYQ=_res";
			}else if(testname.equalsIgnoreCase("QUIZ")) {
				colName= "UVVJWI1XDWL_res";
			}else {
				throw new ServiceCommonException("406");
				
			}
				
			List<Document> listDoc = commonDao.searchtest(colName,testname , paginationReq);
			List<String> lst = listDoc.stream().map(doc->doc.toJson()).collect(Collectors.toCollection(ArrayList::new));
		  
			logger.debug("<searchResource> size {}",lst.size());
			
			
			logger.debug("</retrieveCollection>");
			return lst;
		}
			
			public String saveCollectionBulk( String domain, String subDomain,String userId,String colname,String resourceJson) throws Exception {
			    logger.debug("<createResource>");
			    List<MongokeyvaluePair<? extends Object>> lstKeyValuePairs = new ArrayList<>();
				if(Optional.of(ValidationUtil.validateDomainAndId(domain, subDomain, userId)).get()) {
					lstKeyValuePairs.add(new MongokeyvaluePair<String>("domain", domain));
					lstKeyValuePairs.add(new MongokeyvaluePair<String>("subdomain", subDomain));
					lstKeyValuePairs.add(new MongokeyvaluePair<ObjectId>("userId", new ObjectId(userId)));
				}			
				
				
				String json = commonDao.insertDocumentBulk(colname,resourceJson);		
				logger.info("</createResource>");
				
					return json;
			}
			public Long retrieveCountForField(PaginationReqParam paginationReq, String field, String value, String collectionName) throws Exception {
			    List<MongokeyvaluePair<? extends Object>> keyValuePairs = new ArrayList<>();
			    keyValuePairs.add(new MongokeyvaluePair<>(field, value));

			    return commonDao.getCount(collectionName, keyValuePairs, new HashMap<>(), paginationReq);
			}
	  
}
