package project1;

public class Token {
	
	public enum Terminal {
		COMMA, LPAREN, RPAREN, COLON, SEMICOLON, PERIOD, END,
		STRING, NUMBER,
		WINDOW, LAYOUT, FLOW, GRID, BUTTON, GROUP, LABEL, PANEL, TEXTFIELD, RADIO,
		ENDOFTOKENS;
	}
	
	public Terminal key;
	public Object value;
	
	public Token(Terminal key, Object value) {
		this.key = key;
		this.value = value;
	}
	
	public Token(Terminal key) {
		this(key, null);
	}
	
	public Token() {
		this(null, null);
	}
	
	public void set(Terminal key, Object value) {
		this.key = key;
		this.value = value;
	}
	
	public void set(Token other) {
		set(other.key, other.value);
	}
	
	@Override
	public String toString() {
		return key + (value == null ? "" : ": " + value);
	}
	
}
