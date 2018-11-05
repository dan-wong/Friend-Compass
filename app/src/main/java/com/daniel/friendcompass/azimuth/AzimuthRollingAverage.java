package com.daniel.friendcompass.azimuth;

import java.util.concurrent.LinkedBlockingQueue;

public class AzimuthRollingAverage {
    private static final int CAPACITY = 10;

    private LinkedBlockingQueue<Double> queue = new LinkedBlockingQueue<>(CAPACITY);

    public AzimuthRollingAverage() {}

    public double getAverageBearing(double bearing) {
        if (queue.size() == CAPACITY) queue.poll();
        queue.add(bearing);

        double x = 0.0, y = 0.0;
        for (double value : queue) {
            double angleR = Math.toRadians(value);

            x += Math.cos(angleR);
            y += Math.sin(angleR);
        }

        double avgR = Math.atan2(y / queue.size(), x / queue.size());

        return Math.toDegrees(avgR);
    }
}