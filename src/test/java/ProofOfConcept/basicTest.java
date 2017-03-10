package ProofOfConcept;

import K360NLP.Parser;

public class basicTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Parser parser = new Parser("http://www.cnn.com/2017/01/26/politics/donald-trump-mexico-import-tax-border-wall");
		System.out.println(parser.GetTitle());
		System.out.println(parser.GetContent());
	}

	
}
