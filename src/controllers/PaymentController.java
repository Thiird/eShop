package controllers;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.Customer;
import models.PaymentMethod;
import models.Product;
import models.ShoppingCart;
import models.User;

public class PaymentController extends Controller implements Initializable
{
	@FXML
	private ToggleGroup paymentMethods;
	private ShopController shopController;

	@FXML
	private RadioButton rbCreditCard, rbPaypal, rbOnDelivery;

	@FXML
	private Button btnPay;

	@FXML
	private ComboBox <Date> deliveryDate;

	@FXML
	private Pane container;

	private Map <String,Product> products;
	private ShoppingCart shoppingCart;

	private Map <Product,ArrayList <Integer>> unavailableProds;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		Platform.runLater(() ->
		{
			generateDeliveryDates();
			selectPreferredPaymentMethod();
			initEventHandlers();
		});

		unavailableProds = new HashMap <Product,ArrayList <Integer>>();
	}

	private void initEventHandlers()
	{
		btnPay.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler <Event>()
		{
			@Override
			public void handle(Event event)
			{
				pay();
			}
		});

		// Init radio buttons
		rbCreditCard.setUserData(PaymentMethod.CREDIT_CARD);
		rbPaypal.setUserData(PaymentMethod.PAYPAL);
		rbOnDelivery.setUserData(PaymentMethod.ON_DELIVERY);

		rbCreditCard.setToggleGroup(paymentMethods);
		rbPaypal.setToggleGroup(paymentMethods);
		rbOnDelivery.setToggleGroup(paymentMethods);

		rbCreditCard.setTooltip(new Tooltip("Credit card"));
		rbPaypal.setTooltip(new Tooltip("Paypal"));
		rbOnDelivery.setTooltip(new Tooltip("On delivery"));

		// Toogle event
		paymentMethods.selectedToggleProperty().addListener(new ChangeListener <Toggle>()
		{
			public void changed(ObservableValue <? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle)
			{
				if ( paymentMethods.getSelectedToggle() != null )
					shoppingCart.setPaymentMethod((PaymentMethod) paymentMethods.getSelectedToggle().getUserData());
			}
		});
	}

	private void pay()
	{
		if ( allFieldsFilled() )
		{
			if ( checkProductAvailability() )
			{
				Map <String,ArrayList <ShoppingCart>> shoppingCarts = getShoppingCarts(null);

				// Setup cart before saving on file
				shoppingCart.setPaymentMethod((PaymentMethod) paymentMethods.getSelectedToggle().getUserData());
				shoppingCart.setID(getNextCartID(shoppingCarts));
				shoppingCart.setExpectedDate(deliveryDate.getSelectionModel().getSelectedItem());

				// Adds points to fidelity card
				if ( ((Customer) getCurrentUser()).getFidelityCard() != null )
					((Customer) getCurrentUser()).getFidelityCard().addPoints((int) shoppingCart.getTotalPrice());

				String email = shoppingCart.getCustomer().getEmail();
				ArrayList <ShoppingCart> carts;

				// Add cart to structure
				if ( shoppingCarts.containsKey(email) )
					carts = shoppingCarts.get(email);
				else
					carts = new ArrayList <ShoppingCart>();

				carts.add(shoppingCart);
				shoppingCarts.put(email, carts);

				updateProducts();

				// Write on file
				Map <String,User> users = getUsers();
				users.replace(getCurrentUser().getName(), getCurrentUser());
				setUsers(users);
				setShoppingCarts(shoppingCarts);
				setProducts(products);

				shopController.reloadProducts();

				alertWarning(AlertType.INFORMATION, "Payment", "Transaction complete.\nThank you for your purchase!");
			}

			shopController.clearApp();

			((Stage) container.getScene().getWindow()).close();
		}
	}

	private void updateProducts()
	{
		products = getProducts();

		// Check for unavailable products
		for ( Product p : shoppingCart.getProducts().keySet() )
		{
			for ( String s : products.keySet() )
			{
				if ( products.get(s).getName().equals(p.getName()) )
				{
					products.get(s)
							.setQtyAvailable(products.get(s).getQtyAvailable() - shoppingCart.getProducts().get(p));
				}
			}
		}
	}

	private int getNextCartID(Map <String,ArrayList <ShoppingCart>> shoppingCarts)
	{// Loads nextCartID as lastCartID + 1

		int lastID = 0;

		for ( String client : shoppingCarts.keySet() )
		{
			for ( ShoppingCart sc : shoppingCarts.get(client) )
			{
				if ( sc.getID() > lastID )
					lastID = sc.getID();
			}
		}

		lastID++;

		return lastID;
	}

	private void selectPreferredPaymentMethod()
	{
		if ( ((Customer) getCurrentUser()).getPreferredPaymentMethod() != null )
		{
			switch ( ((Customer) getCurrentUser()).getPreferredPaymentMethod() )
			{
				case CREDIT_CARD:
					rbCreditCard.setSelected(true);
					break;
				case PAYPAL:
					rbPaypal.setSelected(true);
					break;
				case ON_DELIVERY:
					rbOnDelivery.setSelected(true);
					break;
			}
		}
	}

	public void setData(Customer c, ShoppingCart cart, HashMap <String,Product> products, ShopController sc)
	{
		this.setCurrentUser(c);
		this.shoppingCart = cart;
		this.products = products;
		this.shopController = sc;
	}

	private boolean allFieldsFilled()
	{
		String msg = "";

		if ( deliveryDate.getSelectionModel().getSelectedItem() == null )
			msg += "You must select a delivery date.";

		if ( (paymentMethods.getSelectedToggle()) == null )
		{
			if ( !msg.isEmpty() )
				msg += "\n";

			msg += "You must select a payment method.";
		}

		if ( !msg.isEmpty() )
		{
			alertWarning(AlertType.WARNING, "Payment configuration", msg);
			return false;
		}

		return true;
	}

	private boolean checkProductAvailability()
	{// Before completing the payment, products are loaded from db to check if in the
		// meantime other users have rendered unavailable some products present in this
		// shoppingCart
		// Returns true if all products are available

		Map <String,Product> products = getProducts();

		int qtyRequested, qtyAvailable;

		// Check for unavailable products
		for ( Product p : shoppingCart.getProducts().keySet() )
		{
			for ( String s : products.keySet() )
			{
				if ( products.get(s).getName().equals(p.getName()) )
				{
					// Modify product to write to file
					qtyRequested = shoppingCart.getProducts().get(p);
					qtyAvailable = products.get(s).getQtyAvailable();

					if ( qtyAvailable < qtyRequested )
						unavailableProds.put(p, new ArrayList <>(Arrays.asList(qtyRequested, qtyAvailable)));
				}
			}
		}

		// Show warning
		if ( !unavailableProds.isEmpty() )
		{
			String msg = "The following cart products are not available anymore in the requested quantity, only the available quantity will be bought:\n\n";

			for ( Product p : unavailableProds.keySet() )
			{
				msg += p.getName() + ": requested: " + unavailableProds.get(p).get(0) + ", available: "
						+ unavailableProds.get(p).get(1);

				if ( unavailableProds.get(p).get(1) == 0 )
					shoppingCart.removeProduct(p);
				else
					shoppingCart.getProducts().replace(p, unavailableProds.get(p).get(1));
			}

			alertWarning(AlertType.WARNING, "Unavailable products", msg);

			if ( shoppingCart.getProducts().size() == 0 )
			{
				msg = "After removing the no more available products you shopping cart resulted empty.";
				alertWarning(AlertType.WARNING, "Unavailable products", msg);
				return false;
			}
		}

		return true;
	}

	private void generateDeliveryDates()
	{
		Instant deliveryInstant;
		String[] data;
		Calendar calendar;
		int hour;
		int half;

		for ( int i = 0; i < (new Random().nextInt((5 - 3) + 1) + 3); i++ )
		{
			deliveryInstant = Instant.now().plus(Duration.ofDays(i));

			hour = new Random().nextInt((18 - 9) + 1) + 9;
			half = new Random().nextInt((1 - 0) + 1) + 1;

			// System.out.println(
			// deliveryInstant.toString().substring(0, 10) + " | " + hour + ":" + (half == 1
			// ? "30" : "00"));

			data = deliveryInstant.toString().substring(0, 10).split("-");

			calendar = Calendar.getInstance();

			calendar.set(Calendar.YEAR, Integer.parseInt(data[0]));
			calendar.set(Calendar.MONTH, Integer.parseInt(data[1].replace("0", "")));
			calendar.set(Calendar.DATE, Integer.parseInt(data[2].replace("0", "")));
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, Integer.parseInt((half == 1 ? "30" : "00")));
			calendar.set(Calendar.SECOND, 0);

			deliveryDate.getItems().add(calendar.getTime());
		}
	}
}