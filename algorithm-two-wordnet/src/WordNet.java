import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;

public class WordNet {

	private final static String path = "resources/wordnet/";

	private int V; // Number of words (vertices)
	private List<Bag<Integer>> adj; // adj[v] = adjacency list for vertex V, synset V's hypernyms.
	private List<String> synset; // synset.get(v) = the list of synset words with index v.
	private Map<String, Integer> nouns; // Maps a noun to its containing synset

	/**
	 * Constructs the WordNet from a file.
	 * 
	 * @param synsets   Name for file containing list of synsets.
	 * @param hypernyms Name for file containing list of relationships.
	 */
	public WordNet(String synsets, String hypernyms) {
		this(new In(synsets), new In(hypernyms));
	}

	/**
	 * Constructs the WordNet from a file.
	 * 
	 * @param synsets   Input stream for file containing list of synsets.
	 * @param hypernyms Input stream for file containing list of relationships.
	 */
	private WordNet(In synsets, In hypernyms) {
		adj = new ArrayList<>();
		synset = new ArrayList<>();
		nouns = new HashMap<>();

		// Initialize vertexes
		String line = null;
		while (true) {
			line = synsets.readLine();
			if (line == null) { break; }
			String[] tokens = line.split(",");
			synset.add(tokens[1]);
			adj.add(new Bag<>());
			V++;
			Integer vertex = Integer.parseInt(tokens[0]);
			Arrays.stream(tokens[1].split(" ")).forEach(word -> {
				nouns.put(word, vertex);
			});
		}

		// Fill edges
		while (true) {
			line = hypernyms.readLine();
			if (line == null) { break; }
			String[] tokens = line.split(",");
			Bag<Integer> hypernymIndexes = adj.get(Integer.parseInt(tokens[0]));
			for (int i = 1; i < tokens.length; i++) {
				Integer to = Integer.parseInt(tokens[i]);
				hypernymIndexes.add(to);
			}
		}

		// Graph should contain only 1 root.
		boolean rootFound = false;
		for (Bag<Integer> adjcents : adj) {
			if (adjcents.isEmpty()) {
				if (!rootFound) {
					rootFound = true;
				} else {
					throw new IllegalArgumentException("Input not corresponding to a single-rooted DAG.");
				}
			}
		}

		// Graph should not contain a cycle.
		if (checkAcyclic()) {
			throw new IllegalArgumentException("Input directed graph contains at least a cycle.");
		}
	}

	public Iterable<String> nouns() {
		return nouns.keySet();
	}

	public boolean isNoun(String word) {
		return nouns.containsKey(word);
	}

	public int distance(String nounA, String nounB) {
		if (!isNoun(nounA) || !isNoun(nounB)) {
			throw new IllegalArgumentException("Must pass wordnet nouns to distance()");
		}
		int vertexA = nouns.get(nounA);
		int vertexB = nouns.get(nounB);

		return findLCA(vertexA, vertexB)[1];
	}

	public String sap(String nounA, String nounB) {
		if (!isNoun(nounA) || !isNoun(nounB)) {
			throw new IllegalArgumentException("Must pass wordnet nouns to sap()");
		}
		int vertexA = nouns.get(nounA);
		int vertexB = nouns.get(nounB);
		return synset.get(findLCA(vertexA, vertexB)[0]);
	}

	/**
	 * Use 2-way BFS to find the index of the shortest ancestor wordnet and the
	 * distance between the two wordnets.
	 * 
	 * @param vA vertex of the first wordnet
	 * @param vB vertex of the second wordnet
	 * @return [index of the SAP wordnet, length of the path]
	 */
	private int[] findLCA(int vA, int vB) {
		Map<Integer, Integer> mapA = shortestPaths(vA);
		Map<Integer, Integer> mapB = shortestPaths(vB);
		int minDist = Integer.MAX_VALUE;
		int minNode = -1;
		for (int i = 0; i < V; i ++) {
			if (!mapA.containsKey(i) || !mapB.containsKey(i)) {
				continue;
			}
			int dist = mapA.get(i) + mapB.get(i);
			if (dist < minDist) {
				minDist = dist;
				minNode = i;
			}
		}
		return minNode >= 0 ? new int[] { minNode , minDist } : new int[] { -1, -1 };
	}
	
	private Map<Integer, Integer> shortestPaths(int a) {
		Map<Integer, Integer> shortest = new HashMap<>();
		Queue<Integer> q = new LinkedList<Integer>();
		q.add(a);
		shortest.put(a, 0);
		while (!q.isEmpty()) {
			Integer node = q.poll();
			for (Integer next : adj.get(node)) {
				if (!shortest.containsKey(next)) { 
					q.add(next);
					shortest.put(next, shortest.get(node) + 1);
				}
			}
		}
		return shortest;
	}

	/**
	 * Returns true if the input graph contains a cycle.
	 * 
	 * @return
	 */
	private boolean checkAcyclic() {
		// Stores nodes that are already checked and that its descendent graph
		// does not link to a cycle.
		boolean[] visited = new boolean[V];
		for (int i = 0; i < V; i++) {
			if (visited[i]) {
				continue;
			}
			Stack<Integer> nodes = new Stack<>();
			if (checkAcyclicDfs(i, nodes, visited)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkAcyclicDfs(int node, Stack<Integer> nodes, boolean[] visited) {
		if (nodes.contains(node)) {
			return true;
		}
		if (visited[node]) {
			return false;
		}
		visited[node] = true;
		nodes.push(node);
		for (int adjcent : adj.get(node)) {
			if (checkAcyclicDfs(adjcent, nodes, visited)) {
				return true;
			}
		}
		nodes.pop();
		return false;
	}

	public static void main(String[] args) {
		WordNet wn = new WordNet(path + "synsets.txt", path + "hypernyms.txt");
//		new WordNet(path + "synsets3.txt", path + "hypernyms3InvalidCycle.txt");
//		new WordNet(path + "synsets3.txt", path + "hypernyms3InvalidTwoRoots.txt");
//		new WordNet(path + "synsets6.txt", path + "hypernyms6InvalidCycle.txt");
//		new WordNet(path + "synsets6.txt", path + "hypernyms6InvalidCycle+Path.txt");
//		new WordNet(path + "synsets6.txt", path + "hypernyms6InvalidTwoRoots.txt");
//		new WordNet(path + "synsets8.txt", path + "hypernyms8ModTree.txt");
//		new WordNet(path + "synsets8.txt", path + "hypernyms8ModTree.txt");
//		new WordNet(path + "synsets11.txt", path + "hypernyms11AmbiguousAncestor.txt");
//		new WordNet(path + "synsets11.txt", path + "hypernyms11ManyPathsOneAncestor.txt");
		System.out.println(wn.distance("zucchini", "courgette"));
		System.out.println(wn.distance("zucchini", "zymolysis"));
		System.out.println(wn.sap("zucchini", "zymolysis"));
		System.out.println(wn.distance("speech_sound", "phoenix"));
		System.out.println(wn.sap("speech_sound", "phoenix"));
		System.out.println(wn.distance("academic_requirement", "high"));
		System.out.println(wn.sap("academic_requirement", "high"));
		System.out.println(wn.distance("academic_requirement", "achievability"));
		System.out.println(wn.sap("academic_requirement", "achievability"));
		System.out.println(wn.distance("abduction", "abductor"));
		System.out.println(wn.sap("abduction", "abductor"));
	}

}
