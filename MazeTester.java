import java.io.IOException;
import java.util.Scanner;


public class MazeTester {

	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(System.in);
		System.out.println("An n*n sized maze will be created, please enter an integer n: ");
		int n = in.nextInt();
		Maze m = new Maze(n);
		in.close();
		System.out.println();
		m.createMaze(m.createFrame());
		System.out.println("Maze:");
		m.displayMaze();
		m.copy("Maze.txt");
		System.out.println();
		System.out.println("BFS:");
		m.BFS();
		m.displayMaze();
		m.copy("MazeBFS.txt");
		m.cleanMaze();
		System.out.println();
		m.fill();
		m.displayMaze();
		m.cleanMaze();
		System.out.println();
		System.out.println("DFS:");
		m.DFS();
		m.displayMaze();
		m.copy("MazeDFS.txt");
		m.cleanMaze();
		System.out.println();
		m.fill();
		m.displayMaze();
		m.copy("MazeFill.txt");
		
		System.out.println("Program completed. For JUnit test to work properly, MazeTester must be ran and completed before JUnit test.");
	}
	
}
