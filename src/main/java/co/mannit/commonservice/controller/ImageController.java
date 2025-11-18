package co.mannit.commonservice.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.mannit.commonservice.common.Response;
import co.mannit.commonservice.pojo.PaginationReqParam;
import co.mannit.commonservice.service.ImgService;

@RestController
@RequestMapping("/auth")
public class ImageController {
 

	
	@Autowired
	private ImgService service;
	
	private static final Logger logger =LogManager.getLogger(ImageController.class);
	
	@PostMapping("eUpload")
	public Response<?> uploadImage(@RequestPart(name="file") MultipartFile file,
			                        @RequestPart(name="data") String body,
			                        @RequestParam(name="phoneNumber")String phone,
			                        @RequestParam(name="userId",required=false)String uid) throws IOException{

		logger.debug("retrieve file {} ",file);
		byte [] data =file.getBytes();
		String serv=service.uploadimage(body,phone,data,uid);
		logger.info("uploadImage {}");
		return Response.buildSuccessMsg(200, "sucessfully inserted image", serv);
	};
	@GetMapping("eRetrieve")
	public ResponseEntity<byte[]>retrieveImage(@Validated PaginationReqParam paginationReq, @RequestParam Map<String, String> param
			,@RequestParam(required=false)List<String>fields) throws Exception{
	
			logger.debug("<searchResource> PaginationReqParam {} userId {}",paginationReq, param);
			List<String>img=service.searchResource(paginationReq, param,fields);
			byte[] imageBytes = null;
			for (String jsonString : img) {
                // Parse each JSON string
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(jsonString);

                // Extract the base64 encoded file data
                String base64Data = rootNode.path("filedata").path("$binary").path("base64").asText();

                // Decode base64 to byte array
                 imageBytes = Base64.getDecoder().decode(base64Data);
			}
		     //byte[]img2=img.get(img.indexOf("filedata")).getBytes();
			//System.out.println();
			HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.IMAGE_JPEG);
			return ResponseEntity.ok().headers(headers).body(imageBytes);
		}
	@GetMapping("eRetrievemult")
	public ResponseEntity<byte[]>retrieveImageMultiple(@Validated PaginationReqParam paginationReq, @RequestParam Map<String, String> param
			,@RequestParam(required=false)List<String>fields) throws Exception{
		  logger.debug("<searchResource> PaginationReqParam {} userId {}", paginationReq, param);
		    List<String> imgList = service.searchResource(paginationReq, param,fields);
		    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		    ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

		    for (int i = 0; i < imgList.size(); i++) {
		        ObjectMapper objectMapper = new ObjectMapper();
		        JsonNode rootNode = objectMapper.readTree(imgList.get(i));
		        String base64Data = rootNode.path("filedata").path("$binary").path("base64").asText();

		        byte[] imageBytes = Base64.getDecoder().decode(base64Data);

		        ZipEntry zipEntry = new ZipEntry("image" + (i + 1) + ".jpg");
		        zipOutputStream.putNextEntry(zipEntry);
		        zipOutputStream.write(imageBytes);
		        zipOutputStream.closeEntry();
		    }
		    zipOutputStream.close();

		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		    headers.setContentDispositionFormData("attachment", "images.zip");

		    return ResponseEntity.ok().headers(headers).body(byteArrayOutputStream.toByteArray());
		}
	@GetMapping("eRetrieveimgs")
	public ResponseEntity<List<String>> retrieveImageMultiplebyte(@Validated PaginationReqParam paginationReq, @RequestParam Map<String, String> param
			,@RequestParam(required=false)List<String>fields) throws Exception{
		  logger.debug("<searchResource> PaginationReqParam {} userId {}", paginationReq, param,fields);
		    List<String> imgList = service.searchResource(paginationReq, param,fields);
		    List<String> imageBase64List = new ArrayList<>();

		    for (String jsonString : imgList) {
		        // Parse each JSON string
		        ObjectMapper objectMapper = new ObjectMapper();
		        JsonNode rootNode = objectMapper.readTree(jsonString);

		        // Extract the base64 encoded file data
		        String base64Data = rootNode.path("filedata").path("$binary").path("base64").asText();

		        // Add base64 data to list
		        imageBase64List.add(base64Data);
		    }
           //System.out.println("image list size"+imageBase64List.size());
		    return ResponseEntity.ok(imageBase64List);
	}
	@PutMapping("eUpdateimg")
	public Response<?> updateResource(@RequestPart(name="file",required=false) MultipartFile file,
            @RequestPart(name="data",required=false) String json, @RequestParam(name="userId" ,required=false)String userId, @RequestParam("resourceId")String resourceId) throws Exception {
		logger.debug("<updateResource> userId {} resourceId{} json{}",userId, resourceId, json);
		byte [] data=null;
		if(file!=null&&!file.isEmpty()) {
			data =file.getBytes();
		}	
		return Response.buildSuccessMsg(200, "Resource Updated Successfully", service.updateResource(userId, resourceId, json,data));
	}
	
}
