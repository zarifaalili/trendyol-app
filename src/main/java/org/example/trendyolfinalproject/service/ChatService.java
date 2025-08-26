//package org.example.trendyolfinalproject.service;
//
//import com.theokanning.openai.OpenAiService;
//import com.theokanning.openai.completion.chat.ChatCompletionRequest;
//import com.theokanning.openai.completion.chat.ChatCompletionResult;
//import com.theokanning.openai.completion.chat.ChatMessage;
//import org.springframework.stereotype.Service;
//
//
//import java.util.List;
//@Service
//public class ChatService {
//
//    private final OpenAiService openAiService;
//
//    public ChatService(OpenAiService openAiService) {
//        this.openAiService = openAiService;
//    }
//
//    public String askOpenAi(String question) {
//        ChatCompletionRequest request = ChatCompletionRequest.builder()
//                .model("gpt-3.5-turbo")
//                .messages(List.of(new ChatMessage("user", question)))
//                .maxTokens(100)
//                .temperature(0.5)
//                .build();
//
//        ChatCompletionResult result = openAiService.createChatCompletion(request);
//        return result.getChoices().get(0).getMessage().getContent();
//    }
//}
