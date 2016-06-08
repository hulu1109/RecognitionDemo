package huami.com.recognitiondemo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import huami.com.recognitiondemo.utils.SensorBuffer;
import huami.com.recognitiondemo.utils.SensorModel;

/**
 * Created by test on 2016/5/12.
 */
public class AccelerometerService extends Service{
    public final static String ACTION_HEADER = "com.huami.recognitiondemo.";
    private final static String TAG = "SensorACC";

    MainActivity mainActivity = new MainActivity();
    protected SensorManager mSensorManager;
    protected Sensor mSensor;
    protected int mAccuracy;

    protected boolean mIsSensorReady = false;
    protected File mTargetFile;

    protected int mColumnNum = 3;
    protected String mColumnHeader = "AccX\tAccY\tAccZ";

    private FileOutputStream mWriter;
    private boolean mPrintLock = false;

    public SensorBuffer sensorBuffer;

    private int i = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorBuffer = new SensorBuffer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mIsSensorReady = mSensorManager.registerListener(
            sensorEventListener, mSensor, SensorManager.SENSOR_DELAY_GAME
        );
        startRecord();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if(mIsSensorReady){
            mSensorManager.unregisterListener(sensorEventListener);
            mIsSensorReady = false;
        }
        stopRecord();
        super.onDestroy();
    }

    private final SensorEventListener sensorEventListener;
    {
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                i++;
//                ++mSampleCount;
//
                float[] fixedValues = new float[mColumnNum];

                fixedValues[0] = (float)(event.values[0] / 9.8);
                fixedValues[1] = (float)(event.values[1] / 9.8);
                fixedValues[2] = (float)(event.values[2] / 9.8);
//
//                SensorModel acc;
//                acc = new SensorModel(fixedValues[0], fixedValues[1], fixedValues[2]);
//                sensorBuffer.add(acc);
//
//                String line = Float.toString(fixedValues[0]);
//                for(int i = 1; i <mColumnNum; ++i)
//                    line += "\t" + fixedValues[i];
//                printLine(line + "\n");

//                   float[] fixedValues = new float[mColumnNum];
//                   fixedValues[0] = i;
//                   fixedValues[1] = i;
//                   fixedValues[2] = i;

                   SensorModel acc;
                   acc = new SensorModel(fixedValues[0], fixedValues[1], fixedValues[2]);
                   sensorBuffer.add(acc);

                   String line = Float.toString(fixedValues[0]);
                    for(int j = 1; j <mColumnNum; ++j)
                        line += "\t" + fixedValues[j];
                   printLine(line + "\n");
               }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                mAccuracy = accuracy;
            }
        };
    }

    public void startRecord(){
        mTargetFile = new File(MainActivity.sSensorBaseFolder.getPath(), MainActivity.DATE_FORMAT.format(MainActivity.sStartDate) + ".txt");
        try{
            mWriter = new FileOutputStream(mTargetFile);
            mWriter.write((MainActivity.DATE_FORMAT.format(MainActivity.sStartDate) + "\n").getBytes());
            mWriter.write(("Timestamp\t" + mColumnHeader + "\n").getBytes());
            if(!mIsSensorReady)
                mWriter.write(("Sensor initialization failed\n").getBytes());
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    public void stopRecord(){
        try {
            mWriter.close();
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    protected void printLine(String line){
        try{
            while(mPrintLock)
                continue;
            mPrintLock = true;
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            mWriter.write((df.format(System.currentTimeMillis()) + "\t" + line).getBytes());
            mWriter.write(line.getBytes());
            mPrintLock = false;
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }
}
