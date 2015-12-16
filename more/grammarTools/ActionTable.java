package grammarTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
		this.generateFollow();
		
		//Lets browse the rules lists
		this.initiliazeTable();
		this.generateActionTable();
		
	}
	
	public void initiliazeTable(){
		for (Variable aVariable : this.grammar.getVariables()) {
            matrix.put(aVariable, new HashMap<Terminal, ProductionRule>());
            for (Terminal terminal : this.grammar.getTerminals()) {
    			if(!terminal.equals(epsilon)){
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
		}
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
		
		//int i = 0;
		
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
			/*System.out.println("============== STEP "+i+" ===============");
			this.printFirst();
			System.out.println("=====================================\n");
			++i;*/
			
		}while(!isStabilized);
		
		//this.printFirst();
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
		
		//int i = 0;
		
		do{
			isStabilized = true;
			
			for (ProductionRule aRule : grammar.getProductionRules()) {
				for (int j=0; j<aRule.getRightSide().size()-1; ++j) {
					Token aToken = aRule.getRightSide().get(j);
					if (grammar.isVariable(aToken)) {
						Token otherToken = aRule.getRightSide().get(j+1);
						for (Token addedToken : first.get(otherToken)) {
							if (!addedToken.equals("eps") && !follow.get(aToken).contains(addedToken)) {
								follow.get(aToken).add(addedToken);
								isStabilized = false;
							}
						}
					}
				}
				
				for (Token aToken : aRule.getRightSide()) {
					if (grammar.isVariable(aToken)) {
						for (Token addedToken : follow.get(aRule.getLeftSide())) {
							if (!addedToken.equals("eps") && !follow.get(aToken).contains(addedToken)) {
								follow.get(aToken).add(addedToken);
								isStabilized = false;
							}
						}
					}
				}
			}
				
			/*System.out.println("============== STEP "+i+" ===============");
			this.printFollow();			
			System.out.println("=====================================\n");
			++i;*/
			
		}while(!isStabilized);
		
		follow.get(new Variable("<Program>")).add(new Terminal("eps"));
		
		/*System.out.println("============== STEP "+i+" ===============");
		this.printFollow();			
		System.out.println("=====================================\n");
		++i;*/
			
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

    public String toLatex() {
    	/*
    	 *  Packages required : 
    	 *  \\usepackage{pdflscape}
		 *  \\usepackage[a4paper]{geometry}
		 *  
    	 */
    	
        String result = "";
        
        for (ProductionRule productionRule : grammar.getProductionRules()) {
            result += productionRule.getRuleNumber() +" : " + productionRule.toString() + "\n";
        }
        

        result += "\\newgeometry{left=3cm,bottom=0.4cm}\n";        
        result += "\\begin{center}\n";
        result += "\t\\begin{landscape}\n";
        result += "\t\t\\begin{tabular}{|l";
        
        for(int i=0; i<grammar.getTerminals().size(); ++i){
        	result += "|c";
        }
        
        result += "|}\n";
        result += "\t\t\t\\hline\n\t\t\t &";
       
        for (Terminal aTerminal : grammar.getTerminals()) {
        	if(!aTerminal.equals(epsilon)){
        		result += "\\parbox[t]{2mm}{\\rotatebox[origin=c]{90}{\\textbf{$"+aTerminal.getValue()+"$}}} &";
        	}
        }
        
        result = result.substring(0, result.length()-2);

        result += "\\\\ \t\t\t\\hline\n";

        for (Variable aVariable : grammar.getVariables()) {
        	
            result += "\t\t\t \\textbf{" + aVariable.getValue().replace("<", "").replace(">", "") + "} &";
            for (Terminal aTerminal : grammar.getTerminals()) {
            	if(!aTerminal.equals(epsilon)){
	                ProductionRule productionRule = matrix.get(aVariable).get(aTerminal);
	                if (productionRule == null) {
	                    result += "-" + " & ";
	                } else {
	                    result += productionRule.getRuleNumber() + " & ";
	                }
            	}
                
            }
            
            result = result.substring(0, result.length()-2);
            result += "\\\\ \\hline\n";
            
        }

        result += "\t\t\\end{tabular}\n";
        result += "\t\\end{landscape}\n";
        result += "\\end{center}\n";
        result += "\\restoregeometry";
        
        return result;
    }
    
	
	public String firstFollowToLatex(){
		String result = "";
		
		result += "\\begin{center}\n";
        result += "\t\\begin{tabular}{|l|c|c|}\n";
        result += "\t\t\\hline\n\t\t\t &";
        result += "\t\t  \\TitleParBox{\\textbf{First}} & \\TitleParBox{\\textbf{Follow}} \\\\ \\hline\n";
				
		for (Variable var : grammar.getVariables()) {
			result += "\t\t\t \\textbf{" + var.getValue().replace("<", "").replace(">", "") + "} & ";
			HashSet<Token> tkSet = first.get(var.getValue());
			result += "\\ContentParBox{";
			if(tkSet.isEmpty()){
				result += "- ,";
			}
			for (Token tk : tkSet) {
				result += "$"+tk.getValue()+"$, ";
			}
			result = result.substring(0,  result.length()-2);
			result += "}";
			
			result += " & ";
			
			tkSet = follow.get(var.getValue());
			result += "\\ContentParBox{";
			if(tkSet.isEmpty()){
				result += "- ,";
			}
			for (Token tk : tkSet) {
				result += "$"+tk.getValue()+"$, ";
			}
			result = result.substring(0,  result.length()-2);
			result += "}";
			result += "\\\\ \\hline\n";
		}
		
		result += "\t\\end{tabular}\n";
		result += "\\end{center}\n";
		
		return result;
	}
		
}
