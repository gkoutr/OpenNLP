package spark.template.handlebars;

import K360NLP.Content;
import K360NLP.Parser;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.template.HandlebarsTemplateEngine;
import spark.utils.IOUtils;
import static spark.Spark.*;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GKoutroumpis on 2/3/2017.
 */
public class Main {

    private static Content content = new Content();

    public static void main(String[] args) {




        HashMap<String, Integer> m;

        get("/hello", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            //model.put("message", "Hello Handlebars!");
            return new ModelAndView(model, "hello.hbs"); // located in resources/templates
        }, new HandlebarsTemplateEngine());


       post("/hello:text", (Request request, Response response) -> {
            String a;
            a = request.queryParams("textbox");
            content = new Content(a);

            String text = content.GenerateGson();


            return text;
        });


        post("/hello:url", (Request request, Response response) -> {
            String a;
            a = request.queryParams("urlTextBox");
            Parser parser = new Parser(a);
            String URLContent = parser.GetContent();
            content = new Content(URLContent);


          //  HashMap<String,Integer> entities = content.MergeMaps("entities");
           // String text = content.HashMapToString(entities);
            String text = content.GenerateGson();
            return text;

        });


        //get("/hello", (req, res) -> "Hello World");
    }

}