package co.mannit.commonservice.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;



@Configuration
public class RestTemplateConfig {

	@Bean
	public RestTemplate Resttemplate() {
		return new RestTemplate();
	}
	@Bean
    public ExecutorService emailExecutorService() {
        // You can tweak the pool size as needed
        return Executors.newFixedThreadPool(5);
    }	

}
