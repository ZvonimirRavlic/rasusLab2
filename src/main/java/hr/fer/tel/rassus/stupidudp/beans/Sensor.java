package hr.fer.tel.rassus.stupidudp.beans;

import com.opencsv.bean.CsvToBeanBuilder;
import hr.fer.tel.rassus.stupidudp.NodeApplication;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static hr.fer.tel.rassus.stupidudp.NodeApplication.clock;
import static hr.fer.tel.rassus.stupidudp.NodeApplication.startupTime;

public class Sensor {

    private Integer id;
    private IpPort ipPort;
    private final List<Reading> readings;

    public Sensor(Integer id, Integer port) throws FileNotFoundException {
        this.id = id;
        this.ipPort = new IpPort(id.toString(), "localhost", port);
        this.readings = new CsvToBeanBuilder(new FileReader("readings.csv"))
                .withType(Reading.class).build().parse();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public IpPort getIpPort() {
        return ipPort;
    }

    public void setIpPort(IpPort ipPort) {
        this.ipPort = ipPort;
    }

    public Reading getReading() {
        final Reading reading = readings.get((int) ChronoUnit.SECONDS.between(startupTime, LocalDateTime.now()) % 100);
        reading.setId(NodeApplication.id.get());
        NodeApplication.id.set(NodeApplication.id.get() + 1);
        reading.setTime(clock.currentTimeMillis());
        return reading;
    }
}
