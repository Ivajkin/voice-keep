import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import static spark.Spark.*;

import pro.tmedia.CardController;
import pro.tmedia.CardService;
import pro.tmedia.JsonUtil;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;

import com.heroku.sdk.jdbc.DatabaseUrl;

public class Main {

  public static void main(String[] args) {

    port(Integer.valueOf(System.getenv("PORT")));
    staticFileLocation("/public");

    new CardController(new CardService());

    /*get("/is-server-available-status", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Server is available!");

            return new ModelAndView(attributes, "index.ftl");
        }, new FreeMarkerEngine());*/

    get("/is-server-available-status", (req, res) -> "Server is available!", JsonUtil.json());


    get("/depot", (req, res) -> {
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

    /* Using DB with recording of "mantras" */
    get("/", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
      try {
        connection = DatabaseUrl.extract().getConnection();

        Statement stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS mantra (wisdom varchar, tick timestamp)");
        //stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
        ResultSet rs = stmt.executeQuery("SELECT wisdom FROM mantra");

        ArrayList<String> output = new ArrayList<String>();
        output.add("<button>Записать</button><button>Сохранить</button><div id=\"speech-box\">...</div><br/>");
        while (rs.next()) {
          //output.add( "Read from DB: " + rs.getTimestamp("tick"));
          //output.add( "Read from DB: " + rs.getString("wisdom"));
          output.add( "<div class=\"wisdom\" style=\"background: #CCCCCC;\">" + rs.getString("wisdom") + "</div>");
        }

        attributes.put("results", output);
        return new ModelAndView(attributes, "db.ftl");
      } catch (Exception e) {
        attributes.put("message", "There was an error: " + e);
        return new ModelAndView(attributes, "error.ftl");
      } finally {
        if (connection != null) try{connection.close();} catch(SQLException e){}
      }
    }, new FreeMarkerEngine());


    get("/mantra/create", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
      try {
        connection = DatabaseUrl.extract().getConnection();

        Statement stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS mantra (wisdom varchar, tick timestamp)");
        stmt.executeUpdate("INSERT INTO mantra (wisdom,tick) VALUES ('" + req.queryParams("wisdom") + "',now())");
      } catch (Exception e) {
          //attributes.put("message", "There was an error: " + e);
          //return new ModelAndView(attributes, "error.ftl");
        return "There was an error: " + e;
      } finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
      }
      return "Created OK";
    });

    get("/mantra/list", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
      String output = "[";
      try {
        connection = DatabaseUrl.extract().getConnection();

        Statement stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS mantra (wisdom varchar, tick timestamp)");
        ResultSet rs = stmt.executeQuery("SELECT wisdom FROM mantra");

        if(rs.next())
          output += ( "\"" + rs.getString("wisdom") + "\"");
        while (rs.next()) {
          output += ( ", \"" + rs.getString("wisdom") + "\"");
        }

      } catch (Exception e) {
        //attributes.put("message", "There was an error: " + e);
        //return new ModelAndView(attributes, "error.ftl");
        return "There was an error: " + e;
      } finally {
        if (connection != null) try{connection.close();} catch(SQLException e){}
      }
      output += "]";
      return output;
    });

    enableCORS("http://*.jsbin.com", "GET", "");
  }
  // Enables CORS on requests. This method is an initialization method and should be called once.
  private static void enableCORS(final String origin, final String methods, final String headers) {

    options("/*", (request, response) -> {

      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });

    before((request, response) -> {
      response.header("Access-Control-Allow-Origin", origin);
      response.header("Access-Control-Request-Method", methods);
      response.header("Access-Control-Allow-Headers", headers);
      // Note: this may or may not be necessary in your particular application
      response.type("application/json");
    });
  }

}
