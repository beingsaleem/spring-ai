package com.tool.calling.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class DateTimeTool {

    @Tool(name = "currentDateTime", description = "Get the current date and time")
    public String getCurrentDateTime() {
        return java.time.LocalDateTime.now().toString();
    }

    @Tool(name = "zonedDateTime", description = "Get the current date and time in a specific time zone")
    public String getZonedDateTime(String zoneId) {
        return java.time.ZonedDateTime.now(java.time.ZoneId.of(zoneId)).toString();
    }
}
