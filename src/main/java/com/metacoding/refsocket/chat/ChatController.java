package com.metacoding.refsocket.chat;

import com.metacoding.refsocket.config.SseEmitters;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Controller
public class ChatController {
    private final ChatService chatService;
    private final SseEmitters sseEmitters;
    private final HttpSession session; // stateless 서버에서는 session에 redis를 연결해둬야 함.


    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect() {

        // 1분 동안 서버측 응답 없으면, 브라우저에서 자동으로 서버에 재연결 요청을 함
        String clientId = session.getId();
        System.out.println("새로고침 : "+clientId);
        SseEmitter emitter = new SseEmitter(60*1000L); 
        sseEmitters.add(clientId, emitter);
        try {
            // Emmitter 생성 후 1분동안 아무런 데이터도 브라우저에 보내지 않으면
            // 브라우저 측에서 재연결 요청시에 403 Service Unavailable 에러 발생함
            // 이를 방지 하기 위해서 data에 더미를 보냄.
            emitter.send(SseEmitter.event().name("connect").data("dummy-data"));
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(emitter);
    }

    @GetMapping("/save-form")
    public String saveForm() {
        return "save-form";
    }

    @GetMapping("/")
    public String index(Model model) {
        session.setAttribute("clientId", session.getId());
        model.addAttribute("models", chatService.findAll());
        return "index";
    }

    @PostMapping("/chat")
    public String save(String msg) {
        Chat chat = chatService.save(msg);
        // 메시지가 저장되면, 모든 구독자들에게 메시지 알림
        sseEmitters.sendAll(chat);
        return "redirect:/";
    }
}






