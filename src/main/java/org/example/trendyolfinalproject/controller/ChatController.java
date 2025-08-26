//package org.example.trendyolfinalproject.controller;
//
//import org.example.trendyolfinalproject.service.ChatService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/ask")
//public class ChatController {
//
//    private final ChatService chatService;
//
//    public ChatController(ChatService chatService) {
//        this.chatService = chatService;
//    }
//
//    @PostMapping
//    public ResponseEntity<String> askQuestion(@RequestBody Map<String, String> payload) {
//        String question = payload.get("question");
//        String response = chatService.askOpenAi(question);
//        return ResponseEntity.ok(response);
//    }
//}
//
