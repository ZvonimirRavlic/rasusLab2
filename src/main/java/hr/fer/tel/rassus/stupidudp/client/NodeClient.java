package hr.fer.tel.rassus.stupidudp.client;

import com.google.gson.Gson;
import hr.fer.tel.rassus.stupidudp.beans.Reading;
import hr.fer.tel.rassus.stupidudp.beans.ReadingServer;
import hr.fer.tel.rassus.stupidudp.beans.Tuple;
import hr.fer.tel.rassus.stupidudp.network.SimpleSimulatedDatagramSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import static hr.fer.tel.rassus.stupidudp.NodeApplication.sensor;
import static hr.fer.tel.rassus.stupidudp.NodeApplication.vector;

public class NodeClient implements Runnable {

    private static final Logger logger = Logger.getLogger(NodeClient.class.getName());
    private static final Gson gson = new Gson();
    private static final Charset charset = StandardCharsets.US_ASCII;
    private static final AtomicBoolean running = new AtomicBoolean(true);
    public static final Set<Reading> readingsAll = new ConcurrentSkipListSet<>();
    public static final Set<Reading> newReadings = new ConcurrentSkipListSet<>();
    private final String address;
    private final int port;

    public NodeClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    private void transmitReadings() throws IOException, InterruptedException {

        final DatagramSocket socket = new SimpleSimulatedDatagramSocket(0.3, 1000);
        final InetAddress inetAddress = InetAddress.getByName(address);
        final Map<Long, Reading> readings = new HashMap<>();
        final List<Long> confirmations = new LinkedList<>();
        while (running.get()) {
            final Reading newReading = sensor.getReading();
            vector.getOrDefault(sensor.getId(), new Tuple(sensor.getId(), 1)).setValue(vector.getOrDefault(sensor.getId(), new Tuple(sensor.getId(), 0)).getValue() + 1);
            newReading.setVector(vector.values().stream().toList());
            confirmations.add(newReading.getId());
            readings.put(newReading.getId(), newReading);
            newReadings.add(newReading);
            readingsAll.add(newReading);
            //logger.info("THREADID: " + Thread.currentThread().getId() + " UNCONFIRMED: " + confirmations.size() + " " + confirmations);
            confirmations.forEach(readingId -> {
                byte[] sendBuf = charset.encode(gson.toJson(readings.get(readingId))).array();
                final DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, inetAddress, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //logger.info("THREADID: " + Thread.currentThread().getId() + " CLIENT: Sent msg to: " + address + ":" + port + ". Msg content: " + gson.toJson(readings.get(readingId)));
            });
            while (true) {
                byte[] rcvBuf = new byte[1024];
                final DatagramPacket rcvPacket = new DatagramPacket(rcvBuf, rcvBuf.length);
                try {
                    socket.receive(rcvPacket);
                } catch (SocketTimeoutException e) {
                    break;
                } catch (IOException ex) {
                    Logger.getLogger(StupidUDPClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                //logger.info("THREADID: " + Thread.currentThread().getId() + " CLIENT: Recieved confirmation for: " + new String(rcvPacket.getData(), rcvPacket.getOffset(), rcvPacket.getLength()));
                confirmations.remove(Long.parseLong(new String(rcvPacket.getData(), rcvPacket.getOffset(), rcvPacket.getLength())));
            }
            Thread.sleep(1000L);
        }
        socket.close();
    }

    public static Set<Reading> stopClient() {
        running.set(false);
        logger.info("Shutting down client's");
        return readingsAll;
    }

    @Override
    public void run() {
        try {
            transmitReadings();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
