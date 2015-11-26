package grammarTools;

import scanner.LexicalUnit;
import scanner.Symbol;

public class Terminal extends Token{
	
	public Terminal(String value){
		super(value);
	}
	
	public Terminal(Symbol aSymbol) {
		super(getSymbolValue(aSymbol));
	}
	
	private static String getSymbolValue(Symbol aSymbol) {
		String res; 
		if (aSymbol.getType().equals(LexicalUnit.VARNAME)) {
			res = "[VarName]";
		} else if (aSymbol.getType().equals(LexicalUnit.NUMBER)) {
			res= "[Number]";
		} else {
			res = aSymbol.getValue().toString();
		}
		return res;
		
	}
}
