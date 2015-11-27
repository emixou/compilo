package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

import grammarTools.ActionTable;
import grammarTools.Grammar;
import grammarTools.ProductionRule;
import grammarTools.Terminal;
import grammarTools.Token;
import scanner.LexicalAnalyzer;
import scanner.SyntaxErrorException;

public class Parser {
	private Stack stack;
	private ActionTable actionTable;
	private Grammar grammar;
	private LexicalAnalyzer scanner;
	
	public Parser(String grammarFileName, java.io.FileReader program) {
		stack = new Stack();
		grammar = new Grammar(grammarFileName);
		for(Terminal terminal : grammar.getTerminals()){
			System.out.println(terminal.getValue());
		}
		
		actionTable = new ActionTable(grammar);
		actionTable.build();

		System.out.println(actionTable.toLatex());
		System.out.println(actionTable.firstFollowToLatex());
		
		scanner = new LexicalAnalyzer(program);
	}
	
	public void parse() throws PatternSyntaxException, IOException, SyntaxErrorException {
		stack.push(grammar.getStartSymbol());
		
		Terminal head = new Terminal(scanner.nextToken());
		Token top = stack.top();
		while (true) { // no error nor accept	
			if (head.getValue()==null && stack.isEmpty()) {
				accept();
				break;
			} else if (grammar.isTerminal(top) && head.equals(top)) {
				head = new Terminal(scanner.nextToken());
				top = stack.pop();
			}else if (!grammar.isTerminal(top) && actionTable.getRule(top,head)!= null) {
				produce(actionTable.getRule(top,head));
				top = stack.pop();
			} else {
				error();
				break;
			}
		}
		
	}
	
	private void produce(ProductionRule aRule) {
		//System.out.println("Produce");
		ArrayList<Token> tokens = aRule.getRightSide();
		for (int i=tokens.size()-1; i>-1 ;--i) {
			if (!tokens.get(i).getValue().equals("eps")) {
				stack.push(tokens.get(i));
			}
		}
	}
	
	private void accept() {
		System.out.println("Accepted.");
	}
	
	private void error() {
		System.out.println("Error.");
	}
	
}
