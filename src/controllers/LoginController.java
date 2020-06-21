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
		Platform.runLater(() -> container.requestFocus());

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

	public void checkInput()
	{
		if ( isAllCompiled() )
		{
			User user = checkUser();

			if ( user == null )
			{
				clearFields();
			}
			else
			{
				if ( user instanceof Customer )
				{
					((Stage) container.getScene().getWindow()).close();

					((ShopController) openView("/views/Shop.fxml", "Shop")).setData((Customer) user);

					logInOutUser(true);

					ShopController.showStage();
				}
				else if ( user instanceof Employee )
				{
					((Stage) container.getScene().getWindow()).close();

					((EmployeeController) openView("/views/Employee.fxml", "Employee")).setData((Employee) user);

					logInOutUser(true);

					EmployeeController.showStage();
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
		Map <String,User> users = getUsers();

		User user = users.get(email.getText());

		if ( user == null )
		{
			alertWarning(AlertType.WARNING, "Warning", "Invalid email/password");
			return null;
		}

		if ( user.getPassword().equals(password.getText()) )
		{
			if ( user.getLoggedIn() )
			{
				alertWarning(AlertType.WARNING, "Login", "This user is already logged in!");
				return null;
			}
			else
				return user;
		}
		else
			alertWarning(AlertType.WARNING, "Warning", "Invalid password");

		return null;
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

	public void switchToRegistration()
	{
		openView("/views/Registration.fxml", "Registration");

		RegistrationController.showAndWaitStage();
	}
}