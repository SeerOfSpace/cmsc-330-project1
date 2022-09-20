package project1;

import java.util.List;

public class Main {
	
	public static void main(String[] args) {
		String input = "Window \"Calculator\" (200, 200) Layout Flow:\n"
				+ "Textfield 20:\n"
				+ "Panel Layout Grid(4, 3, 5, 5):\n"
				+ "Button \"7\";\n"
				+ "Button \"8\";\n"
				+ "Label \"\";\n"
				+ "End;\n"
				+ "End.";
		List<Token> tokens = Parser.tokenize(input);
		tokens.forEach(System.out::println);
		
		System.out.println();
		Node root = new Parser().parse("Window \"Calculator\" (200, 200) End.");
		System.out.println(root.inOrder());
	}
	
}
