package grammarTools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import grammarTools.Token;
import grammarTools.Terminal;
import grammarTools.Variable;;


public class Grammar {

	protected ArrayList<Terminal> terminals;
	protected ArrayList<Variable> variables; //Intermediary Symbol
	protected ArrayList<ProductionRule> productionRules;
	protected Variable startSymbol;
	
	public Grammar(){
		this.terminals = new ArrayList<Terminal>();
		this.variables = new ArrayList<Variable>();
		this.productionRules = new ArrayList<ProductionRule>();
	}
 
	public Grammar(String fileName) {
	    try {
	    	BufferedReader reader = new BufferedReader(new FileReader(fileName));
	    	String line;
	    	productionRules = new ArrayList<ProductionRule>();
	    	terminals = new ArrayList<Terminal>();
	    	variables = new ArrayList<Variable>();
	    	
			while ((line = reader.readLine()) != null) {
				String splitLine[] = line.split("->");
				String leftSide = splitLine[0].trim();
				String rightSide[] = splitLine[1].trim().split("\\s\\|\\s");
				
				if (leftSide.startsWith("<") && leftSide.endsWith(">")) {
					Variable aVariable = new Variable(leftSide);
					if (!variables.contains(aVariable)) {
						variables.add(aVariable);
					}
				}
				
				for (String expr : rightSide) {
					ArrayList<Token> rightSideTokens = new ArrayList<Token>();
					
					for (String token : expr.split("\\s")) {
						token = token.trim();
						if (token.startsWith("<") && token.endsWith(">")) {
							Variable aVariable = new Variable(token);
							rightSideTokens.add(aVariable);
							if (!variables.contains(aVariable)) {
								variables.add(aVariable);
							}
						} else {
							Terminal aTerminal = new Terminal(token);
							rightSideTokens.add(aTerminal);
							if (!terminals.contains(aTerminal)) {
								terminals.add(aTerminal);
							}
						}
						
					}
					productionRules.add(new ProductionRule(new Variable(leftSide),rightSideTokens));
				}
				
			}
			startSymbol = productionRules.get(0).getLeftSide();
			
			reader.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	public Grammar(ArrayList<Terminal> terminals, ArrayList<Variable> variables, ArrayList<ProductionRule> productionRules, Variable startSymbol){
		this.terminals = terminals;
		this.variables = variables;
		this.productionRules = productionRules;
		this.startSymbol = startSymbol;
	}
	
	public void setTerminals(ArrayList<Terminal> terminals){
		this.terminals = terminals;
	}
	
	public ArrayList<Terminal> getTerminals(){
		return this.terminals;
	}
	
	public void setVariables(ArrayList<Variable> variables){
		this.variables = variables;
	}
	
	public ArrayList<Variable> getVariables(){
		return this.variables;
	}
	
	public void setProductionRules(ArrayList<ProductionRule> productionRules){
		this.productionRules = productionRules;
	}
	
	public ArrayList<ProductionRule> getProductionRules(){
		return this.productionRules;
	}
	
	public ArrayList<ProductionRule> getProductionRule(Variable leftSide){
		ArrayList<ProductionRule> list = new ArrayList<ProductionRule>();
		for(ProductionRule rule : this.productionRules){
			if(rule.getLeftSide().equals(leftSide)){
				list.add(rule);
			}
		}
		return list;
	}
	
	
	public ArrayList<ProductionRule> getProductionRule(Token rightSide){
		ArrayList<ProductionRule> list = new ArrayList<ProductionRule>();
		for(ProductionRule rule : this.productionRules){
			if(rule.getRightSide().contains(rightSide)){
				list.add(rule);
			}
		}
		return list;
	}
	
	
	public void setStartSymbol(Variable startSymbol){
		this.startSymbol = startSymbol;
	}
	
	public Variable getStartSymbol(){
		return this.startSymbol;
	}
	
	public Boolean isTerminal(Token token){
		if(terminals.contains(token))
			return true;
		else
			return false;
		
	}
	
	public boolean isVariable(Token token) {
		if(variables.contains(token))
			return true;
		else
			return false;
	}
	
	public void repr() {
		System.out.println("Terminals :");
		System.out.print('[');
		for (Terminal aTerminal : this.terminals) {
			System.out.print(aTerminal.getValue()+", ");
		}
		System.out.println(']');
		
		System.out.println("Variables :");
		System.out.print('[');
		for (Variable aVariable : this.variables) {
			System.out.print(aVariable.getValue()+", ");
		}
		System.out.println(']');
		
		System.out.println("Production Rules :");
		for (ProductionRule aProductionRule : this.productionRules) {
			aProductionRule.repr();
		}
		
		System.out.println("Startsymbol :");
		System.out.println(this.startSymbol.getValue());
	}
	
}
