package hr.fer.tel.rassus.stupidudp.server;

import com.google.gson.Gson;
import hr.fer.tel.rassus.stupidudp.beans.ReadingServer;
import hr.fer.tel.rassus.stupidudp.beans.Tuple;
import hr.fer.tel.rassus.stupidudp.network.SimpleSimulatedDatagramSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static hr.fer.tel.rassus.stupidudp.NodeApplication.*;

public class NodeServer implements Runnable {

    private static final Logger logger = Logger.getLogger(NodeServer.class.getName());
    private static final Gson gson = new Gson();
    public static final Set<ReadingServer> readings = new ConcurrentSkipListSet<>();
    public static final Set<ReadingServer> newReadings = new ConcurrentSkipListSet<>();
    private static final AtomicBoolean running = new AtomicBoolean(true);
    private final int port;

    public NodeServer(int port) {
        this.port = port;
    }

    public void startServer() throws IOException {
        byte[] rcvBuf = new byte[10000000];
        logger.info("UDP server started on port: " + port);
        final DatagramSocket socket = new SimpleSimulatedDatagramSocket(port, 0.3, 1000);

        while (running.get()) {
            final DatagramPacket packet = new DatagramPacket(rcvBuf, rcvBuf.length);
            socket.receive(packet);
            final String rcvStr = new String(packet.getData(), packet.getOffset(), packet.getLength());
            logger.info("SERVER: received: " + rcvStr + " On port: " + packet.getPort());
            final ReadingServer reading = gson.fromJson(rcvStr, ReadingServer.class);
            reading.setPort(packet.getPort());
            readings.add(reading);
            newReadings.add(reading);
            vector.get(sensor.getId()).setValue(vector.get(sensor.getId()).getValue() + 1);
            reading.getVector().stream().filter(e -> !Objects.equals(e.getSensorId(), sensor.getId())).forEach(e -> {
                Tuple t = vector.getOrDefault(e.getSensorId(), new Tuple(e.getSensorId(), e.getValue()));
                if (t.getValue() < e.getValue()) {
                    t.setValue(e.getValue());
                }
                vector.put(e.getSensorId(), t);
            });
            clock.updateTimeMillis(reading.getTime());
            final DatagramPacket sendPacket = new DatagramPacket(reading.getId().toString().getBytes(), reading.getId().toString().getBytes().length, packet.getAddress(), packet.getPort());
            socket.send(sendPacket);
        }
    }

    public static Set<ReadingServer> stopServer() {
        running.set(false);
        logger.info("Shutting down server");
        return readings;
    }

    @Override
    public void run() {
        try {
            startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
