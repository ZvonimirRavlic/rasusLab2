/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package hr.fer.tel.rassus.stupidudp.network;

import java.util.Random;
import java.util.logging.Logger;

/**
 * @author Aleksandar
 */
public class EmulatedSystemClock {
    private static final Logger logger = Logger.getLogger(EmulatedSystemClock.class.getName());
    private final long startTime;
    private long latency;
    private final double jitter;

    public EmulatedSystemClock() {
        startTime = System.currentTimeMillis();
        Random r = new Random();
        jitter = (r.nextInt(20)) / 100d;
    }

    public long currentTimeMillis() {
        long current = System.currentTimeMillis();
        long diff = current + latency - startTime;
        double coef = diff / 1000d;
        return startTime + Math.round(diff * Math.pow((1 + jitter), coef));
    }

    public void updateTimeMillis(long remoteTime) {
        long diff = remoteTime - currentTimeMillis();
        if (diff > 0) {
            latency = latency + diff;
        }
    }


}