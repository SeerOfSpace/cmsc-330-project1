package project1;

import java.util.List;

import javafx.application.Platform;

public class Test {

	public static void main(String[] args) {
		test1();
	}
	
	static void test1() {
		String[] args = new String[] {Main.class.getResource("/project1/testfile.txt").getPath()};
		Main.main(args);
	}
	
	static void test2() {
		String input = "Window \"Calculator\" (200, 200) Layout Flow:\r\n" + 
				"Textfield 20;\r\n" + 
				"Panel Layout Grid(4, 3, 5, 5):\r\n" + 
				"Button \"7\";\r\n" + 
				"Button \"8\";\r\n" + 
				"Button \"9\";\r\n" + 
				"Button \"4\";\r\n" + 
				"Button \"5\";\r\n" + 
				"Button \"6\";\r\n" + 
				"Button \"1\";\r\n" + 
				"Button \"2\";\r\n" + 
				"Button \"3\";\r\n" + 
				"Label \"\";\r\n" + 
				"Button \"0\";\r\n" + 
				"End;\r\n" + 
				"End.";
		List<Token> tokens = Parser.tokenize(input);
		tokens.forEach(System.out::println);
		
		System.out.println();
		Node root = new Parser().parse(input);
		System.out.println(root.treeToString2(1));
		Platform.exit();
	}
	
}
