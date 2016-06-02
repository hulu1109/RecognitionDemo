package huami.com.recognitiondemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import huami.com.recognitiondemo.utils.DecisionTree;

public class MainActivity extends Activity {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    public static final String SENSOR_TYPE = "ACC";
    public static long sStartTimestamp = 0;

    public static File sSensorBaseFolder = null;
    public static Date sStartDate = null;

    boolean mIsRecording = false;

    Button mStartBtn = null;
    public static TextView mFrameActivityTV = null;
    public static TextView mMinuteActivityTV = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartBtn = (Button)findViewById(R.id.btn_start);
        mFrameActivityTV = (TextView)findViewById(R.id.tv_frame_activity);
        mMinuteActivityTV = (TextView)findViewById(R.id.tv_minute_activity);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            sSensorBaseFolder = new File(Environment.getExternalStorageDirectory(), "Recognition");
            sSensorBaseFolder.mkdir();
        }


    }

    private void startRecord() {
        sStartDate = new Date();
        sStartTimestamp = sStartDate.getTime();
        if (!sSensorBaseFolder.exists())
            sSensorBaseFolder.mkdir();

        mStartBtn.setText("Stop");
        mIsRecording = true;
        startSensorService(SENSOR_TYPE);
    }

    private void stopRecord(){
        if(mIsRecording){
            mStartBtn.setText("Start");
            mFrameActivityTV.setText("Activity");
            mMinuteActivityTV.setText("Activity");
            mIsRecording = false;
            stopSensorService(SENSOR_TYPE);
        }
    }

    private void startSensorService(String name){
        Intent mIntent = new Intent();
        mIntent.setAction(AccelerometerService.ACTION_HEADER + name);
        mIntent.setPackage(getPackageName());
        startService(mIntent);
    }

    private void stopSensorService(String name){
        Intent mIntent = new Intent();
        mIntent.setAction(AccelerometerService.ACTION_HEADER + name);
        mIntent.setPackage(getPackageName());
        stopService(mIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        stopRecord();
        super.onDestroy();
    }

    public void onStartBtnClick(View source) throws IOException{
        if(!mIsRecording)
            startRecord();
        else
            stopRecord();
    }

//    public TextView getTextView(){
//        TextView textView = (TextView)findViewById(R.id.tv_recognition);
//        return textView;
//    }
}
