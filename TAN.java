import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Solution {
	int N;
	Stop[] stops;
	int startIndex;
	int endIndex;
	int edges = 0;
	ArrayList<Edge>[] adj;
	ArrayList<Integer> res;
	Solution(int n){
		N = n;
		stops = new Stop[N];
		adj = new ArrayList[N];
	}
	public void setStart(int index) {
		startIndex = index;
	}
	public void setEnd(int index) {
		endIndex = index;
	}
    public void add(int i,Stop s){
    	stops[i] = s;
    	//System.err.println(s.toString());
    }
    public void addRoute(String s1, String s2) {
    	int i = find(s1);
    	int j = find(s2);
    	if(adj[i] == null){
    		adj[i] = new ArrayList<>();
    	}
    	//System.err.println(i + " -> " + j);
    	Edge e = new Edge();
    	e.to = j;
    	e.distance = calculateD(i,j);
    	adj[i].add(e);
    	edges++;
    }
    private double calculateD(int i, int j) {
    	Stop f = stops[i];
    	Stop t = stops[j];
    	double x = (t.lag - f.lag)*Math.cos(Math.PI*(t.lat + f.lat)/360);
    	double y = (t.lat - f.lat);
    	return Math.sqrt(x*x + y*y)*6371;
    }
    public void compute(){
        if(endIndex == startIndex){
            System.out.println(stops[startIndex].name);
            return;
        }
    	Heap h = new Heap(N);
    	stops[startIndex].parent = startIndex;
    	stops[startIndex].distance = 0;
    	h.add(stops[startIndex]);
    	while(!h.isEmpty()){
    		Stop g = h.removeMin();
    		//System.err.println(g.index);
    		if(adj[g.index] == null) continue;
    		for(Edge t : adj[g.index]){
    			Stop v = stops[t.to];
    			if(v.distance > g.distance + t.distance){
    				v.distance = g.distance + t.distance;
    				v.parent = g.index;
    			    h.remove(v);
    			    h.add(v);
    			}
    		}
    	}
    	if(stops[endIndex].parent == -1) {
    		System.out.println("IMPOSSIBLE");
    	} else {
    		Stop x = stops[endIndex];
    		String str = stops[endIndex].name;
    		while(x.parent != startIndex) {
    			str = stops[x.parent].name + '\n' + str;
    			x = stops[x.parent];
    		}
    		str = stops[startIndex].name + '\n' + str;
    		System.out.println(str);
    	}
    }
    public int find(String s){
    	for(int i = 0; i < N; i++){
    		if(stops[i].identifier.equals(s)) return i;
    	}
    	return -1;
    }
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        String startPoint = in.next();
        String endPoint = in.next();
        int N = in.nextInt();
        in.nextLine();
        Solution sol = new Solution(N);
        for (int i = 0; i < N; i++) {
            String[] stop = in.nextLine().split(",");
            Stop s = new Stop();
            if(startPoint.equals(stop[0])) sol.setStart(i);
            if(endPoint.equals(stop[0])) sol.setEnd(i);
            s.identifier = stop[0].split(":")[1];
            s.name = stop[1].substring(1, stop[1].length() - 1);
            s.desc = stop[2];
            s.lat = Double.parseDouble(stop[3]);
            s.lag = Double.parseDouble(stop[4]);
            //s.zone_id = stop[5];
            //s.url = stop[6];
            s.type = stop[4];
            s.index = i;
            //s.m_id = stop[8];
            sol.add(i,s);
        }
        int M = in.nextInt();
        in.nextLine();
        for (int i = 0; i < M; i++) {
            String route = in.nextLine();
            String[] node = route.split(" ");
            sol.addRoute(node[0].split(":")[1], node[1].split(":")[1]);
        }

        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        sol.compute();
    }
    static class Edge {
    	int to;
    	double distance;
    }
    static class Stop {
    	String identifier;
    	String name;
    	String desc;
    	double lat;
    	double lag;
    	String zone_id;
    	String url;
    	String type;
    	String m_id;
    	double distance = Double.MAX_VALUE;
    	int index;
    	int parent= -1;
    	public String toString(){
    	    return identifier + " " + name + " " + desc + " " + lat + " " + lag; 
    	}
    }
    static class Heap {
    	Stop[] h;
    	int N;
    	int size;
    	Heap(int n) {
    		N = n;
    		size = 0;
    		h = new Stop[N];
    	}
    	void add(Stop o){
    		int i = size;
    		while(i != 0 && h[(i-1)/2].distance > o.distance) {
    			h[i] = h[(i-1)/2];
    			i = (i-1)/2;
    		}
    		h[i] = o;
    		size++;
    	}
    	void remove(Stop e){
    		int index = 0;
    		for(index = 0; index < size; index++){
    			if(h[index].equals(e)) break;
    		}
    		if(index == size) return;
    		h[index] = h[--size];
    		heapify(index);
    	}
    	Stop removeMin() {
    		if(isEmpty()) return null;
    		/*for(int i=0; i< size; i++){
    			System.err.print("[" + h[i].start + " " + h[i].time + "] < ");
    		}*/
    		Stop res = h[0];
    		size--;
    		if(!isEmpty()) {
    			h[0] = h[size];
    			heapify(0);
    		}
    		return res;
    	}
    	void heapify(int index){
    		Stop o = h[index];
    		int ni = 2*index + 1;
    		if(ni + 1 < size && h[ni].distance > h[ni+1].distance) {
    			ni = ni + 1;
    		}
    		if(ni < size && h[ni].distance < o.distance) {
    			h[index] = h[ni];
    			h[ni] = o;
    			heapify(ni);
    		}
    	}
    	boolean isEmpty() { return size == 0; }
    }
}