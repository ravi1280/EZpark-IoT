package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.park;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Validation;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "CheckIn", urlPatterns = {"/CheckIn"})
public class CheckIn extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);
        Session session = HibernateUtil.getSessionFactory().openSession();

        String mobile = request.getParameter("mobile");
        String vNumber = request.getParameter("vNumber");

        System.out.println(mobile);
        System.out.println(vNumber);

        if (mobile.isEmpty()) {
            responseObject.addProperty("message", "Please Fill The Mobile Number Field !");

        } else if (!Validation.isMobileNumberValid(mobile)) {
            responseObject.addProperty("message", "Invalid Mobile Number !");

        } else if (vNumber.isEmpty()) {
            responseObject.addProperty("message", "Please Fill  Vehical Number !");

        } else {
 
            park park = new park();
            park.setMobile(mobile);
            park.setCar_number(vNumber);
            park.setCheck_in(new Date());

            session.save(park);
            session.beginTransaction().commit();

            responseObject.addProperty("success", true);
            responseObject.addProperty("message", "Vehical Registraion Complete!");

        }
        session.close();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}


