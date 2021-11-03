package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Oleksandr Haleta
 * 2021
 */
public class Starter extends Thread {

    private static final Map<String, String> CONTENT_TYPES = new HashMap<>();
    static {
        CONTENT_TYPES.put("jpeg", "image/jpeg");
        CONTENT_TYPES.put("html", "text/html");
        CONTENT_TYPES.put("json", "application/json");
        CONTENT_TYPES.put("txt", "text/plain");
        CONTENT_TYPES.put("", "text/plain");
    }

    private Socket socket;
    private String directory;

    Starter(Socket socket, String directory) {
        this.socket = socket;
        this.directory = directory;
    }

    @Override
    public void run() {
        try(InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream()) {
                String url = getRequestUrl(inputStream);
                Path filePath = Path.of(directory + url);
                if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                    String extension = getFileExtension(filePath);
                    String type = CONTENT_TYPES.get(extension);
                    byte[] fileBytes = Files.readAllBytes(filePath);
                    sendHeader(outputStream, 200, "OK", type);
                    outputStream.write(fileBytes);
                    outputStream.flush();
                } else {
                    String type = CONTENT_TYPES.get("text");
                    sendHeader(outputStream, 404, "Not found", type);
                    outputStream.write("NOT FOUND".getBytes(StandardCharsets.UTF_8));
                }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRequestUrl(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream).useDelimiter("\r\n");
        String line = scanner.next();
        return line.split("\\s")[1];
    }

    private String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int extensionStart = fileName.lastIndexOf(".");
        return extensionStart == -1 ? "" : fileName.substring(extensionStart + 1);
    }

    private void sendHeader(OutputStream outputStream, int statusCode, String statusText, String type) {
        PrintStream printStream = new PrintStream(outputStream);
        printStream.printf("HTTP/1.1 %s %s\n", statusCode, statusText);
        printStream.printf("Content-Type %s\n", type);
    }

    /*public static void main(String[] args) {
        Server server = new Server();
        server.setPort(3000);
        server.setDirectory("src/main/resources/webapp");
        server.start();
    }*/
}

// GET http://localhost:3000/index.html
// GET /index.html HTTP/1.1
// path to resource = webapp + URI =>
// src/main/resources/webapp/index.html (вычитать этот файл и отдать браузеру)

// GET /css/style.css HTTP/1.1
// path to resource = webapp + URI =>
// src/main/resources/webapp/css/style.css (вычитать этот файл и отдать браузеру)

/*
в GET запросе в заголовке вычитать, какой файл нужно вычитать и отдать браузеру
(файл лежит в webapp)
 */