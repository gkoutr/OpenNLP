package ProofOfConcept;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import edu.stanford.nlp.simple.*;

public class test {
	static HashMap<String, Integer> keyWordsAndCounts = new HashMap<String, Integer>();
	static HashMap<String, Integer> keyPhrasesAndCounts = new HashMap<String, Integer>();
	static List<String> words;
	public static HashSet<String> specialCharacters = new HashSet<>(Arrays.asList("�", "�"));

	public static void main(String[] args) throws IOException {
		String text = readFile("input.txt");
		Sentence sent = new Sentence(text);
			words = sent.words();
			List<String> nerTags = sent.nerTags();

			HashMap<String, List<String>> mapping = new HashMap<String, List<String>>();
			HashMap<String, Integer> occurences = new HashMap<String, Integer>();

/*			LoadKeyWords(sent);
			PrintHashMap(keyWordsAndCounts);

			System.out.println("----------------KEY PHRASES----------------");

			LoadKeyPhrases(sent);
			PrintHashMap(keyPhrasesAndCounts);*/

			System.out.println("----------------KEY PHRASES END----------------");

			for (String tag : nerTags) {
				if (!tag.equals("O")) {
					List<String> obj = sent.mentions(tag);
					if (tag.equals("NUMBER")) {
						obj.removeAll(specialCharacters);
					}
					mapping.put(tag, obj);
				}
			}
			System.out.println("ENTITIES");
			for (String s : mapping.keySet()) {
				List<String> strings = mapping.get(s);
				System.out.print(s + " -> [");
				for (String str : strings) {
					Integer count = occurences.get(str);
					if (count == null) {
						occurences.put(str, 1);
					} else {
						count = count + 1;
						occurences.put(str, count);
					}
					System.out.print(str + "," + " ");
				}
				System.out.print("]");
				System.out.println();
			}

/*			System.out.println("Keys and how many times they have occurred");

			for (String key : occurences.keySet()) {
				System.out.print(key + "->" + occurences.get(key));
				System.out.println();
			}*/
		}

	

	public static void LoadKeyWords(Sentence sent) {
		ArrayList<String> actualWords = new ArrayList<String>();
		List<String> posTags = sent.posTags();
		int index = 0;
		for (String p : posTags) {
			if (p.equals("NN") || p.equals("NNS") || p.equals("NNP") || p.equals("NNPS")) {
				actualWords.add(words.get(index));
			}
			index++;
		}
		keyWordsAndCounts = LoadCountHashMaps(actualWords);
	}

	public static String readFile(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}

	public static void PrintHashMap(HashMap<String, Integer> source) {
		for (String s : source.keySet()) {
			System.out.print(s + "->" + source.get(s));
			System.out.println();
		}
	}

	public static void LoadKeyPhrases(Sentence sent) {
		SentenceAlgorithms helpers = sent.algorithms();
		List<String> keyPhrases = helpers.keyphrases();
		keyPhrasesAndCounts = LoadCountHashMaps(keyPhrases);
	}

	public static HashMap<String, Integer> LoadCountHashMaps(List<String> source) {
		HashMap<String, Integer> counts = new HashMap<String, Integer>();
		for (String s : source) {
			Integer i = counts.get(s);
			if (i == null) {
				counts.put(s, 1);
			} else {
				i++;
				counts.put(s, i);
			}
		}
		return counts;
	}

}
