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
        int L = in.nextInt();
        int H = in.nextInt();
        in.nextLine();
        int[][] surface = new int[H][L];
        for (int i = 0; i < H; i++) {
            String row = in.nextLine();
            for(int j=0; j < row.length(); j++){
                surface[i][j] = row.charAt(j) == '#' ? 0 : 1;
            }
        }
        int N = in.nextInt();
        int[] answer = new int[N];
        Lake[] lake = new Lake[N];
        Stack s = new Stack(L*H);
        for (int i = 0; i < N; i++) {
            int X = in.nextInt();
            int Y = in.nextInt();
            if(surface[Y][X] == 0) {
                answer[i] = 0;
            } else if(surface[Y][X] == 1) {
                Lake l = new Lake();
                int ln = i + 2;
                s.push(new Point(X,Y));
                surface[Y][X] = ln;
                while(!s.isEmpty()){
                    Point p = s.pop();
                    l.area++;
                    if(p.y - 1 >= 0 && surface[p.y - 1][p.x] == 1){
                        surface[p.y - 1][p.x] = ln;
                        s.push(new Point(p.x,p.y - 1));
                    }
                    if(p.y + 1 < H && surface[p.y + 1][p.x] == 1){
                        surface[p.y + 1][p.x] = ln;
                        s.push(new Point(p.x,p.y + 1));
                    }
                    if(p.x - 1 >= 0 && surface[p.y][p.x - 1] == 1){
                        surface[p.y][p.x - 1] = ln;
                        s.push(new Point(p.x - 1,p.y));
                    }
                    if(p.x + 1 < L && surface[p.y][p.x + 1] == 1){
                        surface[p.y][p.x + 1] = ln;
                        s.push(new Point(p.x + 1,p.y));
                    }
                }
                
                lake[i] = l;
                answer[i] = l.area;
            } else {
                answer[i] = lake[surface[Y][X] - 2].area;
            }
        }
        for (int i = 0; i < N; i++) {

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println(answer[i]);
        }
    }
    static class Point {
        int x;
        int y;
        Point(int _x, int _y){
            x = _x; y = _y;
        }
    }
    static class Stack {
        int size;
        int top;
        Point[] st;
        Stack(int s) {
            size = s;
            top = -1;
            st = new Point[size];
        }
        boolean isEmpty() {
            return top == -1;
        }
        void push(Point p){
            if(top < size - 1) {
                top++;
                st[top] = p;
            }
        }
        Point pop() {
            Point res = null;
            if(!isEmpty()){
                res = st[top];
                st[top] = null;
                top--;
            }
            return res;
        }
        void reset() {
            top = -1;
        }
    }
    static class Lake {
        int area;
    }
}