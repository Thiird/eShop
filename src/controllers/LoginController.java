package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.Customer;
import models.Employee;
import models.User;

public class LoginController implements Serializable, Initializable
{
	private static final long serialVersionUID = -7176050266479335730L;

	@FXML
	private Pane container;

	@FXML
	private TextField email;

	@FXML
	private PasswordField password;

	@FXML
	private Button btnLogin;

	@FXML
	private Button btnSignUp;

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

	public void switchToRegistration()
	{
		((Stage) container.getScene().getWindow()).close();

		openView("/views/Registration.fxml", "Registration");
	}

	public void checkInput()
	{
		if ( isAllCompiled() )
		{
			User user = getUser();

			if ( user == null )
			{
				clearFields();

				alert(AlertType.WARNING, "Warning", "Invalid email/password");
			}
			else
			{
				if ( user instanceof Customer )
				{
					((Stage) container.getScene().getWindow()).close();

					openView("/views/Shop.fxml", "Shop");

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

			alert(AlertType.WARNING, "Warning", "All fields must be completed");
		}
	}

	public void openView(String viewPath, String viewTitle)
	{
		Parent parent = null;

		try
		{
			FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath));
			parent = loader.load();

			if ( viewTitle.equalsIgnoreCase("Shop") )
				sendDataToShoppingCartController(loader, (Customer) getUser());
		}
		catch ( IOException e )
		{
			System.err.println("switchToView IOException");
		}

		Scene scene = new Scene(parent);

		Stage stage = new Stage();

		stage.centerOnScreen();
		stage.setResizable(false);
		stage.setTitle(viewTitle);
		stage.setScene(scene);
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/generics/eShop.png")));
		stage.show();
	}

	private void sendDataToShoppingCartController(FXMLLoader loader, Customer customer)
	{
		ShopController controller = loader.getController();
		controller.setData(customer);
	}

	private User getUser()
	{
		User user = null;

		Map <String,User> users = getUsers();

		if ( users != null )
			user = users.get(email.getText());

		if ( user == null )
			return null;

		return (user.getPassword().equalsIgnoreCase(password.getText())) ? user : null;
	}

	@SuppressWarnings ( "unchecked" )
	private static final Map <String,User> getUsers()
	{
		Map <String,User> users = null;

		URL resource = LoginController.class.getClass().getResource("/databases/users.txt");
		File file = null;
		try
		{
			file = new File(resource.toURI());
		}
		catch ( URISyntaxException e1 )
		{
			e1.printStackTrace();
		}

		try ( FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis); )
		{
			users = (HashMap <String,User>) ois.readObject();
		}
		catch ( IOException e )
		{
			return null;
		}
		catch ( ClassNotFoundException e )
		{
			System.err.println("ClassNotFoundException !");
		}

		return users;
	}

	private static final Optional <ButtonType> alert(AlertType type, String title, String header)
	{
		Alert alert = new Alert(type);

		alert.setTitle(title);
		alert.setHeaderText(header);

		return alert.showAndWait();
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
}