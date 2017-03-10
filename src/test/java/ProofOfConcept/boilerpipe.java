package ProofOfConcept;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.SAXException;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;

public class boilerpipe {

	private static TextDocument doc;
	private static HTMLDocument htmlDoc;
	private static String content;
	
	
	public static HTMLDocument CreateHTMLDocument(String url) throws MalformedURLException, IOException{
		final HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(url));
		return htmlDoc;
	}
	
	public static TextDocument CreateTextDocument(String url, HTMLDocument htmlDoc) throws IOException, BoilerpipeProcessingException, SAXException{
		htmlDoc = CreateHTMLDocument(url);
		doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
		return doc;
	}
	
/*	public static String ArticleExtract(String content){
		
	}*/
	
	public static void main(String[]args) throws BoilerpipeProcessingException, SAXException, MalformedURLException, IOException{
			
			String url = "http://www.cnn.com/2017/01/26/politics/donald-trump-mexico-import-tax-border-wall/index.html";
			/*final HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(url));
			final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();*/
			doc = CreateTextDocument(url, htmlDoc);
			String title = doc.getTitle();
		
			String content = ArticleExtractor.INSTANCE.getText(doc);
			System.out.println("Title: " + title);
			System.out.println(content);
/*			final BoilerpipeExtractor extractor = CommonExtractors.KEEP_EVERYTHING_EXTRACTOR;
			System.out.println("");
			System.out.println(extractor.getText(doc));
			BoilerpipeExtractor extract = CommonExtractors.ARTICLE_EXTRACTOR;
			System.out.println("");
			System.out.println(extract.getText(doc));*/
			//final ImageExtractor ie = ImageExtractor.INSTANCE;

			/*
			 * List<Image> images = ie.process(new URL(url), extractor);
			 * 
			 * Collections.sort(images); String image = null; if
			 * (!images.isEmpty()) { image = images.get(0).getSrc(); }
			 * 
			 * return new Content(title, content.substring(0, 200), image);
			 */
		
	}
}