package co.mannit.commonservice.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import com.mongodb.client.result.DeleteResult;

import co.mannit.commonservice.ServiceCommonException;
import co.mannit.commonservice.common.MongokeyvaluePair;
import co.mannit.commonservice.pojo.PaginationReqParam;
import co.mannit.commonservice.pojo.User;
import co.mannit.commonservice.search.PaginationQueryBuilder;
import co.mannit.commonservice.search.SearchQueryBuilder;
import co.mannit.commonservice.search.SortQueryBuilder;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class CommonDao {

	private static final Logger logger = LogManager.getLogger(CommonDao.class);
			
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private SearchQueryBuilder searchQueryBuilder;
	
	public boolean isRecordExist(String collName, List<MongokeyvaluePair<? extends Object>> keyValuePair) {
		logger.debug("<isRecordExist> collName:{} keyValuePair:{}",collName, keyValuePair);
		
		boolean isExist = false;
		
		Query query = new Query();
		if(keyValuePair != null && keyValuePair.size() > 0) {
			keyValuePair.forEach(pair -> {
				query.addCriteria(Criteria.where(pair.getKey()).is(pair.getValue()));
			});
		}
		
		isExist = mongoTemplate.exists(query, collName);

		logger.debug("</isRecordExist> isExist:{}",isExist);
		return isExist;
	}
	
	public boolean isUserAlreadyExist(String colName, String key, Object value) {
		logger.debug("<isUserAlreadyExist> colName:{} key:{} value:{}",colName,key,value);
		
		boolean isExist = false;
		
		Query query = new Query();
		query.addCriteria(Criteria.where(key).is(value));
		isExist = mongoTemplate.exists(query, colName);
		
		logger.debug("</isUserAlreadyExist> isExist:{}",isExist);
		return isExist;
	}
	
	public boolean isUserAlreadyExist(String colName, MongokeyvaluePair<? extends Object> pair) {
		logger.debug("<isUserAlreadyExist> colName:{} pair:{}",colName, pair);
		
		boolean isExist = false;
		
		Query query = new Query();
		query.addCriteria(Criteria.where(pair.getKey()).is(pair.getValue()));
		isExist = mongoTemplate.exists(query, colName);
		
		logger.debug("</isUserAlreadyExist> isExist:{}",isExist);
		return isExist;
	}
	
	public boolean isUserAlreadyExist(String colName, List<MongokeyvaluePair<? extends Object>> keyValuePair) {
		logger.debug("<isUserAlreadyExist> colName:{} keyValuePair:{}",colName, keyValuePair);
		
		boolean isExist = false;
		
		Query query = new Query();
		if(keyValuePair != null && keyValuePair.size() > 0) {
			keyValuePair.forEach(pair -> {
				query.addCriteria(Criteria.where(pair.getKey()).is(pair.getValue()));
			});
		}
		
		isExist = mongoTemplate.exists(query, colName);

		logger.debug("</isUserAlreadyExist> isExist:{}",isExist);
		return isExist;
	}
	
	public String findDocAsString(String colName, List<MongokeyvaluePair<? extends Object>>  keyValuePairs) throws Exception {
		logger.debug("<findDoc> colName:{} keyValuePairs:{}",colName,keyValuePairs);
		
		Query query = new Query();
		if(keyValuePairs != null && keyValuePairs.size() > 0) {
			
			keyValuePairs.forEach(pair -> {
				query.addCriteria(Criteria.where(pair.getKey()).is(pair.getValue()));
			});
			
			/*Stream.of(keyValuePairs).forEach(pair -> {
			  query.addCriteria(Criteria.where(pair.getKey()).is(pair.getValue())); 
			  });*/

		}
		
		List<Document> lsftDoc = new ArrayList<>();
		mongoTemplate.executeQuery(query, colName, new DocumentCallbackHandler() {
			@Override
			public void processDocument(Document document) throws MongoException, DataAccessException {
				lsftDoc.add(document);
			}
		});
		
		if(lsftDoc.size() > 1) {
			throw new Exception("More than one document found for keyValuePairs:{}".formatted(keyValuePairs)); 
		}
		
		String doc = null;
		if(lsftDoc.size() == 1) {
			lsftDoc.get(0).remove("password");
			doc = lsftDoc.get(0).toJson();
		}
		
		logger.debug("</isUserAlreadyExist> Doc:{}",doc);
		return doc;
	}
	
	public Document findDoc(String colName, List<MongokeyvaluePair<? extends Object>>  keyValuePairs) throws Exception {
		logger.debug("<findDoc> colName:{} keyValuePairs:{}",colName,keyValuePairs);
		
		Query query = new Query();
		if(keyValuePairs != null && keyValuePairs.size() > 0) {
			
			keyValuePairs.forEach(pair -> {
				query.addCriteria(Criteria.where(pair.getKey()).is(pair.getValue()));
			});
		}
		
		logger.debug("query {}",query);
		
		List<Document> lsftDoc = new ArrayList<>();
		mongoTemplate.executeQuery(query, colName, new DocumentCallbackHandler() {
			@Override
			public void processDocument(Document document) throws MongoException, DataAccessException {
				lsftDoc.add(document);
			}
		});
		
		if(lsftDoc.size() > 1) {
			throw new Exception("More than one document found for keyValuePairs:{}".formatted(keyValuePairs)); 
		}
		
		
		logger.debug("</isUserAlreadyExist> Doc:{}",lsftDoc);
		return lsftDoc.size() ==0 ? null : lsftDoc.get(0);
	}
	
	public String findDocAsString(String colName, MongokeyvaluePair<? extends Object>  keyValuePairs) throws Exception {
		List<MongokeyvaluePair<? extends Object>> lstKeyValuePairs = new ArrayList<>();
		lstKeyValuePairs.add(keyValuePairs);
		return findDocAsString(colName, lstKeyValuePairs);
	}
	
	public void insertDocument(String colName, String json) {
		logger.debug("<insertDocument> colName:{} json:{}",colName,json);
		mongoTemplate.insert(json, colName);
		logger.debug("</insertDocument>");
	}
	
	public String insertDocument(String colName, String json, List<MongokeyvaluePair<? extends Object>> keyValuPairs) {
		logger.debug("<insertDocument> colName:{} json:{} keyValuPairs:{}",colName,json, keyValuPairs);
	
		Document document = Document.parse(json);
		
		if(keyValuPairs != null && keyValuPairs.size()>0) {
			keyValuPairs.stream().forEach(pair -> {
				document.append(pair.getKey(), pair.getValue());
			});
		}
		
		/*
		 * Optional.of(keyValuPairs).flatMap(lstKeyValue ->
		 * lstKeyValue.stream().forEach(pair->{ document.append(pair.getKey(),
		 * pair.getValue()); }));
		 */
		
		/*
		 * stream().forEach(pair ->{ document.append(pair.g, keyValuPairs) });
		 */

		
		Document document1 = mongoTemplate.insert(document, colName);
		logger.debug("</insertDocument>");
		
		return document1 == null ? "" : document1.toJson();
	}
	public String insertDocumentBulk(String colName, String json) {
		logger.info("<insertDocument> colName:{} json:{} keyValuPairs:{}", colName, json);
		ObjectMapper mapper = new ObjectMapper();
		List<Document> documents = new ArrayList<Document>();
		try {
			documents = mapper.readValue(json, new TypeReference<List<Document>>() {
			});
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collection<Document> insertedDocs = mongoTemplate.insert(documents, colName);
		logger.debug("</insertDocument>");
		return insertedDocs.toString();
	}
	public void saveDocument(String colName, String json, List<MongokeyvaluePair<? extends Object>> keyValuPairs) {
		logger.debug("<saveDocument> colName:{} json:{} keyValuPairs:{}",colName,json, keyValuPairs);
		
		Document document = Document.parse(json);
		
		if(keyValuPairs != null && keyValuPairs.size()>0) {
			keyValuPairs.stream().forEach(pair -> {
				document.append(pair.getKey(), pair.getValue());
			});
		}
		
		mongoTemplate.save(document, colName);
		logger.debug("</saveDocument>");
	}
	
	public void saveDocument(String colName, Document document) {
		logger.debug("<saveDocument> colName:{} Document:{} ",colName,document);
		mongoTemplate.save(document, colName);
		logger.debug("</saveDocument>");
	}
	
	public void printAllDoc(String colName) {
		logger.debug("<printAllDoc> colName:{}",colName);
		
		mongoTemplate.executeQuery(new Query(), "user", new DocumentCallbackHandler() {

			@Override
			public void processDocument(org.bson.Document document) throws MongoException, DataAccessException {
				// TODO Auto-generated method stub
				logger.debug(document);
			}
			
		});
		
		logger.debug("</printAllDoc>");
	}
	
	public List<Document> findDoc(String colName, List<MongokeyvaluePair<? extends Object>>  keyValuePairs, PaginationReqParam paginationReq) throws Exception {
		logger.debug("<findDoc> colName:{} keyValuePairs:{}",colName,keyValuePairs);
		
		Query query = new Query();
		if(keyValuePairs != null && keyValuePairs.size() > 0) {
			
			List<Criteria> lstCriteria = new ArrayList<>();
			
			keyValuePairs.forEach(pair -> {
				if("userId".equalsIgnoreCase(pair.getKey()) || "adminId".equalsIgnoreCase(pair.getKey())) {
					lstCriteria.add(Criteria.where(pair.getKey()).is(pair.getValue()));
//					query.addCriteria(new Criteria().orOperator(Criteria.where(pair.getKey()).is(pair.getValue())));
				}else {
					query.addCriteria(Criteria.where(pair.getKey()).is(pair.getValue()));
				}
			});
			
			if(lstCriteria.size() > 0) {
				query.addCriteria(new Criteria().orOperator(lstCriteria));
			}
		}
		
	
		
		PaginationQueryBuilder.buildPaginationQuery(query, paginationReq);
		SortQueryBuilder.buildSortQuery(query, paginationReq);
		
		logger.debug("Query : {}",query);
		
		List<Document> lsftDoc = new ArrayList<>();
		mongoTemplate.executeQuery(query, colName, new DocumentCallbackHandler() {
			@Override
			public void processDocument(Document document) throws MongoException, DataAccessException {
				lsftDoc.add(document);
			}
		});


		
		logger.debug("</isUserAlreadyExist> Doc:{}",lsftDoc.size());
		return lsftDoc;
	}
	
	public List<Document> finDoc(String colName, MongokeyvaluePair<? extends Object>  keyValuePairs) throws Exception {
		List<MongokeyvaluePair<? extends Object>> lstKeyValuePairs = new ArrayList<>();
		lstKeyValuePairs.add(keyValuePairs);
		return findDoc(colName, lstKeyValuePairs, null);
	}
	
	public long deleteDoc(String colName, List<MongokeyvaluePair<? extends Object>>  keyValuePairs) throws Exception {
		logger.debug("<deleteDoc> colName:{} keyValuePairs:{}",colName,keyValuePairs);
		
		Query query = new Query();
		if(keyValuePairs != null && keyValuePairs.size() > 0) {
			
			keyValuePairs.forEach(pair -> {
				query.addCriteria(Criteria.where(pair.getKey()).is(pair.getValue()));
			});
		}
		
		DeleteResult deleResult = mongoTemplate.remove(query, colName);
		
		logger.debug("</deleteDoc> count {}",deleResult.getDeletedCount());
		
		return deleResult.getDeletedCount();
	}
	
	public Document replaceDoc(String colName, String json, List<MongokeyvaluePair<? extends Object>>  fields, List<MongokeyvaluePair<? extends Object>>  criteria) throws Exception {
		logger.debug("<replaceDoc> colName:{} json:{} keyValuePairs:{}",colName,json, criteria);
		
		
		Document origialDoc = findDoc(colName, fields);
		if(origialDoc == null) throw new ServiceCommonException("112");
		
		Document document = Document.parse(json);
		
		
//		if(fields != null && fields.size()>0) {
//			fields.stream().forEach(pair -> {
//				document.append(pair.getKey(), pair.getValue());
//			});
//		}
		
		if(document != null) {
			origialDoc.putAll(document);
			
		}
		
		
		Query query = new Query();
		if(criteria != null && criteria.size() > 0) {
			
			criteria.forEach(pair -> {
				query.addCriteria(Criteria.where(pair.getKey()).is(pair.getValue()));
			});
		}
		
		mongoTemplate.findAndReplace(query, origialDoc, colName);
		logger.debug("</replaceDoc>");
		
		return origialDoc;
	}
	
	public List<Document> search(String colName, List<MongokeyvaluePair<? extends Object>> keyValuePairs, Map<String, String> filters, PaginationReqParam paginationReq,List<String>fields)
			throws Exception {
		logger.debug("<search>");
		
		Query query = new Query();
		
		if(keyValuePairs != null && keyValuePairs.size() > 0) {
			
			keyValuePairs.forEach(pair -> {
				query.addCriteria(Criteria.where(pair.getKey()).is(pair.getValue()));
			});
			
		}		
		searchQueryBuilder.buildSearchQuery(query, filters);
		
		PaginationQueryBuilder.buildPaginationQuery(query, paginationReq);
		
		SortQueryBuilder.buildSortQuery(query, paginationReq);
		
//		Collation coll = Collation.of("en").strength(2);
//		query.collation(coll);
		
		
		
		List<Document> lsftDoc = new ArrayList<>();
		
		/*
		 * if (fields != null) { fields.forEach(n -> { if (!n.equals("_id")) {
		 * query.fields().include(n).exclude("_id"); } else { query.fields().include(n);
		 * } }); }
		 */
		 
		if (fields != null) {
		    fields.forEach(n -> {
		        if (!n.equals("_id")) {
		            query.fields().include(n);
		        }
		    });
		    if (!fields.contains("_id")) {
		        query.fields().exclude("_id");
		    }
		}

        query.limit(paginationReq.getLimit());
		logger.debug("query => {}", query);
		mongoTemplate.executeQuery(query, colName, new DocumentCallbackHandler() {
			@Override
			public void processDocument(Document document) throws MongoException, DataAccessException {
				lsftDoc.add(document);
			}
		});
		logger.debug("</search> Doc:{}",lsftDoc.size());
		return lsftDoc;		
	}
	public List<Document> searchfields(String colName, List<MongokeyvaluePair<? extends Object>> keyValuePairs, Map<String, String> filters, PaginationReqParam paginationReq)
			throws Exception {
		logger.debug("<search>");
		
		Query query = new Query();
		
		if(keyValuePairs != null && keyValuePairs.size() > 0) {
			
			keyValuePairs.forEach(pair -> {
				query.addCriteria(Criteria.where(pair.getKey()).is(pair.getValue()));
			});
			
		}		
		searchQueryBuilder.buildSearchQuery(query, filters);
		
		PaginationQueryBuilder.buildPaginationQuery(query, paginationReq);
		
		SortQueryBuilder.buildSortQuery(query, paginationReq);
		
//		Collation coll = Collation.of("en").strength(2);
//		query.collation(coll);
		
		logger.debug("query => {}", query);
		
		List<Document> lsftDoc = new ArrayList<>();
		query.fields().exclude("_id");
		mongoTemplate.executeQuery(query, colName, new DocumentCallbackHandler() {
			@Override
			public void processDocument(Document document) throws MongoException, DataAccessException {
				lsftDoc.add(document);
			}
		});
		logger.debug("</search> Doc:{}",lsftDoc.size());
		return lsftDoc;		
	}
	
	public Long getCount(String colName, List<MongokeyvaluePair<? extends Object>> keyValuePairs, Map<String, String> filters, PaginationReqParam paginationReq)
			throws Exception {
		logger.debug("<getCount>");
		
		Query query = new Query();
		
		if(keyValuePairs != null && keyValuePairs.size() > 0) {
			
			keyValuePairs.forEach(pair -> {
				query.addCriteria(Criteria.where(pair.getKey()).is(pair.getValue()));
			});
			
		}		
		
		searchQueryBuilder.buildSearchQuery(query, filters);
		
		PaginationQueryBuilder.buildPaginationQuery(query, paginationReq);
		
		SortQueryBuilder.buildSortQuery(query, paginationReq);
		
		logger.debug("query => {}", query);
		
		long count = mongoTemplate.count(query, colName);

		logger.debug("</getCount> count:{}",count);
		return count;		
	}
	
	public void createCollection(String collName) {
		logger.debug("<createCollection> collName:{}",collName);
		mongoTemplate.createCollection(collName);
		logger.debug("</createCollection>");
	}
	
	public String uploadtofolder(String foldername,byte []imageData,String uniqueid,String phoneNumber) throws FileNotFoundException, IOException {
		logger.info("initiated>uploadtofolder");
         String localDirectory = "D:\\opt\\images\\" + foldername;
         String fileName = new Date().getTime() + "_" + phoneNumber + "_" + uniqueid + ".jpg";
         File directory = new File(localDirectory);
         if (!directory.exists()) {
             directory.mkdir(); 
         }
         
         File file = new File(localDirectory + File.separator + fileName);
         
         try (FileOutputStream fos = new FileOutputStream(file)) {
             fos.write(imageData);
             logger.info("Image saved to file: {}", file.getAbsolutePath());
         }
		return file.getName();
		
	}
	
	
	public Document replaceDocImage(String colName, String json, List<MongokeyvaluePair<? extends Object>>  fields, List<MongokeyvaluePair<? extends Object>>  criteria,byte[]file) throws Exception {
		logger.debug("<replaceDoc> colName:{} json:{} keyValuePairs:{}",colName,json, criteria);
		
		
		Document origialDoc = findDoc(colName, fields);
		if(origialDoc == null) throw new ServiceCommonException("112");
		Document document=null;
		if(json!=null) {
			 document = Document.parse(json);
		}
		
		
		
//		if(fields != null && fields.size()>0) {
//			fields.stream().forEach(pair -> {
//				document.append(pair.getKey(), pair.getValue());
//			});
//		}
		
		if(document != null) {
			origialDoc.putAll(document);
			
			
		}if(file!=null) {
			origialDoc.put("filedata", file);
		}
		
		
		Query query = new Query();
		if(criteria != null && criteria.size() > 0) {
			
			criteria.forEach(pair -> {
				query.addCriteria(Criteria.where(pair.getKey()).is(pair.getValue()));
			});
		}
		
		mongoTemplate.findAndReplace(query, origialDoc, colName);
		logger.debug("</replaceDoc>");
		
		return origialDoc;
	}

	
	
	
public List<Document> searchAndGetDistinct(String colName, List<MongokeyvaluePair<?>> keyValuePairs,
				Map<String, String> filters, PaginationReqParam paginationReq, List<String> fields,
				String distinctField) throws Exception {
			logger.debug("<searchAndGetDistinct>");
			Query query = new Query();
			if (keyValuePairs != null && !keyValuePairs.isEmpty()) {
				keyValuePairs.forEach(pair -> query.addCriteria(Criteria.where(pair.getKey()).is(pair.getValue())));
			}
			searchQueryBuilder.buildSearchQuery(query, filters);

			PaginationQueryBuilder.buildPaginationQuery(query, paginationReq);

			SortQueryBuilder.buildSortQuery(query, paginationReq);

			if (fields != null) {
				fields.forEach(field -> {
					if (!field.equals("_id")) {
						query.fields().include(field);
					}
				});
				if (!fields.contains("_id")) {
					query.fields().exclude("_id");
				}
			}

			query.limit(paginationReq.getLimit());

			List<Document> documents = new ArrayList<>();
			mongoTemplate.executeQuery(query, colName, document -> documents.add(document));

			logger.debug("Initial filtered documents count: {}", documents.size());

			Aggregation aggregation = Aggregation.newAggregation(
					match(Criteria.where("_id")
							.in(documents.stream().map(doc -> doc.get("_id")).collect(Collectors.toList()))),
					group(distinctField).first("$$ROOT").as("distinctDocument"));

			AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, colName, // Use the same collection
																							// as for the query
					Map.class // Use Map for generic structure
			);

			List<Document> distinctResults = results.getMappedResults().stream()
				    .map(result -> {
				        Object inner = result.get("distinctDocument");
				        if (inner instanceof Document) {
				            return (Document) inner;
				        } else if (inner instanceof Map) {
				            return new Document((Map<String, Object>) inner);
				        } else {
				            return new Document("error", "Unexpected type: " + inner.getClass());
				        }
				    })
				    .collect(Collectors.toList());

			logger.debug("Distinct documents count: {}", distinctResults.size());
			return distinctResults;
		}




public List<Document> findNearbyPlaces(double longitude, double latitude, double maxDistanceInMeters,String ColName, PaginationReqParam paginationReq,
		Map<String, String> filters) {
    // Define the center point
    Point location = new Point(longitude, latitude);
    
    // Define the query with geospatial criteria
    Query query = new Query();
    query.addCriteria(Criteria.where("location").nearSphere(location).maxDistance(maxDistanceInMeters / 6378137.0)); // Convert meters to radians
   searchQueryBuilder.buildSearchQuery(query, filters);
    // Execute the query
    logger.info("query  {} =>",query);
    return mongoTemplate.find(query, Document.class, ColName);
}
public Map<String, Object> getAverageRatingAndCount(String ColName,String adminId) {
    Aggregation aggregation = Aggregation.newAggregation(
    		Aggregation.match(Criteria.where("adminId").is(adminId).and("rating").exists(true)),
            Aggregation.group()
                    .avg("rating").as("averageRating") 
                    .count().as("reviewCount")        
    );
    AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, 
            ColName,    
            Map.class     
    );

    Map<String, Object> response = new HashMap<>();
    if (!results.getMappedResults().isEmpty()) {
        Map<String, Object> data = results.getMappedResults().get(0);
        response.put("averageRating", data.get("averageRating"));
        response.put("reviewCount", data.get("reviewCount"));
    } else {
        response.put("averageRating", 0.0);
        response.put("reviewCount", 0);
    }
    return response;
}
	
	/*
	 * public String retrieveImage() { logger.info("");
	 * 
	 * }
	 */
	
	/*public List<Document> search(String colName, List<MongokeyvaluePair<? extends Object>> keyValuePairs, String op,
			Map<String, String> filter1, Map<String, String> filter2, PaginationReqParam pagenationReq)
			throws Exception {
		logger.debug("<findDoc> colName:{} keyValuePairs:{}",colName,keyValuePairs);
		
		Query query = new Query();
		if(keyValuePairs != null && keyValuePairs.size() > 0) {
			
			keyValuePairs.forEach(pair -> {
				query.addCriteria(Criteria.where(pair.getKey()).is(pair.getValue()));
			});
			
			if(op == null || "null".equals(op)) {
				query.addCriteria(buildCriteria(filter1));
			}else if("or".equals(op)) {
				query.addCriteria(
						new Criteria().orOperator(buildCriteria(filter1),
								buildCriteria(filter2)));
			}else if("and".equals(op)) {
				query.addCriteria(
						new Criteria().andOperator(buildCriteria(filter1),
								buildCriteria(filter2)));
			}
		}
		
		final Pageable pageableRequest = PageRequest.of(pagenationReq.getPage()-1, pagenationReq.getSizePerPage());
		query.with(pageableRequest);
		
		logger.debug("Query {}",query);
		List<Document> lsftDoc = new ArrayList<>();
		mongoTemplate.executeQuery(query, colName, new DocumentCallbackHandler() {
			@Override
			public void processDocument(Document document) throws MongoException, DataAccessException {
				lsftDoc.add(document);
			}
		});

		logger.debug("</search> Doc:{}",lsftDoc.size());
		return lsftDoc;
	}
	
	
	private Map<String, Function<String, ? extends Object>> dataTypeConverter = null;
	{
		dataTypeConverter = new HashMap<>();
		dataTypeConverter.put("L", (t)-> Long.parseLong(t));
		dataTypeConverter.put("I", (t)-> Integer.parseInt(t));
		dataTypeConverter.put("S", (t)-> t);
		dataTypeConverter.put("D", (t)-> {
			SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
			try {
				Date d = sd.parse(t);
				
				Calendar calendar = Calendar.getInstance();
			    calendar.setTime(d);
			    calendar.add(Calendar.MINUTE, 330);
			    
			    d = calendar.getTime();
			    logger.debug("Date : {}",d );
				return d;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		});
	}
	
	
	private Criteria buildCriteria(Map<String, String> filter) throws ServiceCommonException {
		Criteria cri = null;
		String operator = filter.get("op");
		
		switch(operator) {
			case "eq":
				cri = Criteria.where(filter.get("name")).is(dataTypeConverter.get(filter.get("dt")).apply(filter.get("value")));
				break;
			case "lt":
				cri = Criteria.where(filter.get("name")).lt(dataTypeConverter.get(filter.get("dt")).apply(filter.get("value")));
				break;
			case "lte":
				cri = Criteria.where(filter.get("name")).lte(dataTypeConverter.get(filter.get("dt")).apply(filter.get("value")));
				break;
			case "gt":
				cri = Criteria.where(filter.get("name")).gt(dataTypeConverter.get(filter.get("dt")).apply(filter.get("value")));
				break;
			case "gte":
				cri = Criteria.where(filter.get("name")).gte(dataTypeConverter.get(filter.get("dt")).apply(filter.get("value")));
				break;
			case "ne":
				cri = Criteria.where(filter.get("name")).ne(dataTypeConverter.get(filter.get("dt")).apply(filter.get("value")));
			case "in":
				cri = Criteria.where(filter.get("name")).in(dataTypeConverter.get(filter.get("dt")).apply(filter.get("value")));
			case "nin":
				cri = Criteria.where(filter.get("name")).nin(dataTypeConverter.get(filter.get("dt")).apply(filter.get("value")));
				break;
			case "regex":
				cri = Criteria.where(filter.get("name")).regex(String.valueOf(dataTypeConverter.get(filter.get("dt")).apply(filter.get("value"))));
				break;
		}
		
		if(cri == null) {
			throw new ServiceCommonException(String.format("This \"%s\" filter option not available", operator));
		}
		
		return cri;
	}*/

public List<Document> searchfieldsD(String colName, List<MongokeyvaluePair<? extends Object>> keyValuePairs, Map<String, String> filters, PaginationReqParam paginationReq, String searchField, String searchString)
        throws Exception {
    logger.debug("<search>");
    
    Query query = new Query();
    
    // Add key-value pair criteria
    if (keyValuePairs != null && keyValuePairs.size() > 0) {
        keyValuePairs.forEach(pair -> {
            query.addCriteria(Criteria.where(pair.getKey()).is(pair.getValue()));
        });
    }
    
    // Add fuzzy matching for a specific field
    if (searchField != null && searchString != null) {
        String regexPattern = ".*" + searchString.replace(" ", ".*") + ".*"; // Create regex for partial matching
        query.addCriteria(Criteria.where(searchField).regex(regexPattern, "i")); // Case-insensitive regex
    }
    
    // Build additional filters, pagination, and sorting
    searchQueryBuilder.buildSearchQuery(query, filters);
    PaginationQueryBuilder.buildPaginationQuery(query, paginationReq);
    SortQueryBuilder.buildSortQuery(query, paginationReq);
    
    // Exclude "_id" from the results if required
   // query.fields().exclude("_id");
    
    logger.debug("query => {}", query);
    
    List<Document> lsftDoc = new ArrayList<>();
    
    // Execute the query
    mongoTemplate.executeQuery(query, colName, new DocumentCallbackHandler() {
        @Override
        public void processDocument(Document document) throws MongoException, DataAccessException {
            lsftDoc.add(document);
        }
    });
    
    logger.debug("</search> Doc:{}", lsftDoc.size());
    return lsftDoc;
}

public List<Document> getRandomQuestions(int count,String colname,String Class,String subject,String chapter) {
	
	
	Criteria criteria = new Criteria();
	

    if (Class != null) {
        criteria = criteria.and("Class".toUpperCase()).is(Class);
    }
    if (subject != null) {
        criteria = criteria.and("subject".toUpperCase()).is(subject);
    }
    if (chapter != null) {
        criteria = criteria.and("chapter".toUpperCase()).is(Integer.parseInt(chapter));
    }

    // ✅ Apply filtering before sampling
    Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(criteria),   
            Aggregation.sample(count)      
    );

    AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, colname, Document.class);
    return results.getMappedResults();
}
public List<Document> getRandomQuestions2(int count,String colname,Map<String,Object>filter) {
	 List<Criteria> criteriaList = new ArrayList<>();

	    for (Map.Entry<String, Object> entry : filter.entrySet()) {
	        String key = entry.getKey().toUpperCase();
	        Object value = entry.getValue();
	  
	      //  System.out.println("Processing filter -> Key: " + key + ", Value: " + value);


	        if (!key.equalsIgnoreCase("colname") && value != null) {
	            // Handle potential Integer/String mismatch
	            if (value instanceof String) {
	                try {
	                    int intValue = Integer.parseInt((String) value);
	                    criteriaList.add(Criteria.where(key).is(intValue)); // Try Integer match
	                 //System.out.println("Converted String to Integer for key: " + key);
	                } catch (NumberFormatException e) {
	                    criteriaList.add(Criteria.where(key).is(value)); // Use as String
	                }
	            } else {
	                criteriaList.add(Criteria.where(key).is(value)); // Handle Integer, Boolean, etc.
	            }
	        }
	    }
	    

	    // Combine all criteria dynamically

	   // System.out.println("Final criteria list: " + criteriaList);
	    Criteria finalCriteria = criteriaList.isEmpty() ? new Criteria() : new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));

	    // Build Aggregation Pipeline
	    Aggregation aggregation = Aggregation.newAggregation(
	        Aggregation.match(finalCriteria),   
	        Aggregation.sample(count)  // Randomly select 'count' documents
	    );

	    AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, colname, Document.class);
	    return results.getMappedResults();
}
public List<Document> getRandomQuestions24(int count, String colname, Map<String, Object> filter) {
    List<Criteria> criteriaList = new ArrayList<>();

    for (Map.Entry<String, Object> entry : filter.entrySet()) {
        String key = entry.getKey().toUpperCase();
        Object value = entry.getValue();

        if (!key.equalsIgnoreCase("colname") && value != null) {
            if (value instanceof String) {
                try {
                    int intValue = Integer.parseInt((String) value);
                    criteriaList.add(Criteria.where(key).is(intValue)); // Try Integer match
                } catch (NumberFormatException e) {
                    criteriaList.add(Criteria.where(key).is(value)); // Use as String
                }
            } else {
                criteriaList.add(Criteria.where(key).is(value)); // Handle Integer, Boolean, etc.
            }
        }
    }

    // Keep finalCriteria for normal question filtering
    Criteria finalCriteria = criteriaList.isEmpty() ? new Criteria() : new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));

    Aggregation aggregation;

    // If colname contains "NEET", fetch (count - 5) normal + 5 random "D" questions
    if (!(colname.toUpperCase().contains("NEET"))) {
        int normalCount = count - 5; // Fetch count - 5 normal questions

        // Normal Questions Aggregation
        Aggregation normalAggregation = Aggregation.newAggregation(
            Aggregation.match(finalCriteria),
            Aggregation.sample(normalCount)
        );

        AggregationResults<Document> normalResults = mongoTemplate.aggregate(normalAggregation, colname, Document.class);
        List<Document> finalResults = new ArrayList<>(normalResults.getMappedResults());

       
        Criteria qidCriteria = new Criteria().andOperator(finalCriteria, Criteria.where("QID").regex("^D\\d+"));

        Aggregation neetAggregation = Aggregation.newAggregation(
            Aggregation.match(qidCriteria),
            Aggregation.sample(5)
        );

        AggregationResults<Document> neetResults = mongoTemplate.aggregate(neetAggregation, colname, Document.class);
        finalResults.addAll(neetResults.getMappedResults());

        return finalResults; // Returning final list

    } else {
        // Normal case: Fetch 'count' normal questions
        aggregation = Aggregation.newAggregation(
            Aggregation.match(finalCriteria),
            Aggregation.sample(count)
        );
    }

    AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, colname, Document.class);
    return results.getMappedResults(); // Keeping your original return type
}

public List<Document> searchtest(String colName, String fieldname,
		PaginationReqParam paginationReq)
		throws Exception {
	logger.debug("<search>");
	
	Query query = new Query();
	
	if(fieldname.toUpperCase().equalsIgnoreCase("NEET-JEE")) {
		query.addCriteria(new Criteria());
		query.fields().include("username")
		.include("gender")
		.include("dob")
		.include("email")
		.include("exam")
		.include("mobileno");
	}else {
		query.addCriteria(Criteria.where("exam").exists(true));
		query.fields().include("username")
		.include("email")
		.include("mobileno")
		.include("stream")
		.include("exam");
	}
	
	//query.addCriteria(new Criteria());
		query.fields().exclude("_id");
	
	PaginationQueryBuilder.buildPaginationQuery(query, paginationReq);
	
	SortQueryBuilder.buildSortQuery(query, paginationReq);

	List<Document> lsftDoc = new ArrayList<>();

    query.limit(paginationReq.getLimit());
	logger.debug("query => {}", query);
	mongoTemplate.executeQuery(query, colName, new DocumentCallbackHandler() {
		@Override
		public void processDocument(Document document) throws MongoException, DataAccessException {
			lsftDoc.add(document);
		}
	});	
	logger.debug("</search> Doc:{}",lsftDoc.size());
	return lsftDoc;		
}

public User finduserDetails(String colName,String user) {
	logger.debug("<findUser> colName:{}",colName);
//	System.out.println(colName);
	Query query = new Query();
	
   query.addCriteria(Criteria.where("username").is(user));

	
	
	logger.debug("query {}",query);
	
	User lastDoc = mongoTemplate.findOne(query, User.class, colName);	
	return lastDoc;
	
	
}


}
