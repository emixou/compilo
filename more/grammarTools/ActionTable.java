package grammarTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

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
		//this.printFirst();
		//this.generateFollow();
		//this.printFollow();
		//Lets browse the rules lists
		
		/*for(int i=0; i<grammar.getProductionRules().size();++i){
			
			//ProductionRule rule = grammar.getProductionRules().get(i);
			
		}*/
		
	}
	
	public void printFirst(){
		for (Variable var : grammar.getVariables()) {
			HashSet<Token> tkSet = first.get(var.getValue());
			System.out.print(var.getValue()+" : ");
			for (Token tk : tkSet) {
				System.out.print(tk.getValue()+" , ");
			}
			System.out.println();
		}
	}
	
	public void printFollow(){
		for (Token name: follow.keySet()){

            String key = name.getValue();
            String value = follow.get(name).toString();  
            
            System.out.print(key +" : {");  
            for(Token token : follow.get(name)){
            	System.out.print(token.getValue()+" , ");
            }
            System.out.println("}");
		}
	}
		

	public void generateFirst(){
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
		
		int i = 0;
		
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
			System.out.println("============== STEP "+i+" ===============");
			this.printFirst();
			System.out.println("=====================================\n");
			++i;
			
		}while(!isStabilized);
		
		this.printFirst();
	}
	
	private boolean isUsable(ProductionRule aRule) {
		for (Token aToken : aRule.getRightSide()){
			if(first.get(aToken.getValue()).isEmpty()){
				return false;
			}
		}
		return true;
	}

	private void generateFollow(){
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
			
		}while(isStabilized);
		
	
	}
}
