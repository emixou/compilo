package grammarTools;

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
	public boolean equals(Object obj){
		//System.err.println(this.value + " == " + ((Token) aToken).value);
		if (obj instanceof Token ) {
			return this.value.equals(((Token) obj).value);
		} else if (obj instanceof String) {
			return this.value.equals((String) obj);
		}
		return false;
	}
	
	@Override
    public int hashCode() {
        int hash = value.hashCode();
        return hash;
    }
	
}
