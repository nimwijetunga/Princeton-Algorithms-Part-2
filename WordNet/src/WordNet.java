import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

public class WordNet {
	
	private HashMap<String, ArrayList<Integer>> nounMap;
	private HashMap<Integer, String> nounMapRev;
	private Digraph wordNet;
//	private HashSet<String> nouns;
	private SAP sap;

   // constructor takes the name of the two input files
   public WordNet(String synsets, String hypernyms) {
	   checkNull(synsets);
	   checkNull(hypernyms);
	   this.nounMap = new HashMap<String, ArrayList<Integer>>();
	   this.nounMapRev = new HashMap<Integer, String>();
//	   this.nouns = new ArrayList<String>();
	   readInputFiles(synsets,hypernyms);
	   this.sap = new SAP(this.wordNet);
   }
   
   private boolean rootedDAG(int numV) {
	   int count = 0;
	   for(int i = 0; i < numV; i++) {
		   if(this.wordNet.outdegree(i) == 0) count++;
		   if(count > 1) return false;
	   }
	   if(count == 0) return false;
	   return true;
   }
   
   private void readInputFiles(String synsets, String hypernyms) {
	   //Create hashmap for noun->index
	   In in = new In(synsets);
	   int id = -1;
	   while(in.hasNextLine()) {
		   String line = in.readLine();
		   List<String> components = Arrays.asList(line.split(","));
		   id = Integer.valueOf(components.get(0));
		   List<String> nouns = Arrays.asList(components.get(1).split("\\s+"));
		   this.nounMapRev.put(id, components.get(1));
		   for(String s : nouns) {
//			   this.nouns.add(s);
			   ArrayList<Integer> tmp = new ArrayList<Integer>();
			   if(this.nounMap.containsKey(s)) {
				   tmp = this.nounMap.get(s);
			   }
			   tmp.add(id);
			   this.nounMap.put(s, tmp);
		   }
	   }
	   //Create Digraph
	   in = new In(hypernyms);
	   this.wordNet = new Digraph(id + 1);
	   while(in.hasNextLine()) {
		   String line = in.readLine();
		   List<String> components = Arrays.asList(line.split(","));
		   int v = Integer.valueOf(components.get(0));
		   for(int i = 1; i < components.size(); i++) {
			   int w = Integer.valueOf(components.get(i));
			   this.wordNet.addEdge(v, w);
		   }
	   }
	   if(!rootedDAG(id + 1)) {
		   throw new IllegalArgumentException("Not rooted DAG!");
	   }
   }
   
   private void checkNull(String s) {
	   if(s == null) throw new IllegalArgumentException("Argument is null!");
   }
 

   // returns all WordNet nouns
   public Iterable<String> nouns(){
	   return this.nounMap.keySet();
   }

   // is the word a WordNet noun?
   public boolean isNoun(String word) {
	  checkNull(word);
	  return this.nounMap.containsKey(word);
   }

   // distance between nounA and nounB (defined below)
   public int distance(String nounA, String nounB) {
	   checkNull(nounA);
	   checkNull(nounB);
	   if(!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException("Not noun!");
	   return sap.length(this.nounMap.get(nounA), this.nounMap.get(nounB));
   }

   // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
   // in a shortest ancestral path (defined below)
   public String sap(String nounA, String nounB) {
	   checkNull(nounA);
	   checkNull(nounB);
	   if(!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException("Not noun!");
	   int sa = this.sap.ancestor(this.nounMap.get(nounA), this.nounMap.get(nounB));
	   if(sa == -1) return null;
	   return this.nounMapRev.get(sa);
   }

   // do unit testing of this class
   public static void main(String[] args) {
//	   WordNet net = new WordNet("synsets.txt","hypernyms.txt");
//	   System.out.println(net.sap("tea", "coffee"));
//	   Iterable<String> nouns = net.nouns();
   }
}