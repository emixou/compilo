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
		
		Boolean hadReachableSymbolsChanged;
		do{
			hadReachableSymbolsChanged = false;
			
			//A belongs to r(G)
			for(Variable variable : reachableSymbols){

				//(A -> a) belongs to P
				for(ProductionRule rule : grammar.getProductionRule(variable)){
					if(rule.getLeftSide().equals(variable)){
						for(Token token : rule.getRightSide()){
							// We only get variable
							// r(G) U {X | E b,c : a = bXc}
							if(grammar.isVariable(token)){
								reachableSymbols.add((Variable) token);
								hadReachableSymbolsChanged = true;
							}
						}
					}
				}
			}
			
		}while(hadReachableSymbolsChanged);
		
	}
	
	public void removeUnproductiveSymbols(){
		ArrayList<Token> productiveSymbols = new ArrayList<Token>();
		
		productiveSymbols.addAll((ArrayList<? extends Token>) grammar.getTerminals());
		
		Boolean hadProductiveSymbolsChanged;
		do{
			hadProductiveSymbolsChanged = false;
			//Token given to get leftside part
			
	
			//a belongs to (g(G))*
			for(Token token : productiveSymbols){

				//(X -> a) belongs to P
				for(ProductionRule rule : grammar.getProductionRule(token)){
					
					for(Token symbols : rule.getRightSide()){
						//g(G) U {X}
						if(!productiveSymbols.contains(token)){
							productiveSymbols.add(token);
							hadProductiveSymbolsChanged = true;
						}
					}
					
				}
			
			}
			
		}while(hadProductiveSymbolsChanged);
		
	}
	
	public void removeSymbols(ArrayList<Token> tokenList){
		
		//Iterate all production rules
		for(ProductionRule rule : grammar.getProductionRules()){
			//If leftpart, delete each rule either rightpart
			if(!tokenList.contains(rule.getLeftSide()) || !tokenList.contains(rule.getRightSide())){
				grammar.getProductionRules().remove(rule);
			}
		}
		
	}

}
