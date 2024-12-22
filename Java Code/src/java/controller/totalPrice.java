package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.park;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "totalPrice", urlPatterns = {"/totalPrice"})
public class totalPrice extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);
        Session session = HibernateUtil.getSessionFactory().openSession();
        //total today price

        LocalDate today = LocalDate.now();
        Date startOfDay = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        Criteria criteria = session.createCriteria(park.class);
        criteria.add(Restrictions.ge("check_out", startOfDay));
        criteria.add(Restrictions.lt("check_out", endOfDay));
        criteria.setProjection(Projections.sum("price"));
        String totalPrice = (String) criteria.uniqueResult();

        if (totalPrice != null) {
            System.out.println("Total Price for Today: $" + totalPrice);
             responseObject.addProperty("success", true);
            responseObject.addProperty("total_price", totalPrice);
        } else {
            System.out.println("No records found for today.");
             responseObject.addProperty("success", true);
            responseObject.addProperty("total_price", 0);
        }
           session.close();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
