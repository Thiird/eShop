package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import models.Customer;
import models.Product;
import models.ShoppingCart;
import models.Ward;

public class ShoppingCartController extends Controller<Customer> implements Initializable
{
	HashMap<ImageView, Product> imageToProduct = new HashMap<ImageView, Product>();
	HashMap<Product, ImageView> productToImage = new HashMap<Product, ImageView>();

	int xShop = 0;
	int yShop = 0;

	Pane selectedShopProduct;

	int xCart = 0;
	int yCart = 0;

	@FXML
	Button btnCartSearch;

	@FXML
	TextField cartSearchbar;

	@FXML
	Button btnShopSearch, btnGoToPayment, btnAddToCart;

	@FXML
	TextField shopSearchbar;

	@FXML
	ImageView currProdImage;

	Image noProductImage = new Image(getClass().getResourceAsStream("/icons/products/noProduct.png"));

	@FXML
	Label currProdName, isPAvaialble, currProdBrand, currProdPrice, currProdQty, currProdQtyAvailable;

	@FXML
	Label lblQuantity;

	@FXML
	TextField txtFldQuantity;

	@FXML
	AnchorPane container;

	@FXML
	Label email;

	@FXML
	ScrollPane shopPane;

	@FXML
	GridPane shopGridPane;

	@FXML
	ScrollPane cartPane;

	@FXML
	GridPane cartGridPane;

	@FXML
	ComboBox<Ward> wardSelection;

	private ShoppingCart shoppingCart;

	HashMap<ImageView, String> imageToProductName;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		initEventHandlers();
		initGridPanes();

		shoppingCart = new ShoppingCart(getCurrentUser(), getNextCartID());

		wardSelection.getItems().setAll(Ward.values());
		wardSelection.getSelectionModel().select(0);

		loadProducts();

		resetProductPanel();

		searchInShop();// To initially fill the shop grid

		Map<String, ArrayList<ShoppingCart>> shoppingCarts = getShoppingCarts();

		//new Thread(new Clock()).start();
	}

	private int getNextCartID()
	{
		int lastID = 0;
		Map<String, ArrayList<ShoppingCart>> shoppingCarts = getShoppingCarts();

		for (String client : shoppingCarts.keySet())
		{
			for (ShoppingCart sc : shoppingCarts.get(client))
			{
				if (sc.getID() > lastID) lastID = sc.getID();
			}
		}

		return lastID + 1;
	}

	private void resetProductPanel()
	{
		currProdImage.setImage(noProductImage);
		currProdName.setText("-");
		currProdPrice.setText("Price: - $");
		currProdBrand.setText("Brand: -");
		currProdQty.setText("Quantity per item: -");
		currProdQtyAvailable.setText("Items available: -");
	}

	private void loadProducts()
	{
		// Load imageToProdcut and productToImage
		Map<String, Product> products = getProducts();

		ImageView im;

		for (String s : products.keySet())
		{
			im = new ImageView(new Image((products.get(s).getImage())));

			imageToProduct.put(im, products.get(s));
			productToImage.put(products.get(s), im);
		}
	}

	private void initEventHandlers()
	{
		cartSearchbar.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if (event.getCode() == KeyCode.ENTER) searchInCart();
			}
		});

		btnCartSearch.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>()
		{
			@Override
			public void handle(Event event)
			{
				searchInCart();
			}
		});

		btnGoToPayment.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>()
		{
			@Override
			public void handle(Event event)
			{
				switchToView("../views/Payment.fxml", "Payment", null, null);

			}
		});

		// Shop search button
		btnShopSearch.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>()
		{
			@Override
			public void handle(Event event)
			{
				searchInShop();
			}
		});

		btnShopSearch.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if (event.getCode() == KeyCode.ENTER) searchInShop();
			}
		});

		// Add to cart button
		btnAddToCart.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>()
		{
			@Override
			public void handle(Event event)
			{
				addProductToCart();
			}
		});

		btnAddToCart.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if (event.getCode() == KeyCode.ENTER) addProductToCart();
			}
		});

		//Go to payment button
		btnGoToPayment.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>()
		{
			@Override
			public void handle(Event event)
			{
				buy();
			}
		});

		btnGoToPayment.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if (event.getCode() == KeyCode.ENTER) buy();
			}
		});

	}

	private void addProductToCart()
	{//Adds currently selected product to customer's cart

		int qtyToAdd = Integer.parseInt(txtFldQuantity.getText());

		if (qtyToAdd <= imageToProduct.get(selectedShopProduct.getChildren().get(0)).getQtyAvailable())
		{
			btnGoToPayment.setDisable(false);

			Product currToAddToCart = imageToProduct.get(selectedShopProduct.getChildren().get(0));

			//Add product to in gui list
			addItemToCartGrid(currToAddToCart, qtyToAdd);

			//Add product to cart obj
			shoppingCart.addProduct(currToAddToCart, qtyToAdd);
		}
		else
		{
			System.out.println("TOO MUCH SELECTED");
		}
	}

	public void addItemToCartGrid(Product p, int qtyToAdd)
	{
		if (!shoppingCart.containsProduct(p))
		{
			//Product name label
			Label l = new Label(p.getName());
			l.setFont(new Font("Arial", 20));
			addEventsToNode(l);
			//setCartNodeEvents(l);

			GridPane.setFillWidth(l, true);
			GridPane.setFillHeight(l, true);
			GridPane.setConstraints(l, 0, yCart);
			cartGridPane.getChildren().add(l);

			//Product qty label
			l = new Label("x" + qtyToAdd);
			l.setFont(new Font("Arial", 20));
			addEventsToNode(l);
			//setCartNodeEvents(l);

			GridPane.setFillWidth(l, true);
			GridPane.setFillHeight(l, true);
			GridPane.setConstraints(l, 1, yCart);
			cartGridPane.getChildren().add(l);

			yCart++;
		}
		else
		{//Search the correct nodes and updated them

			Label lbl;
			String qty;
			int qtyLabelRow = -1;

			//Find product's row index
			for (Node node : cartGridPane.getChildren())
			{
				if (((Label) node).getText().equals(p.getName()))
				{
					qtyLabelRow = GridPane.getRowIndex(node);
					break;
				}
			}

			//Reach qty label and add 1
			for (Node node : cartGridPane.getChildren())
			{
				if (GridPane.getRowIndex(node) == qtyLabelRow && GridPane.getColumnIndex(node) == 1)
				{
					lbl = (Label) node;
					qty = lbl.getText();
					lbl.setText("x" + Integer.toString(Integer.parseInt(qty.substring(1)) + qtyToAdd));
				}
			}
		}

		shoppingCart.addProduct(p, qtyToAdd);
	}

	private void buy()
	{
		Map<String, ArrayList<ShoppingCart>> shoppingCarts = getShoppingCarts();

		String email = shoppingCart.getCustomer().getEmail();
		ArrayList<ShoppingCart> carts;

		//Add cart to structure
		if (shoppingCarts.containsKey(email)) carts = shoppingCarts.get(email);
		else carts = new ArrayList<ShoppingCart>();

		carts.add(shoppingCart);
		shoppingCarts.put(email, carts);

		//Write on file
		setShoppingCarts(shoppingCarts);
	}

	private void addEventsToNode(Node n)
	{
		n.setOnMouseEntered(e -> cartGridPane.getChildren().forEach(c ->
		{
			Integer targetIndex = GridPane.getRowIndex(n);
			if (GridPane.getRowIndex(c) == targetIndex)
			{
				c.setStyle("-fx-background-color:#f9f3c5;");
			}
		}));
		n.setOnMouseExited(e -> cartGridPane.getChildren().forEach(c ->
		{
			Integer targetIndex = GridPane.getRowIndex(n);
			if (GridPane.getRowIndex(c) == targetIndex)
			{
				c.setStyle("-fx-background-color:#ffffff;");
			}
		}));
	}

	private void searchInShop()
	{// Isolates products that match ward and search string

		Ward toSearchWard = wardSelection.getSelectionModel().getSelectedItem();
		String toSearchProduct = shopSearchbar.getText();

		clearShopCart();
		resetProductPanel();

		if (toSearchWard != Ward.ALL && !toSearchProduct.isEmpty())
		{
			for (Product p : productToImage.keySet())
			{
				if (((toSearchWard != Ward.ALL) && p.getWard() == toSearchWard) && ((!toSearchProduct.isEmpty()) && p.getName().toLowerCase().startsWith(toSearchProduct.toLowerCase())))
					addItemToShopGrid(p);
			}
		}
		else
		{// Load all products

			if (toSearchWard == Ward.ALL && toSearchProduct.isEmpty())
			{
				for (Product p : productToImage.keySet())
				{
					addItemToShopGrid(p);
				}
			}
			else
			{
				for (Product p : productToImage.keySet())
				{
					if (toSearchWard != Ward.ALL && p.getWard() == toSearchWard) addItemToShopGrid(p);
					if (((!toSearchProduct.isEmpty()) && p.getName().toLowerCase().startsWith(toSearchProduct.toLowerCase()))) addItemToShopGrid(p);
				}
			}
		}

		setGridNodesEvents();
	}

	private void setGridNodesEvents()
	{
		for (Node node : shopGridPane.getChildren())
		{
			if (node instanceof Pane)
			{
				// Clicking in node event
				node.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>()
				{
					@Override
					public void handle(Event event)
					{
						if (selectedShopProduct == null)
						{
							node.setStyle("-fx-background-color:#ed2850;");
							selectedShopProduct = ((Pane) node);
							loadHoveringProductInfo((ImageView) (selectedShopProduct.getChildren().get(0)));
						}
						else if (selectedShopProduct == ((Pane) node))
						{
							// node.setStyle("-fx-background-color:#b5c6f7;");
							enableProductPanel(false);
							selectedShopProduct = null;
						}
						else
						{
							selectedShopProduct.setStyle("-fx-background-color:#b5c6f7;");

							node.setStyle("-fx-background-color:#ed2850;");
							selectedShopProduct = ((Pane) node);

							loadHoveringProductInfo((ImageView) (selectedShopProduct.getChildren().get(0)));
						}
					}
				});

				// Entering in node event
				node.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<Event>()
				{
					@Override
					public void handle(Event event)
					{
						if (selectedShopProduct != ((Pane) node)) node.setStyle("-fx-background-color:#4d7af7;");

						loadHoveringProductInfo((ImageView) (((Pane) node).getChildren().get(0)));

						enableProductPanel(true);
					}
				});

				// Exiting from node event
				node.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<Event>()
				{
					@Override
					public void handle(Event event)
					{
						if (selectedShopProduct != ((Pane) node)) node.setStyle("-fx-background-color:#b5c6f7;");

						if (selectedShopProduct != null) loadHoveringProductInfo((ImageView) selectedShopProduct.getChildren().get(0));
						else
						{
							resetProductPanel();
							enableProductPanel(false);
						}

					}
				});
			}
		}
	}

	private void searchInCart()
	{// Isolates grid entries that start with the string typed in the search bar

		// CHIAMI LA VISTA RICERCA CARRELLO

		/*
		 * String toSearchItem = cartSearchbar.getText();
		 * 
		 * ArrayList<Node> gridEntries = new ArrayList<Node>();
		 * 
		 * for (Node node : cartGridPane.getChildren()) { if (node instanceof Label) {
		 * Label l = ((Label) node);
		 * 
		 * if (l.getText().startsWith(toSearchItem)) gridEntries.add(node); } //else
		 * .println("Was looking into non-label widget"); }
		 * 
		 * if (gridEntries.size() != 0) { //Remove all entries
		 * cartGridPane.getChildren().removeAll(cartGridPane.getChildren());
		 * 
		 * //Add entries that match search request
		 * cartGridPane.getChildren().addAll(gridEntries); } else cartSearchbar.clear();
		 */
	}

	public void addItemToShopGrid(Product p)
	{// Add item to given pane, in given position

		ImageView b = productToImage.get(p);
		Pane r = new Pane();
		r.getChildren().add(b);
		r.styleProperty().set("-fx-background-color:#f9f3c5;");

		// r.getChildren().add(b);
		// BorderPane.setMargin(b, new Insets(10, 10, 10, 10));

		GridPane.setFillWidth(r, true);
		GridPane.setFillHeight(r, true);
		GridPane.setConstraints(r, xShop, yShop);
		shopGridPane.getChildren().add(r);

		// Update coords
		xShop++;
		if (xShop == 6)
		{
			xShop = 0;
			yShop++;
		}
	}

	private void loadHoveringProductInfo(ImageView imageV)
	{
		Product pToLoad = imageToProduct.get(imageV);

		currProdImage.setImage(imageV.getImage());
		currProdName.setText(pToLoad.getName());
		currProdPrice.setText("Price: " + String.format(Locale.US, "%.2f", pToLoad.getPrice()) + " $");
		currProdBrand.setText("Brand: " + pToLoad.getBrand().toString());
		currProdQty.setText("Quantity per item: " + Float.toString(pToLoad.getQtyPerItem()) + " " + pToLoad.getMeasureUnit());
		currProdQtyAvailable.setText("Items available: " + Integer.toString(pToLoad.getQtyAvailable()));

		if (pToLoad.getQtyAvailable() == 0)
		{
			isPAvaialble.setText("Product not available");
			isPAvaialble.setStyle("-fx-text-fill: red;");

			enableProductPanel(false);
		}
		else
		{
			isPAvaialble.setText("Product available");
			isPAvaialble.setStyle("-fx-text-fill: green;");

			enableProductPanel(true);
		}
	}

	private void enableProductPanel(boolean value)
	{
		if (value)
		{
			btnAddToCart.setDisable(false);

			txtFldQuantity.setText("1");
			txtFldQuantity.setDisable(false);
		}
		else
		{
			btnAddToCart.setDisable(true);

			txtFldQuantity.setText("-");
			txtFldQuantity.setDisable(true);
		}
	}

	private void clearShopCart()
	{
		xShop = 0;
		yShop = 0;

		shopGridPane.getChildren().removeAll(shopGridPane.getChildren());
		shopGridPane.setGridLinesVisible(true);
	}

	public void displayCustomer(Customer customer)
	{
		email.setText(customer.getEmail());
		setCurrentUser(customer);
		shoppingCart = new ShoppingCart(customer, 0); // TODO put correct id
	}

	public ShoppingCart getShoppingCart()
	{
		return shoppingCart;
	}

	@FXML
	public void switchToCustomer()
	{
		switchToView("/views/Customer.fxml", "Customer", "CC", getCurrentUser());
	}

	private void initGridPanes()
	{
		// Initializes the central grid pane
		shopGridPane = new GridPane();
		shopGridPane.setGridLinesVisible(true);
		shopGridPane.setHgap(10); // horizontal gap in pixels
		shopGridPane.setVgap(10); // vertical gap in pixels
		shopGridPane.setPadding(new Insets(10, 10, 10, 10));
		shopGridPane.setFocusTraversable(false);

		shopPane.setContent(shopGridPane);
		shopPane.setFitToWidth(true);
		shopPane.setFitToHeight(true);
		shopPane.setPannable(true);
		shopPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		// Initializes the customer cart grid pane
		cartGridPane = new GridPane();
		// cartGridPane.setGridLinesVisible(true);
		cartGridPane.setHgap(10); // horizontal gap in pixels
		cartGridPane.setVgap(10); // vertical gap in pixels
		cartGridPane.setPadding(new Insets(10, 10, 10, 10));
		cartGridPane.setFocusTraversable(false);

		cartPane.setContent(cartGridPane);
		cartPane.setFitToWidth(true);
		cartPane.setFitToHeight(true);
		cartPane.setPannable(true);
	}
}