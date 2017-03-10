package K360NLP;

import java.util.HashMap;
import java.util.List;

public class NLPDriver {

	public static void main(String[] args) {

		Parser parser = new Parser("http://www.nfl.com/news/story/0ap3000000781590/article/julio-jones-no-one-in-nfl-can-cover-me-oneonone");
		String URLContent = parser.GetContent();
		Content content = new Content("Hello my name is george, and i live in Maryland");

		/*
		This method will generate the GSON which will display entities under entity types and also keywords.

		*/

		content.LoadEntitiesFromDocument();

	}





}
