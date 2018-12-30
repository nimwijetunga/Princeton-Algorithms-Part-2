import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

public class SAP {
	
	private Digraph dg;
	private boolean [] visitedl;
	private boolean [] visitedr;
	private int [] lcount;
	private int [] rcount;

   // constructor takes a digraph (not necessarily a DAG)
   public SAP(Digraph G) {
	   if(G == null) {
		   throw new java.lang.IllegalArgumentException();
	   }
	   this.dg = G;
	   reset();
   }
   
   private void reset() {
	   visitedl = new boolean[dg.V()];
	   visitedr = new boolean[dg.V()];
	   lcount = new int[dg.V()];
	   rcount = new int[dg.V()];
	   for(int i = 0; i < dg.V(); i++) {
		   visitedl[i] = false;
		   visitedr[i] = false;
		   lcount[i] = 0;
		   rcount[i] = 0;
	   }
   }
   
// throw an IllegalArgumentException unless {@code 0 <= v < V}
   private void validateVertex(int v) {
       if (v < 0 || v >= dg.V())
           throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (dg.V()-1));
   }
   
   

   // length of shortest ancestral path between v and w; -1 if no such path
   public int length(int v, int w) {
	   validateVertex(v);
	   validateVertex(w);
	   reset();
	   BFSR(v);
	   BFSL(w);
	   int minLen = Integer.MAX_VALUE;
	   for(int i = 0; i < dg.V(); i++) {
		   if(visitedl[i] && visitedr[i]) {
//			   System.out.println(rcount[i] + " " + lcount[i]);
			   if(rcount[i] + lcount[i] < minLen) {
				   minLen = rcount[i] + lcount[i];
			   }
		   }
	   }
	   return minLen == Integer.MAX_VALUE ? -1 : minLen;
   }

   // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
   public int ancestor(int v, int w){
	   validateVertex(v);
	   validateVertex(w);
	   reset();
	   BFSR(v);
	   BFSL(w);
	   int minLen = Integer.MAX_VALUE, lca = -1;
	   for(int i = 0; i < dg.V(); i++) {
		   if(visitedl[i] && visitedr[i]) {
			   if(rcount[i] + lcount[i] < minLen) {
				   lca = i;
				   minLen = rcount[i] + lcount[i];
			   }
		   }
	   }
	   return lca;
   }
   
   private void BFSR(int v) {
		  int count = 1;
		  LinkedList<Integer> queue = new LinkedList<Integer>();
		  this.visitedr[v] = true;
		  this.rcount[v] = 0;
		  queue.add(v);
		  queue.add(-1);
		  
		  while(!queue.isEmpty()) {
			  v = queue.poll();
			  if(v == -1) {
				  count++;
				  if(!queue.isEmpty())queue.add(-1);
				  continue;
			  }
			  Iterable<Integer> adj = dg.adj(v);
			  for(int a : adj) {
				  if(!this.visitedr[a]) {
					  this.visitedr[a] = true;
					  this.rcount[a] = count;
					  queue.add(a);
				  }
			  }
		  }
	   }
   
   private void BFSL(int v) {
	  int count = 1;
	  LinkedList<Integer> queue = new LinkedList<Integer>();
	  this.visitedl[v] = true;
	  this.lcount[v] = 0;
	  queue.add(v);
	  queue.add(-1);
	  
	  while(!queue.isEmpty()) {
		  v = queue.poll();
		  if(v == -1) {
			  count++;
			  if(!queue.isEmpty()) queue.add(-1);
			  continue;
		  }
		  Iterable<Integer> adj = dg.adj(v);
		  for(int a : adj) {
			  if(!this.visitedl[a]) {
				  this.visitedl[a] = true;
				  this.lcount[a] = count;
				  queue.add(a);
			  }
		  }
	  }
   }
   
   // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
   public int length(Iterable<Integer> v, Iterable<Integer> w) {
	   if(v == null || w == null) {
		   throw new java.lang.IllegalArgumentException();
	   }
	   int minLen = Integer.MAX_VALUE;
	   int size = 0, size2 = 0;
	   for(int i : v) {
		   for(int j : w) {
			   validateVertex(i);
			   validateVertex(j);
			   int len = this.length(i, j);
			   if(len < minLen) minLen = len;
		   }
		   size++;
	   }
	   for(int i : w) {
		   size2++;
	   }
//	   System.out.println(minLen + " " + size + " " + size2);
	   return minLen;
   }
   
   // a common ancestor that participates in shortest ancestral path; -1 if no such path
   public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
	   if(v == null || w == null) {
		   throw new java.lang.IllegalArgumentException();
	   }
	   int vMin = -1, wMin = -1;
	   int minLen = Integer.MAX_VALUE;
	   for(int i : v) {
		   for(int j : w) {
			   validateVertex(i);
			   validateVertex(j);
			   int len = this.length(i, j);
			   if(len < minLen) {
				   vMin = i;
				   wMin = j;
				   minLen = len;
			   }
		   }
	   }
	   if(minLen == -1) return -1;
	   return this.ancestor(vMin, wMin);
   }

   // do unit testing of this class
   public static void main(String[] args) {
//	   SAP sap = new SAP(new Digraph(new In(new File("input.txt"))));
//	   System.out.println(sap.length(7, 17));
//	   ArrayList<Integer> lst = new ArrayList<Integer>();
//	   lst.add(3);
//	   lst.add(7);
//	   lst.add(8);
//	   ArrayList<Integer> lst2 = new ArrayList<Integer>();
//	   lst2.add(5);
//	   lst2.add(10);
//	   lst2.add(15);
//	   System.out.println(sap.length(lst, lst2));
	   
   }
}