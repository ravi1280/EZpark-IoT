
package websocket;

import com.google.gson.JsonObject;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/rfid")
public class RfidWeb {
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
 

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("New connection: " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        // Optionally handle messages from the client
        System.out.println("Received message from client: " + message);
    }

    // Method to broadcast the latest distance to all connected clients
    public static void broadcastRfid(String uid) {
        String id = uid;
         JsonObject jsonResponse = new JsonObject();
       
             jsonResponse.addProperty("Uid", id);
             jsonResponse.addProperty("view", true);
       

        synchronized (sessions) {
            for (Session session : sessions) {
                try {
                    session.getBasicRemote().sendText(jsonResponse.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
