import java.util.ArrayList;
import java.util.HashMap;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
	
	private int [] w, l, r;
	private int [][] g;
	private HashMap<String,Integer> teams;
	private String [] teamNames;
//	private int pairs;
	
	public BaseballElimination(String filename) {
		readFile(filename);
	}                   // create a baseball division from given filename in format specified below
	
	private class vecPair {
		private int one, two;
		
		public vecPair(int one, int two){
			this.one = one;
			this.two = two;
		}
		
		public int getOne() {
			return this.one;
		}
		
		public int getTwo() {
			return this.two;
		}
		
	}
	
	private void readFile(String filename) {
		In in = new In(filename);
		int numTeams = Integer.valueOf(in.readLine());
		
		w = new int [numTeams];
		l = new int [numTeams];
		r = new int [numTeams];
		g = new int [numTeams][numTeams];
		teamNames = new String[numTeams];
		teams = new HashMap<String,Integer>();
		
		for(int i = 0 ; i < numTeams; i++) {
			String line = in.readLine();
			line = line.trim();
			String[] split = line.split("\\s+");
			teams.put(split[0], i);
			teamNames[i] = split[0];
			w[i] = Integer.valueOf(split[1]);
			l[i] = Integer.valueOf(split[2]);
			r[i] = Integer.valueOf(split[3]);
			for(int j = 0, count = 4; j < numTeams; j++, count++) {
				g[i][j] = Integer.valueOf(split[count]);
			}
		}
	}
	
	public int numberOfTeams()    {
		return w.length;		
	}                    // number of teams
	
	public Iterable<String> teams()     {
		return teams.keySet();
	}                           // all teams
	
	private void validTeam(String team) {
		if(!teams.containsKey(team)) {
			throw new java.lang.IllegalArgumentException("Invalid Team: " + team);
		}
	}
	
	private int getTeamIndex(String team) {
		return teams.get(team);
	}
	
	public int wins(String team) {
		validTeam(team);
		return w[getTeamIndex(team)];
	}                     // number of wins for given team
	
	public int losses(String team)      {
		validTeam(team);
		return l[getTeamIndex(team)];
	}              // number of losses for given team
	
	public int remaining(String team)   {
		validTeam(team);
		return r[getTeamIndex(team)];
	}               // number of remaining games for given team
	
	
	public int against(String team1, String team2) {
		validTeam(team1);
		validTeam(team2);
		return g[getTeamIndex(team1)][getTeamIndex(team2)];
		
	}   // number of remaining games between team1 and team2
	
	private FlowNetwork createNetwork(int x) {
		int numTeams = this.numberOfTeams();
		boolean [][] marked = new boolean[numTeams][numTeams];
		ArrayList<vecPair> pairs = new ArrayList<vecPair>();
		HashMap<Integer,Integer> mappings = new HashMap<Integer, Integer>();
		int count = 1;
		for(int i = 0; i < numTeams; i++) {
			if(i != x) {
				mappings.put(i, count);
				count++;
			}
			for(int j = 0; j < numTeams; j++) {
				if(i == x || j == x || i == j) continue;
				if(!marked[i][j]) {
					marked[i][j] = true;
					marked[j][i] = true;
					pairs.add(new vecPair(i, j));
				}
			}
		}
		
		FlowNetwork fn = new FlowNetwork(numTeams + 1 + pairs.size());

		for(int i = 1; i <= pairs.size(); i++) {
			vecPair pair = pairs.get(i - 1);
			FlowEdge e = new FlowEdge(0,i, g[pair.getOne()][pair.getTwo()]);
			fn.addEdge(e);
			e = new FlowEdge(i, pairs.size() + mappings.get(pair.getOne()), Double.POSITIVE_INFINITY);
			fn.addEdge(e);
			e = new FlowEdge(i, pairs.size() + mappings.get(pair.getTwo()), Double.POSITIVE_INFINITY);
			fn.addEdge(e);
		}
		
		count = 1;

		for(int i : mappings.keySet()) {
			int weight = w[x] + r[x] - w[i];
			FlowEdge e = new FlowEdge(pairs.size() + (count++), numTeams + pairs.size(), weight);
			fn.addEdge(e);
		}
//		System.out.println(fn);
		return fn;
	}
	
	
	public boolean isEliminated(String team)  {
		Iterable<String> teams = certificateOfElimination(team);
		return teams != null;		
	}            // is given team eliminated?
	
	public Iterable<String> certificateOfElimination(String team){
		validTeam(team);
		int x = getTeamIndex(team);
		ArrayList<String> teams = new ArrayList<String>();
		
		//Trivial Elimination
		for(int i = 0 ; i < this.numberOfTeams(); i++) {
			if(i != x && w[x] + r[x] < w[i]) {
				teams.add(teamNames[i]);
			}
		}
		if(teams.size() > 0) return teams;
		
		//Non Trivial Elimination
		
		FlowNetwork fn = createNetwork(x);
		FordFulkerson ff = new FordFulkerson(fn,0,fn.V() - 1);
		
		int tCount = this.numberOfTeams() - 1;
						
		for(int i = fn.V() - 2; i >= (fn.V() - this.numberOfTeams()); i--) {
			if(tCount != x) {
				int index = tCount;
				if(ff.inCut(i)) {
					teams.add(teamNames[index]);
				}
			}else {
				i++;
			}
			tCount--;
		}
		return teams.size() > 0 ? teams : null;
	}  // subset R of teams that eliminates given team; null if not eliminated
	
	public static void main(String[] args) {
//	    BaseballElimination division = new BaseballElimination("test_data/teams24.txt");
////	    System.out.println(division.certificateOfElimination("New_York"));
//	    for (String team : division.teams()) {
//	        if (division.isEliminated(team)) {
//	            StdOut.print(team + " is eliminated by the subset R = { ");
//	            for (String t : division.certificateOfElimination(team)) {
//	                StdOut.print(t + " ");
//	            }
//	            StdOut.println("}");
//	        }
//	        else {
//	            StdOut.println(team + " is not eliminated");
//	        }
//	    }
	}

}
