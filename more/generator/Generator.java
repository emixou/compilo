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
	private int condId = 0;
	private int loopId = 0;
	
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
	
	private void inlinePrint(String string, boolean first){
		if(first){
			for(int i=0; i<identationLevel; ++i){
				System.out.print("\t");
			}
		}
		System.out.print(string);
	}
	
	private void inlinePrint(String string){
		inlinePrint(string, false);
	}
	
	private void print(String string, int identationLevel){
		for(int i=0; i<identationLevel; ++i){
			System.out.print("\t");
		}
		System.out.println(string);
	}
	
	private void print(String string){
		print(string, identationLevel);
	}
	
	private void upIdentation(){
		++identationLevel;
	}
	
	private void downIdentation(){
		if(identationLevel == 0){
			identationLevel = 0;
		}else{
			--identationLevel;
		}
	}
	
	private String newTmpVar() {
		++id;
		return "%"+id;
	}
	
	private String newCondLabel(){
		++condId;
		return "cond_"+condId;
	}
	
	private String newLoopLabel(){
		++loopId;
		return "_"+loopId;
	}
	
	private void init() {
		print(	"declare i32 @getchar()\n" +
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
		if (value.equals(";")) instructionGen();
		else if (value.equals("end")) endInstructionGen();
		else if (value.equals("do")) loopGen(); 
		else if (value.equals("then")) ifGen();
		//else if (value.equals("else")) elseGen();
		//else if (value.equals("od")) endLoopGen();
		//else if (value.equals("fi")) endCondGen();
		else accumulator.add(aTerminal);
	}

// ################### CODE FORMATING ###################
	
	private void alloca(String varname) {
		print("%"+varname+" = alloca i32");
	}
	
	private void store(String value, String varname) {
		print("store i32 "+value+", i32* %"+varname);
	}
	
	private String load(String varname) {
		String tmpVar = newTmpVar();
		print(tmpVar+" = load i32, i32* %"+varname);
		return tmpVar;
	}
	
	private String op(String v1, String v2, String op) {
		String tmpVar = newTmpVar();
		
		inlinePrint(tmpVar+" = ", true);
		if (op.equals("+")) {
			inlinePrint("add");
		} else if (op.equals("-")) {
			inlinePrint("sub");
		} else if (op.equals("*")) {
			inlinePrint("mul");
		} else if (op.equals("/")) {
			inlinePrint("sdiv");
		}
		print(" i32 "+v1+", "+v2, 0);
		return tmpVar;
	}
	
	private String binOp(String v1, String v2, String binOp) {
		String tmpVar = newTmpVar();
		print(tmpVar+" = "+binOp+" i1 "+v1+", "+v2, 0);
		return tmpVar;
	}
	
	private String icmp(String v1, String v2, String comparator) {
		String tmpVar = newTmpVar();
		
		print("");
		inlinePrint(tmpVar+" = icmp ", true);
		if(comparator.equals("<=")) {
			inlinePrint("ule");
		} else if(comparator.equals("<")) {
			inlinePrint("ult");
		} else if(comparator.equals(">=")) {
			inlinePrint("uge");
		} else if(comparator.equals(">")) {
			inlinePrint("ugt");
		} else if(comparator.equals("=")) {
			inlinePrint("eq");
		} else {
			inlinePrint("ne");
		}
		print(" i32 "+v1+", "+v2, 0);
		return tmpVar;
	}
	
// ################### GENERAL ###################
	
	private void beginGen() {
		print("define i32 @main() {");
		upIdentation();
	}
	
	private void endGen() {
		print("ret i32 0");
		downIdentation();
		print("}");
	}

// ################### INSTRUCTIONS ###################
	
	public void instructionGen() {
		if (accumulator.get(0).equals("begin")) {
			beginGen();
			accumulator.remove(0);
		} 

		if (accumulator.get(0).equals("read")) {
			handleReadInstGen();
		} else if (accumulator.get(0).equals("print")) {
			handlePrintInstGen();
		} else if (accumulator.get(0).equals(":=")) {
			handleAssignInstGen();
		}
		
		accumulator.clear();
	}
	
	private void endInstructionGen() {
		instructionGen();
		endGen();
	}
	
// ################### READ & WRITE ###################
	
	private void handlePrintInstGen() {
		String varname = accumulator.get(2).getRealValue();
		String tmpVar = load(varname);
		print("call void @printlnint(i32 "+tmpVar+")");
	}
	
	private void handleReadInstGen() {
		String varname = accumulator.get(2).getRealValue();
		if (!varnames.contains(varname)) {
			varnames.add(varname);
			alloca(varname);
		}
		String tmpVar = newTmpVar();
		print(tmpVar+" = call i32 @getint()");
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
		if(jumpLabels.size() > 0){
			upIdentation();
			print("");
		}
		if (accumulator.get(0).equals("while")) {
			handleWhileInstGen();
		} else {
			handleForInstGen();
		}
	}
	
	private void endLoopGen() {
		String label = popLabel();
		print("\tbr label %"+label);
			
		if(jumpLabels.size() != identationLevel-1){
			downIdentation();
			print("");
		}
		
		print("afterLoop_"+ (label.split("_"))[1] +":");
	}
	
	public void pushLabel(String label){
		jumpLabels.add(label);
	}
	
	private String popLabel(){
		String tmp = jumpLabels.get(jumpLabels.size()-1);
		jumpLabels.remove(jumpLabels.size()-1);
		return tmp;
	}


// ################### WHILE ###################	
	
	private void handleWhileInstGen(){
		String loopValue = newLoopLabel();
	
		ArrayList<Terminal> typeCond = new ArrayList<Terminal>();
		ArrayList<ArrayList<Terminal>> condList = new ArrayList<ArrayList<Terminal>>();
		ArrayList<Terminal> tmpCond = new ArrayList<Terminal>();
		
		for(int i = 1; i < accumulator.size(); ++i){
			Terminal tmp = accumulator.get(i);
			if(tmp.getValue().equals("and") || tmp.getValue().equals("or")){
				condList.add(tmpCond);
				//tmpCond.clear();
				tmpCond =  new ArrayList<Terminal>();
				typeCond.add(tmp);
			}else if(i == accumulator.size()-1){ // if last terminal
				tmpCond.add(tmp);
				condList.add(tmpCond);
				//tmpCond.clear();
				tmpCond =  new ArrayList<Terminal>();
			}else{
				tmpCond.add(tmp);
			}
		}
		
		int condNbr = condList.size();
		
		for(int i = 0; i < condList.size(); ++i){

			String label = newCondLabel();
			tmpCond = condList.get(i);
			
			/*
			 * String condVarname = tmpCond.get(0).getRealValue();
			 * String condCompvalue = tmpCond.get(2).getRealValue();
			 */
			
			pushLabel("cond"+loopValue);
		
			//Condition
			if(i < (condNbr-1) && condNbr > 1){
				print("cond_"+(condId)+":");
				print("br i1 "+handleCondGen(tmpCond)+", label %cond_"+(condId+1)+", label %afterLoop"+loopValue);
			}else{
				if(i == (condNbr-1) && (condNbr != 1)){
					print("cond_"+(condId)+":");
				}
				print("br i1 "+handleCondGen(tmpCond)+", label %loop"+loopValue+", label %afterLoop"+loopValue);
			}
		}
		
		//LOOP MAIN
		print("loop"+loopValue+":");
	}

// ################### FOR ###################	
	
	private void handleForInstGen(){
		String loopValue = newLoopLabel() ; //getLoop(); must use newLoopLabel();
		
		int maxSize = 7;
		int exprSize = accumulator.size() - maxSize;
		Terminal variable = accumulator.get(1);
		ArrayList<Terminal> initValue = new ArrayList<Terminal>();
		ArrayList<Terminal> endValue  = new ArrayList<Terminal>();
		ArrayList<Terminal> exprValue = new ArrayList<Terminal>();
			
		int byPos = -1;
		int toPos = -1;
		
		for(int i = 3; i < accumulator.size(); ++i){
			if(accumulator.get(i).equals("by")){
				byPos = i;
			}else if(accumulator.get(i).equals("to")){
				toPos = i;
			}else{
				if(toPos != -1 && byPos != -1){
					endValue.add(accumulator.get(i));
				}else if(byPos != -1 && toPos == -1){
					exprValue.add(accumulator.get(i));
				}else{
					initValue.add(accumulator.get(i));
				}
			}
		}


		//LOOP MAIN
		print("loop"+loopValue+":");
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
		if (aSimpleCond.get(0).equals("not")) {
			// voir ce quil faut faire
			
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
	
// ################### IF ###################	
	
	private void ifGen() {
		String label = newCondLabel();
		// ajouter le label sur un stack
		String cond = handleCondGen(accumulator.subList(1, accumulator.size()));
		print("br i1 "+cond+", label %"+label);
	}
 
}
