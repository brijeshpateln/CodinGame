import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    int N, L, E;
    HashMap<Integer,ArrayList<Integer>> adj;
    ArrayList<Integer> exit;
    int[] cnt;
    public Player(int n, int l, int e, HashMap<Integer,ArrayList<Integer>> list,
        ArrayList<Integer> elist) {
        N=n;L=l;E=e;
        adj = list;
        exit = elist;
        cnt = new int[N];
        for(Integer j : exit) {
            ArrayList<Integer> li = adj.get(j);
            for(Integer i : li) {
                cnt[i] = cnt[i] + 1;
            }
        }
    }
    public void compute(int pos) {
        Queue q = new Queue(N);
        boolean[] visited = new boolean[N];
        int f = -1,t = -1;
        //bfs
        q.enqueue(pos);
            
        while(!q.isEmpty()) {
            Integer node = q.dequeue();
            visited[node] = true;
            
            ArrayList<Integer> gate = getExitGates(node);
            //check if it has more than one exits
            if(gate.size() > 1) {
                remove(node,gate.get(0));
                return;
            } else if(gate.size()==1) {
                // check furthur keeping this gate as the nearest exit
                if(f == -1) {
                    System.err.println("**** t : " + gate.get(0));
                    f = node;
                    t = gate.get(0);
                }
                for(Integer j : adj.get(node)){
                    if(!visited[j])
                        q.enqueue(j);
                }
                // break the immediate link
                if(node == pos) break;
            }
            if(f == -1) {
                for(Integer j : adj.get(node)){
                    if(!visited[j])
                        q.enqueue(j);
                }
            }
        }
        if(f != -1) {
            remove(f,t);
        }
        
    }
    public ArrayList<Integer> getExitGates(Integer n) {
        ArrayList<Integer> list = new ArrayList<>();
        for(Integer i : exit) {
            ArrayList<Integer> con = adj.get(i);
            for(Integer z : con){
                if(n == z) {
                    list.add(i);
                    break;
                }
            }
        }
        return list;
    }
    public void remove(Integer f, Integer t){
        ArrayList<Integer> n1 = adj.get(f);
        ArrayList<Integer> n2 = adj.get(t);
        n1.remove(t);
        n2.remove(f);
        System.out.println(f + " " + t);
    }
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt(); // the total number of nodes in the level, including the gateways
        int L = in.nextInt(); // the number of links
        int E = in.nextInt(); // the number of exit gateways
        HashMap<Integer,ArrayList<Integer>> adj = new HashMap<>();
        for(int i=0; i < N; i++){
            adj.put(i,new ArrayList<>());
        }
        for (int i = 0; i < L; i++) {
            int N1 = in.nextInt(); // N1 and N2 defines a link between these nodes
            int N2 = in.nextInt();
            adj.get(N1).add(N2);
            adj.get(N2).add(N1);
        }
        ArrayList<Integer> exit = new ArrayList<>();
        for (int i = 0; i < E; i++) {
            int EI = in.nextInt(); // the index of a gateway node
            exit.add(EI);
        }
        Player p = new Player(N,L,E,adj,exit);
        
        // game loop
        while (true) {
            int SI = in.nextInt(); // The index of the node on which the Skynet agent is positioned this turn
            p.compute(SI);
        }
    }
    public static class Queue {
        int N;
        int top;
        int size;
        int[] arr;
        public Queue(int n) {
            N = n;
            arr = new int[N];
        }
        public void enqueue(int val) {
            System.err.println("Enqueue : " + val);
            if(size < N - 1) {
                arr[(top + size)%N] = val;
                size++;
            } else {
                System.err.println("Overflow");
            }
        }
        public int dequeue() {
            int temp = arr[top];
            System.err.println("Dequeue : " + temp);
            top = (top+1)%N;
            size--;
            return temp;
        }
        public boolean isEmpty() {
            return (size == 0);
        }
    }
}