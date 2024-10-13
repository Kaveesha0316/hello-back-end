/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Post;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;

/**
 *
 * @author Admin
 */
@WebServlet(name = "DeletePost", urlPatterns = {"/DeletePost"})
public class DeletePost extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responsejson = new JsonObject();
        responsejson.addProperty("message", "Error");
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            Session session = HibernateUtil.getSessionFactory().openSession();

            Post post = (Post) session.get(Post.class, id);
            session.delete(post);
            session.beginTransaction().commit();
            session.close();
            responsejson.addProperty("success", true);
            responsejson.addProperty("message", "delete success");
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responsejson));

    }

}
