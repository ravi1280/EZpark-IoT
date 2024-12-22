
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "parkingSlot", urlPatterns = {"/parkingSlot"})
public class parkingSlot extends HttpServlet {
  private static int slot = 0;
    @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Gson gson = new Gson();
    JsonObject responseObject = new JsonObject();
    responseObject.addProperty("success", false);

    

    String data = request.getParameter("data");
    if ("1".equals(data)) { // Use .equals() for string comparison
   
        
         if (slot < 10) {
                    slot += 1;
                    responseObject.addProperty("success", true);
                      responseObject.addProperty("slot", String.valueOf(slot));
                } else {
                    responseObject.addProperty("message", "Maximum limit reached !");
                }
         
    } else if ("2".equals(data)) {
   
          if (slot > 0) {
                    slot -= 1;
                    responseObject.addProperty("success", true);
                      responseObject.addProperty("slot", String.valueOf(slot));
                } else {
                    responseObject.addProperty("message", "Minimum  limit reached !");
                }
    } else {
        System.out.println(String.valueOf(slot));
        responseObject.addProperty("success", true);
        responseObject.addProperty("slot", String.valueOf(slot));
    }

    response.setContentType("application/json");
    response.getWriter().write(gson.toJson(responseObject));
}


 
}
