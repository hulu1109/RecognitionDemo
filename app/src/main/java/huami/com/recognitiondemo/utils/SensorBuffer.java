package huami.com.recognitiondemo.utils;

import android.widget.TextView;

import org.jtransforms.fft.FloatFFT_1D;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import huami.com.recognitiondemo.MainActivity;
;

/**
 * Created by test on 2016/5/12.
 */
public class SensorBuffer {
    protected MainActivity mActivity;
    private Correlation correlation;

    private int maxSize = 500;
    private int frameLen = maxSize/2;
    private int preProcessingNum = 8;
    private int extractionNum = 18;

    private float Buffer[][] = new float[maxSize+10][3];
    public int writeIndex;
    private int readPos;//下一次读的位置
    private int currentStartIndex;
    private int overlap;

    private FileOutputStream xWriter;
    protected File xACCFile;
    Runnable runnable;

    private float[][] preProcessingOut = new float[frameLen][preProcessingNum];
    private float[][] extractionOut = new float[preProcessingNum][extractionNum];

    private int moveCount;
    private int stillCount;

//    float[] cutPoint = new float[167];//value
//    Integer[] cutPredictor = new Integer[167];//index

    public SensorBuffer(){
        mActivity = new MainActivity();

        writeIndex = 0;
        readPos = 0;
        overlap = 100;

       xACCFile = new File(MainActivity.sSensorBaseFolder.getPath(), "xACC.txt");
        try{
            xWriter = new FileOutputStream(xACCFile);
        }catch (Exception e){
            e.getStackTrace();
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                preProcessing();
            }
        };
    }

    //写数据
    public synchronized boolean add(SensorModel model){

        float[] temp = new float[]{model.x, model.y, model.z};
        System.arraycopy(temp, 0, Buffer[writeIndex], 0, 3);
        writeIndex += 1;
        writeIndex = writeIndex == maxSize ? 0 : writeIndex;

        if(readPos >= maxSize)
            readPos = readPos%maxSize;
        if(((readPos - writeIndex) <= frameLen && (readPos - writeIndex) > 0) || (writeIndex - readPos) >= frameLen ){
            currentStartIndex = readPos;
            new Thread(runnable).start();
            readPos = readPos + frameLen - overlap;
        }
        return true;
    }

    //读取数据
    public boolean preProcessing(){

        float[][] preProcessingIn = new float[frameLen][3];
        int readIndex = currentStartIndex;
        for(int i = 0; i < frameLen; i++){
            readIndex = readIndex == maxSize ? 0 : readIndex;
            System.arraycopy(Buffer[readIndex], 0, preProcessingIn[i], 0, 3);
            //saveFile(preProcessingIn[i], i);
            readIndex++;
        }
        for(int i = 0; i < frameLen; i++){
            float[] temp = new float[8];
            System.arraycopy(preProcessingIn[i], 0, temp, 0, 3);
            float accX = temp[0];
            float accY = temp[1];
            float accZ = temp[2];
            float mag = (float) Math.sqrt(accX * accX + accY * accY + accZ * accZ);
            float angular1 = (float) Math.acos(accZ / mag);
            float angular2 = (float) Math.atan(accX / (float) Math.sqrt(accY * accY + accZ * accZ));
            float angular3 = (float) Math.atan(accY / (float) Math.sqrt(accX * accX + accZ * accZ));
            float angular4 = (float) Math.atan((float) Math.sqrt(accX * accX + accY * accY) / accZ);
            temp[3] = mag; temp[4] = angular1; temp[5] = angular2; temp[6] = angular3; temp[7] = angular4;

            System.arraycopy(temp, 0, preProcessingOut[i], 0, 8);
        }
        featureExtraction(preProcessingOut);

//        try{
////            float[][] processing = new float[frameLen][8];
//            int i;
//            for(i = 0; i < frameLen; i++){
//                readIndex = readIndex == maxSize ? 0 : readIndex;
//                float[] item = new float[8];
//                System.arraycopy(Buffer[readIndex], 0, item, 0, 3);//数据写入item数组
//                Log.d("test", String.valueOf(readIndex));
//                float accX = item[0];
//                float accY = item[1];
//                float accZ = item[2];
//                float mag = (float) Math.sqrt(accX * accX + accY * accY + accZ * accZ);
//                float angular1 = (float) Math.acos(accZ / mag);
//                float angular2 = (float) Math.atan(accX / (float) Math.sqrt(accY * accY + accZ * accZ));
//                float angular3 = (float) Math.atan(accY / (float) Math.sqrt(accX * accX + accZ * accZ));
//                float angular4 = (float) Math.atan((float) Math.sqrt(accX * accX + accY * accY) / accZ);
//                item[3] = mag; item[4] = angular1; item[5] = angular2; item[6] = angular3; item[7] = angular4;
//
//                //测试X输出
////                String strItem = String.valueOf(accX);
////                strItem = strItem +"\t" + readIndex +"\t";
////                if(xWriter != null)
////                    xWriter.write(strItem.getBytes());
////                Log.d("test", strItem);
//                pos = readIndex%frameLen;//pos-填充到数组的具体位
//                System.arraycopy(item, 0, processing[pos], 0, 8);//数组填充
//                String strItem = Arrays.toString(processing[pos]);
//                if(xWriter != null)
//                    xWriter.write(strItem.getBytes());
//
//                readIndex++;//读指针后移一位
//                Log.d("test", strItem);
//            }
//            if(i == frameLen){
//                xWriter.write("\n".getBytes() );
//                Log.d("test","线程执行完毕");
//            }
//        }catch (FileNotFoundException e){
//            e.printStackTrace();
//        }catch(Exception e){
//            e.getMessage();
//        }
        return true;
    }

    public void saveFile(float[] item, int i){
        String strItem = Arrays.toString(item);
        try {
            if (xWriter != null)
                xWriter.write(strItem.getBytes());
            if(i == 7)
                xWriter.write("\n".getBytes());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void saveFft(float[] item){
        String strItem = Arrays.toString(item);
        try {
            if (xWriter != null)
                xWriter.write((strItem + "\n").getBytes());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void featureExtraction(float[][] item){
        float[][] extractionIn = new float[preProcessingNum][frameLen];
        float[] maxXcorr = new float[6];

        //数组翻转
        for(int i = 0; i < frameLen; i++)
            for(int j = 0; j < 8; j++){
                extractionIn[j][i] = item[i][j];
            }

//        float[] aa = new float[]{1, 2, 3, 4, 5};
//        float[] bb = new float[]{2, 4, 7, 9, 10};
//        correlation = new Correlation(aa, bb);

        correlation = new Correlation(extractionIn[0], extractionIn[1]);
        float xcorrMax = correlation.findMax(correlation.X);
        maxXcorr[0] = xcorrMax;
        correlation = new Correlation(extractionIn[0], extractionIn[2]);
        xcorrMax = correlation.findMax(correlation.X);
        maxXcorr[1] = xcorrMax;
        correlation = new Correlation(extractionIn[0], extractionIn[2]);
        xcorrMax = correlation.findMax(correlation.X);
        maxXcorr[2] = xcorrMax;
        correlation = new Correlation(extractionIn[0], extractionIn[2]);
        xcorrMax = correlation.findMax(correlation.X);
        maxXcorr[3] = xcorrMax;
        correlation = new Correlation(extractionIn[0], extractionIn[2]);
        xcorrMax = correlation.findMax(correlation.X);
        maxXcorr[4] = xcorrMax;
        correlation = new Correlation(extractionIn[0], extractionIn[2]);
        xcorrMax = correlation.findMax(correlation.X);
        maxXcorr[5] = xcorrMax;




        for(int i = 0; i < 8; i++){
            float sum = 0; float mean;
            float sumSquare = 0; float meanSquare;
            float sumForVar = 0; float var;
            float sumForSkewness = 0; float skewness;
            float sumForKurtosis = 0; float kurtosis;
            float max = extractionIn[i][0]; float min = extractionIn[i][0];
            float entropy; float crossingRate;

            for(int j = 0; j < frameLen; j++){
                sum = sum + extractionIn[i][j];
                sumSquare = sumSquare + extractionIn[i][j] * extractionIn[i][j];
                if(extractionIn[i][j] >= max)
                    max = extractionIn[i][j];
                if(extractionIn[i][j] <= min)
                    min = extractionIn[i][j];
            }
            mean = sum/frameLen; meanSquare = sumSquare/frameLen;
            int[] crossing = new int[frameLen]; float crossingCount = 0;
            for(int k = 0; k < frameLen; k++){
                sumForVar = sumForVar + (extractionIn[i][k] - mean)*(extractionIn[i][k] - mean);
                sumForKurtosis = sumForKurtosis + (float)Math.pow((extractionIn[i][k] - mean), 4);
                sumForSkewness = sumForSkewness + (float)Math.pow((extractionIn[i][k] - mean), 3);

                if(extractionIn[i][k] >= mean)
                    crossing[k] = 1;//计算crossing data rate
            }
            for(int j = 0;j < frameLen-1; j++){
                if(crossing[j+1] != crossing[j])
                    crossingCount++;
            }
            crossingRate = crossingCount/frameLen;
            var = sumForVar/frameLen;
            skewness = (sumForSkewness/frameLen)/(float)Math.pow(Math.sqrt(var), 3);
            kurtosis = (sumForKurtosis/frameLen)/(float)Math.pow(var, 2);

            //fft
            float[] temp = new float[frameLen];
            System.arraycopy(extractionIn[i], 0, temp, 0, frameLen);
            FloatFFT_1D fft = new FloatFFT_1D(frameLen);
            fft.realForward(temp);

            float[] fftResult = new float[frameLen/2];
//            fftResult[0] = (float)Math.sqrt(temp[0] * temp[0]);/////第一个数没有用
            for(int j = 1; j < frameLen/2; j++){
                float re = temp[j * 2];
                float im = temp[j * 2 + 1];
                fftResult[j] = (float)Math.sqrt(re * re + im * im);
            }
//            saveFft(fftResult);

            float fftMax = fftResult[0]; int fftPeakPos = 0;
            float sumFFT = fftResult[0]; float sumEntropy = 0;
            float sum0_1 = 0, sum1_3 = 0, sum3_5 = 0, sum5_16 = 0;
//            for(int j = 1; j < frameLen/2; j++){
//                sumFFT = sumFFT + fftResult[j];
//                sumEntropy = sumEntropy + fftResult[j] * (float)(Math.log(fftResult[j])/Math.log(2));
//                if(j == 9)
//                    sum0_1 = sumFFT;
//                if(j == 25)
//                    sum1_3 = sumFFT - sum0_1;
//                if(j == 41)
//                    sum3_5 = sumFFT - sum1_3 - sum0_1;
//                if(j == 124)
//                    sum5_16 = sumFFT -sum3_5 - sum1_3 - sum0_1;
//
//                if(fftResult[j] >= fftMax){
//                    fftMax = fftResult[j];
//                    fftPeakPos = j;
//                }
//            }
            for(int j = 1; j < frameLen/2; j++){
                sumFFT = sumFFT + fftResult[j];
                sumEntropy = sumEntropy + fftResult[j] * (float)(Math.log(fftResult[j])/Math.log(2));

                if(fftResult[j] >= fftMax){
                    fftMax = fftResult[j];
                    fftPeakPos = j;
                }
            }
            for(int j = 1; j < 9; j++)
                sum0_1 += fftResult[j];
            for(int j = 9; j < 25; j++ )
                sum1_3 += fftResult[j];
            for(int j = 25; j < 41; j++)
                sum3_5 += fftResult[j];
            for(int j = 41; j < frameLen/2; j++)
                sum5_16 += fftResult[j];

            entropy = (float)(Math.log(frameLen)/Math.log(2)) - sumEntropy/frameLen;

            extractionOut[i][0] = mean; extractionOut[i][1] = var;
            extractionOut[i][2] = max;  extractionOut[i][3] = min;
            extractionOut[i][4] = skewness; extractionOut[i][5] = kurtosis;
            extractionOut[i][6] = meanSquare; extractionOut[i][7] = fftPeakPos;
            extractionOut[i][8] = sum0_1; extractionOut[i][9] = sum1_3;
            extractionOut[i][10] = sum3_5; extractionOut[i][11] = sum5_16;
            extractionOut[i][12] = sum0_1/sum1_3; extractionOut[i][13] = sum3_5/sum5_16;
            extractionOut[i][14] = (sum0_1 + sum1_3)/(sum3_5 + sum5_16);
            extractionOut[i][15] = sumFFT; extractionOut[i][16] = entropy;
            extractionOut[i][17] = crossingRate;

//            saveFile(extractionOut[i], i);
        }

        recognition(extractionOut);//改变界面值
        reShape(extractionOut, maxXcorr);

    }
    public void reShape(float[][] item1, float[] item2){
        float[] testData = new float[150];
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 18; j++)
                testData[18 * i + j] = item1[i][j];
        for(int i = 0;i < 6; i++)
            testData[144 + i] = item2[i];

        //decision tree
        DecisionTree decisionTree = new DecisionTree();
        decisionTree.classification(testData);

    }

    private void recognition(float[][] item){
        if(moveCount + stillCount >= 8 && moveCount >= stillCount){
            update(MainActivity.mMinuteActivityTV, "moving");
            moveCount = stillCount = 0;
        }else if(moveCount + stillCount >= 8 && moveCount < stillCount){
            update(MainActivity.mMinuteActivityTV, "stationary");
            moveCount = stillCount = 0;
        }
        if(item[3][1] > 15){
            update(MainActivity.mFrameActivityTV, "moving");
            moveCount++;
        }
        else{
            update(MainActivity.mFrameActivityTV, "stationary");
            stillCount++;
        }
    }

    private void update(final TextView view, final String str){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setText(str);
            }
        });
    }



}
