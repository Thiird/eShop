package controllers;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import models.Product;
import models.ShoppingCart;

public class PaymentController extends Controller implements Initializable
{
	@FXML
	private ToggleGroup paymentMethods;

	Map <String,Product> products;
	ShoppingCart shoppingCart;

	@FXML
	private Button btnPay;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		btnPay.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler <Event>()
		{
			@Override
			public void handle(Event event)
			{
				goToPayment();
			}
		});
	}

	public void setData(ShoppingCart cart, HashMap <String,Product> products)
	{
		this.shoppingCart = cart;
		this.products = products;

		for ( String s : products.keySet() )
		{
			System.out.println(s);
		}
	}

	private void goToPayment()
	{
		Map <String,ArrayList <ShoppingCart>> shoppingCarts = getShoppingCarts(null);

		String email = shoppingCart.getCustomer().getEmail();
		ArrayList <ShoppingCart> carts;

		shoppingCart.setExpectedDate(randomDate());
		// shoppingCart.getPaymentMethod();

		// Add cart to structure
		if ( shoppingCarts.containsKey(email) )
			carts = shoppingCarts.get(email);
		else
			carts = new ArrayList <ShoppingCart>();

		carts.add(shoppingCart);
		shoppingCarts.put(email, carts);

		// Controllo file aperto

		// Write on file
		setShoppingCarts(shoppingCarts);
		setProducts(products);

		System.out.println("SCRITTO");
	}

	private Date randomDate()
	{
		Random random = new Random();

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

		int minDay = (int) LocalDate.of(currentYear, currentMonth, 5).toEpochDay();
		int maxDay = (int) LocalDate.of(currentYear, currentMonth, 5 + random.nextInt((10 - 1) + 1) + 1).toEpochDay();

		long randomDay = minDay + random.nextInt(maxDay - minDay);

		LocalDate randomBirthDate = LocalDate.ofEpochDay(randomDay);

		Instant instant = Instant.from(randomBirthDate.atStartOfDay(ZoneId.of("GMT")));
		Date date = Date.from(instant);

		return date;
	}
}