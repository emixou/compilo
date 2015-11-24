package grammarAlgorithms;

import java.util.ArrayList;

import grammarTools.Grammar;
import grammarTools.ProductionRule;
import grammarTools.Terminal;
import grammarTools.Token;
import grammarTools.Variable;

public class LeftRecursive implements Algorithm {

	private Grammar grammar;
	
	public LeftRecursive(Grammar aGrammar) {
		this.grammar = aGrammar;
	}
	
	@Override
	public void execute() {
		
		boolean leftRecursionLeft = true;
		Variable toBeRemoved = null;
		
		while (leftRecursionLeft) {
			leftRecursionLeft = false;
			
			for (ProductionRule aProductionRule : this.grammar.getProductionRules()) {
				if (aProductionRule.isLeftRecursive()) {
					toBeRemoved = aProductionRule.getLeftSide();
					leftRecursionLeft = true;
					break;
				}
			}
			if (leftRecursionLeft) {
				ArrayList<ProductionRule> leftRecursivePR = new ArrayList<ProductionRule>();
				ArrayList<ProductionRule> otherPR = new ArrayList<ProductionRule>();
				
				for (ProductionRule aProductionRule : this.grammar.getProductionRules()) {
					if (aProductionRule.getLeftSide().equals(toBeRemoved)) {
						if (aProductionRule.isLeftRecursive()) {
							leftRecursivePR.add(aProductionRule);
						} else {
							otherPR.add(aProductionRule);
						}
					}
				}
				
				this.grammar.getProductionRules().removeAll(leftRecursivePR);
				this.grammar.getProductionRules().removeAll(otherPR);
				
				Variable newVar = new Variable(toBeRemoved.getValue().replace(">","'>"));
				for (ProductionRule aProductionRule : otherPR) {
					ArrayList<Token> newRightSide = aProductionRule.getRightSide();
					newRightSide.add(newVar);
					this.grammar.getProductionRules().add(new ProductionRule(toBeRemoved,newRightSide));
				}
				

				for (ProductionRule aProductionRule : leftRecursivePR) {
					ArrayList<Token> newRightSide = aProductionRule.getRightSide();
					newRightSide.remove(0);
					newRightSide.add(newVar);
					this.grammar.getProductionRules().add(new ProductionRule(newVar, newRightSide));
				}
				ArrayList<Token> newRightSide = new ArrayList<Token>();
				newRightSide.add(new Terminal("ε"));
				this.grammar.getProductionRules().add(new ProductionRule(newVar,newRightSide));
				this.grammar.getVariables().add(newVar);
			
			}
			
		}
		
	}

}
