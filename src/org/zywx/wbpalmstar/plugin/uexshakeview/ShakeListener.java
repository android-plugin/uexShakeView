package org.zywx.wbpalmstar.plugin.uexshakeview;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeListener implements SensorEventListener {

	private Context context;
	private SensorManager manager;
	private OnShakeListener onShakeListener;
	private Sensor sensor;

	public ShakeListener(Context context) {
		this.context = context;
	}

	public void setOnShakeListener(OnShakeListener onShakeListener) {
		this.onShakeListener = onShakeListener;
		start();
	}

	public void start() {
		manager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		if (manager != null) {
			sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		if (sensor != null) {
			manager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	public void stop() {
		if (manager != null) {
			manager.unregisterListener(this);
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		int sensorType = event.sensor.getType();
		float[] values = event.values;
		if (sensorType == Sensor.TYPE_ACCELEROMETER) {
			if (Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17
					|| Math.abs(values[2]) > 17) {
				onShakeListener.onShake();
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public interface OnShakeListener {
		public void onShake();
	}

}
