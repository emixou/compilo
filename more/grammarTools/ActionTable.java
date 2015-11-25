package grammarTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

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
		//this.printFirst();
		//this.generateFollow();
		//this.printFollow();
		//Lets browse the rules lists
		
		/*for(int i=0; i<grammar.getProductionRules().size();++i){
			
			//ProductionRule rule = grammar.getProductionRules().get(i);
			
		}*/
		
	}
	
	public void printFirst(){
		for (Token name: first.keySet()){

            String key = name.getValue();
            String value = first.get(name).toString();  
            
            if(grammar.isVariable(name)){
	            System.out.print("First("+ key +") : { ");  
	            for(Token token : first.get(name)){
	            	
	            	System.out.print(token.getValue()+" , ");
	            }
	            System.out.println("}");
            }
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
	
	private ArrayList<Token> getFirstTerminals(ArrayList<Token> tokenList){
		ArrayList<Token> list = new ArrayList<Token>();
		
		for(Token token : tokenList){	
			if(grammar.isVariable(token)){	
				
				list.add(first.get(token).iterator().next());
				
				return list;
			}else{
				list.add(token);
			}
		}
		
		return list;
	}
	
	private boolean containsEmpty(ArrayList<Token> rightSide) {
		
		for(Token token : rightSide){
			if(first.get(token).isEmpty()){
				return true;
			}
		}
		return false;
		
	}
		
	private void generateFirst(){
		
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
		
		int i = 1;
		
		do{
			isStabilized = false;
			
			System.out.println("Step " + i +" : ");
			for(Variable A : grammar.getVariables()){
				
				for(ProductionRule rule : grammar.getProductionRule(A)){
					//First(A) U x | x belongs T*
					//A -> Y1 Y2 ... Yn ~~ rule
					//x belongs First(Y1) + First(Y2) + ... + First(Yn)
					
					if(!containsEmpty(rule.getRightSide())){
						
						for(Token rightSideToken : this.getFirstTerminals(rule.getRightSide()) ){	
							
							if(!first.get(A).contains(rightSideToken)){
								System.out.println("First("+A.getValue() + ") <= {" + rightSideToken.getValue() +"}");

								//first.get(A).addAll(first.get(rightSideToken));
								first.get(A).add(rightSideToken);
								isStabilized = true;
								break;
							
							}	
							
						}
						
					}
						
					
				}
				
			}
			
			System.out.println("==================================\n");
			System.out.println(isStabilized);
			this.printFirst();
			System.out.println("==================================\n");
			++i;
		}while(isStabilized);
		
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
