package controllers;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.Customer;
import models.FidelityCard;
import models.PaymentMethod;
import models.User;

public class RegistrationController extends Controller<Customer> implements Initializable
{
	@FXML
	private Pane container;

	@FXML
	private TextField name, surname, address, CAP, city, phone, email;

	@FXML
	private PasswordField password;

	@FXML
	private ComboBox<PaymentMethod> paymentMethod;

	@FXML
	private CheckBox fidelityCard;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		Platform.runLater(() -> container.requestFocus());

		ObservableList<PaymentMethod> paymentMethods = FXCollections.observableArrayList(PaymentMethod.values());

		paymentMethod.setItems(paymentMethods);
	}

	public void checkInput()
	{
		if (isAllCompiled())
		{
			FidelityCard fC = null;

			if (fidelityCard.isSelected()) fC = new FidelityCard(0);//TODO new correct ID

			Customer customer = new Customer(name.getText(), surname.getText(), address.getText(), CAP.getText(), city.getText(), phone.getText(), email.getText(), password.getText(), fC,
					paymentMethod.getValue());

			if (isAlreadyExists(customer))
			{
				clearFields();

				alert(AlertType.WARNING, "Warning", "User already exists");
			}
			else
			{
				insertCustomer(customer);

				if (alert(AlertType.INFORMATION, "Information", "Registration completed").get() == ButtonType.OK)
				{
					((Stage) container.getScene().getWindow()).close();

					switchToView("../views/Login.fxml", "Login", null, null);
				}
			}
		}
		else alert(AlertType.WARNING, "Warning", "All fields must be completed");
	}

	public void returnToLogin()
	{
		((Stage) container.getScene().getWindow()).close();

		switchToView("../views/Login.fxml", "Login", null, null);
	}

	private boolean isAllCompiled()
	{
		if (name.getText().isEmpty()) return false;

		if (surname.getText().isEmpty()) return false;

		if (address.getText().isEmpty()) return false;

		if (CAP.getText().isEmpty()) return false;

		if (city.getText().isEmpty()) return false;

		if (phone.getText().isEmpty()) return false;

		if (email.getText().isEmpty()) return false;

		if (password.getText().isEmpty()) return false;

		return true;
	}

	private static final boolean isAlreadyExists(Customer customer)
	{
		Map<String, User> users = getUsers();

		if (users != null && users.get(customer.getEmail()) != null) return true;

		return false;
	}

	private static final void insertCustomer(Customer customer)
	{
		Map<String, User> users = getUsers();

		users.put(customer.getEmail(), customer);

		setUsers(users);
	}

	private void clearFields()
	{
		name.clear();
		surname.clear();
		address.clear();
		CAP.clear();
		city.clear();
		phone.clear();
		email.clear();
		password.clear();
		fidelityCard.setSelected(false);
	}
}