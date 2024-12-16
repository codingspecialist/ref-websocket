package com.metacoding.refsocket.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/save-form")
    public String saveForm(){
        return "save-form";
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("models", chatService.findAll());
        return "index";
    }

    @GetMapping("/api")
    public ResponseEntity<?> api(){
        List<Chat> models = chatService.findAll();
        return ResponseEntity.ok(models);
    }

    @PostMapping("/chat")
    public String save(String msg){
        chatService.save(msg);
        return "redirect:/";
    }
}






