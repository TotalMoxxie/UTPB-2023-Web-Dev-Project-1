import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class HttpServer {
    public static void main(String[] args) {
        int port = 80; // Port 80 for HTTP
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream outputStream = clientSocket.getOutputStream();
        ) {
            String request = reader.readLine();
            if (request != null) {
                System.out.println("Received request: " + request);

                // Parse the request to get the requested resource
                String[] parts = request.split(" ");
                String method = parts[0];
                String resource = parts[1];

                // Process the request and send response
                if ("GET".equals(method)) {
                    sendResponse(outputStream, resource);
                } else {
                    sendErrorResponse(outputStream, 400, "Bad Request");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(OutputStream outputStream, String resource) throws IOException {
        // Implement logic to handle different resources (HTML, CSS, images, etc.)
        // Here, we'll just send a simple response for demonstration purposes
        String response = "HTTP/1.1 200 OK\r\n\r\n";
        response += "<html><body><h1>Hello, World!</h1></body></html>";

        outputStream.write(response.getBytes());
        outputStream.flush();
        clientSocket.close();
    }

    private void sendErrorResponse(OutputStream outputStream, int statusCode, String statusText) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n\r\n";
        response += "<html><body><h1>" + statusCode + " " + statusText + "</h1></body></html>";

        outputStream.write(response.getBytes());
        outputStream.flush();
        clientSocket.close();
    }
}
