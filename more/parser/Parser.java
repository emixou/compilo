package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

import generator.Generator;
import grammarTools.ActionTable;
import grammarTools.Grammar;
import grammarTools.ProductionRule;
import grammarTools.Terminal;
import grammarTools.Token;
import scanner.LexicalAnalyzer;
import scanner.Symbol;
import scanner.SyntaxErrorException;

public class Parser {
	private Stack stack;
	private ActionTable actionTable;
	private Grammar grammar;
	private LexicalAnalyzer scanner;
	
	public Parser(String grammarFileName, java.io.FileReader program) {
		stack = new Stack();
		grammar = new Grammar(grammarFileName);
		actionTable = new ActionTable(grammar);
		actionTable.build();
		scanner = new LexicalAnalyzer(program);
	}
	
	public void parse() throws PatternSyntaxException, IOException, SyntaxErrorException {
		Generator generator = new Generator();
		stack.push(grammar.getStartSymbol());
		
		Symbol symbol = scanner.nextToken();
		Terminal head = new Terminal(symbol);
		Token top = stack.top();
		while (true) { // no error nor accept	
			if (head.getValue()==null && stack.isEmpty()) {
				accept();
				break;
			} else if (grammar.isTerminal(top) && head.equals(top)) {
				generator.accumulate(head);
				
				//}
				if (head.getValue().equals(";") || head.getValue().equals("end")) {
					generator.trigger();
				}
				symbol = scanner.nextToken();
				head = new Terminal(symbol);
				top = stack.pop();
			}else if (!grammar.isTerminal(top) && actionTable.getRule(top,head)!= null) {
				ProductionRule aRule = actionTable.getRule(top,head);
				//System.out.println(aRule.getRuleNumber());
				produce(aRule);
				top = stack.pop();
			} else {
				error(symbol);
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
		//System.out.println("Accepted.");
	}
	
	private void error(Symbol symbol) {
		System.out.println("Error : unexpected symbol '"+symbol.getValue()+"' at line "+symbol.getLine()+":"+symbol.getColumn()+".");
	}
	
}
