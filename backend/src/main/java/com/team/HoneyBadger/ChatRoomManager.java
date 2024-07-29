package com.team.HoneyBadger;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class ChatRoomManager {
    private final ConcurrentHashMap<Long, BlockingQueue<String>> chatRooms = new ConcurrentHashMap<>();

    public void addUser(Long chatRoomId, String username) {
        chatRooms.computeIfAbsent(chatRoomId, k -> new LinkedBlockingQueue<>()).add(username);
    }

    public BlockingQueue<String> getUsers(Long chatRoomId) {
        return chatRooms.get(chatRoomId);
    }

    public void removeChatRoom(Long chatRoomId) {
        chatRooms.remove(chatRoomId);
    }

    public boolean hasChatRoom(Long chatRoomId) {
        return chatRooms.containsKey(chatRoomId);
    }

    public void removeUserFromAllRooms(String username) {
        for (Map.Entry<Long, BlockingQueue<String>> entry : chatRooms.entrySet()) {
            BlockingQueue<String> queue = entry.getValue();
            queue.remove(username);
        }
    }
}