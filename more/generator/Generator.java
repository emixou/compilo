package generator;

import java.util.ArrayList;
import java.util.List;

import grammarTools.Terminal;

public class Generator {
	private ArrayList<Terminal> accumulator;
	private ArrayList<String> jumpLabels;
	private ArrayList<String> varnames;
	private ArrayList<Terminal> opLvl2;
	private ArrayList<Terminal> opLvl1;
	private ArrayList<Terminal> comparators;
	
	private int id = 0;
	private int labelId = 0;
	
	private static int identationLevel = 0;
	
	public Generator() {
		accumulator = new ArrayList<Terminal>();
		varnames = new ArrayList<String>();
		jumpLabels = new ArrayList<String>();
		
		opLvl2 = new ArrayList<Terminal>();
		opLvl2.add(new Terminal("+"));
		opLvl2.add(new Terminal("-"));
		
		opLvl1 = new ArrayList<Terminal>();
		opLvl1.add(new Terminal("*"));
		opLvl1.add(new Terminal("/"));
		
		comparators = new ArrayList<Terminal>();
		comparators.add(new Terminal("<"));
		comparators.add(new Terminal(">"));
		comparators.add(new Terminal("<="));
		comparators.add(new Terminal(">="));
		comparators.add(new Terminal("="));
		comparators.add(new Terminal("/="));
		
		init();
	}
	
	private void indent(){
		for(int i=0; i<identationLevel; ++i){
			System.out.print("\t");
		}
	}
	
	private void upIdentation(){
		++identationLevel;
	}
	
	private void downIdentation(){
		--identationLevel;
	}
	
	private String newTmpVar() {
		++id;
		return "%"+id;
	}
	
	private String newLabel(){
		++labelId;
		return "_"+labelId;
	}
	
	private void pushLabel(String label){
		jumpLabels.add(label);
	}
	
	private String popLabel(){
		String tmp = jumpLabels.get(jumpLabels.size()-1);
		jumpLabels.remove(jumpLabels.size()-1);
		return tmp;
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
	
	public void generate(Terminal aTerminal) {
		
		String value = aTerminal.getValue();
		if (value.equals(";")) handleInstructionGen();
		else if (value.equals("begin")) beginGen();
		else if (value.equals("end")) endInstructionGen();
		else if (value.equals("do")) loopGen(); 
		else if (value.equals("then")) ifGen();
		else if (value.equals("else")) elseGen();
		else if (value.equals("od")) endLoopGen();
		else if (value.equals("fi")) endCondGen();
		else accumulator.add(aTerminal);
	}

// ################### CODE FORMATING ###################
	
	private void alloca(String varname) {
		indent();
		System.out.println("\t%"+varname+" = alloca i32");
	}
	
	private void store(String value, String varname) {
		indent();
		System.out.println("\tstore i32 "+value+", i32* %"+varname);
	}
	
	private String load(String varname) {
		String tmpVar = newTmpVar();
		indent();
		System.out.println("\t"+tmpVar+" = load i32, i32* %"+varname);
		return tmpVar;
	}
	
	private String op(String v1, String v2, String op) {
		String tmpVar = newTmpVar();
		
		indent();
		System.out.print("\t"+tmpVar+" = ");
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
	
	private String binOp(String v1, String v2, String binOp) {
		String tmpVar = newTmpVar();
		indent();
		System.out.println("\t"+tmpVar+" = "+binOp+" i1 "+v1+", "+v2);
		return tmpVar;
	}
	
	private String icmp(String v1, String v2, String comparator) {
		String tmpVar = newTmpVar();
		indent();
		System.out.print("\t"+tmpVar+" = icmp ");
		if(comparator.equals("<=")) {
			System.out.print("ule");
		} else if(comparator.equals("<")) {
			System.out.print("ult");
		} else if(comparator.equals(">=")) {
			System.out.print("uge");
		} else if(comparator.equals(">")) {
			System.out.print("ugt");
		} else if(comparator.equals("=")) {
			System.out.print("eq");
		} else {
			System.out.print("ne");
		}
		System.out.println(" i32 "+v1+", "+v2);
		return tmpVar;
	}
	
	private void br(String label) {
		indent();
		System.out.println("\tbr label %"+label);
	}
	
	private void br(String condVar, String label, String elseLabel) {
		indent();
		System.out.println("\tbr i1 "+condVar+", label %"+label+", label %"+elseLabel);
	}
	
	private void label(String label) {
		indent();
		System.out.println(label+":");
	}
	
// ################### GENERAL ###################
	
	private void beginGen() {
		System.out.println("define i32 @main() {");
	}
	
	private void endGen() {
		System.out.println("\tret i32 0");
		System.out.println("}");
	}

// ################### INSTRUCTIONS ###################
	
	private void handleInstructionGen() {
		if (accumulator.size()>0) {
			if (accumulator.get(0).equals("read")) {
				handleReadInstGen();
			} else if (accumulator.get(0).equals("print")) {
				handlePrintInstGen();
			} else if (accumulator.get(1).equals(":=")) {
				handleAssignInstGen();
			}
			
			accumulator.clear();
		}
	}
	
	private void endInstructionGen() {
		handleInstructionGen();
		endGen();
		accumulator.clear();
	}
	
// ################### READ & WRITE ###################
	
	private void handlePrintInstGen() {
		String varname = accumulator.get(2).getRealValue();
		String tmpVar = load(varname);
		indent();
		System.out.println("\tcall void @printlnint(i32 "+tmpVar+")");
	}
	
	private void handleReadInstGen() {
		String varname = accumulator.get(2).getRealValue();
		if (!varnames.contains(varname)) {
			varnames.add(varname);
			alloca(varname);
		}
		String tmpVar = newTmpVar();
		indent();
		System.out.println("\t"+tmpVar+" = call i32 @getint()");
		store(tmpVar,varname);
	}

// ################### ASSIGNATION ###################	

	private void handleAssignInstGen() {
		String varname = accumulator.get(0).getRealValue();
		String tmpVar = handleExprArithGen(accumulator.subList(2, accumulator.size()));
		handleAssignInstGen(varname, tmpVar);
	}
	
	private void handleAssignInstGen(String varname, String tmpVar) {
		if (!varnames.contains(varname)) {
			varnames.add(varname);
			alloca(varname);
		}
		store(tmpVar,varname);
	}

// ################### EXPR ARITH ###################	

	private String handleExprArithGen(List<Terminal> anExprArith) {
		if (anExprArith.size()==1) {
			if (anExprArith.get(0).equals("[VarName]")) {
				return load(anExprArith.get(0).getRealValue());
			} else {
				return anExprArith.get(0).getRealValue();
			}
		} if (anExprArith.size()==2) {
			String val = handleExprArithGen(anExprArith.subList(1, anExprArith.size()));
			return op("0",val,"-");
		} else { 
			// <ExprArith> +|- <ExprArith> +|- ...
			String res = opExprArithDecomp(anExprArith, opLvl2);
			if (res!=null) return res;
			// <ExprArith> *|/ <ExprArith> *|/ ...
			res = opExprArithDecomp(anExprArith, opLvl1);
			if (res!=null) return res; 
			// -(<ExprArith>) and (<ExprArith>)
			return parenthesisDecomp(anExprArith);
		}
	}
	
	private String decomposeExprArith(List<Terminal> anExprArith, int prev, int curr, String intermediate) {
		String v1 = intermediate;
		if (intermediate==null)
			v1 = handleExprArithGen(anExprArith.subList(0, prev));
		
		String v2 = handleExprArithGen(anExprArith.subList(prev+1, curr));
		return op(v1,v2,anExprArith.get(prev).getValue());
	}
	
	private String opExprArithDecomp(List<Terminal> anExprArith, List<Terminal> op) {
		int parenthesisCpt = 0;
		int i=0;
		String tmpVar=null;
		int prev = -1;
		while (i<anExprArith.size()) {
			Terminal aTerminal = anExprArith.get(i);
			if (aTerminal.getValue().equals("(")) ++parenthesisCpt;
			if (aTerminal.getValue().equals(")")) --parenthesisCpt;
			
			if (parenthesisCpt==0) {
				if (op.contains(aTerminal) && i>0) {
					if (prev==-1) prev = i;
					else {
						tmpVar = decomposeExprArith(anExprArith,prev,i,tmpVar);
						prev=i;
					}
				}
			}
			++i;
		}
		if (prev!=-1) {
			tmpVar = decomposeExprArith(anExprArith,prev,i,tmpVar);
		}
		return tmpVar;
	}
	
	private String parenthesisDecomp(List<Terminal> anExprArith) {
		if (anExprArith.get(0).equals("-")) {
			String tmpVar = handleExprArithGen(anExprArith.subList(2, anExprArith.size()-1));
			return op("0",tmpVar,"-");
		}
		return handleExprArithGen(anExprArith.subList(1, anExprArith.size()-1));
	}
	
// ################### LOOPS ###################	
	
	private void loopGen() {
		if (accumulator.get(0).equals("while")) {
			handleWhileInstGen();
		} else {
			handleForInstGen();
		}
		accumulator.clear();
	}
	
	private void endLoopGen() {
		handleInstructionGen();
		String loopLabel = popLabel();
		br("head"+loopLabel);
		label("after"+loopLabel);
		downIdentation();
	}

// ################### WHILE ###################	
	
	private void handleWhileInstGen(){
		String loopLabel = newLabel();
		pushLabel(loopLabel);
		br("head"+loopLabel);
		upIdentation();
		label("head"+loopLabel);
		String condVar = handleCondGen(accumulator.subList(1, accumulator.size()));
		br(condVar,"body"+loopLabel,"after"+loopLabel);
		label("body"+loopLabel);
	}

// ################### FOR ###################	
	
	private void handleForInstGen()  {
		// for [VarName] from <ExprArith> by <ExprArith> to <ExprArith> do <Code> od
		Terminal varname = accumulator.get(1);
		String label = newLabel();
		pushLabel(label);
		
		//init
		int i = 3;
		while (!accumulator.get(i).equals("by")) ++i;
		String from = handleExprArithGen(accumulator.subList(3, i));
		if (!varnames.contains(varname.getRealValue())) {
			alloca(varname.getRealValue());
		}
		store(from, varname.getRealValue());
		
		int previous = ++i;
		while (!accumulator.get(i).equals("to")) ++i;
		String by = handleExprArithGen(accumulator.subList(previous, i));
		String to = handleExprArithGen(accumulator.subList(++i, accumulator.size()));
		
		br("cond"+label);
		upIdentation();
		
		// Head
		label("head"+label);
		String tmpVar = op(load(varname.getRealValue()), by, "+");
		store(tmpVar, varname.getRealValue()); 
		
		// Cond
		br("cond"+label);
		label("cond"+label);
		
		tmpVar = icmp(load(varname.getRealValue()), to,"<");
		br(tmpVar,"body"+label,"after"+label);
		
		// Body
		label("body"+label);
		
	}
	
// ################### CONDITIONS ###################	
	
	private String decomposeCond(List<Terminal> aCond, int prev, int curr, String intermediate) {
		String v1 = intermediate;
		if (intermediate==null)
			v1 = handleCondGen(aCond.subList(0, prev));
		
		String v2 = handleCondGen(aCond.subList(prev+1, curr));
		return binOp(v1,v2,aCond.get(prev).getValue());
	}
	
	private String binOpCondDecomp(List<Terminal> aCond, String binOp) {
		int i=0;
		String tmpVar=null;
		int prev = -1;
		while (i<aCond.size()) {
			Terminal aTerminal = aCond.get(i);
			if (aTerminal.equals(binOp)) {
				if (prev==-1) prev = i;
				else {
					tmpVar = decomposeCond(aCond,prev,i,tmpVar);
					prev=i;
				}
			}
			
			++i;
		}
		if (prev!=-1) {
			tmpVar = decomposeCond(aCond,prev,i,tmpVar);
		}
		return tmpVar;
	}
	
	private String simpleCondGen(List<Terminal> aSimpleCond) {
		if (aSimpleCond.get(0).equals("not")) { // v xor 1 => 1 xor 1 = 0 & 0 xor 1 = 1
			String tmpVar = simpleCondGen(aSimpleCond.subList(1, aSimpleCond.size()));
			return binOp(tmpVar,"1","xor");
		}
		int i = 0;
		while (!comparators.contains(aSimpleCond.get(i))) ++i;
		String v1 = handleExprArithGen(aSimpleCond.subList(0, i));
		String v2 = handleExprArithGen(aSimpleCond.subList(i+1, aSimpleCond.size()));
		return icmp(v1,v2,aSimpleCond.get(i).getValue());

	}
	
	private String handleCondGen(List<Terminal> aCond){
		// <Cond> and <Cond> and ...
		String res = binOpCondDecomp(aCond, "and");
		if (res!=null) return res;
		// <Cond> or <Cond> or ...
		res = binOpCondDecomp(aCond, "or");
		if (res!=null) return res;
		// not <SimpleCond> | <SimpleCond>
		return simpleCondGen(aCond);
	}
	
// ################### IF ELSE FI ###################	
	
	private void ifGen() {
		String label = newLabel();
		pushLabel(label);
		String cond = handleCondGen(accumulator.subList(1, accumulator.size()));
		br(cond,"body"+label,"else"+label);
		upIdentation();
		label("body"+label);
		accumulator.clear();
	}
	
	private void elseGen() {
		handleInstructionGen();
		String label = popLabel();
		br("after"+label);
		label("else"+label);
		pushLabel(label);
		pushLabel(null);
	}
	
	private void endCondGen() {
		handleInstructionGen();
		String label = popLabel();
		if (label!=null) {
			br("after"+label);
			label("else"+label);
		} else label = popLabel();
		br("after"+label);
		label("after"+label);
		downIdentation();
	}
 
}
