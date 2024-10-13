/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.Chat_status;
import entity.User;
import entity.User_status;
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
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Admin
 */
@WebServlet(name = "LoadHomeData", urlPatterns = {"/LoadHomeData"})
public class LoadHomeData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);
        responseObject.addProperty("message", "Unable to process");
        try {

            Session session = HibernateUtil.getSessionFactory().openSession();

            String userId = request.getParameter("id");
            String name = request.getParameter("searchtext");
          

            User user = (User) session.get(User.class, Integer.parseInt(userId));

            User_status user_status = (User_status) session.get(User_status.class, 1);

            user.setUser_status(user_status);
            session.update(user);

            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.ne("id", user.getId()));
          
            criteria1.add(Restrictions.like("first_name", name, MatchMode.START));
            
            System.out.println(name);
            List<User> otheruserList = criteria1.list();

            JsonArray jsonchatarray = new JsonArray();
            for (User otheruser : otheruserList) {

                //get last chat
                Criteria criteria2 = session.createCriteria(Chat.class);
                criteria2.add(
                        Restrictions.or(
                                Restrictions.and(
                                        Restrictions.eq("from_user", user),
                                        Restrictions.eq("to_user", otheruser)
                                ),
                                Restrictions.and(
                                        Restrictions.eq("from_user", otheruser),
                                        Restrictions.eq("to_user", user)
                                )
                        )
                );
                criteria2.addOrder(Order.desc("id"));
                criteria2.setMaxResults(1);

                JsonObject jsonchatItem = new JsonObject();
                jsonchatItem.addProperty("other_user_id", otheruser.getId());
                jsonchatItem.addProperty("other_user_mobile", otheruser.getMobile());
                jsonchatItem.addProperty("other_user_name", otheruser.getFirst_name() + " " + otheruser.getLast_name());
                jsonchatItem.addProperty("other_user_status", otheruser.getUser_status().getId());
                
              

                String serverPath = request.getServletContext().getRealPath("");
                String newApplicationPath = serverPath.replace("build" + File.separator + "web", "web");

                String avatarImagePath = newApplicationPath + File.separator + "AvatarImage" + File.separator + otheruser.getMobile() + ".png";
                File otherUserAvtarImageFile = new File(avatarImagePath);

                if (otherUserAvtarImageFile.exists()) {
                    //image found
                    jsonchatItem.addProperty("avater_image_found", true);
                } else {
                    //avatrer image not found 
                    jsonchatItem.addProperty("avater_image_found", false);
                    jsonchatItem.addProperty("other_user_avater_letters", otheruser.getFirst_name().charAt(0) + "" + otheruser.getLast_name().charAt(0));
                }

                List<Chat> dbChatList = criteria2.list();
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

                if (dbChatList.isEmpty()) {
                    //no last chat
                    jsonchatItem.addProperty("message", "Let's start start new chat");
                    jsonchatItem.addProperty("dateTime", dateFormat.format(user.getRegistered_date_time()));
                    jsonchatItem.addProperty("chat_status_id", 1);
                } else {
                    //found last chat
                    jsonchatItem.addProperty("message", dbChatList.get(0).getMessage());
                    jsonchatItem.addProperty("dateTime", dateFormat.format(dbChatList.get(0).getDate_time()));
                    jsonchatItem.addProperty("chat_status_id", dbChatList.get(0).getChat_staus().getId());//1=seen , 2=unseen
                    
                    if(dbChatList.get(0).getFrom_user() == user){
                      jsonchatItem.addProperty("tick_from", true);
                    }
                }

                Chat_status chat_status = (Chat_status) session.get(Chat_status.class, 2);

                Criteria criteria3 = session.createCriteria(Chat.class);
                criteria3.add(Restrictions.eq("from_user", otheruser));
                criteria3.add(Restrictions.eq("to_user", user));
               criteria3.add( Restrictions.eq("chat_staus", chat_status));

                int unseenmsgcount = criteria3.list().size();
            
                jsonchatItem.addProperty("unseen_message_count", unseenmsgcount);

                jsonchatarray.add(jsonchatItem);

            }

            responseObject.addProperty("success", true);
            responseObject.addProperty("message", "success");
            responseObject.add("user", gson.toJsonTree(user));
            responseObject.add("jsonchatarray", gson.toJsonTree(jsonchatarray));

            session.beginTransaction().commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
