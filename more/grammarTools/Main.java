package grammarTools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.PatternSyntaxException;

import grammarAlgorithms.LeftRecursive;
import parser.Parser;
import scanner.SyntaxErrorException;

public class Main {

	public static void main(String[] args) throws PatternSyntaxException, IOException, SyntaxErrorException {
		

		//Parser p = new Parser("more/grammar/supralgol-v2.grammar", new java.io.FileReader("test/Operation.alg"));
		Parser p = new Parser("more/grammar/supralgol-v2.grammar", new java.io.FileReader("test/Euclide.alg"));
		//Parser p = new Parser("more/grammar/firstTest.grammar", new java.io.FileReader("test/Euclide.alg"));
		p.parse();
		
	}

}
