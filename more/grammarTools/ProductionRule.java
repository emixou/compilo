package grammarTools;

import java.util.ArrayList;

public class ProductionRule {

	private Variable leftSide;
	private ArrayList<Token> rightSide;
	private static int ruleCounter = 0;
	private int ruleNumber;
	
	public ProductionRule(Variable leftSide, Token rightSide){
		this.leftSide = leftSide;
		this.rightSide = new ArrayList<Token>();
		this.rightSide.add(rightSide);
		++ruleCounter;
		this.ruleNumber = ruleCounter;
	}
	
	public ProductionRule(Variable leftSide, ArrayList<Token> rightSide){
		this.leftSide = leftSide;
		this.rightSide = rightSide;
		++ruleCounter;
		this.ruleNumber = ruleCounter;
	}
	
	public int getRuleNumber(){
		return ruleNumber;
	}
	
	public Variable getLeftSide(){
		return this.leftSide;
	}
	
	public void setLeftSide(Variable leftSide){
		this.leftSide = leftSide;
	}
	
	public ArrayList<Token> getRightSide(){
		return this.rightSide;
	}
	
	public void setRightSide(ArrayList<Token> rightSide){
		this.rightSide = rightSide;
	}

	public void addToken(Token Token){
		this.rightSide.add(Token);
	}

	public boolean isLeftRecursive() {
		return this.getLeftSide().equals(this.getRightSide().get(0));
	}
	
	public void repr() {
		System.out.println(this.toString());
		
	}
	
	@Override
    public String toString() {
        String result = "";
        
        result+= this.getLeftSide().getValue()+" -> ";
		for (Token aToken : this.getRightSide() ) {
			result+= aToken.getValue()+" ";
		}
		
        return result;
	}
	
	public String toLatex(){
		String result = "";
		
		result+= this.getLeftSide().getValue()+" \t\t \\rightarrow \t ";
		for (Token aToken : this.getRightSide() ) {
			result+= aToken.getValue()+" ";
		}
		
		return result;
	}
}
