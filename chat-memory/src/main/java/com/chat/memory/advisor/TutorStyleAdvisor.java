package com.chat.memory.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.prompt.Prompt;

public class TutorStyleAdvisor implements CallAdvisor {
    @Override
    public String getName() {
        return "TutorStyleAdvisor";
    }

    @Override
    public int getOrder() {
        return 0; // position in the advisor chain
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request,
                                         CallAdvisorChain chain) {
        // 1. Get the original user prompt from the request
        String originalPrompt = request.prompt().getContents();
        // 2. Modify it to add tutorial-style instructions
        String modifiedPrompt = originalPrompt
                + "\n\nPlease answer in a tutorial style, with clear explanations "
                + "and an example where appropriate.";
        // 3. Build a new request carrying the modified prompt
        Prompt newPrompt = new Prompt(modifiedPrompt);
        ChatClientRequest newRequest =
                request.mutate().prompt(newPrompt).build();
        // 4. Pass it down the chain to the model
        return chain.nextCall(newRequest);
    }
}
