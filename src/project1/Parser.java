package project1;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project1.Node.AstKey;
import project1.Token.Terminal;

public class Parser {
	
	private ListIterator<Token> tokenIter;
	
	public static List<Token> tokenize(String input) {
		List<Token> tokens = new LinkedList<>();
		Matcher matcher = Pattern.compile("\\\"[\\S ]*?\\\"|[(),;:.]|[^\\s(),;:.]+").matcher(input);
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
					throw new LexerException(s + " is not a valid token");
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
		assertT(Terminal.ENDOFTOKENS);
		return root;
	}
	
	private Node guiGrammar() {
		try {
			assertT(Terminal.WINDOW);
			Node window = new Node(AstKey.WINDOW);
			window.add(AstKey.STRING, assertT(Terminal.STRING));
			assertT(Terminal.LPAREN);
			window.add(AstKey.NUMBER, assertT(Terminal.NUMBER));
			assertT(Terminal.COMMA);
			window.add(AstKey.NUMBER, assertT(Terminal.NUMBER));
			assertT(Terminal.RPAREN);
			
			window.add(layoutGrammar());
			window.add(widgetsGrammar());
			
			assertT(Terminal.END);
			assertT(Terminal.PERIOD);
			return window;
		} catch(ParseException e) {
			throw new GrammarException("Error in gui grammar: ", e);
		}
	}
	
	private Node layoutGrammar() {
		try {
			assertT(Terminal.LAYOUT);
			Node layoutType = layoutTypeGrammar();
			assertT(Terminal.COLON);
			return layoutType;
		} catch(ParseException e) {
			throw new GrammarException("Error in layout grammar: ", e);
		}
	}
	
	private Node layoutTypeGrammar() {
		try {
			Node layoutType;
			if(match(Terminal.FLOW)) {
				layoutType = new Node(AstKey.FLOW);
			} else if(match(Terminal.GRID)) {
				layoutType = new Node(AstKey.GRID);
				assertT(Terminal.LPAREN);
				layoutType.add(AstKey.NUMBER, assertT(Terminal.NUMBER));
				assertT(Terminal.COMMA);
				layoutType.add(AstKey.NUMBER, assertT(Terminal.NUMBER));
				if(match(Terminal.COMMA)) {
					layoutType.add(AstKey.NUMBER, assertT(Terminal.NUMBER));
					assertT(Terminal.COMMA);
					layoutType.add(AstKey.NUMBER, assertT(Terminal.NUMBER));
					assertT(Terminal.RPAREN);
				} else if(match(Terminal.RPAREN)) {
					
				} else {
					throw new ParseException("expected COMMA | RPAREN but got " + tokenIter.next());
				}
			} else {
				throw new ParseException("expected FLOW | GRID but got " + tokenIter.next());
			}
			return layoutType;
		} catch(ParseException e) {
			throw new GrammarException("Error in layoutType grammar: ", e);
		}
	}
	
	private Node widgetsGrammar() {
		Node widgets = new Node(AstKey.WIDGETS);
		widgets.add(widgetGrammar(true));
		Node widget;
		while((widget = widgetGrammar(false)) != null) {
			widgets.add(widget);
		}
		return widgets;
	}
	
	private Node widgetGrammar(boolean throwFlag) {
		try {
			Node widget;
			if(match(Terminal.BUTTON)) {
				widget = new Node(AstKey.BUTTON);
				widget.add(AstKey.STRING, assertT(Terminal.STRING));
				assertT(Terminal.SEMICOLON);
			} else if(match(Terminal.GROUP)) {
				widget = radioButtonsGrammar();
				assertT(Terminal.END);
				assertT(Terminal.SEMICOLON);
			} else if(match(Terminal.LABEL)) {
				widget = new Node(AstKey.LABEL);
				widget.add(AstKey.STRING, assertT(Terminal.STRING));
				assertT(Terminal.SEMICOLON);
			} else if(match(Terminal.PANEL)) {
				widget = new Node(AstKey.PANEL);
				widget.add(layoutGrammar());
				widget.add(widgetsGrammar());
				assertT(Terminal.END);
				assertT(Terminal.SEMICOLON);
			} else if(match(Terminal.TEXTFIELD)) {
				widget = new Node(AstKey.TEXTFIELD);
				widget.add(AstKey.NUMBER, assertT(Terminal.NUMBER));
				assertT(Terminal.SEMICOLON);
			} else if(throwFlag) {
				throw new ParseException("expected BUTTON | GROUP | LABEL | PANEL | TEXTFIELD but got " + tokenIter.next());
			} else {
				widget = null;
			}
			return widget;
		} catch(ParseException e) {
			throw new GrammarException("Error in widget grammar: ", e);
		}
	}
	
	private Node radioButtonsGrammar() {
		Node radios = new Node(AstKey.RADIOS);
		radios.add(radioButtonGrammar(true));
		Node radio;
		while((radio = radioButtonGrammar(false)) != null) {
			radios.add(radio);
		}
		return radios;
	}
	
	private Node radioButtonGrammar(boolean throwFlag) {
		try {
			Node radio;
			if(match(Terminal.RADIO)) {
				radio = new Node(AstKey.STRING, assertT(Terminal.STRING));
				assertT(Terminal.SEMICOLON);
			} else if(throwFlag) {
				throw new ParseException("expected RADIO but got " + tokenIter.next());
			} else {
				radio = null;
			}
			return radio;
		} catch(ParseException e) {
			throw new GrammarException("Error in radio grammar: ", e);
		}
	}
	
	private boolean match(Terminal t, Token ret) {
		Token token = tokenIter.next();
		ret.set(token);
		if(token.key == t) {
			return true;
		}
		tokenIter.previous();
		return false;
	}
	
	private boolean match(Terminal t) {
		Token token = tokenIter.next();
		if(token.key == t) {
			return true;
		}
		tokenIter.previous();
		return false;
	}
	
	private Object assertT(Terminal t) {
		Token ret = new Token();
		boolean b = match(t, ret);
		if(!b) {
			throw new ParseException("expected " + t + " but got " + ret);
		}
		return ret.value;
	}
	
	private static class LexerException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public LexerException(String message) {
			super(message);
		}
	}
	
	private static class ParseException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public ParseException(String message) {
			super(message);
		}
	}
	
	private static class GrammarException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public GrammarException(String message, Exception e) {
			super(message + e.getMessage());
			this.setStackTrace(e.getStackTrace());
		}
	}
	
}
