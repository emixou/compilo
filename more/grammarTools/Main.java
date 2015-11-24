package grammarTools;

import grammarAlgorithms.LeftRecursive;

public class Main {

	public static void main(String[] args) {
		Grammar aGrammar = new Grammar("more/grammar/supralgol-v0.grammar");
		
		LeftRecursive lr = new LeftRecursive(aGrammar);
		lr.execute(); 
		
		aGrammar.repr();
	}

}
