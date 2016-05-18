import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Solution {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt();
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            String subseq = in.next();
            boolean add = true;
            for(int j = 0; j < list.size(); j++){
                if(list.get(j).contains(subseq)){
                	add = false;
                	break;
                }
            }
            if(add) list.add(subseq);
        }
        String[] arr = new String[list.size()];
        list.toArray(arr);
        Permutation p = new Permutation(arr);
        int minLen = Integer.MAX_VALUE;
        do {
        	String curStr = arr[0];
        	for(int i = 1; i < arr.length; i++){
        		String comp = arr[i];
        		boolean match = false;
        		if(curStr.contains(comp)) continue;
        		for(int j = 1; j < curStr.length(); j++){
        			if(comp.length() <= curStr.length() - j) continue;
        			if(comp.substring(0, curStr.length() - j).contains(curStr.substring(j))){
        				curStr = curStr + comp.substring(curStr.length() - j);
        				match = true;
        				break;
        			}
        		}
        		if(!match) {
        			curStr = curStr + comp;
        		}
        	}
        	
        	//System.err.println();
        	if(minLen > curStr.length() ) {
        		//System.err.println(curStr);
        		minLen = curStr.length();
        	}
        }while(p.next_permutation());
        
        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        System.out.println(minLen);
    }
    static class Permutation {
    	int[] arr;
    	Object[] o;
    	int N;
    	Permutation(Object[] _o){
    		N = _o.length;
    		arr = new int[N];
    		o = _o;
    		for(int i = 0; i < N; i++){
    			arr[i] = i;
    		}
    	}
    	public boolean next_permutation(){
    		int i;
    		for(i = N - 2; i >= 0; i--) {
    		    if (arr[i] < arr[i + 1])
    		        break;
    		}
    		if(i == -1) {
    			return false;
    		}
    		int j;
    		for(j = N - 1; j > i; j--) {
    		    if (arr[j] > arr[i])
    		        break;
    		}
    		swap(i++, j);

            for(j = N - 1; j > i; i++, j--) {
    		    swap(i, j);
    		}
            return true;
    	}
    	public void swap(int x, int y) {
    		 arr[x] ^= arr[y];
    		 arr[y] ^= arr[x];
    		 arr[x] ^= arr[y];
    		 Object temp = o[x];
    		 o[x] = o[y];
    		 o[y] = temp;
        }
    }
}