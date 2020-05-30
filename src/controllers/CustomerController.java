package controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.Customer;
import models.FidelityCard;
import models.User;

public class CustomerController extends Controller<Customer>
{
	@FXML
	private TabPane container;

	@FXML
	private Tab fidelityCardTab;

	// User information fields
	@FXML
	private TextField name, surname, address, CAP, city, phone, password;

	@FXML
	private CheckBox fidelityCard;

	@FXML
	private Button save;

	// Fidelity Card fields
	@FXML
	private ImageView fidelityCardFc;

	@FXML
	private Label ID, releaseDate, points;

	public void setData(Customer customer)
	{
		Platform.runLater(() -> container.requestFocus());

		// Initialize User information Tab
		setCurrentUser(customer);

		name.setPromptText(customer.getName());
		surname.setPromptText(customer.getSurname());
		address.setPromptText(customer.getAddress());
		CAP.setPromptText(customer.getCAP());
		city.setPromptText(customer.getCity());
		phone.setPromptText(customer.getPhone());
		password.setPromptText(customer.getPassword());
		fidelityCard.setSelected(customer.getFidelityCard() != null);

		if (customer.getFidelityCard() != null)
		{
			fidelityCard.setVisible(false);
			ID.setText(String.valueOf(getCurrentUser().getFidelityCard().getID()));
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			releaseDate.setText(dateFormat.format(getCurrentUser().getFidelityCard().getReleaseDate()));
			points.setText(String.valueOf(getCurrentUser().getFidelityCard().getPoints()));
		}
		else
		{
			fidelityCardFc.setVisible(false);
			container.getTabs().remove(fidelityCardTab);
		}
	}

	@FXML
	public void updateUser()
	{
		Optional<ButtonType> alert = alert(AlertType.INFORMATION, "Information", "Are you sure about the changes ?");

		try
		{
			if (alert.get() == ButtonType.OK)
			{
				if (name.getText().length() != 0) getCurrentUser().setName(name.getText());

				if (surname.getText().length() != 0) getCurrentUser().setSurname(surname.getText());

				if (address.getText().length() != 0) getCurrentUser().setAddress(address.getText());

				if (CAP.getText().length() != 0) getCurrentUser().setCAP(CAP.getText());

				if (city.getText().length() != 0) getCurrentUser().setCity(city.getText());

				if (phone.getText().length() != 0) getCurrentUser().setPhone(phone.getText());

				if (password.getText().length() != 0) getCurrentUser().setPassword(password.getText());

				if ((getCurrentUser().getFidelityCard() != null) == false && fidelityCard.isSelected()) getCurrentUser().setFidelityCard(new FidelityCard(0));//TODO put correct id

				Map<String, User> users = getUsers();

				users.replace(getCurrentUser().getEmail(), getCurrentUser());

				setUsers(users);

				((Stage) container.getScene().getWindow()).close();

				openView("../views/ShoppingCart.fxml", "Shopping Cart", getCurrentUser(), null );
			}
		}
		catch (NoSuchElementException e)
		{

		}
	}

	public void switchToShopping()
	{
		((Stage) container.getScene().getWindow()).close();

		openView("../views/ShoppingCart.fxml", "Shopping Cart", getCurrentUser(), null );
	}
}