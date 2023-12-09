package hr.fer.tel.rassus.stupidudp.beans;

import java.util.Objects;

public class Tuple {
    private Integer sensorId;
    private Integer value;

    public Tuple(Integer sensorId, int value) {
        this.sensorId = sensorId;
        this.value = value;
    }

    public Integer getSensorId() {
        return sensorId;
    }

    public void setSensorId(Integer sensorId) {
        this.sensorId = sensorId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple tuple = (Tuple) o;
        return Objects.equals(sensorId, tuple.sensorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorId);
    }
}
