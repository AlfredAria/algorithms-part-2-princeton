import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;

public class BaseballElimination {
	
	private Map<String, Integer> teamIndex;
	private Map<Integer, String> teamNames;
	private int[][] mutualGames;
	private int[][] winLoseRemain;
	private int totalGames;
	
	private int totalVertices;
	private int SRC;
	private int SINK;
	
	public BaseballElimination(String filename) // create a baseball division from given filename in format specified
												// below
	{
		In in = new In(filename);
		int teams = Integer.parseInt(in.readLine());
		mutualGames = new int[teams][teams];
		winLoseRemain = new int[teams][3];
		teamNames = new HashMap<>();
		teamIndex = new HashMap<>();
		String line;
		int index = 0;
		totalGames = 0;
		while ((line = in.readLine()) != null) {
			String[] toks = line.strip().split("\\s+");
			teamNames.put(index, toks[0]);
			teamIndex.put(toks[0], index);
			// Compute wins and losses.
			for (int i = 1; i < 4; i ++) {
				winLoseRemain[index][i - 1] = Integer.parseInt(toks[i]);
			}
			totalGames += winLoseRemain[index][0] + winLoseRemain[index][2];
			for (int i = 4; i < 4 + teams; i ++) {
				mutualGames[index][i - 4] = Integer.parseInt(toks[i]);
			}
			index++;
		}
		
		totalVertices = 2 +				 // Source and Sink vertexes
				teams + 			  	 // Team vertexes
				teams * teams; 			 // Game vertexes
		SRC = totalVertices - 2;
		SINK = totalVertices - 1;
	}
	
	private int gameIndex(int team1, int team2) {
		if (team1 < team2) {			
			return numberOfTeams() + team1 * numberOfTeams() + team2;
		} else {
			return numberOfTeams() + team2 * numberOfTeams() + team1;
		}
	}

	public int numberOfTeams() // number of teams
	{
		return teamIndex.size();
	}

	public Iterable<String> teams() // all teams
	{
		return teamIndex.keySet();
	}
	
	private int wins(int teamIndex) {
		return winLoseRemain[teamIndex][0];
	}

	public int wins(String team) // number of wins for given team
	{
		if (!teamIndex.containsKey(team)) {
			throw new IllegalArgumentException("No such team");
		}
		return wins(teamIndex.get(team));
	}

	public int losses(String team) // number of losses for given team
	{
		if (!teamIndex.containsKey(team)) {
			throw new IllegalArgumentException("No such team");
		}
		return winLoseRemain[teamIndex.get(team)][1];
	}
	
	private int remaining(int teamIndex) {
		return winLoseRemain[teamIndex][2];
	}

	public int remaining(String team) // number of remaining games for given team
	{
		if (!teamIndex.containsKey(team)) {
			throw new IllegalArgumentException("No such team");
		}
		return remaining(teamIndex.get(team));
	}
	
	private int against(int team1, int team2) {
		return mutualGames[team1][team2];
	}

	public int against(String team1, String team2) // number of remaining games between team1 and team2
	{
		if (!teamIndex.containsKey(team1) || !teamIndex.containsKey(team2)) {
			throw new IllegalArgumentException("No such team");
		}
		return against(teamIndex.get(team1), teamIndex.get(team2));
	}
	
	private boolean triviallyEliminated(String team) {
		int maxWins = 0;
		int teamId = teamIndex.get(team); 
		for (int i = 0; i < numberOfTeams(); i ++) {
			maxWins = Math.max(wins(i), maxWins);
		}
		return maxWins > (wins(teamId) + remaining(teamId));
	}
	
	/** 
	 * Given team name of team x,
	 * @param team
	 */
	private void completeEdges(FlowNetwork fn, String team) {
		int teamIdx = teamIndex.get(team);
		
		// Make an edge from each team to the sink.
		for (int team1 = 0; team1 < numberOfTeams(); team1 ++) {
			fn.addEdge(new FlowEdge(team1, SINK, wins(teamIdx) + remaining(teamIdx) - wins(team1)));
		}

		for (int team2 = 0; team2 < numberOfTeams(); team2 ++) {
			for (int team1 = team2 + 1; team1 < numberOfTeams(); team1 ++) {
				// Make an edge from source to game vertexes.
				fn.addEdge(new FlowEdge(SRC, gameIndex(team2, team1), against(team2, team1)));
				
				// Make an edge from every game vertex to every team,
				// with total game as capacity (in fact the capacity can be infinite).
				fn.addEdge(new FlowEdge(gameIndex(team2, team1), team1, totalGames));
				fn.addEdge(new FlowEdge(gameIndex(team2, team1), team2, totalGames));
			}
		}
	}
	
	private boolean checkElimination(FlowNetwork fn) {
		for (FlowEdge fe : fn.adj(SRC)) {
			if (fe.flow() < fe.capacity()) {
				// Some flow did not fill up capacity of an edge out from SRC.
				// == Some games were not played to obtain the desired flow values to the sink.
				// == The desirable sink condition is not meet-able.
				// == The team is mathematically eliminated.
				return true;
			}
		}
		return false;
	}
	
	private Iterable<String> trivialEliminationCerts(String team) {
		List<String> certTeams = new ArrayList<>();
		for (String other: teams()) {
			if (wins(other) > wins(team) + remaining(team)) {
				certTeams.add(other);
			}
		}
		return certTeams;
	}

	public boolean isEliminated(String team) // is given team eliminated?
	{
		if (!teamIndex.containsKey(team)) {
			throw new IllegalArgumentException("No such team");
		}
		if (triviallyEliminated(team)) { 
			return true; 
		}
		
		FlowNetwork fn = new FlowNetwork(totalVertices);
		completeEdges(fn, team);

		// Find max flow with FordFulkerson.
		new FordFulkerson(fn, SRC, SINK);
		return checkElimination(fn);
	}

	public Iterable<String> certificateOfElimination(String team) // subset R of teams that eliminates given team; null
																  // if not eliminated
	{
		if (!teamIndex.containsKey(team)) {
			throw new IllegalArgumentException("No such team");
		}
		if (triviallyEliminated(team)) {
			return trivialEliminationCerts(team);
		}
		FlowNetwork fn = new FlowNetwork(totalVertices);
		completeEdges(fn, team);
		final FordFulkerson ff = new FordFulkerson(fn, SRC, SINK);
		if (!checkElimination(fn)) {
			return null;
		}
		return IntStream.range(0, numberOfTeams())
			.filter(ff::inCut)
			.mapToObj(teamNames::get)
			.collect(Collectors.toList());		
	}
	
	public static void main (String[] args) {
		BaseballElimination be = new BaseballElimination("baseball/teams32.txt");
		System.out.println(be.certificateOfElimination("Team25"));
	}

}
