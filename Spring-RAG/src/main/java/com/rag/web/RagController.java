package com.rag.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

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

}
