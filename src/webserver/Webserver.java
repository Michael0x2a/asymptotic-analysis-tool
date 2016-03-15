package webserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.Main;
import main.ParseRequest;
import org.apache.commons.lang3.StringEscapeUtils;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static spark.Spark.*;

public class Webserver {
    public static void main(String[] args) {
        port(7000);

        staticFileLocation("/webserver/webfiles");

        post("/parse/full", Webserver::parseFull, Webserver::toJson);
        post("/parse/equation", Webserver::parseEquation, Webserver::toJson);
        post("/parse/partial", Webserver::parsePartial, Webserver::toJson);
        post("/load", Webserver::loadFile, Webserver::toJson);

        after((req, res) -> res.type("application/json"));
    }

    public static Object parseFull(Request request, Response response) {
        return Main.parseFull(parseRequest(request.body()).getText());
    }

    public static Object parseEquation(Request request, Response response) {
        return Main.parseEquation(parseRequest(request.body()).getText());
    }

    public static Object parsePartial(Request request, Response response) {
        String text = parseRequest(request.body()).getText();
        System.out.println(text);
        return Main.parsePartial(text);
    }

    public static Object loadFile(Request request, Response response) {
        try {
            String name = parseRequest(request.body()).getText();
            String text = readFile("sample/" + name + ".java", Charset.defaultCharset());
            return new ParseRequest(text);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new ParseRequest("IOException");
        }
    }

    public static String toJson(Object object) {
        return new GsonBuilder().serializeNulls().create().toJson(object);
    }

    public static ParseRequest parseRequest(String text) {
        return new Gson().fromJson(StringEscapeUtils.unescapeHtml4(text), ParseRequest.class);
    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
