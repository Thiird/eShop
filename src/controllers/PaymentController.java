package controllers;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
	private Pane container;

	private Map <String,Product> products;
	private ShoppingCart shoppingCart;

	private Map <Product,ArrayList <Integer>> unavailableProds;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		Platform.runLater(() -> selectPreferredPaymentMethod());
		initEventHandlers();

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
				{
					shoppingCart.setPaymentMethod((PaymentMethod) paymentMethods.getSelectedToggle().getUserData());
				}
			}
		});
	}

	private void pay()
	{
		System.out.println("A");
		if ( allFieldsFilled() )
		{
			Map <String,ArrayList <ShoppingCart>> shoppingCarts = getShoppingCarts(null);

			String email = shoppingCart.getCustomer().getEmail();
			ArrayList <ShoppingCart> carts;

			shoppingCart.setExpectedDate(randomDate());

			// Add cart to structure
			if ( shoppingCarts.containsKey(email) )
				carts = shoppingCarts.get(email);
			else
				carts = new ArrayList <ShoppingCart>();

			carts.add(shoppingCart);
			shoppingCarts.put(email, carts);

			// Controllo file aperto
			checkProductAvailability();

			// Write on file
			setShoppingCarts(shoppingCarts);
			setProducts(products);

			shopController.clearApp();

			((Stage) container.getScene().getWindow()).close();
		}
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
		if ( paymentMethods.getSelectedToggle() == null )
		{
			alertWarning(AlertType.WARNING, "Payment method", "You must select a payment method to proceeed.");

			// TODO ADD DATE
			/*
			 * if ( ((PaymentMethod) paymentMethods.getSelectedToggle().getUserData()) ==
			 * null ) { alertWarning(AlertType.WARNING, "Payment method",
			 * "You must select a payment method to proceeed."); }
			 */

			return false;
		}

		return true;
	}

	private void checkProductAvailability()
	{// Before completing the payment, products are loaded from db to check if in the
		// meantime other users have rendered unavailable some products present in this
		// shoppingCart

		Map <String,Product> products = getProducts();

		for ( Product p : shoppingCart.getProducts().keySet() )
		{
			for ( String s : products.keySet() )
			{
				if ( products.get(s) == p && products.get(s).getQtyAvailable() < products.get(s).getQtyAvailable() )
					unavailableProds.put(p, new ArrayList <>(
							Arrays.asList(products.get(s).getQtyAvailable(), products.get(s).getQtyAvailable())));
			}
		}

		if ( !unavailableProds.isEmpty() )
		{
			String msg = "The following cart products are not available anymore in the requested quantity:\n";

			for ( Product p : unavailableProds.keySet() )
			{
				msg += p.getName() + ": qty available: " + unavailableProds.get(p).get(0) + ", reqeusted: "
						+ unavailableProds.get(p).get(1);

				shoppingCart.getProducts().remove(p);
			}

			alertWarning(AlertType.WARNING, "Unavailable products", msg);
		}
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