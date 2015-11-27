package parser;

import java.util.ArrayList;

import grammarTools.Token;

public class Stack extends ArrayList<Token> {

	public void push(Token aToken) {
		this.add(aToken);
	}
	
	public Token pop() {
		return this.remove(this.size()-1);
	}
	
	public Token top() {
		return this.get(this.size()-1);
	}
}
