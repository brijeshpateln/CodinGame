import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
	public static final int GRID_H = 12;
	public static final int GRID_W = 6;
	public static final int ROTATION_0 = 0;
	public static final int ROTATION_1 = 1;
	public static final int ROTATION_2 = 2;
	public static final int ROTATION_3 = 3;
	public static final int DEPTH = 3;
	public static final int MAX_QUEUE = 8;
	public static final int EMPTY = 6;
	public static final int BLOCK = 0;
	public static final double[] weight = { 
			2.0,   //adj
			1.3, //diag 
			1.7, //diag2
			1.7, //col
			2.0, //adjcol
			0.5,  //height
			0.5, //x
	};
	private static final int[] COLOR_B = { 0, 0, 2, 4, 8, 16 };
	public static final int[] WIDTH = { 0, 1, 2, 2, 1, 0 };
    public static final int[] HEIGHT = { 4, 4, 4, 4, 3, 3, 3, 2, 2, 1, 0, 0};
	private static final int[] GROUP_B = { 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 8 };
	public Stack st = new Stack(GRID_H*GRID_W);
	public static final Point[][] point = new Point[GRID_W][GRID_H];
    public static final int MAX_POINTS = 5040;
	String debug;
	DefaultAI ai;
	EnemyAI eai;
	Grid me;
	Grid enemy;
	private BlockList bList;
	int selfBlocksLeft;
	int selfMaxHeight;
	int selfMinHeight;
	int selfDeadBlocks;
	int selfAvgHeight;
	int enemyBlocksLeft;
	int enemyMaxHeight;
	int enemyMinHeight;
	int enemyAvgHeight;
	int criticalPos;
	int criticalRot;
	int rotation = 0;
	int res;
	int enemyAttack;
	boolean aggressive = false;
	int aggressionStep = 0;
	int deadBlockCount = 0;
	Player(BlockList b){
		me = new Grid();enemy = new Grid();bList = b;
		for(int i = 0; i<GRID_H; i++){
			for(int j = 0; j<GRID_W; j++){
				point[j][i] = new Point(j,i);
			}
		}
		eai = new EnemyAI();
		ai = new DefaultAI();
	}
	public void reset(){
		me.reset();
		enemy.reset();
	}
	private void printStat(){
		System.err.println("Enemy : Blocks " + enemyBlocksLeft + " MinHeight " + enemyMinHeight + " MaxHeight " + enemyMaxHeight + " Avg " + enemyAvgHeight);
		System.err.println("Self : Blocks " + selfBlocksLeft + " MinHeight " + selfMinHeight + " MaxHeight " + selfMaxHeight + " Avg " + selfAvgHeight + " Skull " + deadBlockCount );
	}
	public void compute(){
		enemyBlocksLeft = 0;
		enemyMaxHeight = 0;
		enemyMinHeight = GRID_H;
		enemyAvgHeight = 0;
		selfBlocksLeft = 0;
		selfMinHeight = GRID_H;
		selfMaxHeight = 0;
		selfAvgHeight = 0;
		deadBlockCount = 0;
		for(int i = 0; i< GRID_W; i++) {
			enemyBlocksLeft += (12 - enemy.height[i]);
			enemyAvgHeight += enemy.height[i];
			selfBlocksLeft += (12 - me.height[i]);
			selfAvgHeight += me.height[i];
			if(enemy.height[i] > enemyMaxHeight)
				enemyMaxHeight = enemy.height[i];
			if(enemy.height[i] < enemyMinHeight)
				enemyMinHeight = enemy.height[i];
			if(me.height[i] > selfMaxHeight)
				selfMaxHeight = me.height[i];
			if(me.height[i] < selfMaxHeight)
				selfMinHeight = me.height[i];
			deadBlockCount += me.colorCount[i][BLOCK];
		}
		enemyAvgHeight = enemyAvgHeight/GRID_W;
		selfAvgHeight = selfAvgHeight/GRID_W;
		printStat();
		eai.run(enemy);
		ai.run(me);
	}
	private void printGrid(int[][] g){
		System.err.println("------");
	    for(int i = 0; i < GRID_H; i++){
	    	for(int j=0; j < GRID_W; j++){
	    		System.err.print(g[GRID_H-1-i][j] == EMPTY ? "." : "" + g[GRID_H-1-i][j] );
	    	}
	    	System.err.println();
	    }
	    System.err.println("------");
	}
	public String getResult() {
		return res + " " + rotation;
	}
	class Combo {
    	int chain;
    	int count;
        int score;
    	int height;
    	int blocks;
    	int comboValue;
    	int deadBlocks;
    }
    class Score {
    	int score;
    	int maxScore;
    	int maxComboValue;
    	int height = GRID_H;
    	int maxBlocks;
    	int nuisance;
    	int totalFactor;
    	int deadBlocks;
    }
    class EnemyAI extends AI {
    	void run(Grid g){
    		depth = 3;
    		for(int i = 0; i < depth; i++){
    			levelScore[i] = 0;
    		}
    		computeScore(g, 0, 0);
    	}
    	void analyse(Move[] list) {
    		int elimit = (12-selfAvgHeight)*6*35;
    		System.err.println("Enemy attack critera : " + elimit);
    		boolean possibleAttack = false;
    		for(int i = 0; i < depth; i++){
    			System.err.print(levelScore[i] + " ");
    			if(!possibleAttack && levelScore[i] > elimit) {
    				elimit = levelScore[i];
    				possibleAttack = true;
	    			enemyAttack = i;
	    		}
    		}
    		System.err.println();
    		if(possibleAttack) {
    			System.err.println("Enemy possible attack at : " + enemyAttack);
	    		aggressive = true;
	    	}
    	}
    }
	class DefaultAI extends AI {
		double nuisance = 0;
		Move result;
		void run(Grid g){
			debug = "";
 			for(int i = 0; i < depth; i++){
    			levelScore[i] = 0;
    			maxima[i] = null;
    		}
			computeScore(g,0,nuisance);
            
			res = result.x;
			rotation = result.r;
			nuisance += (double)result.curScore/70.0;
	        System.err.println("Nuisance : " + nuisance);
	        nuisance = (nuisance)%6;
		}
		void analyse(Move[] list) {
			for(int i = 0; i < depth; i++){
    			System.err.print(levelScore[i] + " ");
    		}
			System.err.println();
			for(int i = 0; i < list.length; i++){
    			list[i].weight = (int)(0.075*(list[i].totalF + list[i].factor) + 0.2*list[i].blocks + 0.1*list[i].surface);
    			if(list[i].curScore > 0) {
    				list[i].weight += 20*list[i].deadBlocks;
    			}
    		}
    		sort(list);
    		/*for(int i = 0; i < list.length; i++){
    			list[i].detail();
    		}*/
    		/*for(int i = 0; i < maxima.length; i++) {
		    	if(maxima[i] != null) {
		    	    System.err.print("Max level " + i + " : ");
		    	    maxima[i].detail();
		    	}
		    }*/
    		int enemyKillScore = (12 - enemyAvgHeight)*6*70;
    		System.err.println("Enemy kill score is " + enemyKillScore);
    		boolean isResult = false;
    		int desiredScore = 1000;
    		int level = -1;
    		if(aggressive) {
	    		for(int i = 0; i < enemyAttack; i++) {
	    			double nc = nuisance;
	    			if(levelScore[i] > desiredScore && (nuisance + (double)levelScore[i])/70.0 > nc){
	    				nc = (nuisance + (double)levelScore[i])/70.0;
	    				if(nc/6 >= 1)
	    					level = i;
	    			}
	    		}
	    		if(level != -1) {
	    			System.err.println("We can attack at : " + level);
	    			if((levelScore[enemyAttack] > levelScore[level] && levelScore[enemyAttack] > eai.levelScore[enemyAttack] )&& ((enemyAttack - level == 1))) {
	    			    level = enemyAttack;
	    			    System.err.println("But we will attack with him");
	    			}
	    			result = maxima[level];
	    			isResult = true;
	    		} else {
	    			if(levelScore[enemyAttack] > eai.levelScore[enemyAttack] || levelScore[enemyAttack] > desiredScore) {
	    				System.err.println("We can attack together : " + enemyAttack);
	    				result = maxima[enemyAttack];
		    			isResult = true;
	    			}
	    		}
	    		aggressive = false;
	    	}
    		if(!isResult) {
    			desiredScore = enemyKillScore/2 - 50;
    			for(int i = 0; i < depth; i++){
    				if(maxima[i] != null && levelScore[i] > desiredScore) {
    					desiredScore = levelScore[i];
    					level = i;
    					break;
    				}
    			}
    			if(level != -1) {
    				System.err.println("Desired result found from maxima : " + level);
    				result = maxima[level];
    				isResult = true;
    			}
    		}
    		if(!isResult){
    			desiredScore = 1500;
    			for(int i = 0; i < depth; i++){
    			    if(list[i].curScore > desiredScore || list[i].curScore == 0 || (list[i].curScore > 0&&list[i].deadBlocks > 0)) {
    				    result = list[i];
    				    isResult = true;
    				    break;
    			    } else {
        				if(list[i].curScore >= enemyBlocksLeft*35) {
    	    		        result = list[i];
    	    		        isResult = true;
    	    		        break;
    	    	        }
        			}
    			}
    		}
    		if(!isResult) {
    			System.err.println("Choosing highest weight move");
    			result = list[0];
    		}
		}
	}
	abstract class AI{
		protected int depth = 5;
		int[] levelScore = new int[depth];
		boolean ignoreScore = false;
		Move[] maxima = new Move[6];
		Move cur;
		int rec;
		int computeCount;
		abstract void run(Grid g);
		abstract void analyse(Move[] list);
		protected Score computeScore(Grid g, int level, double nuisance){
			if(level == 0) rec = 0;
			if(level == 0) computeCount = 0;
			if(level >= depth) return new Score();
			rec++;
			int maxBlocks = 0;
			int nextFactor = 0;
			Score result = new Score();
			Block b = bList.get(level);
			Move[] list = generateMoveList(g, b.color[0], b.color[1]);
			int count = 0;
			for(int m = list.length-1; m >= 0; m--) {
				if(level != 0 && count >= depth - level && list[m].factor == 0) break;//if(level != 0 && count >= rsteps[level]) break;
				if(level == 0) cur = list[m];
				Grid clone = g.copy();
				Combo c = new Combo();
				if(!clone.placeBlock(b, list[m].x, list[m].r, c)) {
					continue;
				}
				if(list[m].needCompute) {
					computeCount++;
					clone.compute(b, list[m].x, list[m].r, c);
				}
				Score next = new Score();
				if(c.score == 0) {
					if(level == 0 || level + 1 < depth && count < depth - level){
						count++; //if(level + 1 < depth){
				        next = computeScore(clone,level+1,0);
				        if(next.score < 0) continue;
					}
				    result.totalFactor = Math.max(result.totalFactor,list[m].factor);
				    nextFactor = Math.max(nextFactor, next.totalFactor);
				} else {
					list[m].factor = 0;
				}
	            result.maxScore = Math.max(result.maxScore, Math.max(c.score, next.maxScore));
	            result.deadBlocks = Math.max(result.deadBlocks, c.deadBlocks + next.deadBlocks);
				maxBlocks = Math.max(maxBlocks, c.blocks + next.maxBlocks);
				if(level == 0){
					list[m].surface = clone.getSurfaceArea();
				    list[m].totalF = next.totalFactor/(depth-1);
				    list[m].curScore = c.score;
				    list[m].score = c.score + next.maxScore;
				    list[m].blocks = c.blocks + next.maxBlocks;
				    list[m].deadBlocks = c.deadBlocks + next.deadBlocks;
				}
				if(c.score > levelScore[level]){
					levelScore[level] = c.score;
	    			maxima[level] = cur;
				}
			}
			result.maxBlocks = maxBlocks;
			result.totalFactor += nextFactor;
			if(level == 0) {
			    ignoreScore = result.maxScore < (12 - enemyAvgHeight)*6*20 ? true : false;
				analyse(list);
			}
			return result;
		}
		protected void sort(Move[] list){
			Arrays.sort(list, new Comparator<Move>() {
				@Override
				public int compare(Move a, Move b) {
					if(!ignoreScore && a.score > b.score) {
						return -1;
					} else if(!ignoreScore && a.score < b.score) {
						return 1;
					} else {
						if(a.weight > b.weight) {
							return -1;
						} else if(a.weight < b.weight) {
							return 1;
						} else {
							if(a.curScore > b.curScore) {
							    return -1;
						    } else if(a.curScore < b.curScore){
							    return 1;
						    } else {
							    return 0;
						    }
						}
					}
				}
			});
		}
		/*int getComboValue(Grid g){
			int value = 0;
			for(int color = 1; color <= 5; color++){
				for(int x = 0; x < GRID_W; x++){
					Block b = new Block(color, BLOCK);
					value += g.copy().compute(b, x, 1).score;
				}
			}
			return value;
		}*/
		protected Move[] generateMoveList(Grid g, int c1, int c2){
			Move[] list = new Move[22];
			ColorParams[] color1 = new ColorParams[2*GRID_W];
			ColorParams[] color2 = new ColorParams[2*GRID_W];
			for(int x = 0; x < GRID_W; x++){
				if(g.height[x] < GRID_H) {
				    color1[2*x] = g.calcColorFactor(x, g.height[x], c1);
				    color2[2*x] = c1 != c2 ? g.calcColorFactor(x, g.height[x], c2) : color1[2*x];
                } else {
                	color1[2*x] = new ColorParams();
                	color2[2*x] = new ColorParams();
                }
                if(g.height[x] + 1 < GRID_H) {
				    color2[2*x + 1] = g.calcColorFactor(x, g.height[x] + 1, c2);
				    color1[2*x + 1] = c1 != c2 ? g.calcColorFactor(x, g.height[x] + 1, c1) : color2[2*x + 1];
                } else {
                	color1[2*x + 1] = new ColorParams();
                	color2[2*x + 1] = new ColorParams();
                }
			}
			int count = 0;
			int factor = 0;
			boolean compute = false;
			for(int x = 0; x < GRID_W; x++) {
				if(x != GRID_W - 1) {
					compute = color1[2*x].adj != 0 || color2[2*x + 2].adj != 0;
				    factor = color1[2*x].factor + color2[2*x + 2].factor;
				    list[count] = new Move(x , ROTATION_0, factor, count, compute);
				    count++;
				}
				compute = color1[2*x].adj != 0 || color2[2*x + 1].adj != 0;
				factor = color1[2*x].factor + color2[2*x + 1].factor;
				list[count] = new Move(x , ROTATION_1, factor, count, compute);
				count++;
				if(x != 0) {
					compute = color1[2*x].adj != 0 || color2[2*x - 2].adj != 0;
            	    factor = color1[2*x].factor + color2[2*x - 2].factor;
            	    list[count] = new Move(x , ROTATION_2, factor, count, compute);
            	    count++;
				}
				compute = color1[2*x + 1].adj != 0 || color2[2*x].adj != 0;
            	factor = color1[2*x+1].factor + color2[2*x].factor;
            	list[count] = new Move(x , ROTATION_3, factor, count, compute);
            	count++;
			}
			int[][] tieBreaker = new int[6][GRID_W];
			for(int c = 1; c <= 5; c++) {
			    for(int x = 0; x < GRID_W; x++){
				    if(g.height[x] < GRID_H) {
				    	tieBreaker[c][x] = g.calcColorFactor(x, g.height[x], c).factor;
				    }
			    }
			}
		    Arrays.sort(list, new Comparator<Move>() {
				@Override
				public int compare(Move c, Move d) {
					if(c.factor > d.factor) return 1;
					else if(c.factor < d.factor) return -1;
					else if(tieBreaker[c1][c.x] > tieBreaker[c2][c.x]) return 1;
					else if(tieBreaker[c1][c.x] < tieBreaker[c2][c.x]) return -1;
					return 0;
				}
		    	
			});
		    return list;
		}
	}
	class Move {
		int comboVal;
		int curScore;
	    int totalF;
		int blocks;
		int mi;
		int x;
		int r;
		int factor;
		int nuisance;
		int score;
		int surface;
		int weight;
		int deadBlocks;
		boolean needCompute;
		Move(int pos, int rot, int f, int count, boolean comp) { 
			x = pos; r = rot; factor = f; mi = count;
			needCompute = comp;
		}
		void print(){
			System.err.println("M : " + mi + " " + x + " " + r + " " + factor);
		}
	    void detail(){
	    	System.err.println(x + " " + r + " " + " > " + score +" > " + curScore + " > " + factor + " > " + totalF + " " + blocks + " > " + surface + " > " + deadBlocks + " " + weight);
	    }
	}
    class Grid {
    	private int[][] grid;
    	private int[] height;
    	private int[][] colorCount;
    	Grid(){
    		grid = new int[GRID_H][GRID_W];
    		height = new int[GRID_W];
    		colorCount = new int[GRID_W][6];
    	}
    	public void reset() {
			for(int i = 0; i< GRID_W; i++) {
				height[i] = 0;
				for(int j = 0; j < 6; j++) {
					colorCount[i][j] = 0;
				}
			}
		}
		private Grid(Grid g) {
    		grid = copyOf(g.grid);
    		height = copyOf(g.height);
    		colorCount = copyOf(g.colorCount);
    	}
    	public Grid copy(){
    		return new Grid(this);
    	}
    	public int getSurfaceArea(){
    		int res = 6;
    		for(int i = 0; i < GRID_W - 1; i++) {
    			res += Math.abs(height[i] - height[i+1]);
    		}
    		return res;
    	}
    	public void setRow(int i, String s){
    		for(int k = 0; k<s.length(); k++) {
    			char c = s.charAt(k);
    			grid[i][k] =  c == '.' ? EMPTY : c - '0';
    			if(grid[i][k] != EMPTY){
    				height[k]++;
   				    colorCount[k][grid[i][k]]++;
    			}
    		}
    	}
    	public ColorParams calcColorFactor(int x, int y,int color){
    		ColorParams c = new ColorParams();
    		c.p = point[x][y];
    		c.color = color;
    		c.colCount = colorCount[x][color];
    		if(x + 1 < GRID_W) {
    			c.adj += (grid[y][x+1] == color) ? 1 : 0;
    			c.diag += (y + 1 < GRID_H && grid[y+1][x+1] == color) ? 1 : 0;
    			c.diag += (y - 1 >= 0 && grid[y-1][x+1] == color) ? 1 : 0;
    			c.diag2 += (y + 2 < GRID_H && grid[y+2][x+1] == color) ? 1 : 0;
    			c.diag2 += (y - 2 >= 0 && grid[y-2][x+1] == color) ? 1 : 0;
    			c.adjCount += colorCount[x+1][c.color];
    		}
    		if(x - 1 >= 0){
    			c.adj += (grid[y][x-1] == color) ? 1 : 0;
    			c.diag += (y + 1 < GRID_H && grid[y+1][x-1] == color) ? 1 : 0;
    			c.diag += (y - 1 >= 0 && grid[y-1][x-1] == color) ? 1 : 0;
    			c.diag2 += (y + 2 < GRID_H && grid[y+2][x-1] == color) ? 1 : 0;
    			c.diag2 += (y - 2 >= 0 && grid[y-2][x-1] == color) ? 1 : 0;
    			c.adjCount += colorCount[x-1][color];
    		}
    		if(y - 1 >= 0){
    			c.adj += grid[y-1][x] == color ? 1 : 0;
    			c.diag += grid[y-1][x] == color ? 1 : 0;
    		}
    		if(y - 2 >= 0){
    			c.diag += grid[y-2][x] == color ? 1 : 0;
    		}
    		c.factor = (int)((weight[0]*c.adj + weight[1]*c.diag + weight[2]*c.diag2 + weight[3]*c.colCount + weight[4]*c.adjCount + weight[5]*HEIGHT[y] + weight[6]*WIDTH[x])*10);
		    
    		return c;
    	}
    	private boolean placeBlock(Block b, int col, int rotation, Combo c){
    		if(height[col] >= GRID_H - 1 && (rotation == ROTATION_1 || rotation == ROTATION_3)) return false;
            if(rotation == ROTATION_0 && (height[col] >= GRID_H || height[col + 1] >= GRID_H )) return false;
            if(rotation == ROTATION_2 && (height[col] >= GRID_H || height[col - 1] >= GRID_H )) return false;
    		int[][] g = grid;
    		
    		//insert point 1
    		Point p1 = point[col][height[col]];
    		g[p1.y][p1.x] = rotation != ROTATION_3 ? b.color[0] : b.color[1];
    		colorCount[p1.x][g[p1.y][p1.x]]++;
    		height[p1.x]++;
    		st.push(p1);
    		
    		//insert point 2
    		Point p2;
    		if(rotation == ROTATION_0) {
    			p2 = point[col + 1][height[col+1]];
    		} else if(rotation == ROTATION_2) {
    			p2 = point[col - 1][height[col-1]];
    		} else {
    			p2 = point[col][height[col]];
    		}
    		g[p2.y][p2.x] = rotation != ROTATION_3 ? b.color[1] : b.color[0];
    		colorCount[p2.x][g[p2.y][p2.x]]++;
    		height[p2.x]++;
    		st.push(p2);    		
    		
    		c.height = p1.y < p2.y ? p2.y : p1.y;
    		return true;
    	}
    	public Combo compute(Block b, int x, int rotation, Combo c) {
    		while(true) {
    			ArrayList<ArrayList<Point>> blockList = new ArrayList<>();
                boolean[][] visited = new boolean[GRID_H][GRID_W];
    		    while(!st.isEmpty()) {
    			    Point target = st.pop();
    			    if(visited[target.y][target.x])continue;
    			    ArrayList<Point> l = getBlock(target, visited);
    			    if(l.size() == 2 || l.size() == 3)
    			    	c.blocks += l.size()*l.size();
    			    if(l.size() >= 4)
    			    	blockList.add(l);
    			}
    		    if(blockList.size() == 0) break;
    		    destroy(blockList,c);
    		}
    		return c;
    	}
    	public ArrayList<ArrayList<Point>> getConnectedBlocks(int limit){
    		ArrayList<ArrayList<Point>> blockList = new ArrayList<>();
    		boolean[][] visited = new boolean[GRID_H][GRID_W];
    		for(int j=0; j < GRID_W; j++){
		        for(int i=0; i < height[j]; i++){
		    		Point target = point[j][i];
		    		if(visited[target.y][target.x])continue;
		    		ArrayList<Point> l = getBlock(target, visited);
		    		if(l.size() >= limit)
	    			    blockList.add(l);
		    	}
		    }
		    return blockList;
    	}
    	public void destroy(ArrayList<ArrayList<Point>> list, Combo c){
    		int groupBonus = 0;
    		int[][] g = grid;
    		int[] color = new int[6];
    		int count = 0;
    		for(ArrayList<Point> a : list) {
    			groupBonus += a.size() >= 11 ? GROUP_B[11] : GROUP_B[a.size()];
    			for(Point pt : a){
    				color[g[pt.y][pt.x]] = 1;
    				colorCount[pt.x][g[pt.y][pt.x]]--;
    				height[pt.x]--;
    				g[pt.y][pt.x] = EMPTY;
    				count++;
    				if(pt.y + 1 < GRID_H && g[pt.y + 1][pt.x] == BLOCK ) {
    					colorCount[pt.x][BLOCK]--;
    	    			height[pt.x]--;
    	    			g[pt.y + 1][pt.x] = EMPTY;
    	    			c.deadBlocks++;
    	    		}
    	    		if(pt.y - 1 >= 0 && g[pt.y - 1][pt.x] == BLOCK) {
    	    			colorCount[pt.x][BLOCK]--;
    	    			height[pt.x]--;
    	    			g[pt.y - 1][pt.x] = EMPTY;
    	    			c.deadBlocks++;
    	    		}
    	    		if(pt.x + 1 < GRID_W && g[pt.y][pt.x+1] == BLOCK) {
    	    			colorCount[pt.x+1][BLOCK]--;
    	    			height[pt.x+1]--;
    	    			g[pt.y][pt.x + 1] = EMPTY;
    	    			c.deadBlocks++;
    	    		}
    	    		if(pt.x - 1 >= 0 && g[pt.y][pt.x-1] == BLOCK) {
    	    			colorCount[pt.x-1][BLOCK]--;
    	    			height[pt.x-1]--;
    	    			g[pt.y][pt.x - 1] = EMPTY;
    	    			c.deadBlocks++;
    	    		}
    			}
    		}
    		
    		for(int x = 0; x < GRID_W; x++) {
    			int bottom = 0;
    			for(int y = 0; y < GRID_H; y++){
    				if(g[y][x] != EMPTY){
    				    int temp = g[y][x];
    				    g[y][x] = EMPTY;
    					g[bottom][x] = temp;
    					st.push(point[x][bottom]);
    					bottom++;
    				}
    			}
    		}
    		
    		int colorBonus=0;
    		for(int i = 1; i < 6; i++){
    			if(color[i] == 1) colorBonus++;
    		}
    		
    		colorBonus = COLOR_B[colorBonus];
    		int bonusSum = colorBonus + groupBonus + c.chain;
    		bonusSum = Math.max(1, Math.min(bonusSum, 999));
    		c.count += count;
    		c.score += (10*count)*(bonusSum);
    		if(c.chain == 0) 
    			c.chain = 8;
    		else
		        c.chain = 2*c.chain;
    	}
    	//Get block formed at point p
    	ArrayList<Point> getBlock(Point p, boolean[][] visited) {
    		ArrayList<Point> result = new ArrayList<>();
    		if(p.y >= GRID_H || p.x >= GRID_W) return result;
    		int[][] g = grid;
    	    if(g[p.y][p.x] == EMPTY || g[p.y][p.x] == BLOCK) return result;
    	    
    	    int color = g[p.y][p.x];
    	    int index = 0;
    		result.add(p);
    		visited[p.y][p.x] = true;
    	    while(index != result.size()) {
    	    	Point pn = result.get(index);
    	    	
        	    if(pn.y + 1 < GRID_H && g[pn.y + 1][pn.x] == color && !visited[pn.y+1][pn.x]) {
        	        visited[pn.y+1][pn.x] = true;
        			result.add(point[pn.x][pn.y + 1]);
        		}
        	    if(pn.y - 1 >= 0 && g[pn.y - 1][pn.x] == color && !visited[pn.y-1][pn.x]) {
        	    	visited[pn.y-1][pn.x] = true;
        	    	result.add(point[pn.x][pn.y-1]);
        		}
           		if(pn.x + 1 < GRID_W && g[pn.y][pn.x+1] == color && !visited[pn.y][pn.x+1]) {
        			visited[pn.y][pn.x+1] = true;
        			result.add(point[pn.x+1][pn.y]);
        		}
        		if(pn.x - 1 >= 0 && g[pn.y][pn.x-1] == color && !visited[pn.y][pn.x-1]) {
        			visited[pn.y][pn.x-1] = true;
        			result.add(point[pn.x-1][pn.y]);
        		}
        		index++;
    	    }
            return result;
    	}
    	private int[][] copyOf(int[][] g){
    		int[][] res = new int[g.length][g[0].length];
		    for(int k = 0; k < g.length; k++) {
			    for(int y = 0; y < g[k].length; y++){
			    	res[k][y] = g[k][y];
			    }
		    }
		    return res;
    	}
    	private int[] copyOf(int[] g){
    		int[] res = new int[g.length];
		    for(int y = 0; y < g.length; y++){
			    	res[y] = g[y];
			}
		    return res;
    	}
    }
    class ColorParams { 
    	Point p;
    	int color;
    	int adj;
    	int diag;
    	int diag2;
    	int colCount;
    	int adjCount;
    	int factor;
    }
    class Point {
    	int x; int y;
    	Point(int _x, int _y){
    		x = _x; y = _y;
    	}
    }
    class Stack {
    	Point[] st;
    	int top;
    	int size;
    	int N;
    	Stack(int s){
    		st = new Point[s];
    		N = s;
    	}
    	void push(Point p) {
    		if(size == N) return;
    		st[top++] = p;
    		size++;
    	}
    	Point pop(){
    		if(!isEmpty()){
    			Point p = st[--top];
    			size--;
    			return p;
    		}
    		return null;
    	}
    	boolean isEmpty(){
    		return size == 0;
    	}
    }
    static class BlockList {
    	int head;
    	int size;
    	Block[] list = new Block[MAX_QUEUE];
    	void enqueue(Block b) {
    		list[(head + size)%MAX_QUEUE] = b;
    		size++;
    	}
    	Block get(int i) {
    		return list[(head + i)%MAX_QUEUE];
    	}
    	Block dequeue() {
    		Block res = list[head];
    		size--;
    		head = (head+1)%MAX_QUEUE;
    		return res;
    	}
    }
    class Block {
    	int[] color = new int[2];
    	Block(int c1, int c2) {
    		color[0] = c1;
    		color[1] = c2;
    	}
    }
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        BlockList list = new BlockList();
        Player p = new Player(list);
        boolean first = true;
        // game loop
        while (true) {
            for (int i = 0; i < 8; i++) {
            	int colorA = in.nextInt();
                int colorB = in.nextInt();
            	if(first || i == 7) {
            		Block b = p.new Block(colorA,colorB);
            		list.enqueue(b);
            	}
            }
            for (int i = 0; i < 12; i++) {
                String row = in.next();
                p.me.setRow(11 - i, row);
            }
            for (int i = 0; i < 12; i++) {
                String row = in.next();
                p.enemy.setRow(11 - i,row);
            }
            /*****************/
            if(!first)
                p.compute();
            /*****************/
            list.dequeue();
            p.reset();
            if(first) {
            	System.out.println("3 2");
            	first = false;
            } else {
                System.out.println(p.getResult());
            }
        }
    }
}


