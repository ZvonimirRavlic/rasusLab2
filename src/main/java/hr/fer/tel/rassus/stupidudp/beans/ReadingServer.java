package hr.fer.tel.rassus.stupidudp.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReadingServer {
    private Long id;
    private Integer port;
    private Double no2;
    private long time;
    private List<Tuple> vector = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Double getNo2() {
        return no2;
    }

    public void setNo2(Double no2) {
        this.no2 = no2;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
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
        ReadingServer that = (ReadingServer) o;
        return Objects.equals(id, that.id) && Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, port);
    }
}
