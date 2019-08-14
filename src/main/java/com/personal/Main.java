package com.personal;

import com.personal.model.CourseModel;
import com.personal.model.CourseModelDAO;
import com.personal.model.NotFoundException;
import com.personal.model.SimpleCourseModelDAO;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
    private static final String FLASH_MESSAGE = "flash_message";

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
                setFlashMessage(rq,"Please sign in first");
                rs.redirect("/");
                halt();
            }
        });

        // hello.hbs file is in resources/templates directory
        get("/", (rq, rs) -> {
            Map<String, String> model = new HashMap<>();
            model.put("username", rq.attribute("username"));
            model.put("flashMessage", captureFlashMessage(rq));
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
            model.put("flashMessage", captureFlashMessage(rq));
            return new ModelAndView(model, "courses.hbs");
        }, new HandlebarsTemplateEngine());

        post("/courses", (rq, rs) -> {
            String title = rq.queryParams("course");
            CourseModel courseModel = new CourseModel(title, rq.attribute("username"));
            dao.add(courseModel);
            rs.redirect("/courses");
            return null;
        });

        get("/courses/:slug", (rq, rs) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("course", dao.findBySlug(rq.params("slug")));
            return new ModelAndView(model, "course.hbs");
        }, new HandlebarsTemplateEngine());

        post("/courses/:slug/rate", (rq, rs) -> {
            CourseModel course = dao.findBySlug(rq.params("slug"));
            boolean added = course.addRatePerson(rq.attribute("username"));
            if(added){
                setFlashMessage(rq, "Your rate was submitted");
            }else{
                setFlashMessage(rq, "You already rated");
            }
            rs.redirect("/courses");
            return null;
        });

        exception(NotFoundException.class, (exec, rq, rs) -> {
            rs.status(404);
            HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();
            String html = engine.render(new ModelAndView(null, "not-found.hbs"));
            rs.body(html);
        });
    }

    private static void setFlashMessage(Request rq, String message) {
        rq.session().attribute(FLASH_MESSAGE, message);
    }
    private static String getFlashMessage(Request rq){
        if(rq.session(false) == null){
            return null;
        }
        if(!rq.session().attributes().contains(FLASH_MESSAGE)){
            return null;
        }
        return (String) rq.session().attribute(FLASH_MESSAGE);
    }

    private static String captureFlashMessage(Request rq){
        String message = getFlashMessage(rq);
        if(message != null){
            rq.session().removeAttribute(FLASH_MESSAGE);
        }
        return message;
    }
}
