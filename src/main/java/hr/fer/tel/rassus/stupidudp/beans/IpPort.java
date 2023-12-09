package hr.fer.tel.rassus.stupidudp.beans;

import java.util.Objects;

public class IpPort {

    private String id;
    private String address;
    private Integer port;

    public IpPort(String id, String address, Integer port) {
        this.id = id;
        this.address = address;
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpPort ipPort = (IpPort) o;
        return Objects.equals(id, ipPort.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
