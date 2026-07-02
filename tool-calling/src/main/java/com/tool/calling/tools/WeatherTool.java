package com.tool.calling.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherTool {

    private RestTemplate restTemplate;

    public WeatherTool(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Tool(name = "getWeather", description = "Get weather information for a specific location")
    public String getWeather(String location) {
        String url = "https://wttr.in/" + location + "?format=j1";
        return restTemplate.getForObject(url, String.class);
    }
}
