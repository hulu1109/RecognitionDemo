package huami.com.recognitiondemo.utils;

/**
 * Created by test on 2016/5/12.
 */
public class SensorBuffer {
    int maxSize = 500;
    private float Buffer[][] = new float[maxSize+10][3];
    public int writeIndex;
    private int readPos;
    private int overlap;

    Runnable runnable;

    public SensorBuffer(){
        writeIndex = 0;
        readPos = 0;
        overlap = 50;

        runnable = new Runnable() {
            @Override
            public void run() {

            }
        };
    }

    public synchronized boolean add(SensorModel model){

        float[] temp = new float[]{model.x, model.y, model.z};
        System.arraycopy(temp, 0, Buffer[writeIndex], 0, 3);
        writeIndex += 1;
        writeIndex = writeIndex == maxSize ? 0 : writeIndex;

        // readPos = readPos + maxSize/2 - overlap;
        if(readPos >= 500)
            readPos = readPos%500;
        if(((readPos - writeIndex) <= 250 && (readPos - writeIndex) > 0) || (writeIndex - readPos) >= 250 ){
            new Thread(runnable).start();
            readPos = readPos + maxSize/2 - overlap;
        }
        return true;
    }
}
