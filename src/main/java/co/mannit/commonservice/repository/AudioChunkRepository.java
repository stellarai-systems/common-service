package co.mannit.commonservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import co.mannit.commonservice.pojo.AudioChunk;

@Repository
public interface AudioChunkRepository extends MongoRepository<AudioChunk, String> {
    List<AudioChunk> findByFilenameOrderByChunkIndex(String filename);
    

	AudioChunk findByFilename(String filename);


	void deleteByFilename(String filename);
}
