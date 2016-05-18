import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
	private static final int WAIT = 0;
	private static final int BLOCK = 1;
	private static final int ELEVATOR = 2;
	private static final int LEFT = -1;
	private static final int RIGHT = 1;
    int nbFloors; // number of floors
    int width; // width of the area
    int nbRounds; // maximum number of rounds
    int exitFloor; // floor on = in.nextInt(); 
    int exitPos;// position of the exit on its floor
    int nbTotalClones; // number of generated clones
    int nbAdditionalElevators; // number of additional elevators that you can build
    int nbElevators; // number of elevators
    int[][] el;
    Solution res;
    boolean first = false;
    int count = 0;
    Player(Scanner in){
        nbFloors = in.nextInt(); 
        width = in.nextInt();
        nbRounds = in.nextInt();
        exitFloor = in.nextInt();
        exitPos = in.nextInt();
        nbTotalClones = in.nextInt();
        nbAdditionalElevators = in.nextInt();
        nbElevators = in.nextInt();
        el = new int[nbFloors][width];
        for (int i = 0; i < nbElevators; i++) {
            int elevatorFloor = in.nextInt(); // floor on which this elevator is found
            int elevatorPos = in.nextInt(); // position of the elevator on its floor
            el[elevatorFloor][elevatorPos] = 1;
        }
        res = new Solution(nbRounds);
    }
    private String getActionString(int action){
    	switch(action){
    	    case BLOCK:
    	    	return "BLOCK";
    	    case ELEVATOR:
    	    	return "ELEVATOR";
    	    case WAIT:
    	    default:
    	    	return "WAIT";
    	}
    }
	public void compute(int cF, int cP, int d) {
		findSolution(cF, cP, d, nbRounds,nbAdditionalElevators,nbTotalClones);
		System.err.println("Recursion count : " + count);
	}
	private boolean findSolution(int floor, int pos, int direction, int turns, int extraEl, int clones) {
		count++;
		if(floor > exitFloor) return false;
		if(turns <= 0) return false;
		if(clones <= 0) return false;
		boolean isBlock = false;
		int cost = 1;
		for(int z = 0; z < 2; z++) {
			boolean skip = false;
			int i = pos;
			int turnsRemaining = turns;
			// block
			if(isBlock) {
				cost += 3;
				direction = -direction;
				clones--;
				//to significantly decrease recursion calls
				//-------------------------
				i = pos + direction;
				turnsRemaining--;
				//-------------------------
				if(clones == 0) break;
			}
			
		    for(; i < width && i >= 0 && turnsRemaining > 0; i += direction){
			
		    	//reached exit
			    if(isExit(floor,i)) {
			    	if(isBlock) pushCommand(pos, floor, BLOCK);
			    	return true;
			    }
			    
			    //if we find elevator don't search further
			    if(hasElevator(floor, i)){
                    if(findSolution(floor+1, i, direction, turnsRemaining - cost,extraEl,clones)){
                    	//System.err.println("Floor : " + floor + " > " + i + " > " + (turnsRemaining - cost));
                    	if(isBlock) pushCommand(pos, floor, BLOCK);
                	    return true;
                    }
				    break;
			    }
			    
			    //try build elevator
			    if(floor != exitFloor && extraEl > 0) {
			    	if(!skip && findSolution(floor+1,i,direction, turnsRemaining - cost - 3,extraEl - 1,clones-1)){
				    	//System.err.println("Floor : " + floor + " > " + i +  " > " + (turnsRemaining - cost) + " (build elevator)");
				    	pushCommand(i, floor, ELEVATOR);
				    	if(isBlock) pushCommand(pos, floor, BLOCK);
					    return true;
				    }
				    if(skip && hasElevator(floor + 1, i)) {
				    	skip = false;
				    } else {
				    	if(!skip && !hasElevator(floor + 1, i))
				    	    skip = true;
				    }
			    }
			    turnsRemaining--;
			    
		    }
		    isBlock = true;
		}
		return false;
	}
	private boolean hasElevator(int floor, int pos){
		return el[floor][pos] == 1;
	}
	private void pushCommand(int pos, int floor,int type){
		Command c = new Command();
		c.type = type; c.pos = pos; c.floor = floor;
		res.push(c);
	}
	boolean isExit(int floor, int pos){
		return floor == exitFloor && pos == exitPos;
	}
    public String getResult(int cF, int cP){
    	Command c = res.peek();
    	if(c != null && c.floor == cF && c.pos == cP) {
    		res.pop();
    		return getActionString(c.type);
    	}
    	return getActionString(WAIT);
    }
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        Player p = new Player(in);
        boolean first = true;

        // game loop
        while (true) {
            int cloneFloor = in.nextInt(); // floor of the leading clone
            int clonePos = in.nextInt(); // position of the leading clone on its floor
            String direction = in.next(); // direction of the leading clone: LEFT or RIGHT
            
            if(first)
                p.compute(cloneFloor,clonePos,"LEFT".equals(direction) ? 0 : 1);
            first = false;


            System.out.println(p.getResult(cloneFloor, clonePos));
        }
    }
    static class Solution {
    	Command[] stack;
    	int size;
    	int top;
    	Solution(int s){
    		size = s;
    		top = -1;
    		stack = new Command[size];
    	}
    	public boolean push(Command val) {
    		if((top + 1) >= size) return false;
    		stack[++top] = val;
    		return true;
    	}
    	public Command pop(){
    		if(!isEmpty()){
    			Command val = stack[top];
    			top--;
    			return val; 
    		}
    		return null;
    	}
    	public Command peek(){
    		if(!isEmpty()){
    			return stack[top];
    		}
    		return null;
    	}
    	public boolean isEmpty(){
    		return top==-1;
    	}
    }
    static class Command {
    	int pos;
    	int floor;
    	int type;
    }
}