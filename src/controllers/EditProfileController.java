package controllers;

import java.net.URL;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.Customer;
import models.FidelityCard;
import models.User;

public class EditProfileController extends Controller implements Initializable
{
	@FXML
	private Pane container;
	@FXML
	private TextField name, surname, address, CAP, city, phone, password;
	@FXML
	private CheckBox fidelityCard;
	@FXML
	private Button save;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		Platform.runLater(() -> container.requestFocus());
	}

	private void setFields(Customer customer)
	{
		name.setPromptText(customer.getName());
		surname.setPromptText(customer.getSurname());
		address.setPromptText(customer.getAddress());
		CAP.setPromptText(customer.getCAP());
		city.setPromptText(customer.getCity());
		phone.setPromptText(customer.getPhone());
		password.setPromptText(customer.getPassword());
		fidelityCard.setSelected(customer.getFidelityCard() == null ? false : true);

		if ( customer.getFidelityCard() != null )
			fidelityCard.setVisible(false);
	}

	@FXML
	public void updateUser()
	{
		if (alertPrompt(AlertType.INFORMATION, "Information","Are you sure about the changes ?"))
		{
			if ( name.getText().length() != 0 )
				getCurrentUser().setName(name.getText());

			if ( surname.getText().length() != 0 )
				getCurrentUser().setSurname(surname.getText());

			if ( address.getText().length() != 0 )
				getCurrentUser().setAddress(address.getText());

			if ( CAP.getText().length() != 0 )
				getCurrentUser().setCAP(CAP.getText());

			if ( city.getText().length() != 0 )
				getCurrentUser().setCity(city.getText());

			if ( phone.getText().length() != 0 )
				getCurrentUser().setPhone(phone.getText());

			if ( password.getText().length() != 0 )
				getCurrentUser().setPassword(password.getText());

			if ( fidelityCard.isSelected() )
				((Customer) getCurrentUser()).setFidelityCard(new FidelityCard(getNextFidelityCardID()));

			// Update user info on file
			Map <String,User> users = getUsers();
			users.replace(getCurrentUser().getEmail(), getCurrentUser());
			setUsers(users);

			((Stage) container.getScene().getWindow()).close();	
		}
	}

	public void setData(Customer customer)
	{
		setCurrentUser(customer);
		setFields(customer);
	}
}