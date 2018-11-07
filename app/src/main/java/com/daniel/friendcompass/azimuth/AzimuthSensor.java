package com.daniel.friendcompass.azimuth;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.daniel.friendcompass.BaseApplication;

import static android.content.Context.SENSOR_SERVICE;

public class AzimuthSensor implements SensorEventListener {
    private AzimuthListener listener;

    private float[] gData;
    private float[] mData;

    public AzimuthSensor(AzimuthListener listener) {
        Context context = BaseApplication.getInstance();

        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (gravitySensor == null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_GAME);
        } else {
            sensorManager.registerListener(this,
                    gravitySensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }

        sensorManager.registerListener(this,
                magneticSensor,
                SensorManager.SENSOR_DELAY_GAME);

        this.listener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch(sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_GRAVITY:
                gData = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mData = sensorEvent.values.clone();
                break;
            default:
                return;
        }

        if (gData != null && mData != null) {
            float rMat[] = new float[9];
            float iMat[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(rMat, iMat, gData, mData);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(rMat, orientation);

                double azimuth = Math.toDegrees(orientation[0]);
                listener.bearingReceived(azimuth);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
