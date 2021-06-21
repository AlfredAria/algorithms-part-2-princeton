import java.util.HashSet;
import java.util.Set;

/**
 * In this class, we handle 'Qu' case a bit differently from the
 * BoggleSolverBasic version by always immediately checking and then skipping
 * the 'u' character when examining an input string.
 * 
 * @author alfredaria2013
 */
public class BoggleSolverOptimization {

	TST<Integer> tst;

	public BoggleSolverOptimization(String[] dictionary) {
		tst = new TST<>();
		for (String word : dictionary) {
			if (word.replace("QU", "").contains("Q")) {
				// This word will never have a match in the board because
				// all board slots have "QU" grids but no "Q" grids.
				continue;
			}
			tst.put(stripU(word), getScore(word));
		}
	}

	// Returns the set of all valid words in the given Boggle board, as an Iterable.
	public Iterable<String> getAllValidWords(BoggleBoard board) {
		boolean[][] visited = new boolean[board.rows()][board.cols()];
		Set<String> results = new HashSet<>();
		TST.BacktrackTraverser<Integer> bt = tst.getTraverser();
		for (int y = 0; y < board.rows(); y++) {
			for (int x = 0; x < board.cols(); x++) {
				if (!bt.hasDown(board.getLetter(y, x))) {
					continue;
				}
				visited[y][x] = true;
				bt.down(board.getLetter(y, x));
				dfs(y, x, visited, results, board, bt);
				bt.up();
				visited[y][x] = false;
			}
		}

		return results;
	}

	private String stripU(String s) {
		return s.replaceAll("QU", "Q");
	}

	private String appendU(String s) {
		return s.replaceAll("Q", "QU");
	}

	private final int[][] directions = { { -1, 0 }, { -1, -1 }, { 0, -1 }, { 1, -1 }, { 1, 0 }, { 1, 1 }, { 0, 1 },
			{ -1, 1 } };

	private int[] move(int y, int x, int[] del) {
		return new int[] { y + del[0], x + del[1] };
	}

	private boolean inBound(int[] coord, BoggleBoard board) {
		int y = coord[0];
		int x = coord[1];
		return y >= 0 && y < board.rows() && x >= 0 && x < board.cols();
	}

	private void dfs(int y, int x, boolean[][] visited, Set<String> results, BoggleBoard board,
			TST.BacktrackTraverser<Integer> bt) {
		// If a word is found on this TST node, append it to results.
		if (bt.hasValue()) {
			String word = appendU(bt.getWord());
			if (bt.getValue() > 0) {
				results.add(word);
			}
		}

		// Check every unused nearby grid.
		// Only continue on grid if there's some word down the TST.
		for (int[] dir : directions) {
			int[] moved = move(y, x, dir);
			if (!inBound(moved, board)) {
				continue;
			}
			if (visited[moved[0]][moved[1]]) {
				continue;
			}
			char singleLetter = board.getLetter(moved[0], moved[1]);
			if (!bt.hasDown(singleLetter)) {
				continue;
			}
			visited[moved[0]][moved[1]] = true;
			bt.down(singleLetter);
			dfs(moved[0], moved[1], visited, results, board, bt);
			bt.up();
			visited[moved[0]][moved[1]] = false;
		}
	}

	private int getScore(String word) {
		int l = word.length();
		if (l == 3 || l == 4)
			return 1;
		else if (l == 5)
			return 2;
		else if (l == 6)
			return 3;
		else if (l == 7)
			return 5;
		else if (l >= 8)
			return 11;
		else
			return 0;
	}

	// Returns the score of the given word if it is in the dictionary, zero
	// otherwise.
	// (You can assume the word contains only the uppercase letters A through Z.)
	public int scoreOf(String word) {
		String noU = stripU(word);
		return tst.contains(noU) ? tst.get(noU) : 0;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
