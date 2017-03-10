package K360NLP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.SentenceAlgorithms;

public class Content {
	private Document doc;
	
	/**
	 * The reason we also provide a Sentence is because even though this and the Document will have the same text, Stanford's OpenNLP treats a Document and a Sentence differently semantically. 
	 * That should make sense since the context of a super long Sentence is very different than the culminating context of all of its sub sentences. 
	 * Thus, running the same annotations on a Document and Sentence produces different results. Instead of choosing between one, we will just compile them together.
	 * 
	 */
	private Sentence sentence;
	
	/**
	 * A way to keep track of special characters that we do not want to see returned
	 *
	 * 
	 * 
	 */
	public static HashSet<String> specialCharacters = new HashSet<>(Arrays.asList("�", "�"));

	/**
	 * Initializes a Content based off of a provided Document object 
	 *
	 * 
	 * 
	 */
	public Content(Document input) {
		doc = input;
		if (input == null) {
			sentence = null;
		} else {
			String text = doc.text();
			if (text.isEmpty() || text.trim().length() <= 0){
				sentence = null;
				doc = null;
			}
			else{
				sentence = new Sentence(text);	
			}
		}
	}
	
	/**
	 * Initializes a Content based off of a provided String
	 *
	 * 
	 * 
	 */
	public Content(String input){
		if (input.isEmpty() || input.trim().length() <= 0){
			doc = null;
			sentence = null;
		} else {
			doc = new Document(input);
			sentence = new Sentence(input);	
		}
	}
	
	public String GetRawText(){
		return sentence.text();
	}
	
	/**
	 * Base constructor. Use this, and then the SetFields method, to initialize a Content object based off of a filename
	 *
	 * 
	 * 
	 */
	public Content(){
		doc = null;
		sentence = null;
	}
	
	/**
	 * Initializes a Content based off of a given filename
	 *
	 * @param filename The name of the file (or path if it is not in the project directory already)
	 * 
	 */
	public void SetFields(String fileName){
		try {
			String text = readFile(fileName);
			if (text.isEmpty() || text.trim().length() <= 0){
				doc = null;
				sentence = null;
			} else {
				doc = new Document(text);
				sentence = new Sentence(text);
			}
		} catch (IOException e) {
			System.out.println("File not found");
		}
	}
	
	
	/**
	 * Reads in the file
	 *
	 * @param filename The filename
	 * @return String representation of the text within the file
	 */
	private String readFile(String fileName) throws IOException {
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

	/**
	 * Generates HashMap that links every element in the given List to how often it occurs in the list. 
	 * This is useful because it tells us how frequently a keyword, keyphrase, or entity occured in the document.
	 * It is optimized to run in only one pass :).
	 *
	 * @param source - The List to pass in 
	 * @return HashMap representation of the data and its frequency within the source
	 */
	private HashMap<String, Integer> GenerateHashMaps(List<String> source) {
		HashMap<String, Integer> counts = new HashMap<String, Integer>();
		for (String s : source) {
			if (specialCharacters.contains(s)) {
				continue;
			}
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
	
	/**
	 * Gets all of the words from the Document
	 *
	 * 
	 * @return List of all words
	 */
	
	public List<String> GetAllWords(){
		if(doc == null){
			return null;
		}
		ArrayList<String> masterList = new ArrayList<String>();
		for(Sentence text:doc.sentences()){
			masterList.addAll(text.words());
		}
		return masterList;
	}

	/**
	 * Merges two frequency HashMaps into one. Optimized to run in one pass.
	 *
	 * @param source A String identifier that points us to which frequency HashMaps to compile. This identifier MUST BE either: "keywords", "keyphrases", or "entities"
	 * @return HashMap representation of the data and its frequency within the source
	 */
	public HashMap<String,Integer> MergeMaps(String identifier){
		HashMap<String,Integer> documentMap = null;
		HashMap<String,Integer> sentenceMap = null;
		
		if(identifier.equals("keywords")){
			documentMap = LoadKeyWordsFromDocument();
			sentenceMap = LoadKeyWordsFromSentence();
		} 
		else if(identifier.equals("keyphrases")){
			documentMap = LoadKeyPhrasesFromDocument();
			sentenceMap = LoadKeyPhrasesFromSentence();
		} 
		else if(identifier.equals("entities")){
			return LoadEntityFrequency();
		}
		
		if(documentMap == null && sentenceMap == null){
			return null;
		}
		
		if(documentMap == null){
			return sentenceMap;
		} 
		
		if(sentenceMap == null){
			return documentMap;
		}
		
		for(String docKey:documentMap.keySet()){
			Integer docCount = documentMap.get(docKey);
			Integer sentCount = sentenceMap.get(docKey);
			if(sentCount!=null){
				docCount += sentCount;
			}
			
			documentMap.put(docKey, docCount);
		}
		
		return sortHashMap(documentMap);
		
	}

	/**
	 * Generates HashMap that links every keyword found in the Document to its frequency.
	 *
	 * 
	 * @return HashMap representation of all of the keywords and each of their frequencies within the Document
	 */
	public HashMap<String, Integer> LoadKeyWordsFromDocument() {
		if (doc == null) {
			return null;
		}

		ArrayList<String> masterList = new ArrayList<String>();
		for (Sentence text : doc.sentences()) {
			List<String> words = text.words();
			List<String> posTags = text.posTags();
			int index = 0;
			for (String p : posTags) {
				if (p.equals("NN") || p.equals("NNS") || p.equals("NNP") || p.equals("NNPS")) {
					masterList.add(words.get(index));
				}
				index++;
			}

		}
		return GenerateHashMaps(masterList);

	}
	
	/**
	 * Generates HashMap that links every keyword found in the Sentence to its frequency.
	 *
	 * 
	 * @return HashMap representation of all of the keywords and each of their frequencies within the Sentence
	 */
	public HashMap<String, Integer> LoadKeyWordsFromSentence() {
		if(sentence == null){
			return null;
		}
		ArrayList<String> masterList = new ArrayList<String>();
		List<String> words = sentence.words();
		List<String> posTags = sentence.posTags();
		int index = 0;
		for (String p : posTags) {
			if (p.equals("NN") || p.equals("NNS") || p.equals("NNP") || p.equals("NNPS")) {
				masterList.add(words.get(index));
			}
			index++;
		}			
		
		return GenerateHashMaps(masterList);

	}
	
	/**
	 * Generates HashMap that links every keyphrase found in the Document to its frequency.
	 *
	 * 
	 * @return HashMap representation of all of the keyphrases and each of their frequencies within the Document
	 */
	
	public HashMap<String, Integer> LoadKeyPhrasesFromDocument() {
		if(doc == null){
			return null;
		}
		ArrayList<String> masterList = new ArrayList<String>();
		
		for(Sentence text:doc.sentences()){
			SentenceAlgorithms helpers = text.algorithms();
			masterList.addAll(helpers.keyphrases());
			
		}
		
		return GenerateHashMaps(masterList);
	}
	
	/**
	 * Generates HashMap that links every keyphrase found in the Sentence to its frequency.
	 *
	 * 
	 * @return HashMap representation of all of the keyphrases and each of their frequencies within the Sentence
	 */
	public HashMap<String, Integer> LoadKeyPhrasesFromSentence() {
		if(sentence == null){
			return null;
		}
		SentenceAlgorithms helpers = sentence.algorithms();
		List<String> masterList = helpers.keyphrases();
			
		
		return GenerateHashMaps(masterList);
	
	}

	/**
	 * Generates HashMap that links every entity type found in the Document to all of the examples of that entity found in the Document.
	 *
	 * 
	 * @return HashMap representation of all entities to all of the words in the Document that fell under that Entity
	 */
	public HashMap<String, List<String>> LoadEntitiesFromDocument() {
		if(doc == null){
			return null;
		}
		HashMap<String, List<String>> mapping = new HashMap<String, List<String>>();

		for (Sentence text : doc.sentences()) {
			List<String> EntityTags = text.nerTags();
			for (String tag : EntityTags) {
				if (!tag.equals("O")) {
					List<String> mentions = text.mentions(tag);
					if (tag.equals("NUMBER")) {
						mentions.removeAll(specialCharacters);
					}

					List<String> currentlyStored = mapping.get(tag);
					if (currentlyStored == null) {
						mapping.put(tag, mentions);
					} else {
						currentlyStored.addAll(mentions);
						mapping.put(tag, currentlyStored);
					}
				}
			}
		}

		return mapping;
	}
	
	/**
	 * Generates HashMap that links every entity type found in the Sentence to all of the examples of that entity found in the Sentence.
	 *
	 * 
	 * @return HashMap representation of all entities to all of the words in the Sentence that fell under that Entity
	 */
	public HashMap<String, List<String>> LoadEntitiesFromSentence() {
		if (doc == null) {
			return null;
		}
		HashMap<String, List<String>> mapping = new HashMap<String, List<String>>();

		List<String> EntityTags = sentence.nerTags();
		for (String tag : EntityTags) {
			if (!tag.equals("O")) {
				List<String> obj = sentence.mentions(tag);
				if (tag.equals("NUMBER")) {
					obj.removeAll(specialCharacters);
				}
				mapping.put(tag, obj);
			}
		}

		return mapping;
	}
	
	/**
	 * Merges two Entity HashMaps into one.
	 *
	 * @param s  HashMap representing entities found in the Sentence
	 * @param d  HashMap representing entities found in the Document
	 * @return HashMap representation of every entity and the words tagged to that entity, pulled from Sentence and Document. Note that this does NOT return a frequency HashMap
	 */
	private HashMap<String,List<String>> MergeEntities(HashMap<String, List<String>> s, HashMap<String, List<String>> d){
		if(s==null && d == null){
			return null;
		}
		
		if(s == null){
			return d;
		} else if (d == null){
			return s;
		}
		
		for(String entityType : s.keySet()){
			List<String> entities = s.get(entityType);
			List<String> docEntities = d.get(entityType);
			if(docEntities != null){
				entities.addAll(docEntities);
			}
			
			s.put(entityType, entities);
		}
		
		return s;
	}

	/**
	 * Merges two Entity HashMaps into one frequency HashMap.
	 *
	 * @return A HashMap mapping all of the words in both the Document and Sentence tagged as an Entity to their occurences
	 */
	public HashMap<String, Integer> LoadEntityFrequency() {
		if(doc == null){
			return null;
		}
		
		HashMap<String, List<String>> d = LoadEntitiesFromDocument();
		HashMap<String, List<String>> s = LoadEntitiesFromSentence();
		
		HashMap<String, List<String>> entities = MergeEntities(d,s);
		HashMap<String, Integer> frequency = new HashMap<String, Integer>();

		for (String entityType : entities.keySet()) {
			List<String> contents = entities.get(entityType);
			for (String element : contents) {
				Integer count = frequency.get(element);
				if (count == null) {
					frequency.put(element, 1);
				} else {
					count = count + 1;
					frequency.put(element, count);
				}
			}
		}

		return sortHashMap(frequency);
	}
	
	/**
	 * Generates HashMap that links every keyword found in the Document to its frequency.
	 *
	 * @param freq Takes in a HashMap mapping Entity to examples of each entity in the source 
	 * @return A frequency HashMap
	 */
	public HashMap<String, Integer> LoadEntityFrequency(HashMap<String,List<String>> freq){
		HashMap<String, Integer> mapping = new HashMap<String,Integer>();
		
		for (String entityType : freq.keySet()) {
			List<String> contents = freq.get(entityType);
			for (String element : contents) {
				Integer count = mapping.get(element);
				if (count == null) {
					mapping.put(element, 1);
				} else {
					count = count + 1;
					mapping.put(element, count);
				}
			}
		}
		
		return sortHashMap(mapping);
	}
	
	/**
	 * Finds all keywords that have a corresponding keyphrase and maps that keyphrase to how many times it came up
	 *
	 * @param keywords Frequency HashMap of keywords
	 * @param keyphrases Frequency HashMap of keywords
	 * @return Frequency HashMap mapping "verified" keyphrases to their commodity
	 */
	public HashMap<String, Integer> FilterKeyWords(HashMap<String,Integer> keywords, HashMap<String,Integer> keyphrases){
		HashMap<String, Integer> matchedWords = new HashMap<String, Integer>();
		
		for (String word : keywords.keySet()) {
			for (String str : keyphrases.keySet()) {
				if (str.contains(word)) {
					if(str.contains(".") && !(str.charAt(str.length() - 1) == '.')){
						continue;
					}
					Integer prevCount = matchedWords.get(str);
					if(prevCount == null){
						matchedWords.put(str, keywords.get(word) + keyphrases.get(str));
					} else {
						prevCount += prevCount;
						matchedWords.put(str, prevCount);
					}
				}
			}
		}
		
		return sortHashMap(matchedWords);
	}
	
	/**
	 * Finds all keywords that have a corresponding keyphrase and maps that keyphrase to how many times it came up. Automates the computation of the keywords and keyphrases frequency
	 * HashMaps
	 *
	 * @return Frequency HashMap mapping "verified" keyphrases to their commodity
	 */
	public HashMap<String, Integer> FilterKeyWords() {
		HashMap<String, Integer> keywords = MergeMaps("keywords");
		HashMap<String, Integer> keyphrases = MergeMaps("keyphrases");

		HashMap<String, Integer> matchedWords = new HashMap<String, Integer>();

		for (String word : keywords.keySet()) {
			for (String str : keyphrases.keySet()) {
				if (str.contains(word)) {
					if(str.contains(".") && !(str.charAt(str.length() - 1) == '.')){
						continue;
					}
					Integer prevCount = matchedWords.get(str);
					if(prevCount == null){
						matchedWords.put(str, keywords.get(word) + keyphrases.get(str));
					} else {
						prevCount += prevCount;
						matchedWords.put(str, prevCount);
					}
				}
			}
		}
		
		return sortHashMap(matchedWords);

	}
	
	/**
	 * Sorts a HashMap by value	
	 *
	 * @param map Map to be sorted
	 * @return HashMap that is now sorted in descending order by values 
	 */
	private <K, V extends Comparable<? super V>> HashMap<K, V> sortHashMap(Map<K, V> map) {
	    return map.entrySet()
	              .stream()
	              .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
	              .collect(Collectors.toMap(
	                Map.Entry::getKey, 
	                Map.Entry::getValue, 
	                (e1, e2) -> e1, 
	                LinkedHashMap::new
	              ));
	}

	/**
	 * Simply takes a HashMap linking some String key to its Frequency and presents it as a String
	 *
	 * @param mapping The HashMap to get printed out
	 * @return String 
	 */
	public String HashMapToString(HashMap<String,Integer> mapping){
		StringBuilder sb = new StringBuilder();
		for (String s : mapping.keySet()) {
			sb.append(s);
			sb.append("->");
			sb.append(mapping.get(s));
			sb.append("\n");

		}
		
		return sb.toString();
	}
	
	/**
	 * Simply takes a HashMap linking some String key to its Frequency and prints it out
	 *
	 * @param source The HashMap to get printed out
	 */
	public void PrintHashMapAndCount(HashMap<String, Integer> source) {
		System.out.println(this.HashMapToString(source));
	}
	
	/**
	 * Generates a JSON string generated from all the data we have generated
	 *
	 * @return a String
	 */

	public String GenerateGson() {
		//HashMap<String, Integer> keyphrases = FilterKeyWords();
		HashMap<String, Integer> entities = MergeMaps("entities");

		HashMap<String, List<String>> rawEntities = MergeEntities(LoadEntitiesFromDocument(),
				LoadEntitiesFromSentence());

		for (String str : rawEntities.keySet()) {
			List<String> list = rawEntities.get(str);
			HashSet<String> set = new HashSet<String>(list);
			list.clear();
			list.addAll(set);
			Collections.sort(list, new Comparator<String>() {
				public int compare(String a, String b) {
					return entities.get(b) - entities.get(a);
				}
			});
			rawEntities.put(str, list);
		}
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("Entities", rawEntities);
		//map.put("Keywords", keyphrases);

		Gson json = new Gson();

		return json.toJson(map);
	}
	
	/**
	 *  Creates the dependency graph for the sentence
	 *
	 * @return Dependency graph of the sentence
	 */
	public SemanticGraph generateDependencyGraph(){
		return sentence.dependencyGraph();
	}

}
