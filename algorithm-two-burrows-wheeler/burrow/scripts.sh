alias java-algs4=java -cp algs4.jar
# HexDump
java-algs4 edu.princeton.cs.algs4.HexDump < abra.txt

java-algs4 edu.princeton.cs.algs4.Huffman - < abra.txt | java-algs4 edu.princeton.cs.algs4.HexDump 16

java-algs4 edu.princeton.cs.algs4.Huffman - < abra.txt | java-algs4 edu.princeton.cs.algs4.Huffman +


# in /bin folder
java -cp .:algs4.jar MoveToFront - < abra.txt