package co.mannit.commonservice.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.StringOperators;
import org.springframework.data.mongodb.core.aggregation.StringOperators.Substr;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import co.mannit.commonservice.repository.TestRepository;

@Service
public class SubmissionService {

	    @Autowired
	    private TestRepository submissionRepository;
	    
	    @Autowired
	    private MongoTemplate mongoTemplate;
	    private static final String COLLECTION_NAME = "QUXCQVLBTKHVQI1HBGJHEWFUAHV_Result";

		/*
		 * public Map<String, Object> getDashboardAnalytics() { List<Map<String,
		 * Object>> allSubs =
		 * submissionRepository.findAll("QUXCQVLBTKHVQI1HBGJHEWFUAHV_Result");
		 * 
		 * Map<String, Object> result = new LinkedHashMap<>();
		 * 
		 * result.put("monthlyAssessmentActivity", getMonthlyActivity(allSubs));
		 * System.out.println("go success monthly");
		 * result.put("performanceDistribution", getPerformanceDistribution(allSubs));
		 * System.out.println("go success performanceDistrib");
		 * result.put("testPerformanceOverview", getTestover());
		 * System.out.println("go success performanceDistribut");
		 * result.put("dailyActivityTrends", getDailyActivityTrends());
		 * System.out.println("go success performanceDistributeeeee");
		 * result.put("monthlyPerformance", getMonthlyPerformance());
		 * System.out.println("go success performanceDistributeeeee5");
		 * result.put("trendAnalysis", getTrendAnalysis()); return result; }
		 */
	    public Map<String, Object> getDashboardAnalytics(int days) {
	        Map<String, Object> result = new LinkedHashMap<>();

	        try {
	            // Fetch all submissions within the selected period dynamically
	            LocalDateTime start = LocalDateTime.now().minusDays(days);
	            Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());

	            List<Map<String, Object>> allSubs = submissionRepository.findAllSince(COLLECTION_NAME, startDate);

	            result.put("monthlyAssessmentActivity", getMonthlyActivity(allSubs));
	           
	            result.put("performanceDistribution", getPerformanceDistribution(allSubs));
	           
	            result.put("testPerformanceOverview", getTestoverWithinDays(days));
	           

	            result.put("dailyActivityTrends", getDailyActivityTrendsWithinDays(days));
	            

	            result.put("monthlyPerformance", getMonthlyPerformanceWithinDays(days));
	           
	            result.put("trendAnalysis", getTrendAnalysisWithinDays(days));
	           

	        } catch (Exception e) {
	            e.printStackTrace();
	            System.err.println("Error in getDashboardAnalytics(" + days + "): " + e.getMessage());
	        }

	        return result;
	    }

		/*
		 * private Map<String, Object> getDailyActivityTrends() { Map<String, Object>
		 * dailyTrends = new LinkedHashMap<>();
		 * 
		 * // Aggregation: group by day and count Aggregation agg =
		 * Aggregation.newAggregation(
		 * Aggregation.match(Criteria.where("date").ne(null)), // only include documents
		 * with date Aggregation.project()
		 * .andExpression("dateToString('%Y-%m-%d', date)").as("day"),
		 * Aggregation.group("day").count().as("count"),
		 * Aggregation.sort(Sort.Direction.ASC, "_id") ); AggregationResults<Document>
		 * results = mongoTemplate.aggregate(agg, COLLECTION_NAME, Document.class);
		 * 
		 * // Prepare last 7 days with default 0 for (int i = 6; i >= 0; i--) {
		 * LocalDate date = LocalDate.now().minusDays(i);
		 * dailyTrends.put(date.getDayOfWeek().name().substring(0, 3), 0); // Mon, Tue,
		 * ... }
		 * 
		 * for (Document doc : results.getMappedResults()) { System.out.println(doc);
		 * String day = doc.getString("_id"); int count = doc.getInteger("count", 0);
		 * LocalDate d = LocalDate.parse(day); String label =
		 * d.getDayOfWeek().name().substring(0, 3); dailyTrends.put(label, count); }
		 * 
		 * return dailyTrends; }
		 * 
		 * 
		 * private Map<String, Object> getTrendAnalysis() { Map<String, Object> trend =
		 * new LinkedHashMap<>();
		 * 
		 * LocalDateTime now = LocalDateTime.now(); LocalDateTime startCurrentWeek =
		 * now.minusDays(7); LocalDateTime startPreviousWeek = now.minusDays(14);
		 * 
		 * Date currentStart =
		 * Date.from(startCurrentWeek.atZone(ZoneId.systemDefault()).toInstant()); Date
		 * previousStart =
		 * Date.from(startPreviousWeek.atZone(ZoneId.systemDefault()).toInstant());
		 * 
		 * // Count total submissions long currentSubmissions = mongoTemplate.count( new
		 * Query(Criteria.where("date").gte(currentStart)), COLLECTION_NAME); long
		 * previousSubmissions = mongoTemplate.count( new
		 * Query(Criteria.where("date").gte(previousStart).lt(currentStart)),
		 * COLLECTION_NAME);
		 * 
		 * // Average scores double currentAvg = getAverageScore(currentStart, new
		 * Date()); double prevAvg = getAverageScore(previousStart, currentStart);
		 * 
		 * // Unique candidates long currentCandidates = mongoTemplate.aggregate(
		 * Aggregation.newAggregation(
		 * Aggregation.match(Criteria.where("date").gte(currentStart)),
		 * Aggregation.group("candidateId") ), COLLECTION_NAME, Document.class
		 * ).getMappedResults().size();
		 * 
		 * long prevCandidates = mongoTemplate.aggregate( Aggregation.newAggregation(
		 * Aggregation.match(Criteria.where("date").gte(previousStart).lt(currentStart))
		 * , Aggregation.group("candidateId") ), COLLECTION_NAME, Document.class
		 * ).getMappedResults().size();
		 * 
		 * // Calculate % trend.put("Assessment Growth",
		 * getPercentageChange(previousSubmissions, currentSubmissions));
		 * trend.put("Score Improvement", getPercentageChange(prevAvg, currentAvg));
		 * trend.put("Candidates Engagement", getPercentageChange(prevCandidates,
		 * currentCandidates));
		 * 
		 * return trend; }
		 */

	    private double getAverageScore(Date start, Date end) {
	        Aggregation agg = Aggregation.newAggregation(
	            Aggregation.match(Criteria.where("date").gte(start).lt(end)),
	            Aggregation.group().avg("score").as("avgScore")
	        );

	        AggregationResults<Document> res = mongoTemplate.aggregate(agg, COLLECTION_NAME, Document.class);
	        if (res.getUniqueMappedResult() != null)
	            return res.getUniqueMappedResult().getDouble("avgScore");
	        return 0.0;
	    }

	    private double getPercentageChange(double previous, double current) {
	        if (previous == 0) return current > 0 ? 100 : 0;
	        return Math.round(((current - previous) / previous) * 1000.0) / 10.0; // e.g., 8.2%
	    }


	    
	    private Map<String, Integer> getMonthlyActivity(List<Map<String, Object>> subs) {
	        Map<String, Integer> monthly = new LinkedHashMap<>();
	        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	        for (Map<String, Object> s : subs) {
	        	 String dateStr = (String) s.get("submissionDateTime");
	            if (dateStr == null) continue;

	            LocalDateTime dateTime = LocalDateTime.parse(dateStr, fmt);
	            LocalDate date = dateTime.toLocalDate();
	            String month = date.getMonth().name().substring(0, 3); 
	            monthly.put(month, monthly.getOrDefault(month, 0) + 1);
	        }
	        return monthly;
	    }


	    private Map<String, Double> getPerformanceDistribution(List<Map<String, Object>> subs) {
	    	
	        long total = subs.size();
	        long below = subs.stream().filter(s -> getInt(s.get("score")) < 70).count();
	        long average = subs.stream().filter(s -> {
	            int sc = getInt(s.get("score"));
	            return sc >= 70 && sc <= 79;
	        }).count();
	        long good = subs.stream().filter(s -> {
	            int sc = getInt(s.get("score"));
	            return sc >= 80 && sc <= 89;
	        }).count();
	        long excellent = subs.stream().filter(s -> getInt(s.get("score")) >= 90).count();

	        Map<String, Double> distribution = new LinkedHashMap<>();
	        if (total > 0) {
	            distribution.put("Below Average (<70%)", below * 100.0 / total);
	            distribution.put("Average (70-79%)", average * 100.0 / total);
	            distribution.put("Good (80-89%)", good * 100.0 / total);
	            distribution.put("Excellent (90-100%)", excellent * 100.0 / total);
	        }
	        return distribution;
	    }
	    private List<Map<String,Object>> getTestover() {
	        AggregationOperation unwindStage = Aggregation.unwind("testHistory");

	        // Project stage as raw Documenst (since $replaceOne + $toInt not supported by builder)
	        AggregationOperation projectStage = context -> new Document("$project",
	            new Document("testName", "$testHistory.testName")
	                .append("scoreNum",
	                    new Document("$toInt",
	                        new Document("$replaceOne",
	                            new Document("input", "$testHistory.score")
	                                .append("find", "%")
	                                .append("replacement", "")
	                        )
	                    )
	                )
	        );

	        AggregationOperation groupStage = Aggregation.group("testName")
	            .count().as("Participants")
	            .avg("scoreNum").as("AverageScore");

	        AggregationOperation addFieldsStage = Aggregation.addFields()
	            .addField("Difficulty")
	            .withValue(ArithmeticOperators.Subtract.valueOf(100).subtract("AverageScore"))
	            .addField("CompletionRate")
	            .withValue(100)
	            .build();

	        AggregationOperation finalProject = Aggregation.project()
	            .and("_id").as("testName")
	            .andInclude("Participants", "AverageScore", "Difficulty", "CompletionRate");

	        Aggregation aggregation = Aggregation.newAggregation(
	            unwindStage,
	            projectStage,
	            groupStage,
	            addFieldsStage,
	            finalProject
	        );

	        AggregationResults<Document> results = mongoTemplate.aggregate(
	            aggregation,
	            "QUXCQVLBTKHVQI1HBGJHEWFUAHV_Result",
	            Document.class
	        );
            List<Map<String,Object>>mop = results.getMappedResults().stream().map(doc->new HashMap<>(doc)).collect(Collectors.toList());
	        return mop;
	    }
	   

	    /**
	     * Returns a map of month → average score
	     */
	    public Map<String, Object> getMonthlyPerformance() {
	        Map<String, Object> monthlyPerformance = new LinkedHashMap<>();

	        Aggregation agg = Aggregation.newAggregation(
	        	    Aggregation.match(Criteria.where("date").ne(null)), // skip null dates
	        	    Aggregation.project("score")
	        	        .andExpression("month(date)").as("month")
	        	        .andExpression("year(date)").as("year"),
	        	    Aggregation.group("year", "month").avg("score").as("avgScore"),
	        	    Aggregation.sort(Sort.Direction.ASC, "_id.year", "_id.month")
	        	);


	        AggregationResults<Document> results = mongoTemplate.aggregate(agg, COLLECTION_NAME, Document.class);

	        String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
	                            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	        for (Document doc : results.getMappedResults()) {
	            Document idDoc = (Document) doc.get("_id");
	            int month = idDoc.getInteger("month");
	            double avg = doc.getDouble("avgScore");
	            monthlyPerformance.put(months[month - 1], Math.round(avg * 100.0) / 100.0);
	        }

	        return monthlyPerformance;
	    }

	   

	    /**
	     * Utility: Check if a given date has expired (based on current UTC time)
	     */
	    public boolean isDateExpired(Date targetDate) {
	        if (targetDate == null) return false;
	        return targetDate.toInstant().isBefore(LocalDateTime.now().toInstant(ZoneOffset.UTC));
	    }


	

	    private int getInt(Object obj) {
	        if (obj == null) return 0;
	        if (obj instanceof Integer) return (Integer) obj;
	        if (obj instanceof String) return Integer.parseInt((String) obj);
	        return 0;
	    }

	    private List<Map<String, Object>> getTestOverview(List<Map<String, Object>> subs) {
	        // Group by testName inside personalInformation
	        Map<String, List<Map<String, Object>>> grouped = subs.stream()
	            .collect(Collectors.groupingBy(s -> {
	                Map<String, Object> info = (Map<String, Object>) s.get("testHistory");
	                return info != null ? (String) info.get("testName") : "Unknown";
	            }));

	        List<Map<String, Object>> overview = new ArrayList<>();

	        for (String testName : grouped.keySet()) {
	            List<Map<String, Object>> testSubs = grouped.get(testName);

	            int participants = (int) testSubs.stream()
	                .map(s -> ((Map<String, Object>) s.get("personalInformation")).get("email"))
	                .filter(Objects::nonNull)
	                .distinct()
	                .count();

	            double avgScore = testSubs.stream()
	                .mapToInt(s -> getInt(s.get("score")))
	                .average().orElse(0);

	            double completionRate = testSubs.stream()
	                .mapToDouble(s -> {
	                    List<Map<String, Object>> answers = (List<Map<String, Object>>) s.get("answers");
	                    if (answers == null || answers.isEmpty()) return 0;
	                    long answered = answers.stream().filter(a -> a.get("Selopt") != null).count();
	                    return (answered * 100.0) / answers.size();
	                })
	                .average().orElse(0);

				/*
				 * String difficulty; if (avgScore >= 90) difficulty = "Easy"; else if (avgScore
				 * >= 75) difficulty = "Medium"; else difficulty = "Hard";
				 */

	            Map<String, Object> row = new LinkedHashMap<>();
	            row.put("testName", testName);
	            row.put("participants", participants);
	            row.put("averageScore", String.format("%.0f%%", avgScore));
	            row.put("completionRate", String.format("%.0f%%", completionRate));
	           // row.put("difficulty", difficulty);

	            overview.add(row);
	        }

	        return overview;
	    }
	    private Map<String, Integer> getMonthlyActivity2(List<Map<String, Object>> subs) {
	        Map<String, Integer> monthly = new LinkedHashMap<>();
	        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	        for (Map<String, Object> s : subs) {
	            String dateStr = (String) s.get("submissionDateTime"); // you will store date here
	            System.out.println(dateStr);
	            if (dateStr == null) continue;

	            LocalDate date = LocalDate.parse(dateStr, fmt);
	            String month = date.getMonth().name().substring(0, 3); // JAN, FEB ...
	            monthly.put(month, monthly.getOrDefault(month, 0) + 1);
	        }
	        return monthly;
	    }
	    private Map<String, Object> getDailyActivityTrendsWithinDays(int days) {
	        Map<String, Object> dailyTrends = new LinkedHashMap<>();
	        LocalDateTime start = LocalDateTime.now().minusDays(days);
	        Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());

	        Aggregation agg = Aggregation.newAggregation(
	                Aggregation.match(Criteria.where("date").gte(startDate)),
	                Aggregation.project()
	                        .andExpression("dateToString('%Y-%m-%d', date)").as("day"),
	                Aggregation.group("day").count().as("count"),
	                Aggregation.sort(Sort.Direction.ASC, "_id")
	        );

	        AggregationResults<Document> results = mongoTemplate.aggregate(agg, COLLECTION_NAME, Document.class);

	        // Pre-fill period days
	        for (int i = days - 1; i >= 0; i--) {
	            LocalDate date = LocalDate.now().minusDays(i);
	            dailyTrends.put(date.getDayOfWeek().name().substring(0, 3), 0);
	        }

	        for (Document doc : results.getMappedResults()) {
	            String day = doc.getString("_id");
	            if (day == null) continue;
	            try {
	                LocalDate d = LocalDate.parse(day);
	                String label = d.getDayOfWeek().name().substring(0, 3);
	                int count = doc.getInteger("count", 0);
	                dailyTrends.put(label, count);
	            } catch (Exception ignored) {}
	        }

	        return dailyTrends;
	    }

	    private Map<String, Object> getMonthlyPerformanceWithinDays(int days) {
	        Map<String, Object> monthlyPerformance = new LinkedHashMap<>();
	        LocalDateTime start = LocalDateTime.now().minusDays(days);
	        Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());

	        Aggregation agg = Aggregation.newAggregation(
	                Aggregation.match(Criteria.where("date").gte(startDate)),
	                Aggregation.project("score")
	                        .andExpression("month(date)").as("month")
	                        .andExpression("year(date)").as("year"),
	                Aggregation.group("year", "month").avg("score").as("avgScore"),
	                Aggregation.sort(Sort.Direction.ASC, "_id.year", "_id.month")
	        );

	        AggregationResults<Document> results = mongoTemplate.aggregate(agg, COLLECTION_NAME, Document.class);

	        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
	                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

	        for (Document doc : results.getMappedResults()) {
	            Document idDoc = (Document) doc.get("_id");
	            if (idDoc == null) continue;
	            int month = idDoc.getInteger("month", 0);
	            double avg = doc.getDouble("avgScore") == null ? 0.0 : doc.getDouble("avgScore");
	            if (month >= 1 && month <= 12) {
	                monthlyPerformance.put(months[month - 1], Math.round(avg * 100.0) / 100.0);
	            }
	        }

	        return monthlyPerformance;
	    }

	    private List<Map<String, Object>> getTestoverWithinDays(int days) {
	        LocalDateTime start = LocalDateTime.now().minusDays(days);
	        Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());

	        Aggregation aggregation = Aggregation.newAggregation(
	                Aggregation.match(Criteria.where("date").gte(startDate)),
	                Aggregation.unwind("testHistory"),
	                Aggregation.project()
	                        .and("testHistory.testName").as("testName")
	                        .andExpression("toInt(replaceAll(testHistory.score, '%', ''))").as("scoreNum"),
	                Aggregation.group("testName")
	                        .count().as("Participants")
	                        .avg("scoreNum").as("AverageScore"),
	                Aggregation.addFields()
	                        .addField("Difficulty")
	                        .withValue(ArithmeticOperators.Subtract.valueOf(100).subtract("AverageScore"))
	                        .addField("CompletionRate")
	                        .withValue(100)
	                        .build(),
	                Aggregation.project()
	                        .and("_id").as("testName")
	                        .andInclude("Participants", "AverageScore", "Difficulty", "CompletionRate")
	        );

	        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, COLLECTION_NAME, Document.class);
	        return results.getMappedResults().stream()
	                .map(doc -> new HashMap<String, Object>(doc))
	                .collect(Collectors.toList());
	    }

	    private Map<String, Object> getTrendAnalysisWithinDays(int days) {
	        // Reuse your existing trend logic but with dynamic window
	        Map<String, Object> trend = new LinkedHashMap<>();
	        LocalDateTime now = LocalDateTime.now();
	        LocalDateTime startCurrent = now.minusDays(days);
	        LocalDateTime startPrevious = now.minusDays(days * 2);

	        Date currentStart = Date.from(startCurrent.atZone(ZoneId.systemDefault()).toInstant());
	        Date previousStart = Date.from(startPrevious.atZone(ZoneId.systemDefault()).toInstant());

	        long currentSubmissions = mongoTemplate.count(
	                new Query(Criteria.where("date").gte(currentStart)), COLLECTION_NAME);
	        long previousSubmissions = mongoTemplate.count(
	                new Query(Criteria.where("date").gte(previousStart).lt(currentStart)), COLLECTION_NAME);

	        double currentAvg = getAverageScore(currentStart, new Date());
	        double prevAvg = getAverageScore(previousStart, currentStart);

	        long currentCandidates = mongoTemplate.aggregate(
	                Aggregation.newAggregation(
	                        Aggregation.match(Criteria.where("date").gte(currentStart)),
	                        Aggregation.group("email")
	                ),
	                COLLECTION_NAME, Document.class
	        ).getMappedResults().size();

	        long prevCandidates = mongoTemplate.aggregate(
	                Aggregation.newAggregation(
	                        Aggregation.match(Criteria.where("date").gte(previousStart).lt(currentStart)),
	                        Aggregation.group("email")
	                ),
	                COLLECTION_NAME, Document.class
	        ).getMappedResults().size();

	        trend.put("Assessment Growth", getPercentageChange(previousSubmissions, currentSubmissions));
	        trend.put("Score Improvement", getPercentageChange(prevAvg, currentAvg));
	        trend.put("Candidates Engagement", getPercentageChange(prevCandidates, currentCandidates));

	        return trend;
	    }

	    public Map<String, Object> getDo() {
	        Map<String, Object> summary = new LinkedHashMap<>();

	        // Calculate date 6 months ago
	        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
	        Date startDate = Date.from(sixMonthsAgo.atZone(ZoneId.systemDefault()).toInstant());

	        // --- 1️⃣ Average Score Across All Assessments (last 6 months) ---
	        Aggregation avgAgg = Aggregation.newAggregation(
	                Aggregation.match(Criteria.where("date").gte(startDate)),
	                Aggregation.group().avg("score").as("avgScore")
	        );

	        AggregationResults<Document> avgResult =
	                mongoTemplate.aggregate(avgAgg, COLLECTION_NAME, Document.class);

	        double avgScore = 0.0;
	        if (avgResult.getUniqueMappedResult() != null) {
	            Object val = avgResult.getUniqueMappedResult().get("avgScore");
	            if (val instanceof Number num) {
	                avgScore = num.doubleValue();
	            }
	        }
	        summary.put("Average Score (Last 6 Months)", Math.round(avgScore * 100.0) / 100.0);

	        // --- 2️⃣ Top Performers (score > 90%, last 6 months) ---
	        Aggregation topAgg = Aggregation.newAggregation(
	                Aggregation.match(new Criteria().andOperator(
	                        Criteria.where("date").gte(startDate),
	                        Criteria.where("score").gt(90)
	                )),
	                Aggregation.project("name", "email", "score", "testHistory.testName"),
	                Aggregation.sort(Sort.Direction.DESC, "score")
	        );

	        List<Document> topDocs = mongoTemplate.aggregate(topAgg, COLLECTION_NAME, Document.class).getMappedResults();
	        List<Map<String, Object>> topPerformers = new ArrayList<>();

	        for (Document doc : topDocs) {
	            Map<String, Object> performer = new LinkedHashMap<>();
	            performer.put("name", doc.getString("name"));
	            performer.put("email", doc.getString("email"));

	            Object scoreVal = doc.get("score");
	            performer.put("score", scoreVal instanceof Number ? ((Number) scoreVal).doubleValue() : 0.0);

	         //   performer.put("testName", doc.getEmbedded(List.of("testHistory", "testName"), String.class));
	            topPerformers.add(performer);
	        }

	        summary.put("Top Performers (Last 6 Months)", topPerformers);

	        // --- 3️⃣ Average Score Across All Tests ---
	        Aggregation avgTestAgg = Aggregation.newAggregation(
	                Aggregation.unwind("testHistory"),
	                Aggregation.project()
	                        .andExpression("toDouble(substr(testHistory.score, 0, strLenBytes(testHistory.score)-1))")
	                        .as("score"),
	                Aggregation.group().avg("score").as("avgTestScore")
	        );

	        AggregationResults<Document> avgTestResult =
	                mongoTemplate.aggregate(avgTestAgg, COLLECTION_NAME, Document.class);

	        double avgTestScore = 0.0;
	        if (avgTestResult.getUniqueMappedResult() != null) {
	            Object val = avgTestResult.getUniqueMappedResult().get("avgTestScore");
	            if (val instanceof Number num) {
	                avgTestScore = num.doubleValue();
	            }
	        }
	        summary.put("Average Score Across All Tests", Math.round(avgTestScore * 100.0) / 100.0);

	        // --- 4️⃣ Completion Rate ---
	        Aggregation assignedAgg = Aggregation.newAggregation(
	                Aggregation.match(Criteria.where("asId").exists(true)), // assigned assessments only
	                Aggregation.group("asId") // unique asIds
	        );
	        long totalAssigned = mongoTemplate.aggregate(assignedAgg, "QUXCQVLBTKHVQI1HBGJHEWFUAHV_Candidates", Document.class)
	                                          .getMappedResults().size();

	        Aggregation completedAgg = Aggregation.newAggregation(
	                Aggregation.match(Criteria.where("Completed").is("completed")),
	                Aggregation.group("asId") // unique completed asIds
	        );
	        long totalCompleted = mongoTemplate.aggregate(completedAgg, COLLECTION_NAME, Document.class)
	                                           .getMappedResults().size();

	        double completionRate = 0.0;
	        if (totalAssigned != 0) {
	            completionRate = ((double) totalCompleted / totalAssigned) * 100.0;
	        }

	        summary.put("completionrate", Math.round(completionRate * 100.0) / 100.0);

	        return summary;
	    }


}
