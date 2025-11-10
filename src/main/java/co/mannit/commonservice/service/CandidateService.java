package co.mannit.commonservice.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.mannit.commonservice.CustomRequestInterceptor;
import co.mannit.commonservice.ServiceCommonException;
import co.mannit.commonservice.po.EmailMessageRequestBody;
import co.mannit.commonservice.pojo.PaginationReqParam;
import co.mannit.commonservice.repository.TestRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CandidateService {

	@Autowired
	private Msgserv mailService;
	@Autowired
	private CollectionService collectionService;
	@Autowired
	private RestTemplate templ;
	@Autowired
	private TestRepository  testRepository;
	@Autowired
	private MongoTemplate template;
	
	/*
	 * public List<String> savemongoCreateUnique(List<Map<String, Object>>
	 * candidatesPayload) throws Exception { List<String> emailsSent =
	 * Collections.synchronizedList(new ArrayList<>());
	 * 
	 * // Get uniqueId only once (not inside loop) String uniqueIdPrefix =
	 * CustomRequestInterceptor.getUniqueId();
	 * 
	 * // Thread pool (tune size based on expected load & infra capacity)
	 * ExecutorService executor = Executors.newFixedThreadPool(10); List<Future<?>>
	 * futures = new ArrayList<>();
	 * 
	 * for (Map<String, Object> candidate : candidatesPayload) {
	 * futures.add(executor.submit(() -> { try { // 1. Generate unique test ID
	 * String uniqueTestId = candidate.get("uniqueTestId") != null ?
	 * candidate.get("uniqueTestId").toString() :
	 * UUID.randomUUID().toString().replace("-", "").substring(0, 12);
	 * 
	 * String link = "https://albayan-assessment.mannit.co/" + uniqueTestId; String
	 * collectionName = uniqueIdPrefix + "_Candidates";
	 * 
	 * // 2. Update candidate document Document doc = new Document("uniqueTestId",
	 * uniqueTestId); collectionService.updateResource(null,
	 * candidate.get("_id").toString(), doc.toJson(), collectionName);
	 * 
	 * // 3. Build personalized message String msg = "Hello " +
	 * candidate.get("name") + ",\n\n" +
	 * "You have been invited to complete your assessment.\n" +
	 * "Please use the following unique link to access your test:\n\n" + link +
	 * "\n\n" + "Make sure to complete the assessment by the given deadline.\n\n" +
	 * "If you have any questions, feel free to contact us at support@mannit.co.\n\n"
	 * + "Best regards,\n" + "Mannit Team";
	 * 
	 * // 4. Send email EmailMessageRequestBody body = new
	 * EmailMessageRequestBody(); body.setMsg(msg);
	 * body.setFromEmail("contact@mannit.co");
	 * body.setToEmail(Collections.singletonList(candidate.get("email").toString()))
	 * ; mailService.sendMessageEmail(body);
	 * 
	 * // 5. Track sent emails emailsSent.add(candidate.get("email").toString());
	 * 
	 * } catch (Exception e) { System.err.println("Failed for candidate: " +
	 * candidate + " - " + e.getMessage()); e.printStackTrace(); } })); }
	 * 
	 * // Wait for all tasks to complete for (Future<?> f : futures) { try {
	 * f.get(); // block until each finishes } catch (Exception e) {
	 * e.printStackTrace(); } }
	 * 
	 * executor.shutdown(); return emailsSent; }
	 */
	
	public List<String> savemongoCreateUnique(Map<String, Object> candidatesPayload) throws Exception {
		List<String> emails2 = new ArrayList<>();
        List<Map<String,Object>> candidatePayload2 = (List<Map<String,Object>>)candidatesPayload.get("candidates");
        String message =candidatesPayload.get("message").toString();
        String subject =candidatesPayload.get("subject").toString();
		for (Map<String, Object> candidate : candidatePayload2) {
			String uniqueTestId = candidate.get("uniqueTestId") != null ? candidate.get("uniqueTestId").toString()
					: UUID.randomUUID().toString().replace("-", "").substring(0, 12);
			String link = "https://albayan-assessment.mannit.co/" + uniqueTestId;
			String name = CustomRequestInterceptor.getUniqueId() + "_Candidates";
			Document doc = new Document();
			
			doc.append("uniqueTestId", uniqueTestId);
			collectionService.updateResource(null, candidate.get("_id").toString(), doc.toJson(), name);
			List<String> emails = new ArrayList<>();
			emails.add(candidate.get("email").toString());
			EmailMessageRequestBody body = new EmailMessageRequestBody();
			body.setMsg(message+"\n\nYour test link: " + link);
			body.setFromEmail("contact@mannit.co");
			body.setToEmail(emails);
			body.setSubject(subject);//"Your Al-Bayan Assessment Test Link – Valid for 24 Hours"
			try {
				mailService.sendMessageEmail(body);	
			}catch(Exception e) {
			
				System.out.println("couldn't send the email");
				throw new ServiceCommonException("500");
			}
			
			emails2.add(candidate.get("email").toString());

		}
		return emails2;
	}

	public String fetchtest(String uniqueTestId) throws Exception {
	    String candidatesCollection = CustomRequestInterceptor.getUniqueId() + "_Candidates";
	    String testLibraryCollection = CustomRequestInterceptor.getUniqueId() + "_TestLibrary";
          
	    // 1. Find candidate by uniqueTestId
	    Map<String, String> mpo = new HashMap<>();
	    mpo.put("filtercount", "1");
	    mpo.put("f1_field", "uniqueTestId_S");
	    mpo.put("f1_op", "eq");
	    mpo.put("f1_value", uniqueTestId);
	    mpo.put("ColName", candidatesCollection);
Document doc = new Document();
	    PaginationReqParam paginationReq = new PaginationReqParam();
	    List<String> candidateJsonList = collectionService.retrieveCollection(paginationReq, mpo, new ArrayList<>());

	    if (candidateJsonList == null || candidateJsonList.isEmpty()) {
	        return "{\"error\": \"Candidate not found for uniqueTestId: " + uniqueTestId + "\"}";
	    }


	    Document candidateDoc = Document.parse(candidateJsonList.get(0));
	    List<Document> assignedTests = (List<Document>) candidateDoc.get("asnT");
	    String email = candidateDoc.getString("email");
	    String assesmentname=  candidateDoc.getString("assT");
	    String assId =candidateDoc.getString("asId");
	    
	    if(isCompleted(email,assesmentname,assId)) {
	    	doc.append("error", "Test is already completed");
	    	doc.append("preferredLanguage", candidateDoc.get("preferredLanguage"));
	    	return doc.toJson();
	    }
	    //2. Collect all test responses
	    List<String> responseTests = new ArrayList<>();
	    
	    Date expiryDate = (Date) candidateDoc.get("expiryDate");
	    boolean isExpired = Instant.now().isAfter(expiryDate.toInstant());
	    if(isExpired) {
	    	doc.append("error", "The Test Is Expired");
	    	doc.append("preferredLanguage", candidateDoc.get("preferredLanguage"));
	    	return  doc.toJson();
	    }
	   
	   
	    for (Document assigned : assignedTests) {
	    	int testdur = (int)assigned.get("dur");
	        String tid = assigned.getString("tid");
	      
	      
	        List<String> qns = (List<String>) assigned.get("qns");

	        // 3. Fetch testLibrary entry
	        Map<String, String> mpo2 = new HashMap<>();
	        mpo2.put("filtercount", "1");
	        mpo2.put("f1_field", "tid_S");
	        mpo2.put("f1_op", "eq");
	        mpo2.put("f1_value", tid);
	        mpo2.put("ColName", testLibraryCollection);

	        List<String> testJsonList = collectionService.retrieveCollection(paginationReq, mpo2, new ArrayList<>());
	        if (testJsonList.isEmpty()) continue;

	        Document testDoc = Document.parse(testJsonList.get(0));
	        int minqu =(int)testDoc.getOrDefault("minQuestions",0);
	        List<Document> allQs = (List<Document>) testDoc.get("qs");
          
	        // 4. Filter only candidate's questions
	        List<Document> filteredQs = allQs.stream()
	                .filter(q -> qns.contains(q.getString("id")))
	                .map(q -> {
	                    q.remove("ans");   // hide correct answers
	                    q.remove("notes"); // hide notes
	                    return q;
	                })
	                .collect(Collectors.toList());
	        // 5. Build response
	        Document responseTest = new Document();
	        responseTest.put("tid", tid);
	        responseTest.put("title", testDoc.getString("title"));
	        responseTest.put("qs", filteredQs);
	        responseTest.put("dur", testdur);
	        responseTest.put("minQuestions", minqu);
	        responseTests.add(responseTest.toJson());
	    }
	    Document candidateSummary = new Document();
	    candidateSummary.put("name", candidateDoc.getString("name"));
	    candidateSummary.put("email", candidateDoc.getString("email"));
	    candidateSummary.put("phone", candidateDoc.getString("phone"));
	    candidateSummary.put("skills", candidateDoc.get("skills")); // if it's a list/array
	    candidateSummary.put("Tdur", candidateDoc.get("Tdur"));
	    candidateSummary.put("assT", candidateDoc.get("assT"));
	    candidateSummary.put("expiryDate", candidateDoc.get("expiryDate"));
	    candidateSummary.put("asId", assId);
	    candidateSummary.put("uniqueId", uniqueTestId);
	    candidateSummary.put("preferredLanguage",candidateDoc.get("preferredLanguage"));
	    
	    responseTests.add(candidateSummary.toJson());
	    return responseTests.toString();
	}
	public boolean isCompleted(String email, String assesmentname, String assId) {
		// TODO Auto-generated method stub
		
		Query qu =new Query();
		qu.addCriteria(Criteria.where("email").is(email).and("asId").is(assId).and("assT").is(assesmentname));
		return template.exists(qu,Map.class, "QUXCQVLBTKHVQI1HBGJHEWFUAHV_Result") ;
	}

	public Map<String, Object> calculateScoreAsync(String id) {
		Map<String, Object> submission=testRepository.getsubmissionByid(id);
		submission.remove("_id");
	    List<Map<String, Object>> answers = (List<Map<String, Object>>) submission.getOrDefault("answers", Collections.emptyList());
	    int overallScore = 0;
	    Map<String, Map<String, Object>> testAggregation = new HashMap<>();
	    Map<String, Integer> discTraits = new HashMap<>();
	    discTraits.put("Dominance", 0);
	    discTraits.put("Influence", 0);
	    discTraits.put("Steadiness", 0);
	    discTraits.put("Conscientiousness", 0);

	    Map<String, Integer> techScores = new HashMap<>();
	    Map<String, Integer> techMaxScores = new HashMap<>();
	    Map<String, Integer> testTimeSpent = new HashMap<>();

	    for (Map<String, Object> answer : answers) {
	        String tid = (String) answer.get("Tid");
	        String qid = (String) answer.get("QID");
	        String selOpt = (String) answer.get("Selopt");
	        int timeSpent = ((Number) answer.getOrDefault("TimeTaken", 0)).intValue();

	        if (tid == null || qid == null || selOpt == null) continue;

	        // Fetch the test dynamically
	        Map<String, Object> test = testRepository.getTestByTid(tid);
	        if (test == null) continue;

	        List<Map<String, Object>> questions = (List<Map<String, Object>>) test.getOrDefault("qs", Collections.emptyList());
	        Map<String, Object> question = questions.stream()
	                .filter(q -> qid.equals(q.get("id")))
	                .findFirst()
	                .orElse(null);
	        if (question == null) continue;

	        String type = String.valueOf(answer.get("type")).toLowerCase();
	        int questionScore = (int) question.getOrDefault("score", 0);
	        int scoreToAdd = 0;

	        switch (type) {
	            case "singleselect":
	            case "true/false":
	            case "yes/no":
	            case "fillup":
	                if (String.valueOf(question.get("ans")).equalsIgnoreCase(selOpt)) scoreToAdd = questionScore;
	                break;

	            case "multipleselect":
	                String correctAns = String.valueOf(question.get("ans")).trim();
	                Set<String> correctSet = Arrays.stream(correctAns.split(",")).map(String::trim).collect(Collectors.toSet());
	                Set<String> candidateSet = Arrays.stream(selOpt.split(",")).map(String::trim).collect(Collectors.toSet());
	                boolean hasWrong = candidateSet.stream().anyMatch(opt -> !correctSet.contains(opt));
	                if (!hasWrong) {
	                    int correctCount = (int) candidateSet.stream().filter(correctSet::contains).count();
	                    scoreToAdd = (int) Math.round((double) correctCount / correctSet.size() * questionScore);
	                }
	                break;

	            case "essay":
	                try {
	                    Map<String, Object> payload = new HashMap<>();
	                    payload.put("CORRECTANS", question.get("ans"));
	                    payload.put("GIVENANS", selOpt);
	                    ResponseEntity<Map> response = templ.exchange(
	                            "https://uncongruous-genevive-nonpantheistically.ngrok-free.dev/calculate-score",
	                            HttpMethod.POST,
	                            new HttpEntity<>(payload),
	                            Map.class
	                    );
	                    Map<String, Object> essayResult = response.getBody();
	                    if (essayResult != null) {
	                        int candidateScore = (int) essayResult.get("candidate_score"); // 0–100
	                        scoreToAdd = (int) Math.round((candidateScore / 100.0) * questionScore);
	                    }
	                } catch (Exception e) {
	                    // Essay scoring failed
	                }
	                break;

	            case "coding":
	            case "image":
	                if (String.valueOf(question.get("ans")).equalsIgnoreCase(selOpt)) scoreToAdd = questionScore;
	                break;

	            case "disc":
	                Map<String, Object> opts = (Map<String, Object>) question.get("opts");

	                if (opts != null) {
	                    // Normalize the submitted option to match the key format in opts
	                    String normalizedSelOpt = selOpt.replaceAll("\\s+", ""); // "Option 1" -> "Option1"

	                    if (opts.containsKey(normalizedSelOpt)) {
	                        Map<String, Object> selectedTrait = (Map<String, Object>) opts.get(normalizedSelOpt);
	                        String trait = (String) selectedTrait.get("trait");
	                        System.out.println(trait + "  trait ");
	                        discTraits.put(trait, discTraits.getOrDefault(trait, 0) + 1);
	                    }
	                }
	                break;
	        }

	        overallScore += scoreToAdd;

	        // Aggregate test-level scores
	        Map<String, Object> testEntry = testAggregation.computeIfAbsent(tid, k -> {
	            Map<String, Object> m = new HashMap<>();
	            m.put("testName", test.get("title"));
	            m.put("date", test.getOrDefault("date", submission.getOrDefault("submissionDateTime", new Date().toString())));
	            m.put("totalScore", 0);
	            m.put("maxScore", 0);
	            return m;
	        });
	        testEntry.put("totalScore", (int) testEntry.get("totalScore") + scoreToAdd);
	        testEntry.put("maxScore", (int) testEntry.get("maxScore") + questionScore);

	        // Accumulate time per test
	        testTimeSpent.merge(tid, timeSpent, Integer::sum);

	        // Aggregate skill/tech scores
	        List<String> skills = (List<String>) answer.getOrDefault("Skills", Collections.emptyList());
	        for (String skill : skills) {
	            techScores.merge(skill, scoreToAdd, Integer::sum);
	            techMaxScores.merge(skill, questionScore, Integer::sum);
	        }
	    }

	    // Build final testHistory
	    List<Map<String, Object>> testHistory = new ArrayList<>();
	    int testsAttempted = 0;
	    int testsPassed = 0;
	    int totalTimeSpent = 0;

	    for (Map.Entry<String, Map<String, Object>> entrySet : testAggregation.entrySet()) {
	        Map<String, Object> agg = entrySet.getValue();
	        int total = (int) agg.get("totalScore");
	        int max = (int) agg.get("maxScore");
	        int percent = (max == 0) ? 0 : (int) Math.round((total * 100.0) / max);

	        Map<String, Object> entry = new HashMap<>();
	        entry.put("testName", agg.get("testName"));
	        entry.put("date", agg.get("date"));
	        entry.put("score", percent + "%");
	        testHistory.add(entry);

	        testsAttempted++;
	        if (percent >= 50) testsPassed++;

	        String tid = entrySet.getKey();
	        totalTimeSpent += testTimeSpent.getOrDefault(tid, 0);
	    }

	    int avgTimeSec = (testsAttempted == 0) ? 0 : totalTimeSpent / testsAttempted;
	    String avgTimeStr = (avgTimeSec / 60) + " min " + (avgTimeSec % 60) + " sec";

	    // Build personal info
	    Map<String, Object> personalInfo = new HashMap<>();
	    personalInfo.put("name", submission.getOrDefault("name", "Unknown"));
	    personalInfo.put("email", submission.getOrDefault("email", "Unknown"));
	    personalInfo.put("phone", submission.getOrDefault("phone", "Unknown"));
	    personalInfo.put("registrationDate", submission.getOrDefault("submissionDateTime", "Unknown"));

	    // Build tech % scores
	    Map<String, Integer> techPercentage = new HashMap<>();
	    for (String skill : techScores.keySet()) {
	        int score = techScores.get(skill);
	        int max = techMaxScores.getOrDefault(skill, 0);
	        int percent = (max == 0) ? 0 : (int) Math.round((score * 100.0) / max);
	        techPercentage.put(skill.toLowerCase(), percent);
	    }

	    // Build performance summary
	    int maxOverallScore = testAggregation.values().stream().mapToInt(m -> (int) m.get("maxScore")).sum();
	    Map<String, Object> performanceSummary = new HashMap<>();
	    performanceSummary.put("overallScore", (maxOverallScore == 0) ? 0 :
	            (int) Math.round((overallScore * 100.0) / maxOverallScore));
	    performanceSummary.put("testsAttempted", testsAttempted);
	    performanceSummary.put("testsPassed", testsPassed);
	    performanceSummary.put("averageTimeSpent", avgTimeStr);
	    performanceSummary.put("disc", discTraits);
	    performanceSummary.put("tech", techPercentage);

	    // Save into submission
	    submission.put("score", performanceSummary.get("overallScore"));
	    submission.put("personalInformation", personalInfo);
	    submission.put("testHistory", testHistory);
	    submission.put("performanceSummary", performanceSummary);
	  //  submission.put("date", isoDate);

	    // Persist in Mongo
	    try {
	        ObjectMapper objectMapper = new ObjectMapper();
	        String jsonToUpdate = objectMapper.writeValueAsString(submission);
	        collectionService.updateResource(null, id, jsonToUpdate,
	                "QUXCQVLBTKHVQI1HBGJHEWFUAHV_Result");
	        Query q = new Query();
	        q.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
	        Update update = new Update().set("date", new Date());

	        template.findAndModify(q, update, Document.class,"QUXCQVLBTKHVQI1HBGJHEWFUAHV_Result");
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to update submission", e);
	    }
	    
	    //{
	  //  "email": "c.logesh@gmail.com",
	  //  "api_headers": {
	  //   "Content-Type": "application/json",
	  //   "xxxid":"h"
	  // },
	  // "folder": "binary_pdf"
	// }
	    
	/*
	 * Map<String, Object> payload = new HashMap<>(); Map<String, Object> payload2 =
	 * new HashMap<>(); payload2.put("Content-Type", payload2);
	 * payload2.put("xxxid", "a"); payload.put("email", submission.get("email"));
	 * payload.put("api_headers", payload2);
	 * System.out.println("......sending request......"); ResponseEntity<Map>
	 * response = templ.exchange(
	 * "https://uncongruous-genevive-nonpantheistically.ngrok-free.dev/fetch_and_compute",
	 * HttpMethod.POST, new HttpEntity<>(payload), Map.class );
	 * System.out.println(response);
	 */
	    return submission;
	}

		
	}


