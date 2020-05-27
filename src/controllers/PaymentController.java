package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleGroup;
import models.Customer;

public class PaymentController extends Controller<Customer> implements Initializable
{
	@FXML
	private ToggleGroup paymentMethods;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{

	}
}