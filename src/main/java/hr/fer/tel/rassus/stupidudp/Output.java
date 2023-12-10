package hr.fer.tel.rassus.stupidudp;

import hr.fer.tel.rassus.stupidudp.beans.Reading;
import hr.fer.tel.rassus.stupidudp.beans.ReadingServer;
import hr.fer.tel.rassus.stupidudp.beans.SkalarComparator;
import hr.fer.tel.rassus.stupidudp.beans.VectorComparator;
import hr.fer.tel.rassus.stupidudp.client.NodeClient;
import hr.fer.tel.rassus.stupidudp.server.NodeServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class Output implements Runnable {


    private static final Logger logger = Logger.getLogger(Output.class.getName());
    private static final AtomicBoolean running = new AtomicBoolean(true);

    @Override
    public void run() {
        while (running.get()) {
            try {
                Thread.sleep(5000L);
                generateOutput();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void generateOutput() {
        final List<Reading> readings = new ArrayList<>(NodeClient.newReadings.stream().toList());
        NodeClient.newReadings.clear();
        readings.addAll(NodeServer.newReadings.stream().map(ReadingServer::toReading).toList());
        NodeServer.newReadings.clear();
        final SkalarComparator skalarComparator = new SkalarComparator();
        readings.sort(skalarComparator);
        logger.info("SORTED BY SKALAR: " + readings);
        final VectorComparator vectorComparator = new VectorComparator();
        readings.sort(vectorComparator);
        logger.info("SORTED BY VECTOR: " + readings);
        logger.info("AVERAGE: " + readings.stream().filter(reading -> reading.getNo2() != null).mapToDouble(Reading::getNo2).average().orElse(0));
    }

    private static void generateOutputEnd() {
        final List<Reading> readings = new ArrayList<>(NodeClient.readingsAll.stream().toList());
        readings.addAll(NodeServer.readings.stream().map(ReadingServer::toReading).toList());
        final SkalarComparator skalarComparator = new SkalarComparator();
        readings.sort(skalarComparator);
        logger.info("FINAL SORT BY SKALAR: " + readings);
        final VectorComparator vectorComparator = new VectorComparator();
        readings.sort(vectorComparator);
        logger.info("FINAL SORT BY VECTOR: " + readings);
        logger.info("AVERAGE: " + readings.stream().filter(reading -> reading.getNo2() != null).mapToDouble(Reading::getNo2).average().orElse(0));
    }

    public static void stopOutput() {
        running.set(false);
        generateOutputEnd();
    }
}
