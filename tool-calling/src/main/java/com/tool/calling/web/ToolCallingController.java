package com.tool.calling.web;

import com.tool.calling.tools.DateTimeTool;
import com.tool.calling.tools.NewsTool;
import com.tool.calling.tools.WeatherTool;
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

    private NewsTool newsTool;

    private WeatherTool weatherTool;

    public ToolCallingController(OpenAiChatModel openAiChatModel, ChatClient.Builder builder, DateTimeTool dateTimeTool, NewsTool newsTool, WeatherTool weatherTool) {
        this.openAiChatModel = openAiChatModel;
        this.chatClient = builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
                .defaultAdvisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId)).build();
        this.dateTimeTool = dateTimeTool;
        this.newsTool = newsTool;
        this.weatherTool = weatherTool;
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

    @GetMapping("/info4/{prompt}")
    public String getInfo4(@PathVariable String prompt) {
        return chatClient.prompt(prompt)
                .tools(dateTimeTool, newsTool)
                .call()
                .content();
    }

    @GetMapping("/weather/{prompt}")
    public String getWeather(@PathVariable String prompt) {
        return chatClient.prompt(prompt)
                .tools(dateTimeTool, newsTool, weatherTool)
                .call()
                .content();
    }

    @GetMapping("/plan-my-day/{prompt}")
    public String planMyDay(@PathVariable String prompt) {
        return chatClient.prompt(prompt)
                .system(
                        """
                                You are a smart personal assistant.
                                You have access to the tools for date/time, news, and weather information.
                                Use whichever tools are necessary to provide the best answer to the user.
                                Always give a helpful, well-organized response.
                                """
                )
                .tools(dateTimeTool, newsTool, weatherTool)
                .call()
                .content();
    }
}