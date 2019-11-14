package sample;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;

public class TCPClientHandler extends Thread {
    private final Socket socket;
    private Double bytesReceived = 0.0;
    public Boolean running = true;
    private final Main guiClass;
    private final TCPServer tcpServer;
    @Override
    public void run() {
        InputStream in = null;
        DataInputStream dis = null;
        try {
            Instant startTime = Instant.now();
            in = socket.getInputStream();
            dis = new DataInputStream(in);
            int dataSize = dis.readInt();
            System.out.println(dataSize);
            while (running) {
                byte[] dataReceived = dis.readNBytes(dataSize);
                if (dataReceived.length == 0) { // If no data was sent assume that connection was closed
                    System.out.println("TCP Client socket disconnected!");
                    running = false;
                } else {
                    bytesReceived += dataReceived.length / 1000.0;
                    long timeElapsed = Duration.between(startTime, Instant.now()).toMillis();
                    guiClass.setTcpDataSizeProperty(bytesReceived);
                    guiClass.setTcpTimeElapsedProperty(timeElapsed);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dis.close();
                in.close();
                socket.close();
                tcpServer.TCPClientCommunicator = null;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    void disconnect() {
        running = false;
    }

    TCPClientHandler(Socket socket, Main guiClass, TCPServer tcpServer) {
        this.socket = socket;
        this.guiClass = guiClass;
        this.tcpServer = tcpServer;
        System.out.println("TCP client connected");
    }
}
