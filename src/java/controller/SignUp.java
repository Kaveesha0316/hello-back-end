/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import entity.User_status;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import model.Validation;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responsejson = new JsonObject();
        responsejson.addProperty("message", "Error");

        String mobile = request.getParameter("mobile");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String password = request.getParameter("password");
        Part avaterImage = request.getPart("avaterImage");

//        else if (!Validation.isMobileNumberValid(mobile)) {
//            responsejson.addProperty("message", "invalid mobile number");
//        }
        if (firstName.isEmpty()) {
            responsejson.addProperty("message", "fill first Name");
        } else if (lastName.isEmpty()) {
            responsejson.addProperty("message", "fill last Name");
        } else if (mobile.isEmpty()) {
            responsejson.addProperty("message", "fill mobile number");
        } else if (!Validation.isMobileNumberValid(mobile)) {
            responsejson.addProperty("message", "inavalid mobile");
        } else if (password.isEmpty()) {
            responsejson.addProperty("message", "fill password");
        }
//        else if (!Validation.isPasswordValid(password)) {
//            responsejson.addProperty("message", "Password must include ate leat one uppercase letter, number,special charactor and be at least eight chracters long.");
//
//        } 
        else {

            Session session = HibernateUtil.getSessionFactory().openSession();

            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("mobile", mobile));

            if (!criteria1.list().isEmpty()) {
                responsejson.addProperty("message", "mobile aready exist");
            } else {

                User user = new User();
                user.setFirst_name(firstName);
                user.setLast_name(lastName);
                user.setMobile(mobile);
                user.setPassword(password);
                user.setRegistered_date_time(new Date());

                //get user status 2 = offline
                User_status user_status = (User_status) session.get(User_status.class, 2);
                user.setUser_status(user_status);

                session.save(user);
                session.beginTransaction().commit();

                if (avaterImage != null) {

                    String serverPath = request.getServletContext().getRealPath("");
                    String newApplicationPath = serverPath.replace("build" + File.separator + "web", "web");

                    String avatarImagePath = newApplicationPath + File.separator + "AvatarImage" + File.separator + mobile + ".png";
                    File file = new File(avatarImagePath);
                    Files.copy(avaterImage.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                responsejson.addProperty("success", true);
                responsejson.addProperty("message", "Registration Complete");

                session.close();
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responsejson));

    }

}
