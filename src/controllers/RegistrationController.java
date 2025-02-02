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

public class RegistrationController extends Controller implements Initializable
{
	@FXML
	private Pane container;

	@FXML
	private TextField name, surname, address, CAP, city, phone, email;

	@FXML
	private PasswordField password;

	@FXML
	private ComboBox <PaymentMethod> paymentMethod;

	@FXML
	private CheckBox fidelityCard;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		Platform.runLater(() -> container.requestFocus());

		ObservableList <PaymentMethod> paymentMethods = FXCollections.observableArrayList(PaymentMethod.values());

		paymentMethod.setItems(paymentMethods);
	}

	public void checkInput()
	{
		if ( isAllCompiled() )
		{
			FidelityCard fC = null;

			if ( fidelityCard.isSelected() )
				fC = new FidelityCard(getNextFidelityCardID());

			Customer customer = new Customer(name.getText(), surname.getText(), address.getText(), CAP.getText(),
					city.getText(), phone.getText(), email.getText(), password.getText(), fC, paymentMethod.getValue());

			if ( isAlreadyExists(customer) )
			{
				clearFields();

				alertWarning(AlertType.WARNING, "Warning", "User already exists");
			}
			else
			{
				insertCustomer(customer);

				alertWarning(AlertType.INFORMATION, "Information", "Registration completed");

				((Stage) container.getScene().getWindow()).close();
			}
		}
		else
			alertWarning(AlertType.WARNING, "Warning", "All fields must be completed");
	}

	private boolean isAllCompiled()
	{
		if ( name.getText().isEmpty() || surname.getText().isEmpty() || address.getText().isEmpty()
				|| CAP.getText().isEmpty() || city.getText().isEmpty() || phone.getText().isEmpty()
				|| email.getText().isEmpty() || password.getText().isEmpty() )
			return false;

		return true;
	}

	private boolean isAlreadyExists(Customer customer)
	{
		Map <String,User> users = getUsers();

		if ( users != null && users.get(customer.getEmail()) != null )
			return true;

		return false;
	}

	private void insertCustomer(Customer customer)
	{
		Map <String,User> users = getUsers();

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