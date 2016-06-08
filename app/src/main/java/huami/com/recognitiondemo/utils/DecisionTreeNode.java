package huami.com.recognitiondemo.utils;

import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by test on 2016/6/6.
 */
public class DecisionTreeNode {
    private static int len = 137;
//    private static Integer[][] children = new Integer[2][len];
//    private static double[] value = new double[len];
//    private static Integer[] index = new Integer[len];
//    private static int[] activity = new int[len];

    public double val;
    public Integer idx;
    public DecisionTreeNode left;
    public DecisionTreeNode right;

    public static Integer[][] children = new Integer[][]
            {{2, 4, 6, 0, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 0, 0, 28, 30, 32, 34, 36, 0, 38, 0, 0, 0, 0, 0, 40, 42, 44, 46, 0, 48, 50, 52, 54, 56, 58, 0, 60, 62, 64,
                    66, 68, 0, 70, 0, 0, 0, 72, 0, 74, 76, 78, 0, 80, 82, 84, 0, 0, 0, 0, 0, 0, 86, 0, 88, 90, 0, 0, 92, 0, 94, 0, 0, 96, 0, 0, 98, 0, 0, 100, 102, 104,
                    106, 108, 0, 110, 112, 114, 0, 0, 0, 116, 0, 0, 0, 118, 0, 0, 0, 0, 0, 0, 0, 120, 0, 0, 122, 0, 0, 0, 124, 126, 128, 0, 130, 0, 132, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 134, 0, 0, 136, 0, 0, 0, 0},
                    {3, 5, 7, 0, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 0, 0, 29, 31, 33, 35, 37, 0, 39, 0, 0, 0, 0, 0, 41, 43, 45, 47, 0, 49, 51, 53, 55, 57, 59, 0, 61,
                            63, 65, 67, 69, 0, 71, 0, 0, 0, 73, 0, 75, 77, 79, 0, 81, 83, 85, 0, 0, 0, 0, 0, 0, 87, 0, 89, 91, 0, 0, 93, 0, 95, 0, 0, 97, 0, 0, 99, 0, 0,
                            101, 103, 105, 107, 109, 0, 111, 113, 115, 0, 0, 0, 117, 0, 0, 0, 119, 0, 0, 0, 0, 0, 0, 0, 121, 0, 0, 123, 0, 0, 0, 125, 127, 129, 0, 131, 0,
                            133, 0, 0, 0, 0, 0, 0, 0, 0, 0, 135, 0, 0, 137, 0, 0, 0, 0}};
    //cutPoint
    public static double[] value = new double[]{0.0505379590611844, 0.0671043610167655, 50.6501330458988, 0.0, 0.357050561527107, 1.0355, 0.398, 45.5295025, 1.5,
            0.849994000000001, 0.0883504271750604, -588.411715324213, 1.1078360542304, 1.5, 0.0, 0.0, 0.277775893783356, 0.0844377536008174, 221.875111735676,
            33.3038210754297, 235.363599050036, 0.0, 191.368963276927, 0.0, 0.0, 0.0, 0.0, 0.0, 8.26085943775101E-6, 5.22861122267558, 3.59144071564443, 0.254056,
            0.0, 0.977671132, 0.198, 24.9666090270312, 6.19844399146313, 3.84697611849539, 194.120955277955, 0.0, 135.024154584138, 0.83600099632652, 27.0838177811531,
            0.598, 3.92521596576242, 0.0, 0.656590394417278, 0.0, 0.0, 0.0, 0.351092036, 0.0, 0.677058736, 0.190387119939638, 1.2137709590751, 0.0, 0.974649644143648,
            0.334969011880849, 359.739349455326, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.438, 0.0, 0.0837601835946263, 4.94957732874163, 0.0, 0.0, 1.02959141891875, 0.0,
            81.0620526919069, 0.0, 0.0, 7.05486897411531, 0.0, 0.0, 0.162318996683932, 0.0, 0.0, 225.88468636101, 2.9428813823398, -884.199070065168, 54.3077145,
            0.0866520803176357, 0.0, 8.17890062798954, 2.58411191952777, 0.158, 0.0, 0.0, 0.0, 131.794476993444, 0.0, 0.0, 0.0, 7.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            215.838296107605, 0.0, 0.0, 9.1050473397304, 0.0, 0.0, 0.0, 1.0156926890385, 5.64897032050837, 100.87115190215, 0.0, 82.1298469772191, 0.0, 0.97946638687026,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0388369668514056, 0.0, 0.0, 1.01046697121323, 0.0, 0.0, 0.0, 0.0};
    //cutPredictor
    public static Integer[] index = new Integer[]{118, 45, 12, null, 91, 21, 72, 145, 44, 37, 67, 35, 55, 80, null, null, 122, 67, 64, 82, 149, null, 102, null, null,
            null, null, null, 20, 64, 66, 1, null, 25, 72, 66, 66, 60, 100, null, 150, 120, 100, 72, 60, null, 9, null, null, null, 7, null, 25, 32, 111, null, 115, 69,
            102, null, null, null, null, null, null, 72, null, 69, 12, null, null, 31, null, 147, null, null, 46, null, null, 97, null, null, 149, 60, 71, 145, 45, null,
            71, 120, 36, null, null, null, 119, null, null, null, 80, null, null, null, null, null, null, null, 147, null, null, 120, null, null, null, 55, 101, 63, null,
            120, null, 55, null, null, null, null, null, null, null, null, null, 20, null, null, 55, null, null, null, null};
    //nodeClass
    public static int[] activity = new int[]{9, 10, 9, 10, 10, 9, 4, 9, 10, 9, 1, 4, 3, 10, 9, 9, 10, 9, 9, 5, 1, 8, 4, 3, 2, 9, 10, 10, 9, 5, 9, 9, 6, 5, 6, 9, 1, 6, 4,
            10, 9, 9, 5, 9, 9, 9, 9, 5, 1, 6, 2, 9, 9, 9, 1, 6, 4, 2, 4, 9, 10, 5, 9, 5, 6, 9, 5, 2, 9, 10, 9, 9, 2, 9, 1, 9, 1, 5, 1, 4, 1, 2, 4, 4, 4, 9, 9, 2, 5, 9,
            9, 5, 9, 9, 4, 1, 9, 9, 4, 4, 1, 6, 4, 8, 4, 9, 9, 10, 9, 5, 1, 5, 9, 9, 2, 4, 2, 4, 6, 9, 7, 5, 2, 7, 9, 1, 2, 9, 4, 7, 4, 9, 9, 3, 7, 7, 9};


    public DecisionTreeNode(double x, Integer y){
        val = x;
        idx= y;
    }

   public static DecisionTreeNode root = new DecisionTreeNode(value[0], index[0]);
    public static int recogAct;


    public static DecisionTreeNode deSerialization(){
        int count = 0;
        Queue<DecisionTreeNode> queue = new LinkedList<>();
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
            }else{
                node.val = activity[count];
            }
            queue.poll();
            count++;
        }
        return root;
    }

    //层次遍历
    public void levelTraversal(){
        Queue<DecisionTreeNode> queue = new LinkedList<>();
        queue.add(root);
        DecisionTreeNode tempNode;
        while(!queue.isEmpty()){
            tempNode = queue.poll();
            System.out.print(tempNode.val + ",");
            Log.d("test", String.valueOf(tempNode.idx));
//            Log.d("test", tempNode.idx);
            if(tempNode.left != null)
                queue.add(tempNode.left);
            if(tempNode.right != null)
                queue.add(tempNode.right);
        }
    }

    public double classification(float[] item){
//    public double classification(double[] item){
//        DecisionTreeNode root = deSerialization();
        if(root == null){
            System.out.print("nullnull");
            return 0;
        }
//        levelTraversal(root);
//        preOrderTraversal(root);


        recognition(item, root);
        return recogAct;
    }

        //changed double to void
    public double recognition(float[] item, DecisionTreeNode node){
//    public double recognition(double[] item, DecisionTreeNode node){
        Log.d("test", String.valueOf(node.idx));
        if(node == null){
            System.out.println("树为空");
            Log.d("test", "tree is null");
        }
        if(node.idx == null){
            Log.d("test", "value" + String.valueOf(node.val));
            recogAct = (int)node.val;
            Log.d("test1", String.valueOf(recogAct));
            return node.val;
        }

        if((item[node.idx - 1] < node.val) && (node.left != null))
            recognition(item, node.left);
        else if((item[node.idx - 1] >= node.val) && (node.right != null))
            recognition(item, node.right);

        return 0;
    }
}
