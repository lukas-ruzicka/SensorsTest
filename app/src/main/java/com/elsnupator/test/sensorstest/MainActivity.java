package com.elsnupator.test.sensorstest;

import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.*;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Map<Integer,Sensor> availableSensors = new HashMap<>();
    private Map<Sensor,TextView[]> sensorViews = new HashMap<>();

    private long lastUpdate;
    private static final int UPDATE_FREQ = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        List<Integer> allSensors = new ArrayList<>();
        allSensors.add(Sensor.TYPE_ACCELEROMETER);
        allSensors.add(Sensor.TYPE_GYROSCOPE);
        allSensors.add(Sensor.TYPE_LINEAR_ACCELERATION);
        allSensors.add(Sensor.TYPE_GRAVITY);
        allSensors.add(Sensor.TYPE_AMBIENT_TEMPERATURE);
        allSensors.add(Sensor.TYPE_TEMPERATURE);
        allSensors.add(Sensor.TYPE_LIGHT);
        allSensors.add(Sensor.TYPE_PROXIMITY);
        allSensors.add(Sensor.TYPE_MAGNETIC_FIELD);
        allSensors.add(Sensor.TYPE_MOTION_DETECT);
        allSensors.add(Sensor.TYPE_ORIENTATION);
        allSensors.add(Sensor.TYPE_ROTATION_VECTOR);
        allSensors.add(Sensor.TYPE_PRESSURE);
        allSensors.add(Sensor.TYPE_RELATIVE_HUMIDITY);
        allSensors.add(Sensor.TYPE_STEP_COUNTER);
        allSensors.add(Sensor.TYPE_STEP_DETECTOR);

        List<Integer> notAvailable = new ArrayList<>();

        LinearLayout layout = findViewById(R.id.sensorsLayout);

        for (int sensorNumber : allSensors) {
            Sensor sensor = sensorManager.getDefaultSensor(sensorNumber);
            if(sensor == null){
                notAvailable.add(sensorNumber);
                continue;
            }

            availableSensors.put(sensorNumber,sensor);

            TextView header = new TextView(MainActivity.this);
            header.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            header.setPadding(0,20,0,10);
            header.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorPrimaryDark));
            header.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            header.setTextSize(17);
            header.setText(getSensorNameStringResource(sensorNumber));
            layout.addView(header);

            TextView[] views;
            switch(sensorNumber){
                case Sensor.TYPE_ACCELEROMETER:
                case Sensor.TYPE_GYROSCOPE:
                case Sensor.TYPE_LINEAR_ACCELERATION:
                case Sensor.TYPE_GRAVITY:
                case Sensor.TYPE_MAGNETIC_FIELD:
                case Sensor.TYPE_ROTATION_VECTOR:
                    views = new TextView[3];
                    for (int i = 0; i < 3; i++) {
                        LinearLayout row = buildRowLayout();

                        TextView desc = buildDescView();
                        switch(i){
                            case 0:
                                desc.setText(getResources().getString(R.string.x_value));
                                break;
                            case 1:
                                desc.setText(getResources().getString(R.string.y_value));
                                break;
                            case 2:
                                desc.setText(getResources().getString(R.string.z_value));
                                break;
                        }

                        TextView value = buildValueView();

                        views[i] = value;

                        row.addView(desc);
                        row.addView(value);
                        layout.addView(row);
                    }
                    sensorViews.put(sensor,views);
                    break;
                case Sensor.TYPE_ORIENTATION:
                    views = new TextView[3];
                    for (int i = 0; i < 3; i++) {
                        LinearLayout row = buildRowLayout();

                        TextView desc = buildDescView();
                        switch(i){
                            case 0:
                                desc.setText(getResources().getString(R.string.azimuth));
                                break;
                            case 1:
                                desc.setText(getResources().getString(R.string.pitch));
                                break;
                            case 2:
                                desc.setText(getResources().getString(R.string.roll));
                                break;
                        }

                        TextView value = buildValueView();

                        views[i] = value;

                        row.addView(desc);
                        row.addView(value);
                        layout.addView(row);
                    }
                    sensorViews.put(sensor,views);
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                case Sensor.TYPE_TEMPERATURE:
                case Sensor.TYPE_LIGHT:
                case Sensor.TYPE_PROXIMITY:
                case Sensor.TYPE_PRESSURE:
                case Sensor.TYPE_MOTION_DETECT:
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                case Sensor.TYPE_STEP_COUNTER:
                case Sensor.TYPE_STEP_DETECTOR:
                    views = new TextView[1];
                    LinearLayout row = buildRowLayout();

                    TextView desc = buildDescView();
                    switch(sensorNumber){
                        case Sensor.TYPE_AMBIENT_TEMPERATURE:
                        case Sensor.TYPE_TEMPERATURE:
                            desc.setText(getResources().getString(R.string.temp));
                            break;
                        case Sensor.TYPE_LIGHT:
                            desc.setText(getResources().getString(R.string.light_text));
                            break;
                        case Sensor.TYPE_PROXIMITY:
                            desc.setText(getResources().getString(R.string.prox));
                            break;
                        case Sensor.TYPE_PRESSURE:
                            desc.setText(getResources().getString(R.string.press));
                            break;
                        case Sensor.TYPE_MOTION_DETECT:
                            desc.setText(getResources().getString(R.string.motion));
                            break;
                        case Sensor.TYPE_RELATIVE_HUMIDITY:
                            desc.setText(getResources().getString(R.string.humidity));
                            break;
                        case Sensor.TYPE_STEP_COUNTER:
                            desc.setText(getResources().getString(R.string.step_count));
                            break;
                        case Sensor.TYPE_STEP_DETECTOR:
                            desc.setText(getResources().getString(R.string.step_detect));
                            break;
                    }

                    TextView value = buildValueView();
                    views[0] = value;

                    row.addView(desc);
                    row.addView(value);
                    layout.addView(row);

                    sensorViews.put(sensor,views);
                    break;
            }
        }

        // Unavailable sensors
        if(notAvailable.size() > 0){
            TextView unavailable = new TextView(MainActivity.this);
            unavailable.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            unavailable.setPadding(20,40,20,20);
            unavailable.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
            unavailable.setTextSize(13);
            String unavailableText = getResources().getString(R.string.unavailable);
            for(int sensorNumber: notAvailable)
                unavailableText += getResources().getString(getSensorNameStringResource(sensorNumber)) + ", ";
            unavailableText = unavailableText.substring(0,unavailableText.length()-2);
            unavailable.setText(unavailableText);
            layout.addView(unavailable);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        for (Sensor sensor: availableSensors.values())
            sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_UI);
        lastUpdate = System.currentTimeMillis();
    }

    @Override
    protected void onPause(){
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = availableSensors.get(event.sensor.getType());

        long currentTime = System.currentTimeMillis();
        if(currentTime - lastUpdate > UPDATE_FREQ){
            lastUpdate = System.currentTimeMillis();
            TextView[] views = sensorViews.get(sensor);
            for (int i = 0; i < views.length; i++)
                views[i].setText(String.valueOf(event.values[i]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    // Views builders

    private LinearLayout buildRowLayout(){
        LinearLayout ll = new LinearLayout(MainActivity.this);
        ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ll.setPadding(5,5,5,5);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        return ll;
    }

    private TextView buildDescView(){
        TextView desc = new TextView(MainActivity.this);
        desc.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        desc.setPadding(0,0,5,0);
        desc.setTypeface(Typeface.SANS_SERIF);
        desc.setTextSize(14);
        return desc;
    }

    private TextView buildValueView(){
        TextView value = new TextView(MainActivity.this);
        value.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        value.setTypeface(Typeface.SANS_SERIF);
        value.setTextSize(14);
        value.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.grey));
        return value;
    }

    // Sensors names

    private int getSensorNameStringResource(int sensorNumber){
        switch(sensorNumber){
            case Sensor.TYPE_ACCELEROMETER:
                return R.string.accelerometer;
            case Sensor.TYPE_GYROSCOPE:
                return R.string.gyroscope;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return R.string.linear_acceleration;
            case Sensor.TYPE_GRAVITY:
                return R.string.gravity;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return R.string.ambient_temperature;
            case Sensor.TYPE_TEMPERATURE:
                return R.string.temperature;
            case Sensor.TYPE_LIGHT:
                return R.string.light;
            case Sensor.TYPE_PROXIMITY:
                return R.string.proximity;
            case Sensor.TYPE_MAGNETIC_FIELD:
                return R.string.magnetic_field;
            case Sensor.TYPE_MOTION_DETECT:
                return R.string.motion_detect;
            case Sensor.TYPE_ORIENTATION:
                return R.string.orientation;
            case Sensor.TYPE_ROTATION_VECTOR:
                return R.string.rotation_vector;
            case Sensor.TYPE_PRESSURE:
                return R.string.pressure;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return R.string.relative_humidity;
            case Sensor.TYPE_STEP_COUNTER:
                return R.string.step_counter;
            case Sensor.TYPE_STEP_DETECTOR:
                return R.string.step_detector;
            default:
                return 0;
        }
    }
}