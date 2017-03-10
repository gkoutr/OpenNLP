package UnitTests;

import org.junit.Test;

import K360NLP.Content;
import K360NLP.Parser;
import org.junit.Assert;


public class ParserTest {
	Parser parser;
	
	@Test
	public void TestNull(){
		// This is a broken link
		parser = new Parser("http://ww.cnn.com/2017/01/26/politics/donald-trump-mexico-import-tax-border");
		Assert.assertEquals("", parser.GetContent());
		Assert.assertEquals("", parser.GetTitle());
	}
	
	@Test
	public void TestWithWorkingLink(){
		parser = new Parser("http://www.cnn.com/2017/01/26/politics/donald-trump-mexico-import-tax-border-wall");
		Assert.assertNotEquals(parser.GetTitle(), "");
		Assert.assertNotEquals(parser.GetContent(), "");
	}
	
	@Test
	public void MakeContent(){
		parser = new Parser("http://www.cnn.com/2017/01/26/politics/donald-trump-mexico-import-tax-border-wall");
		StringBuilder sb = new StringBuilder();
		sb.append(parser.GetTitle());
		sb.append(". ");
		sb.append(parser.GetContent());
		Content content = new Content(sb.toString());
		
		Assert.assertNotNull(content.GetRawText());
	}

}
