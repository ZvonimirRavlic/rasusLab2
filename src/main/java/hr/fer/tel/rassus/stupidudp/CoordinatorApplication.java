package hr.fer.tel.rassus.stupidudp;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class CoordinatorApplication {
    public static void main(String[] args) throws IOException {
        final Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", "127.0.0.1:9092");
        kafkaProps.put("group.id", "test-consumer-group");
        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        final KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaProps);
        final ProducerRecord<String, String> recordStart = new ProducerRecord<>("Command", "1", "Start");
        try {
            producer.send(recordStart);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Zaustavi senzore");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.readLine();
        final ProducerRecord<String, String> recordStop = new ProducerRecord<>("Command", "2", "Stop");
        try {
            producer.send(recordStop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}