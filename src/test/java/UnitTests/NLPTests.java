package UnitTests;

import java.util.*;

import org.junit.Assert;
import org.junit.Test;
import K360NLP.Content;

public class NLPTests {
	@Test
	public void TestNullCase() {
		Content test = new Content();

		Assert.assertNull(test.LoadEntitiesFromDocument());
		Assert.assertNull(test.LoadEntitiesFromSentence());
		Assert.assertNull(test.LoadEntityFrequency());
		Assert.assertNull(test.LoadKeyPhrasesFromDocument());
		Assert.assertNull(test.LoadKeyPhrasesFromSentence());
		Assert.assertNull(test.MergeMaps("keyphrases"));
		Assert.assertNull(test.MergeMaps("keywords"));
		Assert.assertNull(test.MergeMaps("entities"));
		Assert.assertNull(test.LoadKeyWordsFromDocument());
		Assert.assertNull(test.LoadKeyWordsFromSentence());

	}

	@Test
	public void TestEmptyCase() {
		Content test = new Content("");
		Assert.assertNull(test.LoadEntitiesFromDocument());
		Assert.assertNull(test.LoadEntitiesFromSentence());
		Assert.assertNull(test.LoadEntityFrequency());
		Assert.assertNull(test.LoadKeyPhrasesFromDocument());
		Assert.assertNull(test.LoadKeyPhrasesFromSentence());
		Assert.assertNull(test.MergeMaps("keyphrases"));
		Assert.assertNull(test.MergeMaps("keywords"));
		Assert.assertNull(test.MergeMaps("entities"));
		Assert.assertNull(test.LoadKeyWordsFromDocument());
		Assert.assertNull(test.LoadKeyWordsFromSentence());
	}

	@Test
	public void TestEntitiesMerging() {
		String fileName = "input.txt";
		Content test = new Content();
		test.SetFields(fileName);

		HashMap<String, List<String>> s = test.LoadEntitiesFromSentence();
		HashMap<String, List<String>> d = test.LoadEntitiesFromDocument();

		HashMap<String, Integer> sCount = test.LoadEntityFrequency(s);
		HashMap<String, Integer> dCount = test.LoadEntityFrequency(d);
		HashMap<String, Integer> compiled = test.LoadEntityFrequency();

		for (String key : compiled.keySet()) {
			Integer expectedTotal = compiled.get(key);
			Integer sTotal = sCount.get(key);
			Integer dTotal = dCount.get(key);
			if (dTotal == null) {
				dTotal = 0;
			}
			if (sTotal == null) {
				sTotal = 0;
			}

			Integer actualTotal = sTotal + dTotal;
			Assert.assertEquals(expectedTotal, actualTotal);
		}
	}
	
	@Test
	public void TestKeyPhrasesMerging() {
		String fileName = "input.txt";
		Content test = new Content();
		test.SetFields(fileName);

		HashMap<String, Integer> sCount = test.LoadKeyPhrasesFromSentence();
		HashMap<String, Integer> dCount = test.LoadKeyPhrasesFromDocument();
		HashMap<String, Integer> compiled = test.MergeMaps("keyphrases");

		for (String key : compiled.keySet()) {
			Integer expectedTotal = compiled.get(key);
			Integer sTotal = sCount.get(key);
			Integer dTotal = dCount.get(key);
			if (dTotal == null) {
				dTotal = 0;
			}
			if (sTotal == null) {
				sTotal = 0;
			}

			Integer actualTotal = sTotal + dTotal;
			Assert.assertEquals(expectedTotal, actualTotal);
		}
	}
	
	@Test
	public void TestKeyWordsMerging() {
		String fileName = "input.txt";
		Content test = new Content();
		test.SetFields(fileName);

		HashMap<String, Integer> sCount = test.LoadKeyWordsFromDocument();
		HashMap<String, Integer> dCount = test.LoadKeyWordsFromSentence();
		HashMap<String, Integer> compiled = test.MergeMaps("keywords");

		for (String key : compiled.keySet()) {
			Integer expectedTotal = compiled.get(key);
			Integer sTotal = sCount.get(key);
			Integer dTotal = dCount.get(key);
			if (dTotal == null) {
				dTotal = 0;
			}
			if (sTotal == null) {
				sTotal = 0;
			}

			Integer actualTotal = sTotal + dTotal;
			Assert.assertEquals(expectedTotal, actualTotal);
		}
	}
	
}
