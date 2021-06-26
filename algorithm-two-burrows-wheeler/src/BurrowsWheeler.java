import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output 
    public static void transform() {
    	String input = BinaryStdIn.readString();
    	int length = input.length();
    	CircularSuffixArray csa = new CircularSuffixArray(input);

    	int index = -1;

    	// Build transformed string.
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < csa.length(); i ++) {
    		int suffixIndex = csa.index(i);
    		if (suffixIndex == 0 && index < 0) {
    			index = i;
    		}
    		sb.append(input.charAt((suffixIndex + length - 1) % length));
    	}
    	// Output rank of first suffix (original string).
    	BinaryStdOut.write(index);

    	// Output transformed column.
    	BinaryStdOut.write(sb.toString());
    	BinaryStdOut.flush();

    	BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
    	// Answer of 3 "ARD!RCAAAABB" is "ABRACADABRA!".
    	int next = BinaryStdIn.readInt();
    	String input = BinaryStdIn.readString();

    	// Get a bucket sorted index mapping for the prefixes from the suffixes.
    	// O(n + R) time and space complexity for constructing the prefix arrays.
    	List<LinkedList<Integer>> chrs = new ArrayList<>();
    	for (int i = 0; i < 256; i ++) chrs.add(new LinkedList<>());
    	for (int i = 0; i < input.length(); i ++) 
    		chrs.get(input.charAt(i)).add(i);
    	int[] prefixIndexTable = new int[input.length()];
    	char[] prefixTable = new char[input.length()];
    	int i = 0;
    	int character = 0;
    	while(character < 256) {
    		while (chrs.get(character).size() > 0) {
    			prefixIndexTable[i] = chrs.get(character).poll();
    			prefixTable[i] = (char) character;
    			i++;
    		}
    		character++;
    	}

    	// "Next" array for inverse transform. 
		// we define next[i] to be the row in the sorted order where the (j + 1)st 
    	// original suffix appears. For example, if first is the row in which the 
    	// original input string appears, then next[first] is the row in the sorted 
    	// order where the 1st original suffix (the original string left-shifted by 1) 
    	// appears; next[next[first]] is the row in the sorted order where the 2nd 
    	// original suffix appears; next[next[next[first]]] is the row where the 3rd 
    	// original suffix appears; and so forth.
    	// O(n) time complexity for looking up.
    	int cnt = 0;
    	while (cnt < input.length()) {
    		char c = prefixTable[next];
    		next = prefixIndexTable[next];
        	BinaryStdOut.write(c);
        	cnt++;
    	}    	

    	BinaryStdOut.flush();
    	BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
    	if (args[0].equals("-")) {
    		transform();
		} else if (args[0].equals("+")) {
			inverseTransform();
		} else {
			System.out.println("Use -(encode) or +(decode) only.");
		}
    }

}

