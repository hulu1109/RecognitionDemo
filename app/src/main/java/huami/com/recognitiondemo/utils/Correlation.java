package huami.com.recognitiondemo.utils;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by test on 2016/5/26.
 */
public class Correlation {
    private int len = 250;
    public float[] X = new float[2*len - 1];

//    public Correlation(float[] X1, float[] X2) {
////        X = CrossCorrelation(ZeroMeanNormalizedTransform(X1),ZeroMeanNormalizedTransform(X2));
//        X = CrossCorrelation(X1,X2);
//        findMax(X);
//    }

    public float calculateCorrelation(float[] X1, float[] X2){
        float max;
        X = CrossCorrelation(X1, X2);
        max = findMax(X);
        return max;
    }


    private float calculateAverage(float[] x) {
        float sum = 0;
        for(int i = 0; i < x.length; i++)
            sum += x[i];
        return sum/x.length;
    }


    private double AbsMean(Float[] x) {
        float sum = 0;
        for(int i = 0; i < x.length; i++)
            sum += x[i]*x[i];
        return sum/x.length;
    }


    private float[] ZeroMeanNormalizedTransform(float[] x) {
        float norm = 0;
        float x_mean = calculateAverage(x);
        for(int i = 0; i < x.length; i++){
            norm += (x[i] - x_mean)*(x[i] - x_mean);
        }
        norm = (float)Math.sqrt(norm / x.length);
        float[] y = new float[len];
        if(norm != 0) {
            for(int i = 0; i < x.length; i++){
                y[i] = (x[i] - x_mean)/norm;
            }
        }
        return y;
    }


    private float SumMult(float[] x, float[] y) {
        float sum=0;
        for (int i=0; i<x.length; i++) {
            sum += x[i] * y[i];
        }
        return sum;
    }

    private float[] CrossCorrelation(float[] x, float[] y) {

        float[] Xc = new float[2*len - 1];

        int j1, j2, k1, k2;

        for(int i = 0; i < (2 * x.length -1); i++){
            if(i > (x.length - 1)){
                j1 = 0;
                k1 = 2 * x.length - i - 2;
                j2 = i - x.length + 1;
                k2 = x.length - 1;
            }else{
                j1 = x.length - i - 1;
                k1 = x.length - 1;
                j2 = 0;
                k2 = i;
            }
            int a = 0;int b = 0;
            float[] tempX = new float[len];
            float[] tempY = new float[len];
            for(int j = j1; j <= k1;j++){
                tempX[a++] = x[j];
            }
            for(int j = j2; j <= k2; j++)
                tempY[b++] = y[j];
            Xc[i] = SumMult(tempX,tempY);
        }
        return Xc;
    }

    public float findMax(float[] x){
        float max = x[0];
        for(int i = 0; i < x.length; i++){
            if(x[i] < 0)
                x[i] = -x[i];
            if(x[i] > max)
                max = x[i];
        }
        return max;
    }
}
