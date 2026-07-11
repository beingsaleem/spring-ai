package com.chat.memory.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movie")
public class MovieController {

    private final ChatClient chatClient;


    public MovieController(OpenAiChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    @GetMapping
    public String getMovieInfo(@RequestParam String movieName) {
        String prompt = "Tell me about the movie " + movieName;
        return chatClient.prompt(prompt).call().content();
    }
}
