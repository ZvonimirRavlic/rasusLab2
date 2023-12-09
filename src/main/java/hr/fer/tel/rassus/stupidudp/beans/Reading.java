package hr.fer.tel.rassus.stupidudp.beans;

import com.opencsv.bean.CsvBindByName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Reading implements Comparable<Reading>{

    private Long id;
    @CsvBindByName(column = "NO2")
    private Double no2;
    private Long time;
    private List<Tuple> vector = new ArrayList<>();

    public Reading(Long id, Double no2, long time, List<Tuple> vector) {
        this.id = id;
        this.no2 = no2;
        this.time = time;
        this.vector = vector;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getNo2() {
        return no2;
    }

    public void setNo2(Double no2) {
        this.no2 = no2;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public List<Tuple> getVector() {
        return vector;
    }

    public void setVector(List<Tuple> vector) {
        this.vector = vector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reading reading = (Reading) o;
        return Objects.equals(id, reading.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Reading o) {
        return this.getId().compareTo(o.getId());
    }
}

