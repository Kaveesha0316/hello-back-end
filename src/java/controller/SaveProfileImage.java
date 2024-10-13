/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig
@WebServlet(name = "SaveProfileImage", urlPatterns = {"/SaveProfileImage"})
public class SaveProfileImage extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responsejson = new JsonObject();
        responsejson.addProperty("message", "Error");

        String mobile = request.getParameter("mobile");
        Part avaterImage = request.getPart("avaterImage");

        if (mobile.isEmpty()) {
            responsejson.addProperty("message", "no mobile number");
        } else {
            if (avaterImage != null) {

                String serverPath = request.getServletContext().getRealPath("");
                String newApplicationPath = serverPath.replace("build" + File.separator + "web", "web");

                String avatarImagePath = newApplicationPath + File.separator + "AvatarImage" + File.separator + mobile + ".png";
                File file = new File(avatarImagePath);
                Files.copy(avaterImage.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                responsejson.addProperty("success", true);
                responsejson.addProperty("message", "image uploaded");
            }

        }
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responsejson));

    }

}
