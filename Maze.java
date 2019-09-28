import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

/**
 * @author Harry Tran
 * @author Connie Huynh
 * Models a maze and solves the maze
 * This code uses 2 2d arrays to create a single maze display
 */
public class Maze {

	private static int[][] grid; // the inner grid, this array is used to create the format of the maze
	private String[][] maze; // the outer grid, this is array is the maze that is seen with ASCII
	private ArrayList<Cell> cells; // list of cells occupying the maze
	private Random rng; // random number generator
	private int order; // the "time stamp" of when each cell is visited (0 to 9)
	private boolean completed; // tells DFS when the maze has been completed due to recursive nature of DFS
	
	/**
	 * ctor for Maze
	 * @param n will set the size of the maze to be n*n
	 */
	public Maze(int n) {
		rng = new java.util.Random(0); // initialize random number generator with seed 0
		grid = new int[n][n];
		// creates the inner grid to be n*n size
		for(int i = 0; i < grid.length; i++)
			for(int j = 0; j < grid[0].length; j++)
				grid[i][j] = 1; // all 1's in the inner grid are cells with all walls intact
	}
	
	/**
	 * creates the outer ASCII grid based on the inner grid
	 * @return the outer ASCII grid
	 */
	public String[][] createFrame() {
		// creates a new 2d Array that will hold the ASCII maze
		// each "cell" in the inner grid will be translated onto the ASCII maze
		// as: row # * 2 + 1 and column # * 2 + 1
		int width = grid.length * 2 + 1;
		int height = grid.length * 2 + 1; 
		String[][] frame = new String[width][height];
		// fill the outer maze with spaces
		for(int i = 0; i < frame.length; i++) 
			for(int j = 0; j < frame[0].length; j++)
				frame[i][j] = " ";
		// fill the columns with ASCII components
		for(int a = 0; a < frame.length; a += 2) {
			for(int i = 0; i < width; i++) 
				if((i + 1) % 2 == 0)
					frame[i][a] = "|";
				else
					frame[i][a] = "+";
		}
		// fill the rows with ASCII components
		for(int a = 0; a < frame.length; a += 2) {
			for(int i = 0; i < height; i++)
				if((i + 1) % 2 == 0)
					frame[a][i] = "-";
				else
					frame[a][i] = "+";
		}
		// create an entrance at top left
		frame[0][1] = " ";
		// create an exit at bottom right
		frame[frame.length - 1][frame.length - 2] = " ";
		return frame;
	}
	
	/**
	 * knocks down walls on the ASCII maze using the inner grid and a non recursive DFS
	 * follows the algorithm given in the prompt
	 * @param frame the ASCII maze
	 */
	public void createMaze(String[][] frame) {
		cells = new ArrayList<Cell>(); // Initialize cells
		Stack<Cell> cellStack = new Stack<Cell>(); // create a stack of cells
		int totalCells = grid.length * grid.length; // total amount of cells in the maze
		Cell currentCell = new Cell(0, 0); // start at 0,0 (top left)
		Cell connectCell = currentCell; // initialize edge
		cells.add(currentCell); // add current cell to list of cells
		grid[currentCell.getRow()][currentCell.getCol()] = 0; // 0's in inner grid represent cells with walls knocked down
		int visitedCells = 1;
		while (visitedCells < totalCells) {
			Cell cellOnMaze = new Cell((currentCell.getRow()*2)+1, (currentCell.getCol()*2)+1); // translating inner grid onto ASCII maze
			ArrayList<Cell> wallsIntact = findCellsWithWalls(currentCell); // find neighbors will all walls intact
			if(wallsIntact.size() != 0){
				connectCell = wallsIntact.get(rng.nextInt(wallsIntact.size())); // randomly select a neighbor 
				currentCell.addEdge(connectCell); // selected neighbor is now an edge
				modifyWall(frame, connectCell, currentCell, cellOnMaze, " "); // knock down the wall on the ASCII maze
				grid[connectCell.getRow()][connectCell.getCol()] = 0; // mark that a wall has been knocked down
				cellStack.push(currentCell); // push into stack
				currentCell = connectCell; // move to selected neighbor and
				visitedCells++;
				cells.add(currentCell); // update list of cells occupying the maze
			}
			else {
				currentCell = cellStack.pop(); // backtracking
			}
		}
		maze = frame; // ASCII maze has been fully completed
	}
	
	/**
	 * modify the wall between the a cell and its neighbor 
	 * @param frame ASCII maze
	 * @param connectCell neighboring cell
	 * @param currentCell current cell
	 * @param cellOnMaze cell translated on the ASCII maze
	 * @param mod what this code needs to change on the ASCII maze
	 */
	public static void modifyWall(String[][] frame, Cell connectCell, Cell currentCell, Cell cellOnMaze, String mod) {
		int cDir = connectCell.getCol() - currentCell.getCol(); // translating the neighbor's position
		int rDir = connectCell.getRow() - currentCell.getRow(); // translating the neighbor's position
		frame[cellOnMaze.getRow() + rDir][cellOnMaze.getCol() + cDir] = mod; // modify the wall
	}
	
	/**
	 * Finds all neighboring cells to a cell with all walls intact using case by case analysis
	 * in commentation, "x" = the cell in focus, "*" = potential neighbors, "#" = cells not checked in the case 
	 * @param c the current cell
	 * @return a list of neighbors of cell c with all walls intact
	 */
	public static ArrayList<Cell> findCellsWithWalls(Cell c) {
		int row = c.getRow();
		int col = c.getCol();
		ArrayList<Cell> wallsIntact = new ArrayList<Cell>(); // list of neighbors
		// #*#  case where current cell is in the middle
		// *x* 
		// #*#
		if(row != 0 && row != grid.length - 1 && col != 0 && col != grid.length -1) {
			if(grid[row + 1][col] == 1) 
				wallsIntact.add(new Cell(row + 1, col));
			if(grid[row][col + 1] == 1) 
				wallsIntact.add(new Cell(row, col + 1));
			if(grid[row - 1][col] == 1) 
				wallsIntact.add(new Cell(row - 1, col));
			if(grid[row][col - 1] == 1) 
				wallsIntact.add(new Cell(row, col - 1));
		}
		//  *x* case where current cell is on the top or bottom rows
		//  ### 
		//  *x*
		else if ((row == 0 || row == grid.length - 1) && col != 0 && col != grid.length - 1) {
			if(row == 0)
				if(grid[row + 1][col] == 1) 
					wallsIntact.add(new Cell(row + 1, col));
				else if(row == grid.length - 1)
					if(grid[row - 1][col] == 1) 
						wallsIntact.add(new Cell(row - 1, col));
			if(grid[row][col + 1] == 1) 
				wallsIntact.add(new Cell(row, col + 1));
			if(grid[row][col - 1] == 1) 
				wallsIntact.add(new Cell(row, col - 1));
		}
		//  *#*  case where current cell is on the left or rightmost columns
		//  x#x
		//  *#*
		else if ((col == 0 || col == grid.length - 1) &&  row != 0 && row != grid.length - 1) {
			if(col == 0)
				if(grid[row][col + 1] == 1) 
					wallsIntact.add(new Cell(row, col + 1));
			else if(col == grid.length - 1) 
				if(grid[row][col - 1] == 1) 
					wallsIntact.add(new Cell(row, col - 1));
			if(grid[row + 1][col] == 1) 
				wallsIntact.add(new Cell(row + 1, col));
			if(grid[row - 1][col] == 1) 
				wallsIntact.add(new Cell(row - 1, col));
		}
		// case where current cell is at the top left
		else if(row == 0 && col == 0) {
			if(grid[row + 1][col] == 1) 
				wallsIntact.add(new Cell(row + 1, col));
			if(grid[row][col + 1] == 1) 
				wallsIntact.add(new Cell(row, col + 1));
		}
		// case where current cell is at the bottom left
		else if(row == grid.length - 1 && col == 0){
			if(grid[row - 1][col] == 1) 
				wallsIntact.add(new Cell(row - 1, col));
			if(grid[row][col + 1] == 1) 
				wallsIntact.add(new Cell(row, col + 1));
		}
		// case where current cell is at the bottom right
		else if(row == grid.length - 1 && col == grid.length - 1) {
			if(grid[row - 1][col] == 1) 
				wallsIntact.add(new Cell(row - 1, col));
			if(grid[row][col - 1] == 1) 
				wallsIntact.add(new Cell(row, col - 1));
		}	
		// case where current cell is at the top right
		else if(row == 0 && col == grid.length - 1) {
			if(grid[row][col - 1] == 1) 
				wallsIntact.add(new Cell(row, col - 1));
			if(grid[row + 1][col] == 1) 
				wallsIntact.add(new Cell(row + 1, col));
		}
		return wallsIntact;
	}
	
	/**
	 * fills the solved maze with "#" representing the shortest path from entrance to exit
	 */
	public void fill() {
		Cell proxy = new Cell(0, 0);
		// finds the cell at the exit
		for(Cell c : cells) 
			if(c.getRow() == grid.length - 1 && c.getCol() == grid.length - 1)
				proxy = c;
		// fills the path backwards from the exit to the entrance using the cell's parent
		while(proxy.getParent() != null) {
			Cell cellOnMaze = new Cell((proxy.getRow()*2)+1, (proxy.getCol()*2)+1);
			maze[cellOnMaze.getRow()][cellOnMaze.getCol()] = "#";
			modifyWall(maze, proxy.getParent(), proxy, cellOnMaze, "#");
			proxy = proxy.getParent();
		}
		maze[1][1] = "#"; //fill the entrance with "#"
	}
	
	/**
	 * cleans the maze, wiping all numbers and "#" from the maze
	 */
	public void cleanMaze() {
		for(int i = 0; i < maze.length; i++)
			for(int j = 0; j < maze.length; j++)
				if(maze[i][j].equals("#") || maze[i][j].equals("0") || maze[i][j].equals("1") || maze[i][j].equals("2") || 
				   maze[i][j].equals("3") || maze[i][j].equals("4") || maze[i][j].equals("5") || maze[i][j].equals("6") ||
				   maze[i][j].equals("7") || maze[i][j].equals("8") || maze[i][j].equals("9"))
					maze[i][j] = " ";
	}
	
	/**
	 * BFS implementation
	 */
	public void BFS() {
		order = 0; //Initialize order
		maze[1][1] = "0"; // start of the maze
		LinkedList<Cell> q = new LinkedList<Cell>(); // Initialize the queue 
		q.add(cells.get(0)); // Enqueue the starting cell
		// keep going until the queue is empty
		while(!q.isEmpty()) {
			Cell proxy = q.getFirst(); // get the head of the queue
			q.removeFirst(); // dequeue
			if(proxy.getRow() == grid.length - 1 && proxy.getCol() == grid.length - 1) // if the current cell is at the exit, stop the method
				break;
			// explore neighbors 
			for(Cell edge : proxy.getEdges()) {
				if(edge.getColor().equals("w")) {
					edge.setColor("g"); // mark as visited
					edge.setParent(proxy); // set the parent
					q.add(edge); // enqueue the neighbor
					maze[edge.getRow()*2 +1][edge.getCol()*2 +1] = String.valueOf((order + 1)%10); // mark order on ASCII maze
					order++; // increment order
				}
			}
			proxy.setColor("b"); // mark as explored
		}
		// reset all cells to be unexplored
		for(Cell c: cells)
			c.setColor("w");
	}
	
	/**
	 * DFS implementation
	 */
	public void DFS() {
		order = 0; // initalize order
		Cell start = cells.get(0); // get starting cell
		completed = false; // initalize completed
		maze[1][1] = "0"; // mark starting cell on ASCII maze
		visitDFS(start); // recursive method call
		// reset exploration status of all cells
		for(Cell c: cells) 
			c.setColor("w");
	}
	
	/**
	 * recursive DFS implementation
	 * @param c the cell
	 */
	public void visitDFS(Cell c) {
		c.setColor("g"); // set as visited
		// check if the maze has reached the end
		if(c.getRow() == grid.length - 1 && c.getCol() == grid.length - 1) {
			completed = true;
		}
		for(Cell edge : c.getEdges()) {
			if (edge.getColor().equals("w")) {
				// stop if the maze has reached the end
				if(completed == true)
					break;
				edge.setParent(c); // set parent
				maze[edge.getRow()*2 +1][edge.getCol()*2 +1] = String.valueOf((order + 1)%10); // mark order on ASCII maze
				order++; // increment order
				visitDFS(edge); // recursive call on the edge
			}
		}
		c.setColor("b"); // mark as explored
	}
	
	/**
	 * displays the maze
	 */
	public void displayMaze() {
		for(int i = 0; i < maze.length; i++) {
			for(int j = 0; j < maze[0].length; j++)
				System.out.print(maze[i][j]);
			System.out.println();
		}
	}
	
	//getter for cells
	public ArrayList<Cell> getCells() {return cells;}
	public String[][] getMaze() {return maze;}
	
	public void copy(String file) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		// loop to copy maze
		for(int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze.length; j++){
				bw.write(maze[i][j]); 
			}
			bw.newLine();
		}
		bw.close();
	}
	
	public String getLine(int i) {
		String line = "";
		for (int j = 0; j < maze.length; j++)
			line = line + maze[i][j];
		return line;
	}
}
