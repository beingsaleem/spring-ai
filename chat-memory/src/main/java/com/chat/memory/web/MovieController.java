package com.chat.memory.web;

import com.chat.memory.Movie;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/movie")
public class MovieController {

    private final ChatClient chatClient;


    public MovieController(OpenAiChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    @GetMapping
    public String getMovie(@RequestParam String movieName) {
        String prompt = "Tell me about the movie " + movieName;
        return chatClient.prompt(prompt).call().content();
    }

    @GetMapping("/info")
    public Movie getMovieInfo(@RequestParam String movieName) {
        return chatClient.prompt()
                .user(u -> u.text("Give me details about the movie {movie}")
                        .param("movie", movieName))
                .call()
                .entity(new BeanOutputConverter<Movie>(Movie.class));
    }

    @GetMapping("/get-movies")
    public List<String> getDirectorMovie(@RequestParam String directorName) {
        String prompt = "List movies directed by " + directorName;
        return chatClient
                .prompt()
                .user("List movies directed by " + directorName)
                .call()
                .entity(new ListOutputConverter(new DefaultConversionService()));

    }
}
