package grammarAlgorithms;

import java.util.ArrayList;

import grammarTools.Grammar;
import grammarTools.ProductionRule;
import grammarTools.Token;
import grammarTools.Variable;

public class UselessSymbol implements Algorithm{
	
	private Grammar grammar;

	public UselessSymbol(Grammar grammar){
		this.grammar = grammar;
	}
	
	@Override
	public void execute() {
		
	}
	
	public void removeUnreachableSymbol(){
		ArrayList<Variable> reachableSymbols = new ArrayList<Variable>();
		
		//First grammar symbol is reachable.
		reachableSymbols.add(grammar.getStartSymbol());
		
		for(Variable symbol : reachableSymbols){
			ProductionRule rule = grammar.getProductionRule(symbol);
			for(Token rightSideToken : rule.getRightSide()){
				if(grammar.isTerminal(rightSideToken)){
					//If it's a terminal, we can reach it
				}
			}
		}
	}
	
	public void removeUnproductiveSymbol(){
		
	}

}
