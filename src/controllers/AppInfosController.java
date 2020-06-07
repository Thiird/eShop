package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

public class AppInfosController extends Controller implements Initializable
{
	@FXML
	private Pane container;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		Platform.runLater(() -> container.requestFocus());
	}
}
