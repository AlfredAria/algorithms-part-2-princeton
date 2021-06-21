import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
import java.util.List;

import edu.princeton.cs.algs4.In;

public class BoggleSolver {

//	Version to produce correct results but doing it in an unoptimized way (very slow).
//	private BoggleSolverBasic bs;
//    public BoggleSolver(String[] dictionary) {
//    	bs = new BoggleSolverBasic(dictionary);
//    }

	private BoggleSolverOptimization bs;

	// Version for submission.
	public BoggleSolver(String[] dictionary) {
		bs = new BoggleSolverOptimization(dictionary);
	}

	// Returns the set of all valid words in the given Boggle board, as an Iterable.
	public Iterable<String> getAllValidWords(BoggleBoard board) {
		return bs.getAllValidWords(board);
	}

	// Returns the score of the given word if it is in the dictionary, zero
	// otherwise.
	// (You can assume the word contains only the uppercase letters A through Z.)
	public int scoreOf(String word) {
		return bs.scoreOf(word);
	}

	private static String[] getDict(String file) {
		List<String> words = new ArrayList<>();
		In in = new In(file);
		String line;
		while ((line = in.readLine()) != null) {
			words.add(line);
		}
		return words.toArray(new String[0]);
	}

	private static void printScores(BoggleSolver bs, Iterable<String> validWords) {
		int total = 0;
		List<String> sorted = new ArrayList<>();
		for (String word : validWords)
			sorted.add(word);
		sorted.sort(String.CASE_INSENSITIVE_ORDER);

		for (String word : sorted) {
    		System.out.println(word);
			total += bs.scoreOf(word);
		}
		System.out.println("Total score: " + total);
	}

	public static void main(String[] args) {
//		String dic = "boggle/dictionary-16q.txt";
//		String brd = "boggle/board-16q.txt";
//		String brd = "boggle/board-antidisestablishmentarianisms.txt";
//		String brd = "boggle/board-points500.txt";
//		String brd = "boggle/board-points777.txt";
//		String brd = "boggle/board-points4527.txt";
//		String brd = "boggle/board-points4540.txt";
//		String brd = "boggle/board-points13464.txt";
//		String brd = "boggle/board-points26539.txt";
//		String brd = "boggle/board-points1.txt";
//		String brd = "boggle/board-qaimaqam.txt";
		String brd = "boggle/board-sequ-1.txt";
//		String brd = "boggle/board-random-hasbro-board.txt";
//		String brd = "boggle/board-points1000.txt";

		// ???
//		String brd = "boggle/board-points777.txt";
//		String brd = "boggle/board-points500.txt";

//		String dic = "boggle/dictionary-common.txt";
//		String dic = "boggle/dictionary-yawl.txt";
//		String dic = "boggle/board-rotavator.txt";
//		String dic = "boggle/dictionary-end-s.txt";
//		String dic = "boggle/dictionary-qu-words.txt";
		String dic = "boggle/dictionary-common.txt";

		BoggleSolver solver = new BoggleSolver(getDict(dic));
		printScores(solver, solver.getAllValidWords(new BoggleBoard(brd)));
	}

}
