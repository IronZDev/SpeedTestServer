package sample;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPServer extends Thread {
    private Integer port;
    private Boolean running = true;
    private Socket clientSocket = null;
    TCPClientHandler TCPClientCommunicator = null;
    private ServerSocket mainServerSocket;
    private final Main guiClass;
    @Override
    public void run() {
        try {
            mainServerSocket = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println("Could not initialize server socket: "+e.getMessage());
            return;
        }
        System.out.println("TCP Server started on "+ mainServerSocket.getLocalSocketAddress());
        while (running) {
            try {
                clientSocket = mainServerSocket.accept();
                if (TCPClientCommunicator == null) {
                    TCPClientCommunicator = new TCPClientHandler(clientSocket, guiClass, this);
                    TCPClientCommunicator.start();
                } else {
                    OutputStream out = clientSocket.getOutputStream();
                    out.write("BUSY".getBytes());
                    System.out.println("TCP connection refused");
                    clientSocket.close();
                }
            } catch (SocketException e) {
                System.out.println("TCP Client socket closed");
                return;
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return;
            }
        }
    }

    TCPServer(int port, Main guiClass) {
        this.port = port;
        this.start();
        this.guiClass = guiClass;
    }

    void disconnect() {
        running = false;
        try {
            if (TCPClientCommunicator != null) {
                TCPClientCommunicator.disconnect();
            }
            if (clientSocket != null && clientSocket.isConnected()) {
                clientSocket.close();
            }
            mainServerSocket.close();
            System.out.println("TCP server stopped!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
