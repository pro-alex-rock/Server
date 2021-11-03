package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Oleksandr Haleta
 * 2021
 */
public class Server {

    private int port;
    private String directory;

    public Server() {
    }

    public Server(int port, String directory) {
        this.port = port;
        this.directory = directory;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(3000)) {
            while (true) {
                Socket socket = serverSocket.accept();
                print(socket);
                Starter starter = new Starter(socket, directory);
                starter.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.setPort(3000);
        server.setDirectory("src/main/resources/webapp");
        server.start();
    }

    private void print(Socket socket) {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            String line = "";
            while (!(line = bufferedReader.readLine()).isEmpty()) {
                System.out.println(line);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(3000)) {
            try(Socket socket = serverSocket.accept();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));) {
                String line = "";
                while (!(line = bufferedReader.readLine()).isEmpty()) {
                    System.out.println(line);
                }
                System.out.println("Ready to write response");
                bufferedWriter.write("HTTP/1.1 200 OK");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write("Hello browser!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/
}
