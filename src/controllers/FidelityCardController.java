package controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import models.Customer;

public class FidelityCardController extends Controller
{
	@FXML
	private ImageView imageView;
	@FXML
	private Label ID, releaseDate, points;

	public void setData(Customer customer)
	{
		setCurrentUser(customer);
		setFields();
	}

	private void setFields()
	{
		ID.setText(String.valueOf(((Customer) getCurrentUser()).getFidelityCard().getID()));
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		releaseDate.setText(dateFormat.format(((Customer) getCurrentUser()).getFidelityCard().getReleaseDate()));
		points.setText(String.valueOf(((Customer) getCurrentUser()).getFidelityCard().getPoints()));
	}
}