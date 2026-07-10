package com.rag.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
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
    private VectorStore vectorStore;

    public RagController(OpenAiChatModel model) {
        this.chatClient = ChatClient.create(model);
    }


    @GetMapping("/embeddings")
    public float[] getEmbeddings(@RequestParam String text) {
        return embeddingModel.embed(text);
    }

    @GetMapping("/get-course")
    public String getCourseWithRag(@RequestParam String query) {

        return chatClient.prompt(query)
                .advisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .call().content();

    }

}
