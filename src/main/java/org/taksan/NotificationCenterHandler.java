package org.taksan;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebSocket
public class NotificationCenterHandler {
    
    private static List<Session> sessions = new LinkedList<>();
    
    @OnWebSocketConnect
    public synchronized void onConnect(Session user) throws Exception {
        sessions.add(user);
    }

    @OnWebSocketClose
    public synchronized void onClose(Session user, int statusCode, String reason) {
        sessions.remove(user);
    }
    
    public static void notifyGroupAdded(SkypeEntry entry) {
        sendNotification("ADDED", new EntryEvent(entry));
    }

    public static void notifyGroupRemoved(SkypeEntry entry) {
        sendNotification("DELETED", new EntryEvent(entry));
    }

    public static void notifyGroupUpdated(SkypeEntry entry) {
        sendNotification("UPDATED", new EntryEvent(entry));
    }

    public static void sendNotification(String string, EntryEvent entryEvent) {
        entryEvent.operation = string;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        sessions.stream().forEach(session -> {
            try {
                session.getRemote().sendString(gson.toJson(entryEvent));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
