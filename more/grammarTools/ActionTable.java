package grammarTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ActionTable {
	protected Grammar grammar;
	protected HashMap<Variable, Token> matrix;
	
	protected HashMap<Token, HashSet<Token>> first;
	protected HashMap<Token, HashSet<Token>> follow;
	
	public ActionTable(Grammar grammar){
		this.grammar = grammar;
	}
	
	public void build(){
		
		this.generateFirst();
		this.generateFollow();
		
		//Lets browse the rules lists
		
		for(int i=0; i<grammar.getProductionRules().size();++i){
			
			//ProductionRule rule = grammar.getProductionRules().get(i);
			
		}
		
	}
		
	public void generateFirst(){
		
		// Method slide 236 - 237
		
		/*
		ArrayList<ArrayList<Token>> first = new ArrayList<ArrayList<Token>>();
		
		if(grammar.getVariables().contains(token) || grammar.getTerminals().contains(token)){
			for(ProductionRule rule : grammar.getProductionRule((Variable) token)){
				if(rule.getRightSide().size() > k){
					ArrayList<Token> tokenList = new ArrayList<Token>();
					int i = 0;
					do{
						tokenList.add(rule.getRightSide().get(i));
						++i;
					}while(grammar.isTerminal(rule.getRightSide().get(i)) && i<k);
					
					if(tokenList.size() == k){
						first.add(tokenList);
					}
				}			
				
			}
		}
		
		return first;
		*/
		
		// Method slide 239
		
		// We are in LL(1) so we doesn't need to use K
		
		// Base :
		first = new HashMap<Token, HashSet<Token>>();
		
		//Firstk(a) = {a}
		for(Terminal a : grammar.getTerminals()){
			HashSet<Token> tmp = new HashSet<Token>();
			tmp.add(a);
			
			first.put(a, tmp);
		}
		
		//Firstk(A) = {} or 0
		for(Variable A : grammar.getVariables()){			
			first.put(A, new HashSet<Token>());
		}
		
		//Induction : loop until stabilisation (no more changes)
		boolean isStabilized;
		
		do{
			isStabilized = false;
			for(Variable A : grammar.getVariables()){
				for(ProductionRule rule : grammar.getProductionRule(A)){
					//First(A) U x | x belongs T*
					//A -> Y1 Y2 ... Yn ~~ rule
					//x belongs First(Y1) + First(Y2) + ... + First(Yn)
					
					for(Token rightSideToken : rule.getRightSide()){
						if(!first.get(A).contains(rightSideToken)){
							if(grammar.isVariable(rightSideToken)){
								break;
							}else{
								first.get(A).addAll(first.get(rightSideToken));
								isStabilized = true;
							}
						}
						
					}
					
				}
			}
		}while(!isStabilized);
		
	}
	
	public void generateFollow(){
		// Base :
		follow = new HashMap<Token, HashSet<Token>>();
		
		//Followk(A) = {} or 0
		for(Variable A : grammar.getVariables()){			
			follow.put(A, new HashSet<Token>());
		}
		
		//Induction : loop until stabilisation (no more changes)
		boolean isStabilized;
		
		do{
			isStabilized = false;
			for(Variable A : grammar.getVariables()){
				for(ProductionRule rule : grammar.getProductionRule(A)){
					for(int i = rule.getRightSide().size(); i > 0; --i){
						Token rightSideToken = rule.getRightSide().get(i);
						if(!follow.get(A).contains(rightSideToken)){
							if(grammar.isVariable(rightSideToken)){
								follow.get(A).addAll(first.get(rightSideToken));
								isStabilized = true;
								break;
							}else{
								follow.get(A).addAll(follow.get(rightSideToken));
								isStabilized = true;
							}
						}
					}
				}
			}
		}while(!isStabilized);
		
	
	}
}
