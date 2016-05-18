import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
	private final static int SPEED = 0;
	private final static int UP = 1;
	private final static int DOWN = 2;
	private final static int WAIT = 3;
	private final static int SLOW = 4;
	private final static int JUMP = 5;
	int M;
	int V;
	int[][] lane;
	int lane_length;
	Bike[] bike;
	int speed;
	ResultQ result;
	int max_saved;
    public Player(int _m, int _v){
    	M = _m; V = _v;
    	bike = new Bike[M];
    	for(int i = 0; i<M; i++){
    		bike[i] = new Bike();
    	}
    }
    public void addLane(int i, String l){
    	if(lane == null) {
    		lane = new int[4][];
    	}
    	if(lane[i] == null){
    		lane[i] = new int[l.length()];
    		lane_length = lane[i].length;
    	}
    	int count = 0;
    	for(int j=1; j<l.length(); j++) {
    		lane[i][j] = l.charAt(j) == '0' ? ++count : count;
    	}
    }
    public void setSpeed(int s){
    	speed = s;
    	//System.err.println("Speed : " + speed);
    }
    public void setBike(int i, int x, int y, int a){
    	Bike b = bike[i];
    	b.x = x;
    	b.y = y;
    	b.a = a;
    	//System.err.println("Bike " + i + " : " + x + " " + y + " " + a);
    }
    public void compute(){
    	if(result == null){
    		GameState gs = getNext(0,null);
    		Result cur = new Result(50);
    		makeDecision(gs,cur);
    	}
    }
    private boolean makeDecision(GameState gs, Result q){
    	if(gs.rCount >= V){ result = new ResultQ(q); V = gs.rCount+1; return true; }
    	if(gs.aCount < V) return false;
    	if(q.isFull()) return false;
    	for(int i=0; i < 6; i++){
    		if(isDecisionValid(i, gs)){
    			q.push(i);
    			makeDecision(getNext(i,gs),q);
    			q.pop();
    		}
    	}
    	return false;
    }
    private GameState getNext(int decision, GameState gs){
		GameState res = new GameState();
		Bike[] list;
		if(gs == null) {
			list = bike;
			res.speed = speed;
			res.aCount = M;
			res.rCount = 0;
		} else {
			list = gs.b;
			res.speed = gs.speed;
			res.aCount = gs.aCount;
			res.rCount = gs.rCount;
		}
		res.b = new Bike[list.length];
		for(int i=0; i<list.length; i++){
			res.b[i] = new Bike();
			res.b[i].x = list[i].x;
			res.b[i].y = list[i].y;
			res.b[i].a = list[i].a;
			res.b[i].reached = list[i].reached;
		}
		if(gs != null) {
		    int yChange = 0;
		    int xChange = 0;
		    switch(decision) {
		        case SPEED:
		    	    res.speed++;
		    	    break;
		        case SLOW:
		    	    res.speed--;
		    	    break;
		        case UP:
		    	    yChange = -1;
		    	    break;
		        case DOWN:
		    	    yChange = 1;
		    	    break;
		    }
		    xChange = res.speed;
			for(int i=0; i<list.length; i++){
				Bike br = res.b[i];
				if(br.a == 1 && !br.reached) {
					br.x += xChange;
					br.y += yChange;
					System.err.println(br.x + " " + br.y);
					if(br.y == 0) res.canGoUp = false; 
					if(br.y == 3) res.canGoDown = false;
					if(br.x >= lane_length) { br.x = lane_length - 1; }
					if(br.x != 0 && lane[br.y][br.x] != lane[br.y][br.x-1]) {
						br.a = 0; //not moved on a hole
					} else if(decision != JUMP) {
					    if(yChange != 0) {
						    br.a = (lane[br.y][list[i].x] != lane[br.y][br.x] ||
						    		    lane[list[i].y][list[i].x] != lane[list[i].y][br.x - 1]) ? 0 : 1;
					    } else {
					    	br.a = lane[br.y][br.x] != lane[br.y][list[i].x] ? 0 : 1;
					    }
					}
					if(br.a == 0) res.aCount--;
					if(br.a == 1) {
						if(br.y == 0) {
							res.canGoUp = false;
						} else if(br.y == 3) {
							res.canGoDown = false;
						}
						if(br.x == lane_length - 1) {
						    br.reached = true;
						    res.rCount++;
					    }
					}
				}
			}
		    
		} else {
			for(int i=0; i<res.b.length; i++){
 			    if(res.b[i].y == 0) res.canGoUp = false;
				if(res.b[i].y == 3) res.canGoDown = false;
			}
		}
		return res;
	}
    private boolean isDecisionValid(int val, GameState gs){
    	if(val == UP && (!gs.canGoUp || gs.speed == 0)) return false;
    	if(val == DOWN && (!gs.canGoDown || gs.speed == 0)) return false;
    	if(val == SLOW && gs.speed <= 1) return false;
    	if(val == WAIT && gs.speed == 0) return false;
    	return true;
    }
    public String getResult(){
    	switch(result.dequeue()) {
    	    case SPEED:
    	    	return "SPEED";
    	    case SLOW:
    	    	return "SLOW";
    	    case JUMP:
    	    	return "JUMP";
    	    case WAIT:
    	    	return "WAIT";
    	    case UP:
    	    	return "UP";
    	    case DOWN:
    	    	return "DOWN";
    	    default:
    	    	return "SPEED";
    	}
    }
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int M = in.nextInt();
        int V = in.nextInt();
        Player p = new Player(M,V);
        p.addLane(0,in.next());
        p.addLane(1,in.next());
        p.addLane(2,in.next());
        p.addLane(3,in.next());

        // game loop
        for(int k=0; k<50; k++) {
            p.setSpeed(in.nextInt());
            for (int i = 0; i < M; i++) {
                p.setBike(i, in.nextInt(), in.nextInt(), in.nextInt());
            }

            p.compute();

            System.out.println(p.getResult());
        }
        in.close();
    }
    static class Bike {
    	int x;
    	int y;
    	int a;
    	boolean reached;
    }
    static class Result {
    	int[] stack;
    	int size;
    	int top;
    	Result(int s){
    		size = s;
    		top = -1;
    		stack = new int[size];
    	}
    	public boolean push(int val) {
    		if((top + 1) >= size) return false;
    		stack[++top] = val;
    		return true;
    	}
    	public int pop(){
    		if(!isEmpty()){
    			int val = stack[top];
    			top--;
    			return val; 
    		}
    		return -1;
    	}
    	public boolean isEmpty(){
    		return top==-1;
    	}
    	public boolean isFull(){
    		return top==(size-1);
    	}
    }
    static class ResultQ {
    	int[] queue;
    	int size;
    	int start;
    	int len;
    	ResultQ(int s){
    		len = s;
    		start = 0;
    		size = 0;
    		queue = new int[len];
    	}
    	ResultQ(Result s){
    		len = s.size;
    		start = 0;
    		size = s.top + 1;
    		queue = new int[len];
    		for(int i=0; i<size; i++){
    			queue[(start+i)%len] = s.stack[(start+i)%len];
    		}
    	}
    	public boolean enqueue(int val) {
    		if(size == len) return false;
    		queue[(start+size)%len] = val;
    		size++;
    		return true;
    	}
    	public int dequeue(){
    		if(!isEmpty()){
    			int val = queue[start];
    			start = (start+1)%len;
    			size--;
    			return val; 
    		}
    		return -1;
    	}
    	public boolean isEmpty(){
    		return size == 0;
    	}
    	public boolean isFull(){
    		return size == len;
    	}
    }
    class GameState {
    	boolean canGoUp = true;
    	boolean canGoDown = true;
    	int speed;
    	Bike[] b;
    	int rCount;
    	int aCount;
    	int count;
    }
}

