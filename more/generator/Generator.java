package generator;

import java.util.ArrayList;
import java.util.List;

import grammarTools.Terminal;

public class Generator {
	private ArrayList<Terminal> accumulator;
	private ArrayList<String> varnames;
	
	private int id = 0;
	
	public Generator() {
		accumulator = new ArrayList<Terminal>();
		varnames = new ArrayList<String>();
		init();
	}
	
	private String newTmpVar(String name) {
		++id;
		return name+"_"+id;
	}
	
	private void init() {
		System.out.println("declare i32 @getchar()");
		System.out.println("declare i32 @putchar(i32)");
	}
	
	public void accumulate(Terminal aTerminal) {
		accumulator.add(aTerminal);
		
	}
	
	public void trigger() {
		if (accumulator.get(0).equals("begin")) {
			handleBeginGen();
			accumulator.remove(0);
		} 
		
		boolean end=false;
		if (accumulator.get(accumulator.size()-1).equals("end")) {
			end = true;
			accumulator.remove(accumulator.size()-1);
		}
		
		if (accumulator.get(accumulator.size()-1).equals(";")) {
			accumulator.remove(accumulator.size()-1);
		}

		if (accumulator.get(0).equals("for")) {
			//System.out.println("for");
		} else if (accumulator.get(0).equals("while")) {
			//System.out.println("while");
		} else if (accumulator.get(0).equals("if")) {
			//System.out.println("if");
		} else if (accumulator.get(0).equals("read")) {
			handleReadInstGen();
		} else if (accumulator.get(0).equals("print")) {
			handlePrintInstGen();
		} else if (accumulator.get(accumulator.size()-1).equals("od")){ // assign
			// handle last loop end
			accumulator.remove(accumulator.size()-1);
			trigger();
		} else {
			handleAssignInstGen();
		}
		
		if (end) {
			handleEndGen();
		}
		
		accumulator.clear();
	}
	
	private void handleAssignInstGen() {
		Terminal rightPart = accumulator.get(0);
		accumulator.remove(0); accumulator.remove(0); 
		if (accumulator.get(accumulator.size()-1).equals(";")) {
			accumulator.remove(accumulator.size()-1);
		}
		String tmpVar = handleExprArithGen(accumulator);
		String varname = rightPart.getRealValue();
		if (!varnames.contains(varname)) {
			varnames.add(varname);
			System.out.println("%"+varname+" = alloca i32");
		}
		System.out.println("store i32 "+tmpVar+", i32* %"+varname);
	}
	
	private String handleExprArithGen(List<Terminal> anExprArith) {
		if (anExprArith.size()==1) {
			if (anExprArith.get(0).equals("[VarName]")) {
				String tmpVar = newTmpVar("vv");
				System.out.println("%"+tmpVar+" = load i32, i32* %"+anExprArith.get(0).getRealValue());
				return "%"+tmpVar;
			} else {
				return anExprArith.get(0).getRealValue();
			}
		} else if (anExprArith.size()==2) {
			if (anExprArith.get(0).equals("-")) {
				String tmpVar = newTmpVar("lv");
				anExprArith.remove(0);
				String val = handleExprArithGen(anExprArith);
				System.out.println("%"+tmpVar+" = sub i32 0,"+val);
			}
		} else if (anExprArith.size()==3){
			if (anExprArith.get(0).equals("(")) {
				anExprArith.remove(0); anExprArith.remove(anExprArith.size()-1);
				return handleExprArithGen(anExprArith);
			} else {
				String v1 = handleExprArithGen(anExprArith.subList(0, 1));
				String v2 = handleExprArithGen(anExprArith.subList(2, 3));
				String tmpVar = newTmpVar("ov");
				System.out.print("%"+tmpVar+" = ");
				if (accumulator.get(1).equals("+")) {
					System.out.print("add");
				} else if (accumulator.get(1).equals("-")) {
					System.out.print("sub");
				} else if (accumulator.get(1).equals("*")) {
					System.out.print("mul");
				} else if (accumulator.get(1).equals("/")) {
					System.out.print("sdiv");
				}
				System.out.println(" i32 "+v1+", "+v2);
				return "%"+tmpVar;
			}
			
		}

		return "";
	}
	
	private void handleBeginGen() {
		System.out.println("define i32 @main() {");
	}
	
	private void handlePrintInstGen() {
		String varname = accumulator.get(2).getRealValue();
		String tmpVar = newTmpVar("pv");
		System.out.println("%"+tmpVar+" = load i32, i32* %"+varname);
		System.out.println("call i32 @putchar(i32 %"+tmpVar+")");
	}
	
	private void handleReadInstGen() {
		String varname = accumulator.get(2).getRealValue();
		if (!varnames.contains(varname)) {
			varnames.add(varname);
			System.out.println("%"+varname+" = alloca i32");
		}
		String tmpVar = newTmpVar("or");
		System.out.println("%"+tmpVar+" = call i32 @getchar()");
		System.out.println("store i32 %"+tmpVar+", i32* %"+varname);
	}
	
	private void handleEndGen() {
		System.out.println("ret i32 0");
		System.out.println("}");
	}

}
