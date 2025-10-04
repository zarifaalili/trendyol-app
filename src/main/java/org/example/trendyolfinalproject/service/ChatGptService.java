package org.example.trendyolfinalproject.service;

import com.theokanning.openai.OpenAiService;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatGptService {

    private final OpenAiService openAiService;

    public ChatGptService(@Value("${openai.api.key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey);
    }

    public String askQuestion(String question) {
        try {
            ChatMessage message = new ChatMessage();
            message.setRole("user");
            message.setContent(question);

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(List.of(message))
                    .maxTokens(200)
                    .build();

            ChatCompletionResult result = openAiService.createChatCompletion(request);
            ChatCompletionChoice choice = result.getChoices().get(0);
            return choice.getMessage().getContent().trim();
        } catch (Exception e) {
            return "Xəta baş verdi: " + e.getMessage();
        }
    }
}
