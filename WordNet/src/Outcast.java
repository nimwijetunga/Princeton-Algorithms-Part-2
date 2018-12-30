import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
	
	private WordNet wordnet;
	
   public Outcast(WordNet wordnet) {
	   this.wordnet = wordnet;
   }
   
   public String outcast(String[] nouns) {
	   int maxDist = Integer.MIN_VALUE;
	   String outcast = "";
	   for(int i = 0; i < nouns.length; i++) {
		   int dist = 0;
		   for(int j = 0; j < nouns.length; j++) {
			   int len = this.wordnet.distance(nouns[i], nouns[j]);
			   dist += len;
		   }
		   if(dist > maxDist) {
			   maxDist = dist;
			   outcast = nouns[i];
		   }
	   }
	   return outcast;
   }
   
   public static void main(String[] args) {
//	    WordNet wordnet = new WordNet("synsets.txt","hypernyms.txt");
//	    Outcast outcast = new Outcast(wordnet);
//	    In in = new In("outcast.txt");
//        String[] nouns = in.readAllStrings();
//        StdOut.println(outcast.outcast(nouns));
	}
}