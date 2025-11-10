package co.mannit.commonservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import co.mannit.commonservice.ServiceCommonException;
import co.mannit.commonservice.pojo.AudioChunk;
import co.mannit.commonservice.repository.AudioChunkRepository;
import jakarta.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class AudioService {

	
	private static final Logger logger = LogManager.getLogger(AudioService.class);
	
    @Autowired
    private AudioChunkRepository audioChunkRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    
    // ExecutorService for parallel processing
    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Customize the pool size as needed
    

    private static final int CHUNK_SIZE = 4 * 1024 * 1024; // 4MB chunk size

    
    public Map<String, String> storeAudio(MultipartFile file) throws IOException {
        logger.info("Starting storeAudio() method");
        
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();
        byte[] fileBytes = file.getBytes();
        int totalChunks = (int) Math.ceil((double) fileBytes.length / CHUNK_SIZE);

        logger.debug("Filename: {}, ContentType: {}, TotalChunks: {}", filename, contentType, totalChunks);
        List<AudioChunk> chunks = new ArrayList<>();

        for (int i = 0; i < totalChunks; i++) { 
            int start = i * CHUNK_SIZE;
            int end = Math.min(start + CHUNK_SIZE, fileBytes.length);
            byte[] chunkData = new byte[end - start];
            System.arraycopy(fileBytes, start, chunkData, 0, chunkData.length);

            AudioChunk chunk = new AudioChunk();
            chunk.setFilename(filename);
            chunk.setChunkIndex(i);
            chunk.setData(chunkData);
            chunk.setTotalChunks(totalChunks);
            chunk.setContentType(contentType);

            chunks.add(chunk);
        }
        
        logger.debug("Saving {} chunks to database", chunks.size());
        audioChunkRepository.saveAll(chunks);

        // Add file metadata in fs.files collection
        Document fileMetadata = new Document("filename", filename)
                .append("length", fileBytes.length)
                .append("chunkSize", CHUNK_SIZE)
                .append("uploadDate", new Date());

        mongoTemplate.getCollection("fs.files").insertOne(fileMetadata);
        logger.debug("Metadata stored in fs.files collection for {}", filename);
        
        // Return JSON response
        Map<String, String> response = new HashMap<>();
        response.put("message", "File uploaded successfully");
        response.put("filename", filename);
        response.put("contentType", contentType);
        
        return response;
    }

    @PreDestroy
    public void shutdownExecutor() {
        try {
            logger.info("Shutting down executor service...");
            executorService.shutdown(); // Initiate an orderly shutdown
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow(); // Force shutdown if it doesn't terminate within 60 seconds
            }
        } catch (InterruptedException e) {
            logger.error("Thread pool shutdown interrupted.", e);
            executorService.shutdownNow();
        }
    }
/*------------------------
    //retreive audio
    public Map<String, Object> getAudio(String filename) {
        logger.info("Starting getAudio() method for filename: {}", filename);
        
        List<AudioChunk> chunks = audioChunkRepository.findByFilenameOrderByChunkIndex(filename);
        if (chunks.isEmpty()) {
            logger.warn("Audio file {} not found", filename);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Audio not found");
            errorResponse.put("filename", filename);
            return errorResponse;
        }

        int totalSize = chunks.stream().mapToInt(chunk -> chunk.getData().length).sum();
        logger.debug("Total size of merged audio: {} bytes", totalSize);

        byte[] fullData = new byte[totalSize];
        List<Callable<Void>> tasks = new ArrayList<>();
        int position = 0;

        // Create tasks to copy each chunk in parallel
        for (AudioChunk chunk : chunks) {
            final byte[] chunkData = chunk.getData();
            final int finalPosition = position;

            tasks.add(() -> {
                System.arraycopy(chunkData, 0, fullData, finalPosition, chunkData.length);
                return null;
            });

            position += chunkData.length;
        }

        try {
            // Execute tasks in parallel
            executorService.invokeAll(tasks);
            logger.debug("Audio file {} successfully retrieved and merged", filename);
        } catch (InterruptedException e) {
            logger.error("Error while retrieving and merging audio file {}", filename, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error during audio file retrieval");
            return errorResponse;
        }

        // Return both audio data and metadata in a map
        Map<String, Object> response = new HashMap<>();
        response.put("audioData", fullData);
        response.put("filename", filename);
        return response;
    }

    
    @PreDestroy
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    -------------------*/
    
    public Map<String, Object> getAudio(String filename) {
        logger.info("Starting getAudio() method for filename: {}", filename);
        
        List<AudioChunk> chunks = audioChunkRepository.findByFilenameOrderByChunkIndex(filename);
        if (chunks.isEmpty()) {
            logger.warn("Audio file {} not found", filename);

            // Throwing a custom exception with error code and filename as parameter
            throw new ServiceCommonException("113", new String[]{filename});
        }

        int totalSize = chunks.stream().mapToInt(chunk -> chunk.getData().length).sum();
        logger.debug("Total size of merged audio: {} bytes", totalSize);

        byte[] fullData = new byte[totalSize];
        List<Callable<Void>> tasks = new ArrayList<>();
        int position = 0;

        // Create tasks to copy each chunk in parallel
        for (AudioChunk chunk : chunks) {
            final byte[] chunkData = chunk.getData();
            final int finalPosition = position;

            tasks.add(() -> {
                System.arraycopy(chunkData, 0, fullData, finalPosition, chunkData.length);
                return null;
            });

            position += chunkData.length;
        }

        try {
            // Execute tasks in parallel
            executorService.invokeAll(tasks);
            logger.debug("Audio file {} successfully retrieved and merged", filename);
        } catch (InterruptedException e) {
            logger.error("Error while retrieving and merging audio file {}", filename, e);

            // Throwing an exception for errors during audio retrieval
            throw new ServiceCommonException("114", new String[]{filename});
        }

        // Return both audio data and metadata in a map
        Map<String, Object> response = new HashMap<>();
        response.put("audioData", fullData);
        response.put("filename", filename);
        return response;
    }

    @PreDestroy
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    public Map<String, Object> updateAudio(MultipartFile oldFile, MultipartFile newFile) throws IOException {
        logger.info("Starting updateAudio() method for replacing {} with {}", oldFile.getOriginalFilename(), newFile.getOriginalFilename());

        Map<String, Object> response = new HashMap<>();

        // Check if both files are provided
        if (oldFile.isEmpty() || newFile.isEmpty()) {
            logger.debug("Either old file or new file is missing, update aborted.");
            response.put("error", "Both old and new audio files are required!");
            return response;
        }

        String oldFilename = oldFile.getOriginalFilename();
        String newFilename = newFile.getOriginalFilename();

        // Check if old file exists in the database
        List<AudioChunk> existingChunks = audioChunkRepository.findByFilenameOrderByChunkIndex(oldFilename);
        if (existingChunks.isEmpty()) {
            response.put("error", "Old audio file not found!");
            return response;
        }

        // Delete old audio chunks from the database
        logger.debug("Deleting old audio chunks for filename: {}", oldFilename);
        audioChunkRepository.deleteByFilename(oldFilename);

        // Store the new audio file, using the old filename
        storeAudioWithCustomName(newFile, oldFilename);

        // Update metadata in fs.files collection with updatedDate and same filename
        Document updatedDocument = new Document("$set", new Document("updatedDate", new Date()).append("filename", oldFilename));
        mongoTemplate.getCollection("fs.files").updateOne(new Document("filename", oldFilename), updatedDocument);

        logger.debug("Updated fs.files collection with updatedDate for filename: {}", oldFilename);

        // Prepare response with success message
        response.put("message", "Audio file updated successfully!");
        response.put("filename", oldFilename);
        response.put("updatedDate", new Date());

        return response;
    }

    // Helper method to store new file under the old filename
    private void storeAudioWithCustomName(MultipartFile file, String filename) throws IOException {
        logger.debug("Storing new file {} as {}", file.getOriginalFilename(), filename);

        byte[] fileBytes = file.getBytes();
        int totalChunks = (int) Math.ceil((double) fileBytes.length / CHUNK_SIZE);
        List<AudioChunk> chunks = new ArrayList<>();

        for (int i = 0; i < totalChunks; i++) {
            int start = i * CHUNK_SIZE;
            int end = Math.min(start + CHUNK_SIZE, fileBytes.length);
            byte[] chunkData = new byte[end - start];
            System.arraycopy(fileBytes, start, chunkData, 0, chunkData.length);

            AudioChunk chunk = new AudioChunk();
            chunk.setFilename(filename);  // Retain old filename
            chunk.setChunkIndex(i);
            chunk.setData(chunkData);
            chunk.setTotalChunks(totalChunks);
            chunk.setContentType(file.getContentType());

            chunks.add(chunk);
        }

        logger.debug("Saving {} chunks to database for {}", chunks.size(), filename);
        audioChunkRepository.saveAll(chunks);
    }
}

   
