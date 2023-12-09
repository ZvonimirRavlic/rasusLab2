package hr.fer.tel.rassus.stupidudp;

import com.google.gson.Gson;
import hr.fer.tel.rassus.stupidudp.beans.IpPort;
import hr.fer.tel.rassus.stupidudp.beans.Sensor;
import hr.fer.tel.rassus.stupidudp.beans.Tuple;
import hr.fer.tel.rassus.stupidudp.client.NodeClient;
import hr.fer.tel.rassus.stupidudp.network.EmulatedSystemClock;
import hr.fer.tel.rassus.stupidudp.server.NodeServer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class NodeApplication {

    private static final Logger logger = Logger.getLogger(NodeApplication.class.getName());
    public static final EmulatedSystemClock clock = new EmulatedSystemClock();
    public static final LocalDateTime startupTime = LocalDateTime.now();
    public static final Map<Integer, Tuple> vector = new ConcurrentHashMap<>();
    public static AtomicLong id = new AtomicLong(1L);
    public static Sensor sensor;
    private static final Gson gson = new Gson();
    private static final List<IpPort> sensors = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {

        sensor = new Sensor(Integer.valueOf(args[0]), Integer.valueOf(args[1]));
        logger.info("Sensor created (id = " + sensor.getId() + ", port:" + sensor.getIpPort().getPort() + ").");
        final Tuple tuple = new Tuple(sensor.getId(), 1);
        vector.put(sensor.getId(), tuple);
        final Properties kafkaProps = setProperties();

        final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaProps);
        consumer.subscribe(List.of("Command", "Register"));

        while (true) {
            final ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            if (records.records("Command").iterator().hasNext() && records.records("Command").iterator().next().value().equals("Start")) {
                logger.info("Start message received from coordinator starting registration process.");
                break;
            }
        }
        consumer.commitAsync();

        final KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaProps);
        final ProducerRecord<String, String> registerRecord = new ProducerRecord<>("Register", sensor.getId().toString(), gson.toJson(sensor.getIpPort()));
        try {
            producer.send(registerRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(new NodeServer(sensor.getIpPort().getPort())).start();
        new Thread(new Output()).start();

        while (true) {
            final ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
            if (records.records("Register").iterator().hasNext()) {
                for (ConsumerRecord<String, String> consumerRecord : records) {
                    final IpPort newSensor = gson.fromJson(consumerRecord.value(), IpPort.class);
                    if (!newSensor.getId().equals(sensor.getId().toString()) && !sensors.contains(newSensor)) {
                        sensors.add(newSensor);
                        new Thread(new NodeClient(newSensor.getAddress(), newSensor.getPort())).start();
                        logger.info("Sensor with id " + newSensor.getId() + " received");
                    }
                }
            } else if (records.records("Command").iterator().hasNext() && records.records("Command").iterator().next().value().equals("Stop")) {
                logger.info("Stop message received from coordinator stopping the sensor.");
                NodeServer.stopServer();
                NodeClient.stopClient();
                Output.stopOutput();
                System.exit(0);
            }
        }
    }

    private static Properties setProperties() {
        final Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", "127.0.0.1:9092");
        kafkaProps.put("group.id", sensor.getId().toString());
        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return kafkaProps;
    }
}
