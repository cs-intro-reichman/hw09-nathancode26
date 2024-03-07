import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) throws IOException {
  // Lire le fichier
  try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
    String line;
    while ((line = reader.readLine()) != null) {
      // Traiter chaque ligne comme une fenêtre
      processLine(line);
    }
  }
}

private void processLine(String line) {
  // Créer une nouvelle fenêtre à partir de la ligne
  String window = line.substring(0, windowLength);
  // Obtenir la liste des probabilités pour la fenêtre
  List probs = CharDataMap.get(window);
  if (probs == null) {
    probs = new List();
    CharDataMap.put(window, probs);
  }
  // Mettre à jour les probabilités pour chaque caractère
  for (int i = windowLength; i < line.length(); i++) {
    char c = line.charAt(i);
    probs.update(c);
  }
}


    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
  public void calculateProbabilities(List probs) {
  // Itérateur sur la liste
  ListIterator iterator = new ListIterator(probs.getFirstNode());
  // Total des occurrences
  int totalCount = 0;
  while (iterator.hasNext()) {
    CharData current = iterator.next();
    totalCount += current.count;
  }
  iterator = new ListIterator(probs.getFirstNode());
  double prev = 0;
  while (iterator.hasNext()) {
    CharData current = iterator.next();
    current.p = ((double) current.count) / totalCount;
    current.cp = current.p + prev;
    prev = current.cp;
  }
}


    // Returns a random character from the given probabilities list.
  public char getRandomChar(List probs) {
  calculateProbabilities(probs);
  double r = randomGenerator.nextDouble();
  ListIterator iterator = new ListIterator(probs.getFirstNode());
  while (iterator.hasNext()) {
    CharData current = iterator.next();
    if (current.cp >= r) {
      return current.chr;
    }
  }
  return '_';
}


    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
  public String generate(String initialText, int textLength) {
  if (initialText.length() < windowLength) {
    return initialText;
  }
  String window = initialText.substring(initialText.length() - windowLength);
  String generatedText = window;
  while (generatedText.length() < textLength + windowLength) {
    List probs = CharDataMap.get(window);
    if (probs == null) {
      break;
    }
    char nextChar = getRandomChar(probs);
    generatedText += nextChar;
    window = generatedText.substring(generatedText.length() - windowLength);
  }
  return generatedText;
}


    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		// Your code goes here
    int windowLength = Integer.parseInt(args[0]);
        String initialText = args[1];
        int generatedTextLength = Integer.parseInt(args[2]);
        Boolean randomGeneration = args[3].equals("random");
        String fileName = args[4];
        LanguageModel lm;
        if (randomGeneration)
            lm = new LanguageModel(windowLength);
        else
            lm = new LanguageModel(windowLength, 20);
        // Trains the model, creating the map.
        lm.train(fileName);
        // Generates text, and prints it.
        System.out.println(lm.generate(initialText, generatedTextLength));
    }
}
