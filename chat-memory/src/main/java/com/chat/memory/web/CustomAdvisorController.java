package com.chat.memory.web;

import com.chat.memory.advisor.TutorStyleAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.mongo.MongoChatMemoryRepository;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat-memory")
public class CustomAdvisorController {

    private final ChatClient chatClient;

    public CustomAdvisorController(OpenAiChatModel model,
                                MongoChatMemoryRepository mongoChatMemoryRepository) {

        ChatMemory mongoChatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(mongoChatMemoryRepository)
                .maxMessages(10)
                .build();


        this.chatClient = ChatClient.builder(model)
                .defaultSystem("You are my friendly AI assistant. "
                        + "Keep responses helpful and remember what the user tells you.")

                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(mongoChatMemory).build(),
                        new TutorStyleAdvisor())
                .build();
    }

    @GetMapping("/custom-advisor")
    public String chatUsingCustomAdvisor(@RequestParam String userId,
                              @RequestParam String prompt) {
        return chatClient.prompt(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                .call()
                .content();

    }
}
