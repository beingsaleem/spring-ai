package com.rag.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    @Autowired
    @Qualifier("openAiEmbeddingModel")
    private EmbeddingModel embeddingModel;

    private ChatClient chatClient;

    @Autowired
    private CourseSearchTool courseSearchTool;

    @Autowired
    private VectorStore vectorStore;

    public RagController(OpenAiChatModel model) {
        this.chatClient = ChatClient.create(model);
    }


    @GetMapping("/embeddings")
    public float[] getEmbeddings(@RequestParam String text) {
        return embeddingModel.embed(text);
    }

    @GetMapping("/get-course")
    public String getCourseWithRagLlm(@RequestParam String query) {

        return chatClient
                .prompt(query)
                .system("""
                        You are my course advisor.
                        Answer using only the context provided from the course catalog.
                        If the answer is not in the context, say
                        "I don't have that course information right now."
                        Always mention course name, price, duration, and level when relevant.
                        """)

                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder()
                                .topK(7)
                                .similarityThreshold(0.7)
                                .build())
                        .build())
                .call()
                .content();

    }

    @GetMapping("/get-course-agentic")
    public String getAnswerAgenticRag(@RequestParam String query) {
        return chatClient.prompt(query)
                .system(
                        """
                                You are My AI Course Advisor.
                                
                                If the user asks about:
                                - courses
                                - prices
                                - duration
                                - technologies
                                - what MyLearn offers
                                
                                use the searchCourses tool.
                                
                                For greetings or general AI questions,
                                answer directly without using the tool.
                                """
                )
                .tools(courseSearchTool)
                .call()
                .content();
    }

    @GetMapping("/query-rewrite")
    public String queryRewrite(@RequestParam String query) {
        String rewrittenQuery = chatClient.prompt()
                .system("Rewrite the user query as a clear, complete search " +
                        "query for a course catalog. Respond ONLY with the " +
                        "rewritten query, nothing else.")
                .user(query)
                .call()
                .content();
        System.out.println("User prompt original: " + query);
        System.out.println("User prompt rewritten: " + rewrittenQuery);
        List<Document> chunks = vectorStore.similaritySearch(
                SearchRequest.builder().query(rewrittenQuery)
                        .topK(6)
                        .build());

        return answerWithContext(query, chunks);
    }

    private String answerWithContext(String query, List<Document> chunks) {

        if (chunks.isEmpty()) {
            return "No Relevant courses found ";
        }
        String context = chunks.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

        return chatClient.prompt()
                .system(
                        """
                                You are my helpful course advisor.
                                Use only the provided course catalog context.
                                Mention course name, price, duration, and level.
                                If the answer is not in the context, politely say so.
                                """
                )
                .user("Context:\n" + context + "\n\nUser Query:\n" + query)
                .call()
                .content();

    }


}
