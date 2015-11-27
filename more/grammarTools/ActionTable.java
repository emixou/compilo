package grammarTools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import scanner.Symbol;


public class ActionTable {
	protected Grammar grammar;
	protected HashMap<Variable, HashMap<Terminal, ProductionRule>> matrix;
	
	protected Terminal epsilon;
	
	protected HashMap<String, HashSet<Token>> first;
	protected HashMap<String, HashSet<Token>> follow;
	
	public ActionTable(Grammar grammar){
		this.grammar = grammar;
		this.matrix = new HashMap<Variable, HashMap<Terminal, ProductionRule>>();
		this.epsilon = new Terminal("eps");
	}
	
	public ProductionRule getRule(Token aVariable, Token aTerminal) {
		return matrix.get(aVariable).get(aTerminal);
	}
	
	public void setRule(Token aVariable, Token aTerminal, ProductionRule rule){
		matrix.get(aVariable).put((Terminal) aTerminal, rule);
	}
	
	public void build(){
		
		this.generateFirst();
		//this.printFirst();
		System.out.println("Generate Follow");
		this.generateFollow();
		//this.printFollow();
		
		//Lets browse the rules lists
		this.initiliazeTable();
		System.out.println(toString());
		this.generateActionTable();
		
	}
	
	public void initiliazeTable(){
		for (Variable aVariable : this.grammar.getVariables()) {
            matrix.put(aVariable, new HashMap<Terminal, ProductionRule>());
            for (Terminal terminal : this.grammar.getTerminals()) {
    			if(!terminal.equals(epsilon)){
    				//matrix.get(aVariable).put(terminal, null);
    				setRule(aVariable, terminal, null);
    			}
            }
		}		
	}
	
	public void generateActionTable(){
		for(ProductionRule aRule : grammar.getProductionRules()){
			int i=0;
			
			Variable A = aRule.getLeftSide();
			//a = firstAlpha
			HashSet<Token> firstAlpha = first.get(aRule.getRightSide().get(i).getValue());	
			for(Token a : firstAlpha){
				if(!a.equals(epsilon)){
					setRule(A, a, aRule);
				}
				
			}				
			
			//followA
			if(firstAlpha.contains(epsilon)){
				HashSet<Token> followA = follow.get(A.getValue());	
				//FollowA
					for (Token aToken : followA){
						for(Token anToken : first.get(aToken.getValue())){
							if(aRule.getRightSide().contains(epsilon) && getRule(A, anToken) == null){
								setRule(A, anToken, aRule);
							}
						}	
						
					}
			}
			
			/*
			HashSet<Token> firstAlpha = firstSet(aRule.getRightSide());
			for(Token terminal : firstAlpha){
				System.out.println(terminal.getValue());
				setRule(aRule.getLeftSide(), terminal, aRule);
			}
			
			if(firstAlpha.contains(epsilon)){
				for (Token terminal : follow.get(aRule.getLeftSide().getValue()) ) {
                    // Two production rules for the same M[A,a], the grammar is not LL(1)
    				setRule(aRule.getLeftSide(), terminal, aRule);
                }
			}
			*/
			
		}
	}
	
	/*
	public HashSet<Token> firstSet(ArrayList<Token> tokenList){
		
		HashSet<Token> sum = new HashSet<Token>();
        if (!tokenList.isEmpty()) {
            sum.addAll(first.get(tokenList.get(0).getValue()));
            for (int i = 1; i < tokenList.size(); ++i) {
                sum.addAll(this.addK1(sum, first.get(tokenList.get(i).getValue())));
            }
        }
        return sum;
		
	}
	
	private HashSet<Token> addK1(HashSet<Token> l1, HashSet<Token> l2) {
		HashSet<Token> sumTerminalSetK1 = new HashSet<Token>();

		if (l2.isEmpty()) {
            sumTerminalSetK1.addAll(l1);
        } else if (l1.isEmpty()) {
            sumTerminalSetK1.addAll(l2);
        } else if (!l1.isEmpty() && !l2.isEmpty()) {
            for (Token t1 : l1) {
                for (Token t2 : l2) {                    
                    if (t1.equals(epsilon)) {
                    	sumTerminalSetK1.add(t2);
                    } else {
                    	sumTerminalSetK1.add(t1);
                    }
                }

            }
        }
		
		return sumTerminalSetK1;
	}
	*/
	
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
		for (Variable var : grammar.getVariables()) {
			HashSet<Token> tkSet = follow.get(var.getValue());
			System.out.print(var.getValue()+" : ");
			for (Token tk : tkSet) {
				System.out.print(tk.getValue()+" , ");
			}
			System.out.println();
		}
	}
		

	private void generateFirst(){
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
		follow = new HashMap<String, HashSet<Token>>();
		
		//Followk(A) = {} or 0
		for(Variable A : grammar.getVariables()){			
			follow.put(A.getValue(), new HashSet<Token>());
		}
		
		//Induction : loop until stabilisation (no more changes)
		boolean isStabilized;
		
		int i = 0;
		
		do{
			isStabilized = true;
			
			for(Variable aVariable : grammar.getVariables()){
				for(ProductionRule aRule : grammar.getProductionRule((Token) aVariable)){			
					ArrayList<Token> rightPart = aRule.getRightSide();
						
						int id = aRule.getRightSide().indexOf(aVariable);
						
						HashSet<Token> tokenSetFirst = new HashSet<Token>();
						
						ArrayList<Token> remainingTerminals = new ArrayList<Token>(rightPart.subList(id+1, rightPart.size()));					
						for(Token terminal : remainingTerminals){
							for(Token aToken : first.get(terminal.getValue())){
								//Follow B
								tokenSetFirst.add(aToken);
							}
						}
						
						HashSet<Token> tokenSetFollow = new HashSet<Token>();
						//Follow A
						tokenSetFollow.addAll(follow.get(aRule.getLeftSide().getValue()));
						
						HashSet<Token> finalTokenSet = new HashSet<Token>();
						
						if(tokenSetFollow.isEmpty()){
							finalTokenSet.addAll(tokenSetFirst);
						}else if(tokenSetFirst.isEmpty()){
							finalTokenSet.addAll(tokenSetFollow);
						}else if(!tokenSetFollow.isEmpty() && !tokenSetFirst.isEmpty()){
							for(Token tokenFirst : tokenSetFirst){
								for(Token tokenFollow : tokenSetFollow){
									//Follow will already add first
									if(tokenFirst.equals(epsilon)){
										finalTokenSet.add(tokenFollow);
									}else{
										finalTokenSet.add(tokenFirst);
									}
									
								}
							}
						}
						
						if(finalTokenSet.contains(epsilon)){
							finalTokenSet.remove(epsilon);
						}
						
						for(Token aToken : finalTokenSet){
							if(!follow.get(rightPart.get(id).getValue()).contains(aToken)){
								follow.get(rightPart.get(id).getValue()).add(aToken);
								isStabilized = false;
							}
						}
					
				}
				
			}
				
			System.out.println("============== STEP "+i+" ===============");
			this.printFollow();			
			System.out.println("=====================================\n");
			++i;
			
		}while(!isStabilized);
		
		
		for(Variable aVariable : grammar.getVariables()){
			if(follow.get(aVariable.getValue()).isEmpty()){
				follow.get(aVariable.getValue()).add(epsilon);
			}
		}
		
		System.out.println("============== STEP "+i+" ===============");
		this.printFollow();			
		System.out.println("=====================================\n");
		++i;
			
	}
	
	@Override
    public String toString() {
        String result = "";
        
        for (ProductionRule productionRule : grammar.getProductionRules()) {
            result += productionRule.getRuleNumber() +" : " + productionRule.toString() + "\n";
        }

        result += " \t";
        for (Terminal aTerminal : grammar.getTerminals()) {
        	if(!aTerminal.equals(epsilon)){
        		result += aTerminal.getValue() + "\t";
        	}
        }

        result += "\n";

        for (Variable aVariable : grammar.getVariables()) {
        	
            result += aVariable.getValue() + " \t";
            for (Terminal aTerminal : grammar.getTerminals()) {
            	if(!aTerminal.equals(epsilon)){
	                ProductionRule productionRule = matrix.get(aVariable).get(aTerminal);
	                if (productionRule == null) {
	                    result += "/" + " \t";
	                } else {
	                    result += productionRule.getRuleNumber() + " \t";
	                }
            	}
                
            }
            result += "\n";
            
        }
        return result;
    }
}
