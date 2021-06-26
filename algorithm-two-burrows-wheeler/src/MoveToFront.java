import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
	// Find and insert, O(R)
	private static char findAndMove(char[] arr, char c) {
		int pos = -1;
		for (int i = 0; i < arr.length; i ++) {
			if (arr[i] == c) {
				pos = i;
				break;
			}
		}
		for (int i = pos; i > 0; i --) {
			arr[i] = arr[i - 1];
		}
		arr[0] = c;
		return (char) pos;
	}
	
	// Find and insert, O(R)
	private static char findAndMoveByIndex(char[] arr, int index) {
		char output = arr[index];
		for (int i = index; i > 0; i --) {
			arr[i] = arr[i - 1];
		}
		arr[0] = output;
		return output;
	}
	
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
    	char[] arr = new char[256];
    	for (int i = 0; i < 256; i ++) {
    		arr[i] = (char) i;
    	}
    	while(!BinaryStdIn.isEmpty()) {
    		char c = BinaryStdIn.readChar();
    		BinaryStdOut.write(findAndMove(arr, c));
    	}
    	BinaryStdOut.flush();
    	BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
    	char[] arr = new char[256];
    	for (int i = 0; i < 256; i ++) {
    		arr[i] = (char) i;
    	}
    	while (!BinaryStdIn.isEmpty()) {
    		int index = (int) BinaryStdIn.readChar();
    		BinaryStdOut.write(findAndMoveByIndex(arr, index));
    	}
    	BinaryStdOut.flush();
    	BinaryStdOut.close();    	
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
	public static void main(String[] args) {
		if (args[0].equals("-")) {
			encode();
		} else if (args[0].equals("+")) {
			decode();
		} else {
			System.out.println("Use -(encode) or +(decode) only.");
		}
	}
}
