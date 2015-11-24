package grammarTools;

import java.util.ArrayList;

public class ProductionRule {

	private Variable leftSide;
	private ArrayList<Token> rightSide;
	private int rulesNumber = 0;
	
	public ProductionRule(Variable leftSide, Token rightSide){
		this.leftSide = leftSide;
		this.rightSide = new ArrayList<Token>();
		this.rightSide.add(rightSide);
		++this.rulesNumber;
	}
	
	public ProductionRule(Variable leftSide, ArrayList<Token> rightSide){
		this.leftSide = leftSide;
		this.rightSide = rightSide;
		this.rulesNumber = rightSide.size();
	}
	
	public int getRulesNumber(){
		return this.rulesNumber;
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
		this.rulesNumber = rightSide.size();
	}

	public void addToken(Token Token){
		this.rightSide.add(Token);
		++this.rulesNumber;
	}
}
