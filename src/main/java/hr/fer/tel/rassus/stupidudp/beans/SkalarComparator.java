package hr.fer.tel.rassus.stupidudp.beans;

import java.util.Comparator;

public class SkalarComparator implements Comparator<Reading> {
    @Override
    public int compare(Reading o1, Reading o2) {
        return o1.getTime().compareTo(o2.getTime());
    }
}
