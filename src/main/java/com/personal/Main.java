package com.personal;

import com.personal.model.CourseModel;
import com.personal.model.CourseModelDAO;
import com.personal.model.SimpleCourseModelDAO;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args){
        //get("/", (req, res) -> "Home page");
        staticFileLocation("/public");
        CourseModelDAO dao = new SimpleCourseModelDAO();

        before((rq, rs)->{
           if(rq.cookie("username") != null){
               rq.attribute("username", rq.cookie("username"));
           }
        });

        before("/courses", (rq, rs)->{
            if (rq.cookie("username") == null) {
                rs.redirect("/");
                halt();
            }
        });

        // hello.hbs file is in resources/templates directory
        get("/", (rq, rs) -> {
            Map<String, String> model = new HashMap<>();
            model.put("username", rq.attribute("username"));
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        post("/sign-in", (rq, rs) -> {
            Map<String, String> map1 = new HashMap<>();
            String username = rq.queryParams("username");

            if(username.equals("")){
                username = "Default user";
            }
            String cookieenc = URLEncoder.encode(username, "UTF-8");

            rs.cookie("username", cookieenc);
            //rs.cookie("username", username);

            map1.put("username", username);

            return new ModelAndView(map1, "sign-in.hbs");
        }, new HandlebarsTemplateEngine());

        get("/courses", (rq, rs) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("courses", dao.findAll());
            return new ModelAndView(model, "courses.hbs");
        }, new HandlebarsTemplateEngine());

        post("/courses", (rq, rs) -> {
            String title = rq.queryParams("course");
            CourseModel courseModel = new CourseModel(title, rq.attribute("username"));
            dao.add(courseModel);
            rs.redirect("/courses");
            return null;
        });
    }
}
