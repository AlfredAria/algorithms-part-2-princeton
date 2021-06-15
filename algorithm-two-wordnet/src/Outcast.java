public class Outcast {
	private final static String path = "resources/wordnet/";

	private final WordNet wn;

	// constructor takes a WordNet object
	public Outcast(WordNet wordnet) {
		wn = wordnet;
	}

	// given an array of WordNet nouns, return an outcast
	public String outcast(String[] nouns) {
		String largest = "";
		int largestDist = 0;
		for (int i = 0; i < nouns.length; i++) {
			int sumDist = 0;
			for (int j = 0; j < nouns.length; j++) {
				int dist = wn.distance(nouns[i], nouns[j]);
//				System.out.println(nouns[i] + " " + nouns[j] + ":" + dist);
				sumDist += dist;
			}
//			System.out.println(nouns[i] + "sumDist= "+sumDist);
			if (sumDist > largestDist) {
				largestDist = sumDist;
				largest = nouns[i];
			}
		}
		return largest;
	}

	// see test client below
	public static void main(String[] args) {
		WordNet wn = new WordNet(path + "synsets.txt", path + "hypernyms.txt");
		Outcast oc = new Outcast(wn);
//		String[] testCase = {"car", "auto", "truck", "plane", "tree", "train", "vehicle", "van"};
//		String[] testCase = {"Turing", "von_Neumann", "Mickey_Mouse"};
//		String[] testCase = {"water", "soda", "bed", "orange_juice", "milk", "apple_juice", "tea", "coffee"};
		String[] testCase = { "blue", "green", "yellow", "brown", "black", "white", "orange", "violet", "red",
				"serendipity" };

		System.out.println(oc.outcast(testCase));
	}
}
