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
	
}
