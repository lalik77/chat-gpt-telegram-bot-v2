package com.mami.chatgptbot.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAiConfig {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(openaiApiKey);
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate
                .getInterceptors()
                .add(
                        (request, body, execution) -> {
                            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
                            return execution.execute(request, body);
                        }
                );
        return restTemplate;
    }
}
