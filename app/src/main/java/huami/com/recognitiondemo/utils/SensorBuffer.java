package huami.com.recognitiondemo.utils;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;

import huami.com.recognitiondemo.MainActivity;

/**
 * Created by test on 2016/5/12.
 */
public class SensorBuffer {
    int maxSize = 500;
    private float Buffer[][] = new float[maxSize+10][3];
    public int writeIndex;
    //current read position
    private int readIndex;
    // next read position
    private int readPos;
    private int overlap;
    private int frameLen = maxSize/2;

    private float accX;
    private float accY;
    private float accZ;

    private FileOutputStream xWriter;
    protected File xACCFile;
    Runnable runnable;

    public SensorBuffer(){
        writeIndex = 0;
        readIndex = 0;
        readPos = 0;
        overlap = 100;

        xACCFile = new File(MainActivity.sSensorBaseFolder.getPath(), "xACC.txt");
        try{
            xWriter = new FileOutputStream(xACCFile);
            //xWriter.write("start".getBytes());
        }catch (Exception e){
            e.getStackTrace();
        }

        runnable = new Runnable() {
            @Override
            public void run() {
//                xACCFile = new File(MainActivity.sSensorBaseFolder.getPath(), "xACC.txt");
                test();
            }
        };
    }

    public synchronized boolean add(SensorModel model){
//        xACCFile = new File(MainActivity.sSensorBaseFolder.getPath(), "xACC.txt");
//        try{
//            xWriter = new FileOutputStream(xACCFile);
//            xWriter.write("start".getBytes());
//        }catch (Exception e){
//            e.getStackTrace();
//        }

        float[] temp = new float[]{model.x, model.y, model.z};
        System.arraycopy(temp, 0, Buffer[writeIndex], 0, 3);
        writeIndex += 1;
        writeIndex = writeIndex == maxSize ? 0 : writeIndex;

        if(readPos >= maxSize)
            readPos = readPos%maxSize;
        if(((readPos - writeIndex) <= frameLen && (readPos - writeIndex) > 0) || (writeIndex - readPos) >= frameLen ){
            readIndex = readPos;
            new Thread(runnable).start();
            readPos = readPos + frameLen - overlap;
        }
        return true;
    }

    //读取数据

    public boolean test(){
        try{
//            xWriter = new FileOutputStream(xACCFile);
            int i;
            for(i = 0; i < 250; i++){
                readIndex = readIndex == maxSize ? 0 : readIndex;
                float[] item = new float[3];
                System.arraycopy(Buffer[readIndex], 0, item, 0, 3);//数据写入item数组
                Log.d("test", String.valueOf(readIndex));
                accX = item[0];
                String strItem = String.valueOf(accX);
                strItem = strItem +"\t" + readIndex +"\t";
                //String strItem = Arrays.toString(item); //item数组数据转化为String
                //strItem = strItem.substring(1, strItem.length()-1) + "\n";
                if(xWriter != null)
                    xWriter.write(strItem.getBytes());
                readIndex++;//读指针后移一位
                Log.d("test", strItem);
            }
            if(i == 250){
                xWriter.write("\n".getBytes() );
                Log.d("test","线程执行完毕");
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(Exception e){
            e.getMessage();
        }
        return true;
    }
}
