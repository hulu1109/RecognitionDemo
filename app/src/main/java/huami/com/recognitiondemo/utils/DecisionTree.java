package huami.com.recognitiondemo.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by test on 2016/5/30.
 */
public class DecisionTree {
//    float[] cutPoint = new float[167];//value
//    int[] cutPredictor = new int[167];//index
    private static Integer[][] children = new Integer[][]
        {{2, 4, 6, 8, 10, 12, 14, 0, 16, 0, 18, 20, 22, 24, 26, 0, 28, 30, 32, 34, 36, 38, 40, 0, 42, 44, 0, 0, 0,
            0, 46, 48, 50, 52, 54, 0, 56, 58, 60, 62, 64, 66, 68, 0, 0, 0, 0, 70, 0, 0, 0, 0, 0, 72, 74, 76, 78, 0, 0, 0, 80, 0, 82, 84, 86, 88, 90, 0, 92, 0, 0, 94
            , 0, 0, 96, 98, 100, 0, 0, 102, 0, 104, 106, 108, 110, 112, 0, 114, 0, 116, 0, 118, 0, 120, 122, 124, 126, 0, 0, 0, 128, 0, 0, 0, 130, 0, 132, 0, 0, 0,
            0, 0, 0, 134, 136, 138, 0, 0, 0, 0, 0, 0, 140, 142, 0, 0, 144, 0, 0, 146, 0, 148, 0, 0, 150, 152, 0, 0, 0, 0, 154, 156, 0, 0, 0, 158, 0, 0, 0, 160, 0,
            162, 0, 0, 0, 0, 0, 164, 0, 166, 0, 0, 0, 0, 0, 0, 0},
            {3, 5, 7, 9, 11, 13, 15, 0, 17, 0, 19, 21, 23, 25, 27, 0, 29, 31, 33, 35, 37, 39, 41, 0, 43, 45, 0, 0, 0, 0, 47, 49, 51, 53,
            55, 0, 57, 59, 61, 63, 65, 67, 69, 0, 0, 0, 0, 71, 0, 0, 0, 0, 0, 73, 75, 77, 79, 0, 0, 0, 81, 0, 83, 85, 87, 89, 91, 0, 93, 0, 0, 95, 0, 0, 97, 99, 101, 0, 0, 103,
            0, 105, 107, 109, 111, 113, 0, 115, 0, 117, 0, 119, 0, 121, 123, 125, 127, 0, 0, 0, 129, 0, 0, 0, 131, 0, 133, 0, 0, 0, 0, 0, 0, 135, 137, 139, 0, 0, 0, 0, 0, 0,
            141, 143, 0, 0, 145, 0, 0, 147, 0, 149, 0, 0, 151, 153, 0, 0, 0, 0, 155, 157, 0, 0, 0, 159, 0, 0, 0, 161, 0, 163, 0, 0, 0, 0, 0, 165, 0, 167, 0, 0, 0, 0, 0, 0, 0}};
    //cutPoint
    private static double[] value = new double[]{0.054002024, 1.5, 50.65013, 0.035118353, 0.06704255, 1.0315, 0.398, 0.0, 60.921345, 0.0, 0.40966934, 0.20769806, 0.08613405,
            -603.8221, 1.1056689, 0.0, 1.0118542, 47.869774, 0.31663403, 28.276976, 13.5, 33.30382, 235.85872, 0.0, 0.35021517, 0.122345224, 0.0, 0.0, 0.0, 0.0, 0.82165205,
            1.0084912, 7.811245E-6, 0.378, 0.23309056, 0.0, 244.33727, 0.983932, 15.5, 19.245832, 0.2501732, 251.02168, 640.03253, 0.0, 0.0, 0.0, 0.0, 0.99759364, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.697, 0.0045, 0.12585223, 0.22, 0.0, 0.0, 0.0, 145.2998, 0.0, 0.6738219, 1.0517629, 36.0, 0.78180367, 194.19098, 0.0, 0.278, 0.0, 0.0, 0.05207711,
            0.0, 0.0, 21.662754, 1.3472469, 26.699755, 0.0, 0.0, 140.08386, 0.0, 81.2742, 0.84766436, 1.9374915, 1.0855238, 19.5, 0.0, 0.16166632, 0.0, 564.89374, 0.0, 122.19043,
            0.0, 0.7184133, 0.38916674, 2.8512292, 0.3348043, 0.0, 0.0, 0.0, 0.3545725, 0.0, 0.0, 0.0, 132.96013, 0.0, -4.342091, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.7920284, 82.19332,
            0.306, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.10768022, 0.603, 0.0, 0.0, 0.42115197, 0.0, 0.0, 76.67823, 0.0, 1.0104493, 0.0, 0.0, -24.514355, 234.92075, 0.0, 0.0, 0.0, 0.0,
            1.5876122E-4, 0.64225686, 0.0, 0.0, 0.0, 7.9752064, 0.0, 0.0, 0.0, 10.06161, 0.0, 1.0575, 0.0, 0.0, 0.0, 0.0, 0.0, 141.36688, 0.0, 0.025242886, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0};
    //cutPredictor
    private static Integer[] index = new Integer[]{124, 84, 12, 85, 47, 22, 75, null, 144, null, 99, 76, 70, 36, 58, null, 58, 143,
            128, 143, 65, 86, 147, null, 76, 102, null, null, null, null, 129, 58, 21, 132, 11, null, 148, 20, 65, 69, 76,
            107, 69, null, null, null, null, 58, null, null, null, null, null, 22, 42, 72, 75, null, null, null, 145, null, 26, 67, 65, 26,
            105, null, 75, null, null, 10, null, null, 69, 107, 105, null, null, 67, null, 155, 109, 86, 58, 65, null, 102, null, 88,
            null, 146, null, 98, 31, 88, 118, null, null, null, 76, null, null, null, 125, null, 55, null, null, null, null, null, null, 115, 126,
            75, null, null, null, null, null, null, 11, 23, null, null, 14, null, null, 66, null, 58, null, null, 17, 145, null, null, null, null, 97,
            70, null, null, null, 63, null, null, null, 25, null, 22, null, null, null, null, null, 29, null, 59, null, null, null, null, null, null, null};
    //nodeClass
    private static int[] activity = new int[]{9, 10, 9, 9, 10, 9, 4, 10, 9, 10, 10, 9, 1, 4, 3, 9, 9, 9, 10, 9, 5, 5, 1, 8, 4, 3, 2, 9, 10, 10,
            9, 10, 9, 9, 9, 9, 5, 5, 6, 9, 1, 4, 6, 9, 3, 9, 10, 10, 10, 10, 9, 9, 10, 9, 9, 5, 9, 5, 1, 6, 9, 9, 9, 1, 5, 4, 4, 6, 2, 10, 9,
            9, 9, 7, 9, 2, 5, 6, 9, 9, 2, 9, 1, 9, 1, 5, 2, 4, 1, 2, 4, 8, 2, 9, 7, 9, 5, 5, 2, 5, 6, 9, 5, 9, 4, 1, 9, 1, 9, 1, 5, 1, 5, 9,
            4, 4, 2, 4, 8, 10, 9, 7, 9, 9, 9, 9, 5, 7, 6, 4, 2, 9, 5, 9, 1, 7, 4, 4, 3, 9, 7, 7, 9, 1, 5, 9, 4, 9, 1, 2, 1, 7, 6, 7, 9, 7, 9,
            2, 9, 4, 9, 3, 7, 4, 2, 2, 4};


    public void classification(float[] item){
        DecisionTreeNode root = deSerialization();
        if(root == null){
            System.out.print("nullnull");
            return;
        }
//        levelTraversal(root);
//        preOrderTraversal(root);

//        float[] temp = new float[158];
//        temp[123] = 1;temp[11] = 60; temp[74] = 1;
        recognition(item, root);
    }
    //层次遍历
    public void levelTraversal(DecisionTreeNode root){
        Queue<DecisionTreeNode> queue = new LinkedList<>();
        queue.add(root);
        DecisionTreeNode tempNode;
        while(!queue.isEmpty()){
            tempNode = queue.poll();
            System.out.print(tempNode.val+",");
            Log.d("test", String.valueOf(tempNode.index));
            if(tempNode.left != null)
                queue.add(tempNode.left);
            if(tempNode.right != null)
                queue.add(tempNode.right);
        }
    }

    //先序遍历
    public void preOrderTraversal(DecisionTreeNode root){
        Stack<DecisionTreeNode> stack = new Stack<>();
        DecisionTreeNode node = root;

        while(node != null || stack.size() > 0){
            if(node != null) {
                stack.push(node);
                node = node.left;
            }else{
                node = stack.pop();
                System.out.print(node.val + ",");
                node = node.right;
            }
        }
    }

    //changed double to void
    public double recognition(float[] item, DecisionTreeNode node){
        Log.d("test", String.valueOf(node.index));
        if(node == null){
            System.out.println("树为空");
            Log.d("test", "tree is null");
        }
        if(node.index == null){
            Log.d("test", "value" + String.valueOf(node.val));
            return node.val;
        }

        if((item[node.index - 1] < node.val) && (node.left != null))
            recognition(item, node.left);
        else if((item[node.index - 1] >= node.val) && (node.right != null))
            recognition(item, node.right);

        return 0;
    }

    public static Integer[] readFile(String str)throws IOException{
        File file = new File(str);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String s = null;
        Integer[] test = new Integer[167];
        int i = 0;
        while((s = br.readLine()) != null){
            s = s.substring(2, s.length() - 1);
            try {
                test[i++] = Integer.parseInt(s);
            }catch (NumberFormatException e){
                if("null".equals(s))
                    test[i++] = null;
            }
        }
        System.out.println(Arrays.toString(test));
        br.close();
        return test;
    }


    public static class DecisionTreeNode{
        private double val;
        private Integer index;
        private DecisionTreeNode left;
        private DecisionTreeNode right;

        public DecisionTreeNode(double x, Integer y){
            val = x;
            index = y;
        }
    }

    public static DecisionTreeNode deSerialization(){
        int count = 0;
        Queue<DecisionTreeNode> queue = new LinkedList<>();
        DecisionTreeNode root = new DecisionTreeNode(value[0], index[0]);
        DecisionTreeNode node = root;

        queue.add(node);
        while(!queue.isEmpty()){
            node = queue.peek();
            int left = children[0][count];
            int right = children[1][count];
            if(left != 0 && right != 0){
                DecisionTreeNode nodeLeft = new DecisionTreeNode(value[left - 1], index[left - 1]);
                DecisionTreeNode nodeRight = new DecisionTreeNode(value[right - 1], index[right - 1]);
                node.left = nodeLeft;
                node.right = nodeRight;
                queue.add(nodeLeft);
                queue.add(nodeRight);
            }
            else{
                node.val = activity[count];
            }
            queue.poll();
            count++;
        }
        return root;
    }
}
