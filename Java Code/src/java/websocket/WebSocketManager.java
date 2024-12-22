package websocket;

import javax.websocket.Session;
import java.util.HashSet;
import java.util.Set;

public class WebSocketManager {

    private static Set<Session> activeSessions = new HashSet<>();

    public static void addSession(Session session) { // Add session to active sessions list
        activeSessions.add(session);
    }

    public static void removeSession(Session session) { // Remove session from active sessions list
        activeSessions.remove(session);
    }

    public static void sendMessageToAll(String message) { // Send message to all active sessions
        for (Session session : activeSessions) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Optional method for send message to specific user session
    public static void sendMessageToSession(Session session, String message) {
        if (session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
