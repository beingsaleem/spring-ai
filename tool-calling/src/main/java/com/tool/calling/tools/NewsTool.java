package com.tool.calling.tools;

import com.tool.calling.config.ApiConfig;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NewsTool {

    private final ApiConfig apiConfig;

    private final RestTemplate restTemplate;

    public NewsTool(ApiConfig apiConfig, RestTemplate restTemplate) {
        this.apiConfig = apiConfig;
        this.restTemplate = restTemplate;
    }

    @Tool(name = "getNews", description = "Get news articles for a specific topic")
    public String getNews(String topic) {
        String newsApiKey = apiConfig.getNewsKey();
        String url = "https://newsapi.org/v2/everything?q=" + topic + "&apiKey=" + newsApiKey;
        return restTemplate.getForObject(url, String.class);
    }
}
