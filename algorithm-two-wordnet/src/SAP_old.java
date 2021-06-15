import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

public class SAP_old {

	private final Digraph g;

	// constructor takes a digraph (not necessarily a DAG)
	public SAP_old(Digraph G) {
		g = new Digraph(G);
	}

	public int length(int v, int w) {
		return sapHelper(v, w)[1];
	}

	// a common ancestor of v and w that participates in a shortest ancestral path;
	// -1 if no such path
	public int ancestor(int v, int w) {
		return sapHelper(v, w)[0];
	}

	// length of shortest ancestral path between v and w; -1 if no such path
	private int[] sapHelper(int v, int w) {
		// Find min dist to each reachable node from v.
		Map<Integer, Integer> vR = new HashMap<>();
		LinkedList<int[]> vQ = new LinkedList<>();
		vQ.add(new int[] {v, 0});
		while (!vQ.isEmpty()) {
			int[] node = vQ.poll();
			if (!vR.containsKey(node[0])) {
				vR.put(node[0], node[1]);
			} else {
				continue;
			}
			for (int i : g.adj(node[0])) {
				vQ.add(new int[] {i, node[1] + 1});
			}
		}
		// Find min dist to each reachable node from w. Record min(sum(distV, distW))
		int minFound = Integer.MAX_VALUE;
		int minNode = -1;

		Map<Integer, Integer> wR = new HashMap<>();
		LinkedList<int[]> wQ = new LinkedList<>();
		wQ.add(new int[] {w, 0});
		while (!wQ.isEmpty()) {
			int[] node = wQ.poll();
			if (vR.containsKey(node[0])) {
				int dist = vR.get(node[0]) + node[1];
				if (minFound > dist) {
					minFound = dist;
					minNode = node[0];
				}
			}
			if (!wR.containsKey(node[0])) {
				wR.put(node[0], node[1]);
			} else {
				continue;
			}
			for (int i : g.adj(node[0])) {
				wQ.add(new int[] {i, node[1] + 1});
			}			
		}
		
		return minNode >= 0 
				? new int[] {minNode, minFound}
				: new int[] {-1, -1};
	}
	
	

	// length of shortest ancestral path between any vertex in v and any vertex in
	// w; -1 if no such path
	public int length(Iterable<Integer> v, Iterable<Integer> w) {
		if (v == null || !v.iterator().hasNext() || w == null || !w.iterator().hasNext()) {
			throw new IllegalArgumentException("Not a valid problem with empty input.");
		}
		int[] cv = commonLCA(v);
		int[] cw = commonLCA(w);
		if (cv[0] < 0 || cw[0] < 0) {
			return -1;
		}
		// The common ancestor vv of cluster V, plus the dist from closest node to vv,
		// plus the common ancestor ww of cluster W, plus the dist from closest node to
		// ww.
		return length(cv[0], cw[0]) + cv[1] + cw[1];
	}

	// a common ancestor that participates in shortest ancestral path; -1 if no such
	// path
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		if (v == null || !v.iterator().hasNext() || w == null || !w.iterator().hasNext()) {
			throw new IllegalArgumentException("Not a valid problem with empty input.");
		}
		int[] cv = commonLCA(v);
		int[] cw = commonLCA(w);
		if (cv[0] < 0 || cw[0] < 0) {
			return -1;
		}
		return ancestor(cv[0], cw[0]);
	}

	// Find the common lowest ancestor of a collection of nodes.
	// If the nodes do not share a common ancestor, return [-1, -1].
	// Returns [node_index, shortest_dist_to_lca].
	private int[] commonLCA(Iterable<Integer> a) {
		Digraph reversedGraph = g.reverse();
		int lowestVertex = -1;
		int lowestDist = Integer.MAX_VALUE;
		Set<Integer> find = new HashSet<>();
		for (int aa : a) {
			find.add(aa);
		}
		for (int i = 0; i < g.V(); i++) {
			if (reversedGraph.indegree(i) == 0) {
				int[] results = commonLCAHelper(reversedGraph, i, find);
				if (results[2] == find.size()) {
					if (lowestDist > results[1]) {
						lowestDist = results[1];
						lowestVertex = results[0];
					}
				}
			}
		}
		return lowestVertex >= 0 ? new int[] { lowestVertex, lowestDist } : new int[] { -1, -1 };
	}

	// Returns [lowest_ans_if_count_matches, lowest_dist_if_count_matches,
	// found_children].
	private int[] commonLCAHelper(Digraph g, int root, Set<Integer> a) {
		int lowestVertex = -1;
		int lowestDist = Integer.MAX_VALUE;
		int childrenCount = 0;
		for (int i : g.adj(root)) {
			int[] results = commonLCAHelper(g, i, a);
			if (results[2] == a.size()) {
				// If the lowest common ancestor is found,
				// return its index and the dist considering the current node.
				return new int[] { results[0], results[1] + 1, results[2] };
			} else if (results[2] == 0) {
				// If the child contains no nodes to find,
				// ignore it.
				continue;
			} else {
				childrenCount += results[2];
				if (lowestDist > results[1]) {
					// Override lowestDist with the child closest to current node.
					lowestDist = results[1];
					lowestVertex = results[0];
				}
			}
		}
		if (a.contains(root)) {
			// Root is one of the nodes being looked for.
			return new int[] { root, 0, 1 + childrenCount };
		} else {
			return new int[] { lowestVertex, lowestDist + 1, childrenCount };
		}
	}

	private static void test(int v, int w, String test) {
		SAP sap = new SAP(new Digraph(new In("resources/wordnet/" + test)));
		System.out.println(sap.length(v,w));
//		System.out.println(sap.ancestor(v,w));
	}
	
	public static void main(String[] args) {
		
		test(17, 21, "digraph5.txt");
		
//		System.out.println(sap.length(3, 11));
//		System.out.println(sap.ancestor(3, 11));
//
//		System.out.println(sap.length(9, 12));
//		System.out.println(sap.ancestor(9, 12));
//
//		System.out.println(sap.length(7, 2));
//		System.out.println(sap.ancestor(7, 2));
//
//		System.out.println(sap.length(1, 6));
//		System.out.println(sap.ancestor(1, 6));
	}

}
