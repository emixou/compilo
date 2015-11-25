package grammarTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ActionTable {
	protected Grammar grammar;
	protected HashMap<Variable, Token> matrix;
	
	protected HashMap<String, HashSet<Token>> first;
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
		
		// Method slide 239
		
		// We are in LL(1) so we doesn't need to use K
		
		// Base :
		first = new HashMap<String, HashSet<Token>>();
		
		//Firstk(a) = {a}
		for(Terminal a : grammar.getTerminals()){
			HashSet<Token> tmp = new HashSet<Token>();
			tmp.add(a);
			
			first.put(a.getValue(), tmp);
		}
		
		//Firstk(A) = {} or 0
		for(Variable A : grammar.getVariables()){			
			first.put(A.getValue(), new HashSet<Token>());
		}
		
		//Induction : loop until stabilisation (no more changes)
		boolean isStabilized;
		
		do{
			isStabilized = true;
			for(ProductionRule aRule : grammar.getProductionRules()){
				if (isUsable(aRule)) {
					Token firstToken = aRule.getRightSide().get(0);
					for (Token aToken : first.get(firstToken.getValue())) {
						if (!first.get(aRule.getLeftSide().getValue()).contains(aToken)) {
							first.get(aRule.getLeftSide().getValue()).add(aToken);
							isStabilized = false;
						}
					}
				}	
			}
		}while(!isStabilized);
		
		for (Variable var : grammar.getVariables()) {
			HashSet<Token> tkSet = first.get(var.getValue());
			System.out.print(var.getValue()+" :");
			for (Token tk : tkSet) {
				System.out.print(tk.getValue());
			}
			System.out.println();
		}
		
		for (Terminal var : grammar.getTerminals()) {
			HashSet<Token> tkSet = first.get(var.getValue());
			System.out.print(var.getValue()+" :");
			for (Token tk : tkSet) {
				System.out.print(tk.getValue());
			}
			System.out.println();
		}
		
	}
	
	private boolean isUsable(ProductionRule aRule) {
		for (Token aToken : aRule.getRightSide()){
			if(first.get(aToken.getValue()).isEmpty()){
				return false;
			}
		}
		return true;
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
