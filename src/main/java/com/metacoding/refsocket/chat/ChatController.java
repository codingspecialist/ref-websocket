package com.metacoding.refsocket.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations sms;

    @GetMapping("/save-form")
    public String saveForm(){
        return "save-form";
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("models", chatService.findAll());
        return "index";
    }

    @PostMapping("/chat")
    public String save(String msg){
        Chat chat = chatService.save(msg);
        sms.convertAndSend("/sub/chat", chat);
        return "redirect:/";
    }

    // /pub/room
//    @MessageMapping("/room")
//    public void pubTest1(String number){
//        System.out.println("나 요청돼? : "+ number);
//        sms.convertAndSend("/sub/"+number, "hello world "+number);
//    }

//    @SendTo("/sub")
//    @MessageMapping("/room")
//    public String pubTest2(String number){
//        System.out.println("나 요청돼? : "+ number);
//        return "hello world";
//    }


}






