package webserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.Main;
import main.ParseRequest;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class Webserver {
    public static void main(String[] args) {
        port(7000);

        staticFileLocation("/webserver/webfiles");

        post("/parse/full", Webserver::parseFull, Webserver::toJson);
        post("/parse/equation", Webserver::parseEquation, Webserver::toJson);
        post("/parse/partial", Webserver::parsePartial, Webserver::toJson);

        after((req, res) -> res.type("application/json"));
    }

    public static Object parseFull(Request request, Response response) {
        return Main.parseFull(parseRequest(request.body()).getText());
    }

    public static Object parseEquation(Request request, Response response) {
        return Main.parseEquation(parseRequest(request.body()).getText());
    }

    public static Object parsePartial(Request request, Response response) {
        return Main.parsePartial(parseRequest(request.body()).getText());
    }

    public static String toJson(Object object) {
        return new GsonBuilder().serializeNulls().create().toJson(object);
    }

    public static ParseRequest parseRequest(String text) {
        return new Gson().fromJson(text, ParseRequest.class);
    }
}
