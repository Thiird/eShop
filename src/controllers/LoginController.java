package controllers;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.Customer;
import models.Employee;
import models.User;

public class LoginController extends Controller implements Serializable, Initializable
{
	private static final long serialVersionUID = -7176050266479335730L;

	@FXML
	private Pane container;

	@FXML
	private TextField email;

	@FXML
	private PasswordField password;

	@FXML
	private Button btnLogin, btnSignUp;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		Platform.runLater(() ->
		{
			container.requestFocus();
			setCloseEvent();
		});

		initEventHandlers();
	}

	public void initEventHandlers()
	{
		btnLogin.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler <Event>()
		{
			@Override
			public void handle(Event event)
			{
				checkInput();
			}
		});

		btnLogin.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler <KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if ( event.getCode() == KeyCode.ENTER )
					checkInput();
			}
		});

		btnSignUp.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler <Event>()
		{
			@Override
			public void handle(Event event)
			{
				switchToRegistration();
			}

		});

		btnSignUp.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler <KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if ( event.getCode() == KeyCode.ENTER )
					switchToRegistration();
			}
		});
	}

	public void switchToRegistration()
	{
		((Stage) container.getScene().getWindow()).close();

		openView("/views/Registration.fxml", "Registration");
	}

	public void checkInput()
	{
		if ( isAllCompiled() )
		{
			User user = checkUser();

			if ( user == null )
			{
				clearFields();

				alertWarning(AlertType.WARNING, "Warning", "Invalid email/password");
			}
			else
			{
				if ( user instanceof Customer )
				{
					((Stage) container.getScene().getWindow()).close();

					((ShopController) openView("/views/Shop.fxml", "Shop")).setData((Customer) user);
				}
				else if ( user instanceof Employee )
				{
					((Stage) container.getScene().getWindow()).close();

					openView("/views/Employee.fxml", "Employee");
				}
			}
		}
		else
		{
			clearFields();

			alertWarning(AlertType.WARNING, "Warning", "All fields must be completed");
		}
	}

	private User checkUser()
	{
		User user = null;

		Map <String,User> users = getUsers();

		if ( users != null )
			user = users.get(email.getText());

		if ( user == null )
			return null;

		return (user.getPassword().equalsIgnoreCase(password.getText())) ? user : null;
	}

	private boolean isAllCompiled()
	{
		return (!email.getText().isEmpty() && !password.getText().isEmpty()) ? true : false;
	}

	private void clearFields()
	{
		email.clear();
		password.clear();
	}

	public void setCloseEvent()
	{
		((Stage) container.getScene().getWindow()).setOnCloseRequest(new EventHandler <WindowEvent>()
		{
			@Override
			public void handle(WindowEvent event)
			{
				if ( !alertPrompt(AlertType.CONFIRMATION, "Logout",
						"Are you sure you want to close the application?\nNon saved data will be lost.") )
				{
					event.consume();
				}
			}
		});
	}
}