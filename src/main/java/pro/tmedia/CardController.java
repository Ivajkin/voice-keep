package pro.tmedia; /**
 * Created by TIvaykin on 04.03.2017.
 */
import com.heroku.sdk.jdbc.DatabaseUrl;
import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static pro.tmedia.JsonUtil.*;
import static spark.Spark.after;
import static spark.Spark.get;

public class CardController {

    public CardController(final CardService cardService) {

        get("/cards", (req, res) -> {
            res.type("application/json");
            return cardService.getAllCards();
        }, JsonUtil.json());


        get("/cardsCRUD", (req, res) -> {
            Connection connection = null;
            Map<String, Object> attributes = new HashMap<>();
            try {
                connection = DatabaseUrl.extract().getConnection();

                Statement stmt = connection.createStatement();
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
                stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
                ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

                ArrayList<String> output = new ArrayList<String>();
                while (rs.next()) {
                    output.add( "Read from DB: " + rs.getTimestamp("tick"));
                }

                attributes.put("results", output);
                return new ModelAndView(attributes, "depot.ftl");
            } catch (Exception e) {
                attributes.put("message", "There was an error: " + e);
                return new ModelAndView(attributes, "error.ftl");
            } finally {
                if (connection != null) try{connection.close();} catch(SQLException e){}
            }
        }, new FreeMarkerEngine());


        get("/cardComments", (req, res) -> "<div id=\"fb-root\"></div>\n" +
                "<script>(function(d, s, id) {\n" +
                "  var js, fjs = d.getElementsByTagName(s)[0];\n" +
                "  if (d.getElementById(id)) return;\n" +
                "  js = d.createElement(s); js.id = id;\n" +
                "  js.src = \"//connect.facebook.net/ru_RU/sdk.js#xfbml=1&version=v2.8&appId=1320619968005786\";\n" +
                "  fjs.parentNode.insertBefore(js, fjs);\n" +
                "}(document, 'script', 'facebook-jssdk'));</script>" +
                "<div class=\"fb-like\" data-href=\"https://developers.facebook.com/docs/plugins/\" data-layout=\"standard\" data-action=\"like\" data-size=\"small\" data-show-faces=\"true\" data-share=\"true\"></div>" +
                "<div class=\"fb-comments\" data-href=\"https://developers.facebook.com/docs/plugins/comments#configurator\" data-numposts=\"5\"></div>");
                // "<div class=\"fb-like\" data-href=\"http://business-app-backend.herokuapp.com/card/" + req.params("card_id") + "\" data-layout=\"standard\" data-action=\"like\" data-size=\"small\" data-show-faces=\"true\" data-share=\"true\"></div>" +
                // "<div class=\"fb-comments\" data-href=\"http://business-app-backend.herokuapp.com/card/" + req.params("card_id") + "\" data-numposts=\"5\"></div>");
    }
}