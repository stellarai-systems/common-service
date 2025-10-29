package co.mannit.commonservice.service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import co.mannit.commonservice.ServiceCommonException;
import co.mannit.commonservice.common.MongokeyvaluePair;
import co.mannit.commonservice.common.util.TextUtil;
import co.mannit.commonservice.common.util.ValidationUtil;
import co.mannit.commonservice.dao.ImgDao;
import co.mannit.commonservice.dao.ResourceDao;
import co.mannit.commonservice.pojo.PaginationReqParam;

@Service
public class ImgService {
	private static final Logger logger = LogManager.getLogger(ImgService.class);
	@Autowired
	private ImgDao imagedao;

	
	
	public String uploadimage(String json,String phone, byte [] filedata,String uid) throws FileNotFoundException, IOException {
	    List<MongokeyvaluePair<? extends Object>> lstKeyValuePairs = new ArrayList<>();
		lstKeyValuePairs.add(new MongokeyvaluePair<byte []>("filedata",filedata));
		lstKeyValuePairs.add(new MongokeyvaluePair<String>("phonenumber",phone));
		if(uid!=null) {
			lstKeyValuePairs.add(new MongokeyvaluePair<ObjectId>("userId", new ObjectId(uid)));
		}
		

		// String name =imagedao.uploadDocumen(phone, filedata);
		return imagedao.insertDocument(json,lstKeyValuePairs);
	}
	public List<String> searchResource(PaginationReqParam paginationReq, Map<String, String> params,List<String>fields) throws Exception {
	//	logger.debug("<searchResource>");
		
		List<MongokeyvaluePair<? extends Object>> lstKeyValuePairs = new ArrayList<>();
//		lstKeyValuePairs.add(new MongokeyvaluePair<String>("domain", paginationReq.getDomain()));
//		lstKeyValuePairs.add(new MongokeyvaluePair<String>("subdomain", paginationReq.getSubdomain()));
		
		if(TextUtil.isNotEmpty(paginationReq.getUserId())) {
			//System.out.println(paginationReq.getUserId());
			lstKeyValuePairs.add(new MongokeyvaluePair<ObjectId>("userId", new ObjectId(paginationReq.getUserId())));
		}
		
		/*if(!Optional.of(ValidationUtil.validateDomainAndSubDomain(paginationReq)).get()) {
			throw new ServiceCommonException("106");
		}*/
		
		
		
		List<Document> listDoc = imagedao.search(lstKeyValuePairs, params, paginationReq,fields);
		List<String> lst = listDoc.stream().map(doc->doc.toJson()).collect(Collectors.toCollection(ArrayList::new));
	
	//	logger.debug("<searchResource> size {}",lst.size());
		return lst;
	}
	
	public String updateResource(String userId, String resourceId, String resourceJson,byte[]file) throws Exception {
		logger.debug("<updateResource>");
		
		if(!Optional.of(ValidationUtil.validateUserId(userId)).get()) {
			throw new ServiceCommonException("106");
		}
		
		List<MongokeyvaluePair<? extends Object>> fields = new ArrayList<>();
		fields.add(new MongokeyvaluePair<String>("userId", userId));
		fields.add(new MongokeyvaluePair<ObjectId>("_id", new ObjectId(resourceId)));

	
        String doc =null;
        doc = imagedao.replaceDoc(resourceJson, fields, fields,file);
   
		
		
		logger.debug("</updateResource>");
		
		return doc;
	}


}
