package co.mannit.commonservice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.mannit.commonservice.common.Response;
import co.mannit.commonservice.pojo.CountResponse;
import co.mannit.commonservice.pojo.FieldCount;
import co.mannit.commonservice.pojo.MultiCountResponse;
import co.mannit.commonservice.pojo.PaginationReqParam;
import co.mannit.commonservice.service.CollectionService;

@RestController
public class RetrieveCollection {

	private static final Logger logger = LogManager.getLogger(RetrieveCollection.class);
	
	@Autowired
	private CollectionService collectionService;
			
	@GetMapping("retrievecollection")
	public Response<?> getCollection(@RequestParam(required=false) String distinct_field,@Validated PaginationReqParam paginationReq, @RequestParam Map<String, String> param,@RequestParam(required=false) List<String> fields) throws Exception {
		logger.debug("<retrievecollection> paginationReq {} param {} ",paginationReq, param);
		if(distinct_field!=null) {
			return Response.buildSuccessMsg(200, "Collection Value Retrieved Successfully", collectionService.retrieveCollection(paginationReq, param,fields,distinct_field));
		}else {
			return Response.buildSuccessMsg(200, "Collection Value Retrieved Successfully", collectionService.retrieveCollection(paginationReq, param,fields));
		}
		
	}
	
	@GetMapping("retrievecount")
	public Response<?> getCount(@Validated PaginationReqParam paginationReq, @RequestParam Map<String, String> param) throws Exception {
		logger.debug("<retrievecount> paginationReq {} param {} ",paginationReq, param);
		
		CountResponse count = new CountResponse();
		count.setCount(collectionService.retrieveCount(paginationReq, param));
//		String response = "{count=%s}".formatted(collectionService.retrieveCount(paginationReq, param));
		
		return Response.buildSuccessMsg(200, "Collection Count Retrieved Successfully", count);
	}
	@PutMapping("eUpdateColl")
	public Response<?> updateResource(@RequestBody String json, @RequestParam(name="userId",required=false)String userId, @RequestParam("resourceId")String resourceId,@RequestParam("ColName")String colname) throws Exception {
		logger.debug("<updateResource> userId {} resourceId{} json{}",userId, resourceId, json);
		return Response.buildSuccessMsg(200, "Resource Updated Successfully", collectionService.updateResource(userId, resourceId, json,colname));
	}
	@GetMapping("eRetrieveAverage")
	public Response<?>RetrieveAverage(@RequestParam String ColName,@RequestParam(required=false) String adminId) throws Exception{
		logger.info("Retrieve review");
		return Response.buildSuccessMsg(200,"Retrieved all",collectionService.findrating(ColName,adminId));
	} 
	@GetMapping("/getLocation")
	public Response<?> getnearby(@RequestParam double lat,
			                                @RequestParam double longitude,
			                                @RequestParam double maxdist,@RequestParam String ColName,@Validated PaginationReqParam paginationReq, @RequestParam Map<String, String> param){
		
			//List<LocationData> h=placeService.findNearbyPlaces(longitude,lat, maxdist);	
			return Response.buildSuccessMsg(200,"Retrieved all",collectionService.findnearbyLocation(longitude,lat,maxdist,ColName,paginationReq,param));	
	}
	@GetMapping("eFuzySearch")
	public Response<?> getFuzyMatch(@RequestParam(required=false) String distinct_field,@Validated PaginationReqParam paginationReq, @RequestParam Map<String, String> param,@RequestParam(required=false) List<String> fields,
			@RequestParam String fuzyfield,@RequestParam String fuzystring) throws Exception {
		logger.debug("<retrievecollection> paginationReq {} param {} ",paginationReq, param);
		if(distinct_field!=null) {
			return Response.buildSuccessMsg(200, "Collection Value Retrieved Successfully", collectionService.retrieveCollectionFuzzy(paginationReq, param,fields,fuzyfield,fuzystring));
		}else {
			return Response.buildSuccessMsg(200, "Collection Value Retrieved Successfully", collectionService.retrieveCollectionFuzzy(paginationReq, param,fields,fuzyfield,fuzystring));
		}
		
	}
	@PostMapping("eCreateCol")
	public Response<?> createResource(@RequestBody String json, @RequestParam(name="domain",required=false)String domain, @RequestParam(name="subdomain",required=false)String subdomain, 
			@RequestParam(name="userId",required=false)String userId,@RequestParam("colname")String colname) throws Exception {
		logger.debug("<createResource> domain {} subdomain {} userId {} json{}",domain,subdomain, userId,json);
		
		String doc = collectionService.saveCollection(domain, subdomain, userId, colname,json);
		
		return Response.buildSuccessMsg(200, "Resource Created Successfully", doc);
	}
	/*
	 * @GetMapping("/random/{count}") public
	 * Response<?>getRandomQuestions(@PathVariable int count,@RequestParam String
	 * colname,@RequestParam(required=false) String Class,
	 * 
	 * @RequestParam(required=false) String subject,
	 * 
	 * @RequestParam(required=false) String chapter) { return
	 * Response.buildSuccessMsg(200, "Resource Created Successfully",
	 * collectionService.getRandomQuestions(count,colname,Class,subject,chapter)); }
	 */
	   
	   @GetMapping("/random/{count}/2")
	   public CompletableFuture<Response<?>>getRandomQuestionsv2(@PathVariable int count,@RequestParam("colname") String colname,
			   @RequestParam Map<String,Object>filters) {
		   return CompletableFuture.supplyAsync(() ->
	        Response.buildSuccessMsg(200, "Resource fetched Successfully", 
	        collectionService.getRandomQuestions2(count, colname, filters))
	    );
	    }
	   @GetMapping("/random/{count}")
	   public CompletableFuture<Response<?>>getRandomQuestionsv24(@PathVariable int count,@RequestParam("colname") String colname,
			   @RequestParam Map<String,Object>filters) {
		   
		   
		   return CompletableFuture.supplyAsync(() ->
	        Response.buildSuccessMsg(200, "Resource fetched Successfully", 
	        		collectionService.getRandomQuestions24(count,colname,filters))
	    );
	    }
		
		@GetMapping("retrieve/v1/test")
		public Response<?> getTestDetails(
				@Validated PaginationReqParam paginationReq, 
				@RequestParam("Testname")String Testname,
				@RequestParam(required=false) List<String> fields) throws Exception {
			logger.debug("<retrievecollection> paginationReq {} param {} ",paginationReq, Testname);
		
				
			
				return Response.buildSuccessMsg(200, "Collection Value Retrieved Successfully", collectionService.testdetails(paginationReq, Testname));
			
			
		}
		@DeleteMapping("eDeleteWCol")
		public Response<?>DeleteCollection(@RequestParam(required=false) String userId,@RequestParam String resourceId,@RequestParam String ColName) throws Exception{
			logger.debug("<deleteResource> userId {} resourceId{} colName{}" ,userId,resourceId,ColName);
			long count = collectionService.deletefromCollection(userId,resourceId,ColName);
			return Response.buildSuccessMsg(200,"Deleted Successfully", String.format("%s records deleted successfully", count));
		}
		@PostMapping("eCreateColbulk")
		public Response<?> createResourceBulk(@RequestBody String json, @RequestParam(name="domain",required=false)String domain, @RequestParam(name="subdomain",required=false)String subdomain, 
				@RequestParam(name="userId",required=false)String userId,@RequestParam("colname")String colname) throws Exception {
			logger.debug("<createResource> domain {} subdomain {} userId {} json{}",domain,subdomain, userId,json);
			
			String doc = collectionService.saveCollectionBulk(domain, subdomain, userId, colname,json);
			
			return Response.buildSuccessMsg(200, "Resources Created Successfully", doc);
		}
		@GetMapping("retrievemulticount")
		public Response<?> getMultiCount(@Validated PaginationReqParam paginationReq,
				@RequestParam Map<String, String> param) throws Exception {
			logger.debug("<retrievemulticount> paginationReq {} param {}", paginationReq, param);

			List<FieldCount> fieldCounts = new ArrayList<>();
			String col = param.get("colname");
			param.remove("colname");
			for (Map.Entry<String, String> entry : param.entrySet()) {
				String field = entry.getKey();
				String value = entry.getValue();
				for (String val : value.split(",")) {
		            long count = collectionService.retrieveCountForField(paginationReq, field, val.trim(), col);
		            fieldCounts.add(new FieldCount(field, val.trim(), count));
		        }
			}

			MultiCountResponse response = new MultiCountResponse(fieldCounts);

			return Response.buildSuccessMsg(200, "Multiple Counts Retrieved Successfully", response);
		}
	
}
