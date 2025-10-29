package co.mannit.commonservice.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import co.mannit.commonservice.service.AudioService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/audio")
public class AudioController {

    @Autowired
    private AudioService audioService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadAudio(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, String> response = audioService.storeAudio(file);
        return ResponseEntity.ok(response);
    }

       
    @GetMapping("/download/{filename}")
    public ResponseEntity<?> downloadAudio(@PathVariable String filename) {
        Map<String, Object> response = audioService.getAudio(filename);

        if (response.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        byte[] audioData = (byte[]) response.get("audioData");
        ByteArrayResource resource = new ByteArrayResource(audioData);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateAudio(@RequestParam("oldFile") MultipartFile oldFile, @RequestParam("newFile") MultipartFile newFile) throws IOException {
        if (oldFile.isEmpty() || newFile.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Both old and new audio files are required!");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Map<String, Object> response = audioService.updateAudio(oldFile, newFile);

        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

}
    
  

