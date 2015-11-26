package grammarTools;

import grammarAlgorithms.LeftRecursive;

public class Main {

	public static void main(String[] args) {

		//Grammar aGrammar = new Grammar("more/grammar/supralgol-v0.grammar");
		Grammar aGrammar = new Grammar("more/grammar/firstTest.grammar");
		
		//LeftRecursive lr = new LeftRecursive(aGrammar);
		//lr.execute();
		ActionTable at = new ActionTable(aGrammar);
		at.build();
		
		System.out.println(at);
		
		//aGrammar.repr();
	}

}
