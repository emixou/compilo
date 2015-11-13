import java.lang.Exception;


public class SyntaxErrorException extends Exception {
	public SyntaxErrorException(String msg) {
		super("Illegal token : " + msg);
	}
}