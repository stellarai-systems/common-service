package co.mannit.commonservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/report")
public class ReportController {

	@Autowired
	private MongoTemplate template;
	/*
	 * @GetMapping("/generate/web") public ResponseEntity<?> generate(@RequestParam
	 * String question) { try { // Decode question parameter question =
	 * URLDecoder.decode(question, StandardCharsets.UTF_8);
	 * System.out.println("Decoded question: " + question);
	 * 
	 * // Step 1: Call external API RestTemplate restTemplate = new RestTemplate();
	 * String url = "https://rta-rag-ounk.onrender.com/ask";
	 * 
	 * HttpHeaders headers = new HttpHeaders();
	 * headers.setContentType(MediaType.APPLICATION_JSON);
	 * headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
	 * 
	 * Map<String, String> bodyMap = new HashMap<>(); bodyMap.put("question",
	 * question);
	 * 
	 * HttpEntity<Map<String, String>> request = new HttpEntity<>(bodyMap, headers);
	 * 
	 * // Fetch API response as raw String String responseString =
	 * restTemplate.postForObject(url, request, String.class);
	 * System.out.println("Raw API response: " + responseString);
	 * 
	 * // Parse JSON to extract `graph` ObjectMapper mapper = new ObjectMapper();
	 * JsonNode rootNode = mapper.readTree(responseString); JsonNode graphNode =
	 * rootNode.get("graph"); System.out.println(rootNode.get("summary")); if
	 * (graphNode == null || graphNode.isNull()) { JsonNode summaryNode=
	 * rootNode.get("summary"); String summaryText = summaryNode.asText();
	 * 
	 * // Build response in desired format Map<String, String> dataMap =
	 * Map.of("body_text", summaryText); Map<String, Object> responseMap =
	 * Map.of("data", dataMap);
	 * 
	 * return ResponseEntity.ok(responseMap); }
	 * 
	 * // Step 2: Decode Base64 image String base64 = graphNode.asText(); String[]
	 * parts = base64.split(","); if (parts.length < 2) { return
	 * ResponseEntity.status(HttpStatus.BAD_GATEWAY)
	 * .body("Invalid image data received from API"); }
	 * 
	 * byte[] imageBytes = Base64.getDecoder().decode(parts[1]);
	 * 
	 * // Step 3: Generate PDF String fileName = "report1.pdf"; String savePath =
	 * "C:/Users/biswa/" + fileName;
	 * 
	 * try (FileOutputStream fos = new FileOutputStream(savePath)) { Document
	 * document = new Document(); PdfWriter.getInstance(document, fos);
	 * document.open();
	 * 
	 * document.add(new Paragraph("Report for: " + question)); document.add(new
	 * Paragraph(" ")); // spacing
	 * 
	 * Image img = Image.getInstance(imageBytes); img.scaleToFit(500, 500);
	 * document.add(img);
	 * 
	 * document.close(); }
	 * 
	 * // Step 4: Return public link inside `data` String publicUrl =
	 * "https://cf1ebb96248f.ngrok-free.app/mannit/Wfiles/" + fileName; return
	 * ResponseEntity.ok(new DataResponse(new Data(publicUrl, "Report for: " +
	 * question)));
	 * 
	 * } catch (Exception e) { e.printStackTrace(); return
	 * ResponseEntity.internalServerError().body("Error generating PDF: " +
	 * e.getMessage()); } }
	 */
	@GetMapping("/success/web")
	public ResponseEntity<?>truefalse(@RequestParam String number){
		 System.out.println("from number"+number);
		Map<String, Object> dataMap = new HashMap<>();
		  Query query = new Query();
	        query.addCriteria(Criteria.where("dis").is(number));

	        // Call external API
	        boolean exist=template.exists(query, "_combocoll");
	        System.out.println(exist);
	        if(exist) {
	        	dataMap.put("success", true);
	        	Map<String, Object> responseMap = Map.of("data", dataMap);
	        	return ResponseEntity.ok(responseMap);
	        }else {
	        	dataMap.put("success", false);
	        	Map<String, Object> responseMap = Map.of("data", dataMap);
	        	return ResponseEntity.ok(responseMap);
	        }
		
	}
	@GetMapping("/generate/web")
	public ResponseEntity<?> generate(@RequestParam String question) {
	    try {
	        // Decode question parameter
	        question = URLDecoder.decode(question, StandardCharsets.UTF_8);
	        System.out.println("Decoded question: " + question);

	        Map<String, Object> headerMap = new HashMap<>();
	        Map<String, Object> bodyMapResponse = new HashMap<>();
	        Map<String, Object> interactiveMap = new HashMap<>();
	        Map<String, Object> dataMap = new HashMap<>();

	        // === External API Call ===
	        RestTemplate restTemplate = new RestTemplate();
	        String url = "http://88.198.107.158:5003/query";

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

	        Map<String, String> bodyMap = Map.of("question", question);
	        HttpEntity<Map<String, String>> request = new HttpEntity<>(bodyMap, headers);
	        String responseString = restTemplate.postForObject(url, request, String.class);
	        System.out.println("Raw API response: " + responseString);

	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode rootNode = mapper.readTree(responseString);

	        JsonNode graphNode = rootNode.get("graph");
	        JsonNode summaryNode = rootNode.get("answer");
	        JsonNode sourcesNode = rootNode.get("sources");

	        String bodyText = "";

	        // === If Image Exists ===
	        if (graphNode != null && !graphNode.isNull()) {
	            String base64 = graphNode.asText();
	            String[] parts = base64.split(",");
	            if (parts.length >= 2) {
	                byte[] imageBytes = Base64.getDecoder().decode(parts[1]);
	                String fileName = "report_image_" + System.currentTimeMillis() + ".jpg";
	                String savePath = "/opt/com_images/" + fileName;

	                try (FileOutputStream fos = new FileOutputStream(savePath)) {
	                    fos.write(imageBytes);
	                }

	                String publicUrl = "https://dev-commonmannit.mannit.co/mannit/Wfiles/" + fileName;

	                // Header → image
	                headerMap.put("type", "image");
	                Map<String, Object> imageMap = new HashMap<>();
	                imageMap.put("link", publicUrl);
	                headerMap.put("image", imageMap);

	                // Body
	                bodyText = summaryNode != null && !summaryNode.isNull()
	                        ? summaryNode.asText()
	                        : "Here is the visual report for your query: " + question;
	            }
	        }
	        // === If No Image, Only Text ===
	        else if (summaryNode != null && !summaryNode.isNull()) {
	            headerMap.put("type", "text");
	            headerMap.put("text", "Summary for your query");
	            bodyText = summaryNode.asText();
	        } else {
	            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
	                    .body("No graph or summary found in API response");
	        }

	        // === Add Sources if any ===
	        if (sourcesNode != null && sourcesNode.isArray() && sourcesNode.size() > 0) {
	            List<String> sourceList = new ArrayList<>();
	            for (JsonNode src : sourcesNode) {
	                String s = src.asText().trim();
	                if (!s.isEmpty() && !s.equals(",")) sourceList.add(s);
	            }
	            if (!sourceList.isEmpty()) {
	                bodyText += "\n\nSources: " + String.join(", ", sourceList);
	            }
	        }

	        // === Append Question ===
	        bodyText = "Question: " + question + "\n\n" + bodyText;

	        // === Clean up unsafe markdown for WhatsApp ===
	        bodyText = sanitizeForWhatsApp(bodyText);

	        // === Truncate if too long (WhatsApp limit ~1024 chars for interactive body) ===
	        if (bodyText.length() > 1000) {
	            bodyText = bodyText.substring(0, 980) + "...";
	        }

	        // === Append footer instruction ===
	        bodyText += "\n\nReply 0 to go back to main menu";

	        bodyMapResponse.put("text", bodyText);

	        // === Feedback Buttons ===
	        Map<String, Object> button1 = Map.of(
	                "type", "reply",
	                "reply", Map.of(
	                        "id", "BTN_SATISFIED",
	                        "title", "✅ Satisfied"
	                )
	        );

	        Map<String, Object> button2 = Map.of(
	                "type", "reply",
	                "reply", Map.of(
	                        "id", "BTN_NOT_SATISFIED",
	                        "title", "❌ Not Satisfied"
	                )
	        );

	        interactiveMap.put("type", "button");
	        interactiveMap.put("header", headerMap);
	        interactiveMap.put("body", bodyMapResponse);
	        interactiveMap.put("footer", Map.of("text", "Powered by Mannit"));
	        interactiveMap.put("action", Map.of("buttons", List.of(button1, button2)));

	        dataMap.put("interactive", interactiveMap);
	        Map<String, Object> responseMap = Map.of("data", dataMap);

	        return ResponseEntity.ok(responseMap);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.internalServerError().body("Error generating response: " + e.getMessage());
	    }
	}

	// --- Utility: Sanitize for WhatsApp ---
	private String sanitizeForWhatsApp(String text) {
	    if (text == null) return "";
	    return text.replaceAll("\\*", "")       // remove bold markers
	               .replaceAll("_", "")         // remove underscores
	               .replaceAll("#+", "")        // remove markdown headers
	               .replaceAll(">", "")         // remove quote markers
	               .replaceAll("\\|", "-")      // replace table pipes
	               .replaceAll("\\n{3,}", "\n\n") // collapse extra newlines
	               .trim();
	}



    // Wrapper for `data`
    record DataResponse(Data data) {}

    // Actual data
    record Data(String file_link, String file_caption) {}
}
