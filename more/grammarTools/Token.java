package grammarTools;

import scanner.Symbol;

public abstract class Token {

	private String value;

	public Token(String value){
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object aToken){
		//System.err.println(this.value + " == " + ((Token) aToken).value);
		return this.value.equals(((Token) aToken).value);
	}
	
	@Override
    public int hashCode() {
        int hash = value.hashCode();
        return hash;
    }
	
}
