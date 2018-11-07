package com.daniel.friendcompass.util;

import java.util.concurrent.LinkedBlockingQueue;

public class BearingRollingAverage {
    private static final int CAPACITY = 12;

    private LinkedBlockingQueue<Double> queue = new LinkedBlockingQueue<>(CAPACITY);

    public BearingRollingAverage() {}

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
