import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    int width;
    int height;
    char[][] grid;
    int rounds;
    int bombs;
    BombPool bp;
    int remaining;
    Player(int w, int h, char[][] g, int total){
    	width = w;
    	height = h;
    	grid = g;
    	remaining = total;
    }
    public String compute(int _r, int _b){
    	if(remaining == 0) return "WAIT";
    	if(bp == null) {
        	rounds = _r;
        	bombs = _b;
    		bp = new BombPool(bombs);
    	} else {
    		bp.checkAndExplode(rounds - _r, grid);
    	}
    	int max_damage = 0;
    	Point pt = null;
    	System.err.println(remaining);
    	for(int i = 0; i < height; i++) {
    		for(int j = 0; j < width; j++) {
    		    if(grid[i][j]!='.') continue;
    			Point p = new Point();
                p.x = j; p.y = i;
    			int d = getDamage(p,grid);
    			System.err.println("Point : " + p.x + " " + p.y + " > Damage : "+ d);
    			if(d > max_damage && canWin(p,grid,_b-1,remaining - d)) {
    				max_damage = d;
    				pt = p;
    			}
    		}
    	}
    	printGrid();
    	if(pt == null) {
    		return "WAIT";
    	} else {
    		remaining -= max_damage;
    		bp.place(pt,grid, rounds - _r);
    		return "" + pt.x + " " + pt.y;
    	}
    }
    private boolean canWin(Point p, char[][] g, int bombs, int rem){
    	replace(p,g,'@','&');
    	int max_damage = 1;
    	for(int i = 0; i < height; i++) {
    		for(int j = 0; j < width; j++) {
    		    if(grid[i][j] == '&' || grid[i][j] == '.') {
    			    Point p2 = new Point();
                    p2.x = j; p2.y = i;
    			    int d = getDamage(p2,g);
    			    if(d >= max_damage) {
    				    max_damage = d;
    			    }
    		    }
    		}
    	}
    	replace(p,g,'&','@');
    	System.err.println("Can Win ? " + max_damage + " " + bombs + " " + rem);
    	if(max_damage*bombs >= rem){
    		return true;
    	}
    	return false;
    }
    private void printGrid(){
        for(int i=0; i< height; i++) {
            for(int j = 0; j < width; j++) {
                System.err.print(grid[i][j]);
            }
            System.err.println();
        }
    }
    private int getDamage(Point p, char[][] g) {
    	int damage = 0;
    	//right
    	for(int i = 1; i <= 3 && p.x + i < width; i++) {
    		char node = g[p.y][p.x + i];
    		if(node == '#') break;
    		if(node == '@') damage++;
    	}
    	//left
    	for(int i = 1; i <= 3 && p.x - i >= 0; i++) {
    		char node = g[p.y][p.x - i];
    		if(node == '#') break;
    		if(node == '@') damage++;
    	}
    	//down
    	for(int i = 1; i <= 3 && p.y + i < height; i++) {
    		char node = g[p.y + i][p.x];
    		if(node == '#') break;
    		if(node == '@') damage++;
    	}
    	//up
    	for(int i = 1; i <= 3 && p.y - i >= 0; i++) {
    		char node = g[p.y - i][p.x];
    		if(node == '#') break;
    		if(node == '@') damage++;
    	}
    	return damage;
    }
    private void replace(Point p, char[][] g, char f, char t) {
		//right
    	for(int i = 1; i <= 3 && p.x + i < width; i++) {
    		char node = g[p.y][p.x + i];
    		if(node == '#') break;
    		if(node == f) { g[p.y][p.x + i] = t; }
    	}
    	//left
    	for(int i = 1; i <= 3 && p.x - i >= 0; i++) {
    		char node = g[p.y][p.x - i];
    		if(node == '#') break;
    		if(node == f) { g[p.y][p.x - i] = t; }
    	}
    	//down
    	for(int i = 1; i <= 3 && p.y + i < height; i++) {
    		char node = g[p.y + i][p.x];
    		if(node == '#') break;
    		if(node == f) { g[p.y + i][p.x] = t; }
    	}
    	//up
    	for(int i = 1; i <= 3 && p.y - i >= 0; i++) {
    		char node = g[p.y - i][p.x];
    		if(node == '#') break;
    		if(node == f) { g[p.y - i][p.x] = t; }
    	}
	}
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // width of the firewall grid
        int height = in.nextInt(); // height of the firewall grid
        char[][] g = new char[height][width];
        in.nextLine();
        int total = 0;
        for (int i = 0; i < height; i++) {
            String mapRow = in.nextLine(); // one line of the firewall grid
            for(int j = 0; j < width; j++) {
            	g[i][j] = mapRow.charAt(j);
            	if(g[i][j] == '@') total++;
            }
        }
        boolean first = true;
        Player p = new Player(width,height,g,total);
        // game loop
        while (true) {
            int rounds = in.nextInt(); // number of rounds left before the end of the game
            int bombs = in.nextInt(); // number of bombs left
            /*if(first){
            	p.compute(rounds,bombs);
            	first = false;
            }*/

            System.out.println(p.compute(rounds,bombs));
        }
    }
    class BombPool {
    	Bomb[] b;
    	int start;
    	int end;
    	int size;
    	BombPool(int s) {
    		size = s;
    		b = new Bomb[s];
    	}
    	void place(Point p, char[][] g, int turn) {
    		g[p.y][p.x] = '*';
    		b[end++] = new Bomb(p , turn);
        	replace(p,g,'@','$');
    	}
    	
    	void checkAndExplode(int turn, char[][] g){
    		while(start != end) {
    			Bomb bomb = b[start];
    			if(turn - bomb.turn <= 2) break;
    			replace(bomb.p , g,'$','.');
    			start++;
    		}
    	}
    }
    class Bomb {
    	Point p;
    	int turn;
    	Bomb(Point _p, int _t){
            p = _p;
    		turn = _t;
    	}
    }
    class Point {
    	int x;
    	int y;
    }
}