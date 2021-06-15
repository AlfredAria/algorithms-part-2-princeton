import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

public class SAP {

	private final Digraph g;

	// constructor takes a digraph (not necessarily a DAG)
	public SAP(Digraph G) {
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

		return find(v, w)[1];
	}

	// a common ancestor that participates in shortest ancestral path; -1 if no such
	// path
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		if (v == null || !v.iterator().hasNext() || w == null || !w.iterator().hasNext()) {
			throw new IllegalArgumentException("Not a valid problem with empty input.");
		}

		return find(v, w)[0];
	}
	
	private int[] find(Iterable<Integer> v, Iterable<Integer> w) {
		BreadthFirstDirectedPaths vPaths = new BreadthFirstDirectedPaths(g, v);
		BreadthFirstDirectedPaths wPaths = new BreadthFirstDirectedPaths(g, w);
		int shortest = Integer.MAX_VALUE;
		int node = -1;
		for (int i = 0; i < g.V(); i ++) {
			if (vPaths.hasPathTo(i) && wPaths.hasPathTo(i)) {				
				int dist = vPaths.distTo(i) + wPaths.distTo(i);
				if (dist < shortest) {
					shortest = dist;
					node = i;
				}
			}
		}
		return new int[] { node, shortest };
	}

	private static void test(int v, int w, String test) {
		SAP sap = new SAP(new Digraph(new In("resources/wordnet/" + test)));
		System.out.println(sap.length(v,w));
//		System.out.println(sap.ancestor(v,w));
	}
	
	public static void main(String[] args) {
		
		test(34252, 29893, "digraph-wordnet.txt");
		
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
