import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import edu.princeton.cs.algs4.TrieST;

/**
 * Correctness verified. No optimization for string comparison. No optimization
 * for pruning.
 * 
 * @author alfredaria2013
 */
public class BoggleSolverBasic {

	TrieST<Integer> tst;

	public BoggleSolverBasic(String[] dictionary) {
		tst = new TrieST<>();
		for (String word : dictionary) {
			if (word.replace("QU", "").contains("Q")) {
				// This word will never have a match in the board because
				// all board slots have "QU" grids but no "Q" grids.
				continue;
			}
			tst.put(word.toUpperCase(), getScore(word));
		}
	}

	// Returns the set of all valid words in the given Boggle board, as an Iterable.
	public Iterable<String> getAllValidWords(BoggleBoard board) {
		boolean[][] visited = new boolean[board.rows()][board.cols()];
		Set<String> results = new HashSet<>();
		Stack<Character> chars = new Stack<>();
		for (int y = 0; y < board.rows(); y++) {
			for (int x = 0; x < board.cols(); x++) {
				visited[y][x] = true;
				push(y, x, chars, board);
				dfs(y, x, visited, results, board, chars);
				pop(chars);
				visited[y][x] = false;
			}
		}

		return results;
	}

	private void push(int y, int x, Stack<Character> chars, BoggleBoard board) {
		Character letter = Character.toUpperCase(board.getLetter(y, x));
		if (letter == 'Q') {
			chars.push(letter);
			chars.push('U');
		} else {
			chars.push(letter);
		}
	}

	private void pop(Stack<Character> chars) {
		chars.pop();
		if (!chars.isEmpty() && chars.peek() == 'Q') {
			chars.pop();
		}
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
			Stack<Character> chars) {
		// TODO: By comparing each char with current trie node,
		// no need to keep constructing trees unless when appending
		// to results.

		StringBuilder sb = new StringBuilder();
		chars.forEach(c -> sb.append(c));
		String result = sb.toString();
		if (scoreOf(result) > 0) {
			results.add(result);
		}

		// TODO: If no children with this node in the trie,
		// no need to search down; just prune.

		for (int[] dir : directions) {
			int[] moved = move(y, x, dir);
			if (!inBound(moved, board)) {
				continue;
			}
			if (visited[moved[0]][moved[1]]) {
				continue;
			}
			visited[moved[0]][moved[1]] = true;
			push(moved[0], moved[1], chars, board);
			dfs(moved[0], moved[1], visited, results, board, chars);
			pop(chars);
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
		return tst.contains(word) ? tst.get(word) : 0;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
