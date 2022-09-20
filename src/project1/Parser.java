package project1;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project1.Node.AstKeyword;
import project1.Token.Terminal;

public class Parser {
	
	private ListIterator<Token> tokenIter;
	
	public static List<Token> tokenize(String input) {
		List<Token> tokens = new LinkedList<>();
		Matcher matcher = Pattern.compile("\\\"[\\S ]*\\\"|[(),;:.]|\\w+").matcher(input);
		while(matcher.find()) {
			String s = matcher.group();
			Terminal t;
			Object v = null;
			switch(s) {
				case ",": t = Terminal.COMMA; break;
				case "(": t = Terminal.LPAREN; break;
				case ")": t = Terminal.RPAREN; break;
				case ":": t = Terminal.COLON; break;
				case ";": t = Terminal.SEMICOLON; break;
				case ".": t = Terminal.PERIOD; break;
				case "End": t = Terminal.END; break;
				case "Window": t = Terminal.WINDOW; break;
				case "Layout": t = Terminal.LAYOUT; break;
				case "Flow": t = Terminal.FLOW; break;
				case "Grid": t = Terminal.GRID; break;
				case "Button": t = Terminal.BUTTON; break;
				case "Group": t = Terminal.GROUP; break;
				case "Label": t = Terminal.LABEL; break;
				case "Panel": t = Terminal.PANEL; break;
				case "Textfield": t = Terminal.TEXTFIELD; break;
				case "Radio": t = Terminal.RADIO; break;
				default: t = null;
			}
			if(t == null) {
				Matcher m;
				if((m = Pattern.compile("\\d+").matcher(s)).matches()) {
					t = Terminal.NUMBER;
					v = Integer.parseInt(m.group());
				} else if((m = Pattern.compile("\\\"([\\S ]*)\\\"").matcher(s)).matches()) {
					t = Terminal.STRING;
					v = m.group(1);
				} else {
					throw new ParseException(s + " is not a valid token");
				}
			}
			tokens.add(new Token(t, v));
		}
		tokens.add(new Token(Terminal.ENDOFTOKENS));
		return tokens;
	}
	
	public Node parse(String input) {
		return parse(tokenize(input));
	}
	
	public Node parse(List<Token> tokens) {
		tokenIter = tokens.listIterator();
		Node root = guiGrammar();
		return root;
	}
	
	private Node guiGrammar() {
		try {
			assertT(Terminal.WINDOW);
			Node window = new Node(AstKeyword.WINDOW);
			window.add(AstKeyword.STRING, assertT(Terminal.STRING).value);
			assertT(Terminal.LPAREN);
			window.add(AstKeyword.NUMBER, assertT(Terminal.NUMBER).value);
			assertT(Terminal.COMMA);
			window.add(AstKeyword.NUMBER, assertT(Terminal.NUMBER).value);
			assertT(Terminal.RPAREN);
			
			assertT(Terminal.END);
			assertT(Terminal.PERIOD);
			return window;
		} catch(ParseException e) {
			throw new ParseException("Error in gui grammar: " + e.getMessage());
		}
	}
	
	private boolean match(Terminal t, Token ret) {
		if(tokenIter.hasNext()) {
			Token token = tokenIter.next();
			ret.set(token);
			if(token.key == t) {
				return true;
			}
		}
		return false;
	}
	
	private boolean match(Terminal t) {
		Token token = tokenIter.next();
		if(token.key == t) {
			return true;
		}
		return false;
	}
	
	private Token assertT(Terminal t) {
		Token ret = new Token();
		boolean b = match(t, ret);
		if(!b) {
			throw new ParseException("expected " + t + " but got " + ret.key + (ret.value == null ? "" : ": " + ret.value));
		}
		return ret;
	}
	
	private static class ParseException extends RuntimeException {
		public ParseException() {
			super();
		}
		public ParseException(String message) {
			super(message);
		}
	}
	
}
