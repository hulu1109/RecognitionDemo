package huami.com.recognitiondemo.utils;

import android.util.Log;
import android.widget.TextView;

import org.jtransforms.fft.FloatFFT_1D;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import huami.com.recognitiondemo.MainActivity;

/**
 * Created by test on 2016/5/12.
 */
public class SensorBuffer {
    protected MainActivity mActivity;
//    private Correlation correlation;

    private static final int maxSize = 500;
    private static final int frameLen = maxSize/2;
    private static final int preProcessingNum = 8;
    private static final int extractionNum = 18;

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

    public static int[] activityCount = new int[10];
    public static int activitySum = 0;
    private String[] activityUI = new String[]{"", "", "", "", "", "", "", "", "", "",};

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

        Correlation correlation = new Correlation();
        maxXcorr[0] = correlation.calculateCorrelation(extractionIn[0], extractionIn[1]);
        maxXcorr[1] = correlation.calculateCorrelation(extractionIn[0], extractionIn[2]);
        maxXcorr[2] = correlation.calculateCorrelation(extractionIn[0], extractionIn[3]);
        maxXcorr[3] = correlation.calculateCorrelation(extractionIn[1], extractionIn[2]);
        maxXcorr[4] = correlation.calculateCorrelation(extractionIn[1], extractionIn[3]);
        maxXcorr[5] = correlation.calculateCorrelation(extractionIn[2], extractionIn[3]);


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
                fftResult[j] = re * re + im * im;
            }
            saveFft(fftResult);

            float fftMax = fftResult[0]; int fftPeakPos = 0;
            float sumFFT = fftResult[0]; float sumEntropy = 0;
            float sum0_1 = 0, sum1_3 = 0, sum3_5 = 0, sum5_16 = 0;

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

           // saveFile(extractionOut[i], i);
        }

//        preRecognition(extractionOut);//改变界面值
        reShape(extractionOut, maxXcorr);

    }
    public void reShape(float[][] item1, float[] item2){
        float[] testData = new float[150];
//        float[] testData = new float[150];
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 18; j++)
                testData[18 * i + j] = item1[i][j];
        for(int i = 0;i < 6; i++)
            testData[144 + i] = item2[i];

        String s1 = String.valueOf(testData[117]);
        if(s1.length() > 3)
            s1 = s1.substring(0, 3);
        String s2 = String.valueOf(testData[11]);
        if(s2.length() > 3)
            s2 = s2.substring(0, 3);
        String s3 = String.valueOf(testData[71]);
        if(s3.length() > 3)
            s3 = s3.substring(0, 3);
        String s4 = String.valueOf(testData[34]);
        if(s4.length() > 3)
            s4 = s4.substring(0, 3);

        updateUI(MainActivity.mTestDataTV, s1 + "," + s2 + "," + s3 + "," + s4);
        //print tree
//        mActivity.decisionTreeNode.levelTraversal();
        double tempAct = mActivity.decisionTreeNode.classification(testData);
        Log.d("test", String.valueOf(tempAct));
        finalResult(tempAct);
    }



    private void preRecognition(float[][] item){
        if(moveCount + stillCount >= 6 && moveCount >= stillCount){
            updateUI(MainActivity.mMinuteActivityTV, "moving");
            moveCount = stillCount = 0;
        }else if(moveCount + stillCount >= 6 && moveCount < stillCount){
            updateUI(MainActivity.mMinuteActivityTV, "stationary");
            moveCount = stillCount = 0;
        }
        if(item[3][1] > 1){
            updateUI(MainActivity.mFrameActivityTV, "moving");
            moveCount++;
        }
        else{
            updateUI(MainActivity.mFrameActivityTV, "stationary");
            stillCount++;
        }
    }

    private void updateUI(final TextView view, final String str){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setText(str);
            }
        });
    }

    private void finalResult(double item){
        //                                    1      2      3       4        5         6       7     8       9     10
        String[] keyActivity = new String[]{"开车","刷牙","骑车", "洗澡", "坐车乘车","走路","站立","跑步", "坐", "睡眠"};
        String[] shortActivity = new String[]{"开","刷","骑", "洗", "乘","走","站","跑", "坐", "睡"};

        int temp = (int)item;
        //time format
        SimpleDateFormat sdf = new SimpleDateFormat("ss");
        updateUI(MainActivity.mRecognitionActivityTV, keyActivity[temp - 1] + sdf.format(System.currentTimeMillis()));

        if(activitySum < 9){
            activityUI[activitySum] = shortActivity[temp - 1];
            String s = "";
            for(int i = 0; i <= activitySum; i++)
                s += activityUI[i];
            updateUI(MainActivity.mFrameActivityTV, s);
            activitySum++;
        }else{
            String s = "";
            activityUI[activitySum] = shortActivity[temp - 1];
            for(int i = 0; i < activitySum; i++){
                activityUI[i] = activityUI[i + 1];
                s = s + activityUI[i];
            }
//            s = s + activityUI[activitySum];
            String mostString = findMost(s);
            updateUI(mActivity.mMinuteActivityTV, mostString);
            updateUI(MainActivity.mFrameActivityTV, s);
        }

    }


    public String findMost(String str){
        int maxLen = 0;
        String maxStr = "";
        while(str.length() > 0){
            int length = str.length();
            String first = str.substring(0, 1);
            str = str.replaceAll(first, "");
            if(maxLen < length - str.length()){
                maxLen = length - str.length();
                maxStr = first;
            }
        }
        return maxStr;
    }
}
