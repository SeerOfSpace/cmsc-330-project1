package project1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static Node rootNode;
	private Pane rootPane;
	
	public static void main(String[] args) {
		args = new String[] {Main.class.getResource("/project1/testfile.txt").getPath()};
		if(!loadWithArgs(args)) {
			Platform.exit();
			return;
		}
		launch(args);
		//test();
	}
	
	@Override
	public void start(Stage primaryStage) {	
		setup();
		Scene scene = new Scene(rootPane);
		primaryStage.setScene(scene);
		primaryStage.setTitle(rootNode.nodes.get(0).getString());
		primaryStage.show();
	}
	
	private static boolean loadWithArgs(String[] args) {
		if(args.length != 1) {
			System.out.println("Error: This application takes one and only one argument for a file path");
			return false;
		}
		try {
			load(args[0]);
		} catch (IOException e) {
			System.out.println("Error: Invalid file path");
			return false;
		}
		return true;
	}
	
	private static void load(String path) throws IOException {
		File file = new File(path);
		String input = new String(Files.readAllBytes(file.toPath()));
		rootNode = new Parser().parse(input);
	}
	
	private void setup() {
		rootPane = processLayout(rootNode.nodes.get(3), null);
		int width = rootNode.nodes.get(1).getInt();
		int height = rootNode.nodes.get(2).getInt();
		rootPane.setPrefWidth(width);
		rootPane.setMaxWidth(width);
		rootPane.setMinWidth(width);
		rootPane.setPrefHeight(height);
		rootPane.setMaxHeight(height);
		rootPane.setMinHeight(height);
		List<Parent> widgets = processWidgets(rootNode.nodes.get(4), rootPane);
		addWidgets(rootPane, widgets);
	}
	
	private Pane processLayout(Node paneNode, Pane parent) {
		Pane pane;
		switch(paneNode.key) {
			case FLOW:
				pane = new FlowPane();
				FlowPane flowPane = (FlowPane) pane;
				flowPane.setHgap(10);
				flowPane.setVgap(10);
				flowPane.setAlignment(Pos.CENTER);
				if(parent instanceof FlowPane) {
					flowPane.setPrefWidth(((FlowPane) parent).getPrefWidth());
				}
				break;
			case GRID:
				int[] args = {0,0,0,0};
				List<Node> numberNodes = paneNode.nodes;
				for(int i = 0; i < numberNodes.size(); i++) {
					args[i] = numberNodes.get(i).getInt();
				}
				pane = new MyGridPane(args[0], args[1], args[2], args[3]);
				break;
			default:
				pane = null;
				break;
		}
		return pane;
	}
	
	private List<Parent> processWidgets(Node widgetsNode, Pane parent) {
		List<Parent> widgets = new ArrayList<>();
		for(Node widgetNode : widgetsNode.nodes) {
			switch(widgetNode.key) {
				case BUTTON:
					widgets.add(new Button(widgetNode.nodes.get(0).getString()));
					break;
				case RADIOS:
					ToggleGroup group = new ToggleGroup();
					for(Node stringNode : widgetNode.nodes) {
						RadioButton radioButton = new RadioButton(stringNode.getString());
						radioButton.setToggleGroup(group);
						widgets.add(radioButton);
					}
					break;
				case LABEL:
					widgets.add(new Label(widgetNode.nodes.get(0).getString()));
					break;
				case PANEL:
					Pane p = processLayout(widgetNode.nodes.get(0), parent);
					List<Parent> w = processWidgets(widgetNode.nodes.get(1), p);
					addWidgets(p, w);
					widgets.add(p);
					break;
				case TEXTFIELD:
					TextField textField = new TextField();
					textField.setPrefWidth(widgetNode.nodes.get(0).getInt());
					widgets.add(textField);
					break;
				default:
					break;
			}
		}
		return widgets;
	}
	
	private void addWidgets(Pane pane, List<Parent> widgets) {
		if(pane instanceof MyGridPane) {
			((MyGridPane) pane).addAll(widgets);
		} else {
			pane.getChildren().addAll(widgets);
		}
	}
	
	private static class MyGridPane extends GridPane {
		
		private int rows;
		private int cols;
		
		public MyGridPane(int rows, int cols, int hgap, int vgap) {
			super();
			this.rows = rows;
			this.cols = cols;
			this.setHgap(hgap);
			this.setVgap(vgap);
			RowConstraints rowConstraints = new RowConstraints();
			rowConstraints.setVgrow(Priority.SOMETIMES);
			rowConstraints.setValignment(VPos.CENTER);
			ColumnConstraints colConstraints = new ColumnConstraints();
			colConstraints.setHgrow(Priority.SOMETIMES);
			colConstraints.setHalignment(HPos.CENTER);
			for(int i = 0; i < rows; i++) {
				this.getRowConstraints().add(rowConstraints);
			}
			for(int i = 0; i < cols; i++) {
				this.getColumnConstraints().add(colConstraints);
			}
		}
		
		public void addAll(List<Parent> children) {
			int index = 0;
			for(int i = 0; i < rows; i++) {
				for(int j = 0; j < cols; j++) {
					if(index >= children.size()) {
						return;
					}
					Parent child = children.get(index);
					this.add(child, j, i);
					index++;
				}
			}
		}
	}
	
}
