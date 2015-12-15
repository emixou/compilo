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
		return "%"+name+"_"+id;
	}
	
	private void init() {
		System.out.println(	"declare i32 @getchar()\n" +
							"declare i32 @putchar(i32)\n" +
		
							"define i32 @getint() {\n" +
			                "entry:\n" +
			                "%res = alloca i32\n" +
			                "%digit = alloca i32\n" +
			                "store i32 0, i32* %res\n" +
			                "br label %read\n" +
			                "read:\n" +
			                "%0 = call i32 @getchar()\n" +
			                "%1 = sub i32 %0, 48\n" +
			                "store i32 %1, i32* %digit\n" +
			                "%2 = icmp ne i32 %0, 10\n" +
			                "br i1 %2, label %save , label %exit\n" +
			                "save:\n" +
			                "%3 = load i32, i32* %res\n" +
			                "%4 = load i32, i32* %digit\n" +
			                "%5 = mul i32 %3, 10\n" +
			                "%6 = add i32 %5, %4\n" +
			                "store i32 %6, i32* %res\n" +
			                "br label %read\n" +
			                "exit:\n" +
			                "%7 = load i32, i32* %res\n" +
			                "ret i32 %7\n" +
			                "}\n\n" +
		
							"define void @printlnint(i32 %n) {\n" +
					        "%digitInChar = alloca [32 x i32]\n" +
					        "%number = alloca i32\n" +
					        "store i32 %n, i32* %number\n" +
					        "%numberOfDigits = alloca i32\n" +
					        "store i32 0, i32* %numberOfDigits\n" +
					        "br label %beginloop\n" +
					        "beginloop:\n" +
					        "%1 = load i32, i32* %number\n" +
					        "%2 = icmp ne i32 %1, 0\n" +
					        "br i1 %2, label %ifloop, label %endloop\n" +
					        "ifloop:\n" +
					        "%temp1 = load i32, i32* %numberOfDigits\n" +
					        "%temp2 = load i32, i32* %number\n" +
					        "%divnum = udiv i32 %temp2, 10\n" +
					        "%currentDigit = urem i32 %temp2, 10\n" +
					        "%arrayElem = getelementptr [32 x i32], [32 x i32]* %digitInChar, i32 0, i32 %temp1\n" +
					        "store i32 %currentDigit, i32* %arrayElem\n" +
					        "%temp3 = add i32 %temp1, 1\n" +
					        "store i32 %temp3, i32* %numberOfDigits\n" +
					        "store i32 %divnum, i32* %number\n" +
					        "br label %beginloop\n" +
					        "endloop:\n" +
					        "%temp4 = load i32, i32* %numberOfDigits\n" +
					        "%temp41 = sub i32 %temp4, 1\n" +
					        "store i32 %temp41, i32* %numberOfDigits\n" +
					        "br label %beginloop2\n" +
					        "beginloop2:\n" +
					        "%temp5 = load i32, i32* %numberOfDigits\n" +
					        "%arrayElem2 = getelementptr [32 x i32], [32 x i32]* %digitInChar, i32 0, i32 %temp5\n" +
					        "%arrayElemValue = load i32, i32* %arrayElem2\n" +
					        "%arrayElemValue2 = add i32 %arrayElemValue, 48\n" +
					        "call i32 @putchar(i32 %arrayElemValue2)\n" +
					        "%temp6 = sub i32 %temp5, 1\n" +
					        "store i32 %temp6, i32* %numberOfDigits\n" +
					        "%temp7 = icmp sge i32 %temp6, 0\n" +
					        "br i1 %temp7, label %beginloop2, label %endloop2\n" +
					        "endloop2:\n" +
					        "call i32 @putchar(i32 10)\n" +
					        "ret void\n" +
					        "}\n\n");
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
			alloca(varname);
		}
		store(tmpVar,varname);
	}
	
	private void alloca(String varname) {
		System.out.println("%"+varname+" = alloca i32");
	}
	
	private void store(String value, String varname) {
		System.out.println("store i32 "+value+", i32* %"+varname);
	}
	
	private String load(String varname) {
		String tmpVar = newTmpVar("lv");
		System.out.println(tmpVar+" = load i32, i32* %"+varname);
		return tmpVar;
	}
	
	private String op(String v1, String v2, String op) {
		String tmpVar = newTmpVar("ov");
		System.out.print(tmpVar+" = ");
		if (op.equals("+")) {
			System.out.print("add");
		} else if (op.equals("-")) {
			System.out.print("sub");
		} else if (op.equals("*")) {
			System.out.print("mul");
		} else if (op.equals("/")) {
			System.out.print("sdiv");
		}
		System.out.println(" i32 "+v1+", "+v2);
		return tmpVar;
	}
	
	private String handleExprArithGen(List<Terminal> anExprArith) {
		if (anExprArith.size()==1) {
			if (anExprArith.get(0).equals("[VarName]")) {
				return load(anExprArith.get(0).getRealValue());
			} else {
				return anExprArith.get(0).getRealValue();
			}
		} else if (anExprArith.size()==2) {
			if (anExprArith.get(0).equals("-")) {
				String tmpVar = newTmpVar("lv");
				anExprArith.remove(0);
				String val = handleExprArithGen(anExprArith);
				return op("0",val,"-");
			}
		} else if (anExprArith.size()==3){
			if (anExprArith.get(0).equals("(")) {
				anExprArith.remove(0); anExprArith.remove(anExprArith.size()-1);
				return handleExprArithGen(anExprArith);
			} else {
				String v1 = handleExprArithGen(anExprArith.subList(0, 1));
				String v2 = handleExprArithGen(anExprArith.subList(2, 3));
				return op(v1,v2,accumulator.get(1).getValue());
			}
		} else { // a + ( - 3 + x  * 2 ) - 2
			int parenthesisCpt = 0;
			int i=0;
			String tmpVar=null;
			while (i<anExprArith.size()) {
				Terminal aTerminal = anExprArith.get(i);
				if (aTerminal.getValue().equals("(")) ++parenthesisCpt;
				if (aTerminal.getValue().equals(")")) --parenthesisCpt;
				
				if (parenthesisCpt==0) {
					if ((aTerminal.getValue().equals("-") && i>0) || aTerminal.getValue().equals("+")) {
						int j = i+1;
						if (anExprArith.get(j).equals("(")) {
							++j;
							while (!anExprArith.get(j).equals(")")) j++;
						}
						String v1 = tmpVar;
						if (tmpVar==null)
							v1 = handleExprArithGen(anExprArith.subList(0, i));
						
						String v2 = handleExprArithGen(anExprArith.subList(i+1, j+1));
						tmpVar = op(v1,v2,anExprArith.get(i).getValue());
						i=j;
					}
				}
				++i;
			}
			return tmpVar;
			
		}

		return "";
	}
	
	private void handleBeginGen() {
		System.out.println("define i32 @main() {");
	}
	
	private void handlePrintInstGen() {
		String varname = accumulator.get(2).getRealValue();
		String tmpVar = load(varname);
		System.out.println("call void @printlnint(i32 "+tmpVar+")");
	}
	
	private void handleReadInstGen() {
		String varname = accumulator.get(2).getRealValue();
		if (!varnames.contains(varname)) {
			varnames.add(varname);
			alloca(varname);
		}
		String tmpVar = newTmpVar("or");
		System.out.println(tmpVar+" = call i32 @getint()");
		store(tmpVar,varname);
	}
	
	private void handleEndGen() {
		System.out.println("ret i32 0");
		System.out.println("}");
	}

}
