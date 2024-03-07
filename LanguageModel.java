import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class LanguageModel {

    // La carte de ce modèle.
    // Mappe les fenêtres sur des listes d'objets de données de caractères.
    HashMap<String, List> CharDataMap;

    // La longueur de la fenêtre utilisée dans ce modèle.
    int windowLength;

    // Le générateur de nombres aléatoires utilisé par ce modèle.
    private Random randomGenerator;

    /**
     * Construit un modèle de langage avec la longueur de fenêtre donnée et une valeur de graine donnée.
     * Générer des textes à partir de ce modèle plusieurs fois avec la même valeur de graine produira les mêmes textes aléatoires.
     * Bon pour le débogage.
     */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<>();
    }

    /**
     * Construit un modèle de langage avec la longueur de fenêtre donnée.
     * Générer des textes à partir de ce modèle plusieurs fois produira des textes aléatoires différents.
     * Bon pour la production.
     */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<>();
    }

    /**
     * Construit un modèle de langage à partir du texte dans le fichier donné (le corpus).
     */
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
            probs = new List(); // Remplacez List par le type de liste spécifique dont vous avez besoin (par exemple, ArrayList)
            CharDataMap.put(window, probs);
        }
        // Mettre à jour les probabilités pour chaque caractère
        for (int i = windowLength; i < line.length(); i++) {
            char c = line.charAt(i);
            // Assurez-vous que la méthode update est définie dans votre classe List appropriée
            // probs.update(c);
        }
    }

    // Computes and sets the probabilities (p and cp fields) of all the
    // characters in the given list.
    public void calculateProbabilities(List probs) {
        // Itérateur sur la liste
        ListIterator iterator = new ListIterator(probs.getFirstNode());
        // Total des occurrences
        int totalCount = 0;
        while (iterator.hasNext()) {
            // Assurez-vous que CharData et count sont définis dans votre classe List appropriée
            // CharData current = iterator.next();
            // totalCount += current.count;
        }
        iterator = new ListIterator(probs.getFirstNode());
        double prev = 0;
        while (iterator.hasNext()) {
            // Assurez-vous que CharData, count, p, et cp sont définis dans votre classe List appropriée
            // CharData current = iterator.next();
            // current.p = ((double) current.count) / totalCount;
            // current.cp = current.p + prev;
            // prev = current.cp;
        }
    }

    // Returns a random character from the given probabilities list.
    public char getRandomChar(List probs) {
        calculateProbabilities(probs);
        double r = randomGenerator.nextDouble();
        ListIterator iterator = new ListIterator(probs.getFirstNode());
        while (iterator.hasNext()) {
            // Assurez-vous que CharData, p, cp, et chr sont définis dans votre classe List appropriée
            // CharData current = iterator.next();
            // if (current.cp >= r) {
            //     return current.chr;
            // }
        }
        return '_';
    }

    /**
     * Génère un texte aléatoire, basé sur les probabilités apprises lors de l'entraînement.
     *
     * @param initialText       - texte pour commencer. Si la dernière sous-chaîne de taille numberOfLetters de initialText
     *                          n'apparaît pas comme une clé dans la carte, nous ne générons aucun texte et renvoyons uniquement le texte initial.
     * @param numberOfLetters   - la taille du texte à générer
     * @return le texte généré
     */
    public String generate(String initialText, int numberOfLetters) {
        if (initialText.length() < windowLength) {
            return initialText;
        }
        String window = initialText.substring(initialText.length() - windowLength);
        String generatedText = window;
        while (generatedText.length() < numberOfLetters + windowLength) {
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

    /**
     * Renvoie une chaîne représentant la carte de ce modèle de langage.
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (String key : CharDataMap.keySet()) {
            List keyProbs = CharDataMap.get(key);
            str.append(key).append(" : ").append(keyProbs).append("\n");
        }
        return str.toString();
    }

    public static void main(String[] args) {
        int windowLength = Integer.parseInt(args[0]);
        String initialText = args[1];
        int generatedTextLength = Integer.parseInt(args[2]);
        boolean randomGeneration = args[3].equals("random");
        String fileName = args[4];
        LanguageModel lm;
        if (randomGeneration)
            lm = new LanguageModel(windowLength);
        else
            lm = new LanguageModel(windowLength, 20);
        // Formatez la sortie en utilisant System.out.printf ou System.out.println selon vos besoins
        System.out.println(lm.generate(initialText, generatedTextLength));
    }
}

