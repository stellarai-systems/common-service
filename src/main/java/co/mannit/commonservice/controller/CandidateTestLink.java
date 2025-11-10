package co.mannit.commonservice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.mannit.commonservice.common.Response;
import co.mannit.commonservice.service.CandidateService;
import co.mannit.commonservice.service.SubmissionService;

@RestController
public class CandidateTestLink {

	
	@Autowired
	private CandidateService serv;
	@Autowired
	private SubmissionService service;
	
	@PostMapping("/invite")
	public Response<?> inviteCandidates(@RequestBody Map<String, Object> candidatesPayload) throws Exception {
		
		
		return Response.buildSuccessMsg(200, "Invitation Sent Successfully.",serv.savemongoCreateUnique(candidatesPayload));
		//return "invited";
		
	}
	@GetMapping("/fetchTest/{uniqueTestId}")
	public Response<String> fetchTest(@PathVariable String uniqueTestId) throws Exception {
		return Response.buildSuccessMsg(200, "Collection Value Retrieved Successfully",serv.fetchtest(uniqueTestId));
	//	return  ResponseEntity.ok(serv.fetchtest(uniqueTestId))   ;
	}
	@PostMapping("/score")
	public ResponseEntity<Map<String, Object>> fetch(@RequestParam String id) {

	    // Create a small thread pool (or reuse one)
	    ExecutorService executor = Executors.newFixedThreadPool(5);

	    // Submit the calculation task
	    executor.submit(() -> {
	        serv.calculateScoreAsync(id); // run in background
	    });

	    // Immediately return
	    Map<String, Object> resp = new HashMap<>();
	    resp.put("status", "submitted");
	    resp.put("message", "Score calculation is running in the background.");
	    return ResponseEntity.accepted().body(resp);
	}
	@GetMapping("/getd")
	public Map<String ,Object>returnk(@RequestParam int day){
		return service.getDashboardAnalytics(day);
	}
	@GetMapping("/getD")
	public Map<String,Object>returnk(){
		return service.getDo();
	}
}
