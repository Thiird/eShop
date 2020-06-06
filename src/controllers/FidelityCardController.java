package controllers;

import java.text.SimpleDateFormat;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import models.Customer;

public class FidelityCardController extends Controller
{
	@FXML
	private Label lblID, lblReleaseDate, lblPoints, lblCustomerName;

	public void setData(Customer customer)
	{
		setCurrentUser(customer);
		setFields();
	}

	private void setFields()
	{
		lblCustomerName.setText(getCurrentUser().getName());

		lblID.setText("ID: " + String.valueOf(((Customer) getCurrentUser()).getFidelityCard().getID()));
		lblReleaseDate.setText("Release date: " + new SimpleDateFormat("dd/MM/yyyy")
				.format(((Customer) getCurrentUser()).getFidelityCard().getReleaseDate()));
		lblPoints.setText("Points: " + String.valueOf(((Customer) getCurrentUser()).getFidelityCard().getPoints()));
	}
}