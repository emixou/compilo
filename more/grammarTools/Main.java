package grammarTools;

import java.io.IOException;
import java.util.regex.PatternSyntaxException;

import parser.Parser;
import scanner.SyntaxErrorException;

public class Main {

	public static void main(String[] args) throws PatternSyntaxException, IOException, SyntaxErrorException {
		if (args.length != 1) {
            System.out.println("L'utilisation est : java -jar part2.jar SuprAlgolFile");
        } else {
			Parser p = new Parser("supralgol-v2.grammar", new java.io.FileReader(args[0]));
			p.parse();
        }
		
	}

}
