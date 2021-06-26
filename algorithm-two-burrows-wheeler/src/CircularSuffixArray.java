import java.util.Arrays;

public class CircularSuffixArray {
	
	// Stores a double of the original string S
	// to allow substring accessing from any index
	// 0 <= i < len(S).
	private String str;
	
	private int length;
	private Accessor[] accessors;
	class Accessor implements Comparable<Accessor> {
		// This accessor's associated original suffix array index.
		private final int index;
		Accessor(int index) {
			this.index = index;
		}
		
		int index() {
			return this.index;
		}
		
		@Override
		public String toString() {
			return str.substring(index, index + length);
		}

		@Override 
		public int compareTo(CircularSuffixArray.Accessor o) {
			return toString().compareTo(o.toString());
		}
		
	}

    // Create a circular suffix array of s
	// Space complexity: O(2n) where n is length of input s.
	// Time complexity: O(n log n) sorting suffixes, without copying. 
    public CircularSuffixArray(String s) {
    	if (s == null || s.isEmpty()) {
    		throw new IllegalArgumentException("Null or empty string.");
    	}
    	str = s + s;
    	length = s.length();
    	accessors = new Accessor[length()];
    	for (int i = 0; i < length(); i ++) {
    		accessors[i] = new Accessor(i);
    	}
    	Arrays.sort(accessors);
    }

    // length of s
    public int length() {
    	return this.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
    	if (i < 0 || i >= length()) {
    		throw new IllegalArgumentException("Index out of bound.");
    	}
    	return accessors[i].index();
    }

    /**
     * Test case I used here:

	     i       Original Suffixes           Sorted Suffixes         index[i]
	    --    -----------------------     -----------------------    --------
	     0    A B R A C A D A B R A !     ! A B R A C A D A B R A    11
	     1    B R A C A D A B R A ! A     A ! A B R A C A D A B R    10
	     2    R A C A D A B R A ! A B     A B R A ! A B R A C A D    7
	     3    A C A D A B R A ! A B R     A B R A C A D A B R A !    0
	     4    C A D A B R A ! A B R A     A C A D A B R A ! A B R    3
	     5    A D A B R A ! A B R A C     A D A B R A ! A B R A C    5
	     6    D A B R A ! A B R A C A     B R A ! A B R A C A D A    8
	     7    A B R A ! A B R A C A D     B R A C A D A B R A ! A    1
	     8    B R A ! A B R A C A D A     C A D A B R A ! A B R A    4
	     9    R A ! A B R A C A D A B     D A B R A ! A B R A C A    6
	    10    A ! A B R A C A D A B R     R A ! A B R A C A D A B    9
	    11    ! A B R A C A D A B R A     R A C A D A B R A ! A B    2

     * @param args
     */
    public static void main(String[] args) {
    	String test = "ABRACADABRA!";
    	CircularSuffixArray csa = new CircularSuffixArray(test);
    	System.out.println("Suffix array length: " + csa.length());
    	System.out.println("String length: " + csa.length());
    	for (int i = 0; i < 12; i ++) {
        	System.out.format(
        			"Index of suffix ranked %s in origin array: %s\n",
        			i, csa.index(i));    		
    	}
    }

}