package grammarTools;

import java.util.ArrayList;

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
	
	public ProductionRule getProductionRule(Variable leftSide){
		for(ProductionRule rule : this.productionRules){
			if(rule.getLeftSide().equals(leftSide)){
				return rule;
			}
		}
		return null;
	}
	
	
	public ProductionRule getProductionRule(Token rightSide){
		for(ProductionRule rule : this.productionRules){
			if(rule.getRightSide().equals(rightSide)){
				return rule;
			}
		}
		return null;
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
	
}
