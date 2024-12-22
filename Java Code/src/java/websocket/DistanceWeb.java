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

@ServerEndpoint("/distance")
public class DistanceWeb {

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private static double latestDistance = 0.0;

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
    public static void broadcastDistance(double distance) {
        latestDistance = distance;
         JsonObject jsonResponse = new JsonObject();
        if(distance<5){
       
        jsonResponse.addProperty("distance", true);
        }else{
             jsonResponse.addProperty("distance", false);
        }

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
