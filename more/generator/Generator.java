package generator;

import java.util.ArrayList;

import grammarTools.Terminal;

public class Generator {
	private ArrayList<Terminal> accumulator;
	
	public Generator() {
		accumulator = new ArrayList<Terminal>();
		init();
	}
	
	private void init() {
		System.out.println("declare i32 @getchar ();");
		System.out.println("declare i32 @putchar(i32);");
	}
	
	public void accumulate(Terminal aTerminal) {
		accumulator.add(aTerminal);
		
	}
	
	public void trigger() {
		if (accumulator.get(0).equals("begin")) {
			System.out.println("begin stuff");
			accumulator.remove(0);
		} 
		
		boolean end=false;
		if (accumulator.get(accumulator.size()-1).equals("end")) {
			end = true;
			accumulator.remove(accumulator.size()-1);
		} 

		if (accumulator.get(0).equals("for")) {
			//System.out.println("for");
		} else if (accumulator.get(0).equals("while")) {
			//System.out.println("while");
		} else if (accumulator.get(0).equals("if")) {
			//System.out.println("if");
		} else if (accumulator.get(0).equals("read")) {
			System.out.println("read");
		} else if (accumulator.get(0).equals("print")) {
			handlePrintInstGen();
		} else { // assign
			//System.out.println("assign");
		}
		
		if (end) {
			//System.out.println("end stuff");
		}
		
		accumulator.clear();
	}
	
	private void handlePrintInstGen() {
		
	}

}
