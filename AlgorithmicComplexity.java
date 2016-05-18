import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Solution {
    public static final int O1 = 0;
    public static final int OLOGN = 1;
    public static final int ON = 2;
    public static final int ONLOGN = 3;
    public static final int ONN = 4;
    public static final int ONNLOGN = 5;
    public static final int ONNN = 6;
    public static final int O2N = 7;
    public static final String[] arr = {
        "O(1)","O(log n)","O(n)","O(n log n)","O(n^2)","O(n^2 log n)","O(n^3)","O(2^n)"
    };
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt();
        double[][] co = new double[N][8];
        double[] ratio = new double[8];
        long[] t = new long[N];
        for (int i = 0; i < N; i++) {
            int num = in.nextInt();
            t[i] = in.nextInt();
            //System.err.println(num + " ### " + t);
            for(int j = 0; j < 8; j++) {
            	co[i][j] = Solution.getValue(num, j);
            	ratio[j] += t[i]/co[i][j];
            }
        }
        for(int i = 0; i < 8; i++){
        	ratio[i] = ratio[i]/N;
        	System.err.println(ratio[i]);
        }
        double[] error = new double[8];
        for(int i = 0; i < N; i++){
        	for(int j = 0; j < 8; j++) {
        	    System.err.println(t[i] + " <> " + ratio[j]*co[i][j]);
        		error[j] += Math.abs((t[i] - ratio[j]*co[i][j]));//*(t[i] - ratio[j]*co[i][j]);
        	}
        }

        int order = 0;
        double min = Double.MAX_VALUE;
        for(int i = 0; i < 8; i++){
        	error[i] = error[i]/N;
        	System.err.println(error[i]);
        	if(error[i] < min) {
        		min = error[i];
        		order = i;
        	}
        }
        
        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        System.out.println(Solution.arr[order]);
    }
    public static double getValue(int n, int order) {
    	switch(order){
    	    case O1:
    	    	return 1;
    	    case OLOGN:
    	    	return Math.log10((double)n);
    	    case ON:
    	    	return n;
    	    case ONLOGN:
    	    	return n*Math.log10((double)n);
    	    case ONN:
    	    	return n*n;
    	    case ONNLOGN:
    	    	return n*n*Math.log10((double)n);
    	    case ONNN:
    	    	return Math.pow(n,2.5);
    	    case O2N:
    	    	return 1 << n/2;
            default:
            	return 1;
    	}
    	
    }
}