/**
 * @author Harry Tran
 * @author Connie Huynh
 * Models a node called cell
 */
import java.util.ArrayList;

public class Cell {
	
	private int row; // the row # of the cell
	private int col; // the column # of the cell
	private String color; // the color of the cell for BFS and DFS
	private ArrayList<Cell> edges; // list of cells that are connected to this cell
	private Cell parent; // parent of the cell
	
	/**
	 * ctor for Cell
	 * @param row is the row #
	 * @param col is the column #
	 * will also initialize the list of edges and the color as "w"
	 */
	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
		edges = new ArrayList<Cell>();
		color = "w";
	}
	
	//getter for parent
	public Cell getParent() { return parent; }
	//setter for parent
	public void setParent(Cell c) { parent = c; }
	//getter for row
	public int getRow() { return row; }
	//getter for col
	public int getCol() { return col; }
	//getter for color
	public String getColor() { return color; }
	//setter for color
	public void setColor(String color) { this.color = color; }
	//getter for list of edges
	public ArrayList<Cell> getEdges() { return edges; }
	//adds an edge to the list of edges
	public void addEdge(Cell c) { edges.add(c); }
		
}
