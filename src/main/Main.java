package main;

import controllers.Controller;
import controllers.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application
{
	@Override
	public void start(Stage stage) throws Exception
	{
		Controller.openView("/views/Login.fxml", "Login");

		LoginController.showStage();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}