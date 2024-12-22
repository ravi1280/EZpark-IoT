package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.park;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import java.time.LocalDate;
import java.time.ZoneId;

@WebServlet(name = "CheckOut", urlPatterns = {"/CheckOut"})
public class CheckOut extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);
        Session session = HibernateUtil.getSessionFactory().openSession();

        String mobile = request.getParameter("mobile");
        String vNumber = request.getParameter("vNumber");

//        System.out.println(mobile);
//        System.out.println(vNumber);
        if (mobile.isEmpty()) {
            responseObject.addProperty("message", "Please Fill The Mobile Number Field !");

        } else if (!Validation.isMobileNumberValid(mobile)) {
            responseObject.addProperty("message", "Invalid Mobile Number !");

        } else if (vNumber.isEmpty()) {
            responseObject.addProperty("message", "Please Fill  Vehical Number !");

        } else {

            Criteria criteria1 = session.createCriteria(park.class);
            criteria1.add(Restrictions.eq("mobile", mobile));
            criteria1.add(Restrictions.eq("car_number", vNumber));
            criteria1.addOrder(Order.desc("check_in")); // Order by check_in descending
            criteria1.setMaxResults(1); // Limit to one result

            if (!criteria1.list().isEmpty()) {
                park parkdetails = (park) criteria1.uniqueResult(); // Get the single result

                // Fetch the check_in time and convert it to Instant
                Date checkInDate = parkdetails.getCheck_in();
                Instant checkInInstant = checkInDate.toInstant();

                // Get the current time
                Instant nowInstant = Instant.now();

                // Calculate the difference in minutes
                Duration duration = Duration.between(checkInInstant, nowInstant);
                long totalMinutes = duration.toMinutes();

                // Calculate the number of 5-minute intervals
                long fiveMinuteIntervals = totalMinutes / 1;

                // Calculate the total payment
                long payment = fiveMinuteIntervals * 2;

                System.out.println("Total time parked: " + totalMinutes + " minutes");
                System.out.println("Number of 5-minute intervals: " + fiveMinuteIntervals);
                System.out.println("Total payment: $" + payment);

                responseObject.addProperty("payment",  payment);
                responseObject.addProperty("parkTime", totalMinutes);

                parkdetails.setCheck_out(new Date());
                parkdetails.setPrice(String.valueOf(payment));

                session.update(parkdetails);
                session.beginTransaction().commit();

                responseObject.addProperty("success", true);
                responseObject.addProperty("message", "success !");
                
                //total today price

//                LocalDate today = LocalDate.now();
//                Date startOfDay = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
//                Date endOfDay = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//                Criteria criteria = session.createCriteria(park.class);
//                criteria.add(Restrictions.ge("check_out", startOfDay)); 
//                criteria.add(Restrictions.lt("check_out", endOfDay));  
//                criteria.setProjection(Projections.sum("price"));      
//                String totalPrice = (String )criteria.uniqueResult();
//
//                if (totalPrice != null) {
//                    System.out.println("Total Price for Today: $" + totalPrice);
//                    responseObject.addProperty("total_price", totalPrice);
//                } else {
//                    System.out.println("No records found for today.");
//                    responseObject.addProperty("total_price", 0);
//                }
                
                
            } else {
                responseObject.addProperty("message", "Invalid Details!");
            }

        }
        session.close();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
