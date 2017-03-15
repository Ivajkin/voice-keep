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
        //stmt.executeUpdate("INSERT INTO mantra(wisdom,tick) VALUES (\"" + req.params("wisdom") + "\",now())");
        stmt.executeUpdate("INSERT INTO mantra(wisdom,tick) VALUES (\"TEST\",now())");
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

        while (rs.next()) {
          output += ( "\"" + rs.getString("wisdom") + "\"");
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

  }

}
