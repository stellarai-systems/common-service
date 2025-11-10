package co.mannit.commonservice.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Document(collection = "audio_chunks")
public class AudioChunk {

	
	@Id
    private String id;
    
    private String filename;
    private int chunkIndex;
    private byte[] data;
    private int totalChunks;
    private String contentType;
	public void setFile(byte[] bytes) {
		// TODO Auto-generated method stub
		
	}
}
