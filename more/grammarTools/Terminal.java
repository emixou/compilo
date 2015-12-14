package grammarTools;

import scanner.LexicalUnit;
import scanner.Symbol;

public class Terminal extends Token{
	private String realValue;
	
	
	public Terminal(String value){
		super(value);
		realValue = value;
	}
	
	public Terminal(Symbol aSymbol) {
		super(getSymbolValue(aSymbol));
		realValue = (String) aSymbol.getValue();
	}
	
	public String getRealValue() {
		return realValue;
	}
	
	private static String getSymbolValue(Symbol aSymbol) {
		String res; 
		if (aSymbol.getType().equals(LexicalUnit.VARNAME)) {
			res = "[VarName]";
		} else if (aSymbol.getType().equals(LexicalUnit.NUMBER)) {
			res= "[Number]";
		} else if (aSymbol.getType().equals(LexicalUnit.END_OF_STREAM)) {
			res = null;
		} else {
			res = aSymbol.getValue().toString();
		}
		return res;
		
	}
}
