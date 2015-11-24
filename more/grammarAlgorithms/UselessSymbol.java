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
		removeUnproductiveSymbols();
		removeUnreachableSymbols();
	}
	
	public void removeUnreachableSymbols(){
		ArrayList<Variable> reachableSymbols = new ArrayList<Variable>();
		
		//First grammar symbol is reachable.
		reachableSymbols.add(grammar.getStartSymbol());
		
		for(Variable symbol : reachableSymbols){
			ProductionRule rule = grammar.getProductionRule(symbol);
			for(Token rightSideToken : rule.getRightSide()){
				//if(grammar.isTerminal(rightSideToken)){
					//If it's a terminal, we can reach it
				reachableSymbols.add((Variable) rightSideToken);
				//}
			}
		}
		
		for(ProductionRule rule : grammar.getProductionRules()){
			if(!reachableSymbols.contains(rule.getLeftSide())){
				grammar.getProductionRules().remove(rule);
			}else{
				for(Token rightSideToken : rule.getRightSide()){
					//if(grammar.isTerminal(rightSideToken)){
					if(!reachableSymbols.contains(rightSideToken)){
						grammar.getProductionRules().remove(rule);
					}
					//}
				}
			}
		}
	}
	
	public void removeUnproductiveSymbols(){
		ArrayList<Token> productiveSymbols = new ArrayList<Token>();
		
		productiveSymbols.addAll((ArrayList<? extends Token>) grammar.getTerminals());
		
		int i=0;
		do{
			ProductionRule rule = grammar.getProductionRule(productiveSymbols.get(i));
			
			++i;
		}while(i < productiveSymbols.size());
		
	}

}
