package com.tool.calling.web;

import com.tool.calling.tools.DateTimeTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ToolCallingController {

    private OpenAiChatModel openAiChatModel;

    private ChatClient chatClient;

    ChatMemory memory = MessageWindowChatMemory.builder().build();

    String conversationId = "my-conversation-id";

    private DateTimeTool dateTimeTool;

    public ToolCallingController(OpenAiChatModel openAiChatModel, ChatClient.Builder builder, DateTimeTool dateTimeTool) {
        this.openAiChatModel = openAiChatModel;
        this.chatClient = builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
                .defaultAdvisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId)).build();
        this.dateTimeTool = dateTimeTool;
    }

    @GetMapping("/info/{prompt}")
    public String getInfo(@PathVariable String prompt) {
        return openAiChatModel.call(prompt);
    }

    @GetMapping("/info2/{prompt}")
    public String getInfo2(@PathVariable String prompt) {
        return chatClient.prompt(prompt).call().content();
    }

    @GetMapping("/info3/{prompt}")
    public String getInfo3(@PathVariable String prompt) {
        return chatClient.prompt(prompt)
                .tools(dateTimeTool)
                .call()
                .content();
    }
}