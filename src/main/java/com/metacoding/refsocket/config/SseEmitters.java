package com.metacoding.refsocket.config;

import com.metacoding.refsocket.chat.Chat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/*
CopyOnWriteArrayList - 스레드 safe한 List 구현체
ConcurrentHashMap - 스레드 safe한 Map 구현체
내부적으로 모든 쓰기 작업(삽입, 삭제 등)을 수행할 때, 기존 리스트의 복사본을 생성하고 해당 복사본에서 작업을 수행합니다.
이를 통해 읽기와 쓰기 작업이 동시에 발생하더라도 안전하게 동작합니다.
 */

@Slf4j
@Component
public class SseEmitters {
    
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /*
    SseEmitter를 생성할 때는 비동기 요청이 완료되거나 타임아웃 발생 시 실행할 콜백을 등록할 수 있습니다.
    타임아웃이 발생하면 브라우저에서 재연결 요청을 보내는데, 이때 새로운 Emitter 객체를 다시 생성하기 때문에
    기존의 Emitter를 제거해주어야 합니다. 따라서 onCompletion 콜백에서 자기 자신을 지우도록 등록합니다.
     */
    public SseEmitter add(String clientId, SseEmitter emitter) {
        // 1. F5를 통한 이미터 재등록시 (기존 이미터를 삭제해야해서 sessionId가 필요하다)
        if(emitters.containsKey(clientId)) {
            SseEmitter prevEmitter = emitters.get(clientId);
            prevEmitter.complete();
            log.info("prev emitter remove: {}", clientId);
        }
        this.emitters.put(clientId,emitter);
        log.info("new emitter clientId: {}", clientId);
        log.info("new emitter added: {}", emitter);
        log.info("emitter list size: {}", emitters.size());

        // SSE 메시지 전송이 완료(onCompletion) 혹은 타임 아웃되면, (onTimeout -> onCompletion)
        // 콜백시에 새로운 스레드에서 emitter를 삭제하기 때문에, 다른 스레드에서 사용중이면 동시성 문제 발생
        // CopyOnWriteArrayList를 사용해서 안전하다.
        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            this.emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitter.complete();
        });
        emitter.onError((e) -> {
            log.error("onError: An error occurred", e);
            emitter.complete();
        });
        return emitter;
    }

    // HashMap을 for문 돌릴때는 entrySet과 getKey(), getValue() 메서드를 활용해야 한다.
    public void sendAll(Chat chat){
        emitters.entrySet().forEach(entry -> {
            try {
                entry.getValue().send(SseEmitter.event().name("chat").data(chat));
            }catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
