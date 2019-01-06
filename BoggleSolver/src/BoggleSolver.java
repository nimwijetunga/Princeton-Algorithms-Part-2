import java.util.ArrayList;
import java.util.HashSet;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.TST;

public class BoggleSolver
{	
	private Node root;
	
	private class Node{
		private Node [] children;
		private boolean end;
		public Node(boolean end) {
			this.children = new Node[26];
			this.end = end;
		}
	}
	
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
    	genTrie(dictionary);
    }
    
	private void genTrie(String [] dict) {
		for(String s : dict) {
			root = put(root, s, 0);
		}
    }

	private Node put(Node cur, String s, int i) {
		if(cur == null) cur = new Node(false);

		if(i == s.length()) {
			cur.end = true;
			return cur;
		}
		char c = s.charAt(i);
		cur.children[c - 'A'] = put(cur.children[c - 'A'], s, i+1);
		return cur;
	}
	
	private boolean contains(String s) {
		return contains(root, s, 0);
	}
	
	private boolean contains(Node cur, String s, int i) {
		if(cur == null) return false;
		if(i == s.length()) {
			if(cur.end) return true;
			return false;
		}
		char c = s.charAt(i);
		return contains(cur.children[c - 'A'],s,i+1);
	}
	
    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board){
    	HashSet<String> validWords = new HashSet<String>();
    	boolean [][] marked = new boolean[board.rows()][board.cols()];
    	for(int i = 0; i < board.rows(); i++) {
    		for(int j = 0; j < board.cols(); j++) {
    			getRecursive(root, marked, validWords, board, i, j, "");
    		}
    	}
    	return validWords;
    }
    
    private void getRecursive(Node cur, boolean [][] marked, HashSet<String> validWords, BoggleBoard b, int i, int j, String s) {
    	if(i < 0 || i >= b.rows() || j < 0 || j >= b.cols()) return;

    	if(marked[i][j] || cur == null) return;
    	
    	marked[i][j] = true;
    	
    	char curChar = b.getLetter(i, j);
    	cur = cur.children[curChar - 'A'];
    	s += curChar;
    	if(curChar == 'Q' && cur != null) {
    		cur = cur.children['U' - 'A'];
    		s += 'U';
    	}
    	
    	if(cur != null && cur.end && scoreOf(s) > 0) {
    		validWords.add(s);
    	}

    	//Horizontal Searches
    	getRecursive(cur, marked, validWords, b,i,j - 1, s); //Left
    	getRecursive(cur, marked, validWords, b,i,j + 1, s); //Right
    	//Vertical Searches
    	getRecursive(cur, marked, validWords, b,i - 1,j,s); //Up
    	getRecursive(cur, marked, validWords, b,i + 1,j,s); //Down
    	//Diagonal Searches
    	getRecursive(cur, marked, validWords, b,i - 1, j - 1,s); //Upper-Left
    	getRecursive(cur, marked, validWords, b,i - 1, j + 1,s); //Upper-Right
    	getRecursive(cur, marked, validWords, b,i + 1, j - 1,s); //Bottom-Left
    	getRecursive(cur, marked, validWords, b,i + 1, j + 1,s); //Bottom-Right
    	marked[i][j] = false;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
    	int len = word.length();
    	if((len >= 0 && len <= 2) || !contains(word)) return 0;
    	else if(len >= 3 && len <= 4) return 1;
    	else if(len == 5) return 2;
    	else if(len == 6) return 3;
    	else if(len == 7) return 5;
    	return 11;
    }
    
    public static void main(String [] args) {
//    	In in = new In("test_data/dictionary-algs4.txt");
//    	BoggleSolver bs = new BoggleSolver(in.readAllStrings());
//        BoggleBoard board = new BoggleBoard("test_data/board-q.txt");
//        Iterable<String> words = bs.getAllValidWords(board);
//        System.out.println("-------END-------");
//        int score = 0, count = 0;
//        for(String s : words) {
//        	System.out.println(s);
//        	score += bs.scoreOf(s);
//        	count++;
//        }
//        System.out.println(score + " " + count);
    }
}
