
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import websocket.DistanceWeb;

@WebServlet(name = "test", urlPatterns = {"/test"})
public class test extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(sb.toString(), JsonObject.class);
        double distance = jsonObject.get("distance").getAsDouble();

        // Update WebSocket clients with the new distance
        DistanceWeb.broadcastDistance(distance);

        // Print the received distance to the console
        System.out.println("Received distance: " + distance + " cm");

        response.setContentType("text/plain");
        response.getWriter().write("Hello, distance received: " + distance + " cm");
    }
}
