package co.mannit.commonservice.controller;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/Wfiles")
public class FileServeController {

 @GetMapping("/{filename:.+}")
 public ResponseEntity<Resource> getFile(@PathVariable String filename) {
     try {
         Path filePath = Paths.get("/opt/com_images/").resolve(filename).normalize();
         Resource resource = new UrlResource(filePath.toUri());

         if (!resource.exists()) {
             return ResponseEntity.notFound().build();
         }

         String contentType = "application/octet-stream";
         if (filename.endsWith(".png")) contentType = MediaType.IMAGE_PNG_VALUE;
         else if (filename.endsWith(".jpg")) contentType = MediaType.IMAGE_JPEG_VALUE;
         else if (filename.endsWith(".pdf")) contentType = MediaType.APPLICATION_PDF_VALUE;

         return ResponseEntity.ok()
                 .contentType(MediaType.parseMediaType(contentType))
                 .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                 .body(resource);

     } catch (Exception e) {
         return ResponseEntity.internalServerError().build();
     }
 }
}
