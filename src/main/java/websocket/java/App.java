package websocket.java;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class App {

    public static void main(String[] args) throws IOException {

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(new InetSocketAddress(0).getAddress(), 8080), 0);
        httpServer.createContext("/", new IndexHandler());
        httpServer.setExecutor(Executors.newSingleThreadExecutor());
        httpServer.start();

        SimpleWebsocketServer webSocketServer = new SimpleWebsocketServer(new InetSocketAddress(new InetSocketAddress(0).getAddress(), 9090));
        webSocketServer.start();
    }

    private static class IndexHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = new Scanner(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("index.html")), StandardCharsets.UTF_8).useDelimiter("\\A").next();
            exchange.sendResponseHeaders(200, html.length());
            OutputStream os = exchange.getResponseBody();
            os.write(html.getBytes());
            os.close();
        }
    }

    private static class SimpleWebsocketServer extends WebSocketServer {

        public SimpleWebsocketServer(InetSocketAddress inetSocketAddress) {
            super(inetSocketAddress);
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            conn.send("Welcome to the server!");
            System.out.printf("%s connected!\n", conn.getRemoteSocketAddress().getAddress().getHostAddress());
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.printf("%s has disconnected!\n", conn);
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            System.out.printf("%s: %s\n", conn, message);
            if (message.equals("PING")) {
                conn.send("PONG!");
            } else if (message.equals("DOES IT WORK?")) {
                conn.send("YES IT DOES!");
            }
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            System.out.println(conn + ": " + ex.getMessage());
        }

        @Override
        public void onStart() {
            System.out.println("STARTED!");
        }
    }

}