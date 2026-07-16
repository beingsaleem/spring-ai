package com.rag.web;

import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseSearchTool {

    private final VectorStore vectorStore;

    public CourseSearchTool(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Tool(name = "searchCourse", description = "Search the course catalog for courses matching a topic, technology or skill")
    public String searchCourse(
            @ToolParam(description = "the topic to search for, like Java, AI, DevOps") String topic) {
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(topic)
                        .topK(6).build());

        return documents.stream().map(Document::getText).collect(Collectors.joining("\n"));

    }
}
