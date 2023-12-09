package hr.fer.tel.rassus.stupidudp.beans;

import java.util.Comparator;

import static hr.fer.tel.rassus.stupidudp.NodeApplication.sensor;

public class VectorComparator implements Comparator<Reading> {


    @Override
    public int compare(Reading o1, Reading o2) {
        final Tuple t1 = o1.getVector().stream().filter(e -> e.getSensorId().equals(sensor.getId())).findAny().get();
        final Tuple t2 = o2.getVector().stream().filter(e -> e.getSensorId().equals(sensor.getId())).findAny().get();
        return t1.getValue().compareTo(t2.getValue());
    }
}
