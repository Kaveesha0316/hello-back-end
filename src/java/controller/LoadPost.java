/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import entity.Post;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

/**
 *
 * @author Admin
 */
@WebServlet(name = "LoadPost", urlPatterns = {"/LoadPost"})
public class LoadPost extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        JsonObject responseJson = new JsonObject();

        responseJson.addProperty("message", "Unable to process");

        Criteria criteria1 = session.createCriteria(Post.class);
        criteria1.addOrder(Order.desc("id"));

        List<Post> postList = criteria1.list();

        JsonArray postArray = new JsonArray();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-dd-MM hh:mm a");

        for (Post post : postList) {
            JsonObject postObject = new JsonObject();
            postObject.addProperty("id", post.getId());
            postObject.addProperty("description", post.getDesc());
            postObject.addProperty("date_time", dateFormat.format(post.getDate_time()));
            postObject.addProperty("name", post.getUser().getFirst_name() + " " + post.getUser().getLast_name());
            postObject.addProperty("mobile", post.getUser().getMobile());
            postObject.addProperty("user_stutus", post.getUser().getUser_status().getId());

            String serverPath = request.getServletContext().getRealPath("");
            String newApplicationPath = serverPath.replace("build" + File.separator + "web", "web");

            String avatarImagePath = newApplicationPath + File.separator + "AvatarImage" + File.separator + post.getUser().getMobile() + ".png";
            File otherUserAvtarImageFile = new File(avatarImagePath);

            if (otherUserAvtarImageFile.exists()) {
                //image found
                postObject.addProperty("avater_image_found", true);
            } else {
                //avatrer image not found 
                postObject.addProperty("avater_image_found", false);
                postObject.addProperty("other_user_avater_letters", post.getUser().getFirst_name().charAt(0) + "" + post.getUser().getLast_name().charAt(0));
            }

            postArray.add(postObject);
        }

        responseJson.addProperty("success", true);
        responseJson.addProperty("message", "success");
        responseJson.add("jsonPostarray", gson.toJsonTree(postArray));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));
    }

}
