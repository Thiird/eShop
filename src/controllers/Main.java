package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application
{
	@Override
	public void start(Stage stage) throws Exception
	{
		Parent root = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));

		Scene scene = new Scene(root);

		stage.centerOnScreen();
		stage.setResizable(false);
		stage.setTitle("Login");
		stage.setScene(scene);
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/generics/eShop.png")));
		stage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}