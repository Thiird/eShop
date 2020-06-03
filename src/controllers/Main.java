package controllers;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application
{
	@Override
	public void start(Stage stage) throws Exception
	{
		Controller.openView("/views/Login.fxml", "Login");
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}