package sample;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.Duration;
import java.time.Instant;

public class UDPServer extends Thread {
    private final Main guiClass;
    private int port;
    private Boolean running = true;
    private double bytesReceived = 0.0;
    private DatagramSocket socket = null;
    @Override
    public void run() {

        try {
            socket = new DatagramSocket(port);
        } catch (Exception e) {
            System.out.println("Cannot create socket: "+e.getMessage());
        }
        try {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            String msg = new String(packet.getData());
            int dataSize = Integer.parseInt(msg.trim());
            System.out.println(msg);
            buf = new byte[dataSize];
            Instant startTime = Instant.now();
            packet = new DatagramPacket(buf, buf.length, address, port);
            while (running) {
                socket.receive(packet);
                byte[] data = packet.getData();
                System.out.println();
                if (data != null && new String(data).contains("STOP")) {
                    running = false;
                } else {
                    bytesReceived += data.length;
                    long timeElapsed = Duration.between(startTime, Instant.now()).toMillis();
                    guiClass.setUdpDataSizeProperty(Math.round(bytesReceived));
                    guiClass.setUdpTimeElapsedProperty(timeElapsed);
                }
            }
        } catch (SocketException e) {
            System.out.println("UDP socket closed");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldn't get the message : "+e.getMessage());
        } finally {
            System.out.println("UDP socket closed");
            disconnect();
        }
    }

    UDPServer(int port, Main guiClass) {
        this.port = port;
        this.start();
        this.guiClass = guiClass;
    }

    void disconnect() {
        socket.close();
    }
}
