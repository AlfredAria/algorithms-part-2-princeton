alias java-algs4=java -cp algs4.jar
# HexDump
java-algs4 edu.princeton.cs.algs4.HexDump < abra.txt

java-algs4 edu.princeton.cs.algs4.Huffman - < abra.txt | java-algs4 edu.princeton.cs.algs4.HexDump 16

java-algs4 edu.princeton.cs.algs4.Huffman - < abra.txt | java-algs4 edu.princeton.cs.algs4.Huffman +


# in /bin folder
java -cp .:algs4.jar MoveToFront - < abra.txt

# Step 1: Implement a basic O(nR) version for encoding.
cat abra.txt.mtf | java -cp .:algs4.jar MoveToFront - | java -cp algs4.jar edu.princeton.cs.algs4.HexDump
# Verifications:
cat abra.txt.mtf | java -cp algs4.jar edu.princeton.cs.algs4.HexDump

# 3. Burrel Wheeler encode / decode.
echo "Any random test case" | java -cp .:algs4.jar BurrowsWheeler - | java -cp .:algs4.jar BurrowsWheeler +