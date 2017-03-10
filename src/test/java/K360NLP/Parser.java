package K360NLP;

import java.net.URL;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;

public class Parser {
	URL url;
	TextDocument website;

	public Parser(String address) {
		try {
			url = new URL(address);
			HTMLDocument htmlDoc = HTMLFetcher.fetch(url);
			website = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			url = null;
			website = null;
		}

	}

	public String GetTitle() {
		if (url == null || website == null) {
			return "";
		}

		return website.getTitle();
	}

	public String GetContent() {
		if (url == null || website == null) {
			return "";
		}
		
		try {
			return ArticleExtractor.INSTANCE.getText(website);
		} catch (BoilerpipeProcessingException e) {
			return "";
		}
	}

}
