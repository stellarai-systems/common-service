package co.mannit.commonservice.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class TestRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * Fetch test by tid
	 */
	public List<Map<String, Object>> findAllSince(String colName, Date startDate) {
	    Query query = new Query();
	    query.addCriteria(Criteria.where("date").gte(startDate));  // Make sure your collection has a "date" field
	    return (List<Map<String, Object>>) (List<?>) mongoTemplate.find(query, Map.class, colName);
	}
	public List<Map<String,Object>> findAll(String colName){
		
		 return(List<Map<String, Object>>) (List<?>) mongoTemplate.findAll(Map.class, colName);
	}
	
	public Map<String,Object>getsubmissionByid(String id){
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
//System.out.println(mongoTemplate.findOne(query, Map.class, "QUXCQVLBTKHVQI1HBGJHEWFUAHV_TestLibrary"));
		// Assuming your collection is named "tests"
		return mongoTemplate.findOne(query, Map.class, "QUXCQVLBTKHVQI1HBGJHEWFUAHV_Result");
	}
	
	public Map<String, Object> getTestByTid(String tid) {
		Query query = new Query();
		query.addCriteria(Criteria.where("tid").is(tid));
//System.out.println(mongoTemplate.findOne(query, Map.class, "QUXCQVLBTKHVQI1HBGJHEWFUAHV_TestLibrary"));
		// Assuming your collection is named "tests"
		return mongoTemplate.findOne(query, Map.class, "QUXCQVLBTKHVQI1HBGJHEWFUAHV_TestLibrary");
	}
}
