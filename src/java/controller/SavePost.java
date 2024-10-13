/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Post;
import entity.User;
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
import org.hibernate.Session;
import org.hibernate.Transaction;

@MultipartConfig
@WebServlet(name = "SavePost", urlPatterns = {"/SavePost"})
public class SavePost extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responsejson = new JsonObject();
        Session session = HibernateUtil.getSessionFactory().openSession();
        responsejson.addProperty("message", "Error");

        String desc = request.getParameter("desc");
        Part postImage = request.getPart("postImage");
        String user_id = request.getParameter("user_id");

        if (desc.isEmpty()) {
            responsejson.addProperty("message", "no description");
        } else if (postImage == null) {
            responsejson.addProperty("message", "select Image");
        } else {
            try {
                User logged_user = (User) session.get(User.class, Integer.parseInt(user_id));


                System.out.println(new Date());
                Post post = new Post();
                post.setDesc(desc);
                post.setDate_time(new Date());
                post.setUser(logged_user);

               Integer postId = (Integer) session.save(post);
                session.beginTransaction().commit();

                String serverPath = request.getServletContext().getRealPath("");
                String newApplicationPath = serverPath.replace("build" + File.separator + "web", "web");

                String postImagePath = newApplicationPath + File.separator + "PostImages" + File.separator + postId + ".png";
                File file = new File(postImagePath);
                Files.copy(postImage.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

                responsejson.addProperty("success", true);
                responsejson.addProperty("message", "Registration Complete");

                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responsejson));
    }

}
