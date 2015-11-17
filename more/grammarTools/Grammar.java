package grammarTools;

import java.util.ArrayList;

public class Grammar {

	protected ArrayList<Terminal> terminals;
	protected ArrayList<Variable> variables; //Intermediary Symbol
	protected ArrayList<ProductionRule> productionRules;
	protected Variable startSymbol;
}
