package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.Selector;
import models.Customer;
import models.Product;
import models.ShoppingCart;
import models.Ward;

public class ShopController extends Controller implements Initializable
{
	// Used for shop grid
	private HashMap <ImageView,Product> imageToProduct = new HashMap <ImageView,Product>();
	private HashMap <Product,ImageView> productToImage = new HashMap <Product,ImageView>();

	// Used for cart grid
	private Map <String,Product> nameToProduct = new HashMap <String,Product>();
	private HashMap <Label,Label> prodNameToQty = new HashMap <Label,Label>();

	private Map <String,Product> products = new HashMap <String,Product>();

	private ShoppingCart shoppingCart;
	private Image noProductImage = new Image(getClass().getResourceAsStream("/icons/products/noProduct.png"));

	private int xShop = 0;
	private int yShop = 0;
	private int yCart = 0;

	@FXML
	private Button btnShopSearch, btnGoToPayment, btnAddToCart, btnOpenCart, btnReloadProducts;

	@FXML
	private TextField shopSearchbar, txtFldQuantity;

	@FXML
	private ImageView currProdImage;

	@FXML
	private Label currProdName, isPAvaialble, currProdBrand, currProdPrice, currProdQty, currProdQtyAvailable;

	@FXML
	private Label userEmail, lblQuantity, lblTotToPay;

	@FXML
	private Pane container;

	@FXML
	private ScrollPane cartScrollPane, shopScrollPane;

	@FXML
	private GridPane cartGridPane, shopGridPane;

	@FXML
	private ComboBox <Ward> wardSelection;

	@FXML
	private MenuItem btnLogOut, btnAppInfos;

	// Highlight colors
	public static final String selectedColor = "#f23366";
	public static final String hoveringColor = "#688efc";
	public static final String backgroundColor = "#cfdbff";

	public static Pane selectedShopProduct;
	public static Pane hoveringShopProduct;
	public static Label selectedCartProduct;
	public static Label hoveringCartProduct;

	private Selector productSelector;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		Platform.runLater(() ->
		{
			setCloseEvent();
			userEmail.setText(getCurrentUser().getEmail());
			shoppingCart = new ShoppingCart((Customer) getCurrentUser());
		});

		initEventHandlers();
		initGridPanes();

		wardSelection.getItems().setAll(Ward.values());
		wardSelection.getSelectionModel().select(0);

		loadProducts();

		// Init product panel
		resetProductPanel();
		enableProductPanel(false);

		searchInShop(); // To initially fill the shop grid

		productSelector = new Selector();
		new Thread(productSelector).start();
	}

	private void initEventHandlers()
	{
		// Shop search button
		btnShopSearch.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler <Event>()
		{
			@Override
			public void handle(Event event)
			{
				searchInShop();
			}
		});

		btnShopSearch.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler <KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if ( event.getCode() == KeyCode.ENTER )
				{
					searchInShop();
				}
			}
		});

		// Add to cart button
		btnAddToCart.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler <Event>()
		{
			@Override
			public void handle(Event event)
			{
				if ( txtFldQuantity.getText().matches("^\\d+$") )
					addProductToCart();
				else
					txtFldQuantity.setText("1");
			}
		});

		btnAddToCart.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler <KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if ( event.getCode() == KeyCode.ENTER )
					addProductToCart();
			}
		});

		// Go to payment button
		btnGoToPayment.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler <Event>()
		{
			@Override
			public void handle(Event event)
			{
				goToPaymentView();
			}
		});

		// Reload products from database
		btnReloadProducts.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler <Event>()
		{
			@Override
			public void handle(Event event)
			{
				reloadProducts();
				refreshProductPanel();
				checkCartAgainstShop();
				updateProducts();
			}
		});

		btnReloadProducts.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler <KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				reloadProducts();
				refreshProductPanel();
				checkCartAgainstShop();
				updateProducts();
			}
		});

		wardSelection.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
		{
			searchInShop();
		});
	}

	private void checkCartAgainstShop()
	{// Checks if products in cart are available in shop

		int qtyAvaiable;
		String msg = "";
		// private Map <String,Product> products = new HashMap <String,Product>();
		HashMap <Product,Integer> oldCartProds = shoppingCart.getProducts();
		HashMap <Product,Integer> newCartProds = new HashMap <Product,Integer>();

		// For each product in cart, check if still available
		for ( Product p : oldCartProds.keySet() )
		{
			qtyAvaiable = products.get(p.getImage()).getQtyAvailable();

			if ( qtyAvaiable < oldCartProds.get(p) )
			{// Cart quantity is no longer available

				if ( qtyAvaiable == 0 )
				{
					msg += p.getName() + ": No longer available\n";
				}
				else
				{
					newCartProds.put(p, qtyAvaiable);
					msg += p.getName() + ": only " + qtyAvaiable + " available\n";
				}
			}
			else
				newCartProds.put(p, oldCartProds.get(p));
		}

		shoppingCart.setProducts(newCartProds);

		if ( msg.length() != 0 )
		{
			alertWarning(AlertType.WARNING, "Unavailable products",
					"Some products you had put in the cart are not avaiable\nin the requested quantity anymore. See details below:\n"
							+ msg);
		}
	}

	private void loadProducts()
	{// Loads products data structures

		products = getProducts();

		ImageView im;

		for ( String s : products.keySet() )
		{
			im = new ImageView(new Image((products.get(s).getImage())));

			imageToProduct.put(im, products.get(s));
			productToImage.put(products.get(s), im);

			nameToProduct.put(s.split("/")[3].split("\\.")[0], products.get(s));
		}
	}

	public void reloadProducts()
	{// Loads products from db and updates qty available

		Map <String,Product> ps = getProducts();

		for ( String p1 : ps.keySet() )
		{
			for ( String p2 : products.keySet() )
			{
				if ( p1.equals(p2) )
					products.get(p2).setQtyAvailable(ps.get(p1).getQtyAvailable());
			}
		}
	}

	private void initGridPanes()
	{
		// Shop grid pane
		shopGridPane = new GridPane();
		shopGridPane.setHgap(10); // horizontal gap in pixels
		shopGridPane.setVgap(10); // vertical gap in pixels
		shopGridPane.setPadding(new Insets(10, 10, 10, 10));
		shopGridPane.setFocusTraversable(false);

		shopScrollPane.setContent(shopGridPane);
		shopScrollPane.setFitToWidth(true);
		shopScrollPane.setFitToHeight(true);
		shopScrollPane.setPannable(true);
		shopScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		// Cart grid pane
		cartGridPane = new GridPane();
		cartGridPane.setHgap(10); // horizontal gap in pixels
		cartGridPane.setVgap(10); // vertical gap in pixels
		cartGridPane.setPadding(new Insets(10, 10, 10, 10));
		cartGridPane.setFocusTraversable(false);

		cartScrollPane.setContent(cartGridPane);
		cartScrollPane.setFitToWidth(true);
		cartScrollPane.setFitToHeight(true);
		cartScrollPane.setPannable(true);
	}

	private void addProductToCart()
	{// Adds currently selected product to customer's cart

		int qtyToAdd = Integer.parseInt(txtFldQuantity.getText());

		if ( qtyToAdd <= imageToProduct.get(selectedShopProduct.getChildren().get(0)).getQtyAvailable() )
		{
			btnGoToPayment.setDisable(false);
			btnOpenCart.setDisable(false);

			Product prodToAddToCart = imageToProduct.get(selectedShopProduct.getChildren().get(0));
			prodToAddToCart.setQtyAvailable(prodToAddToCart.getQtyAvailable() - qtyToAdd);

			// Reload info because availability may be none now
			loadProductInfo(productToImage.get(prodToAddToCart));

			// Add/Update product to in gui list
			if ( !shoppingCart.containsProduct(prodToAddToCart) )
			{
				shoppingCart.addProduct(prodToAddToCart, qtyToAdd);
				addItemToCartGrid(prodToAddToCart, qtyToAdd);
			}
			else
			{
				shoppingCart.addProduct(prodToAddToCart, qtyToAdd);
				updateItemInCart(prodToAddToCart);
			}

			lblTotToPay.setText("Total: " + shoppingCart.getTotalPrice() + "$");
		}
		else
		{
			alertWarning(AlertType.WARNING, "Warning", "Selected quantity has exceeded availability");
		}
	}

	private void executeActionOnNode(Node node, boolean highlight, boolean saveRef, boolean selectOrHover)
	{// Executes the required actions on the given node

		if ( selectOrHover )
		{
			if ( highlight )
			{
				Selector.clearSelectionList(false);
				Selector.addNodeToSelection(node);

				node.setStyle("-fx-background-color:" + ShopController.selectedColor + ";");
			}
			else
				node.setStyle("-fx-background-color:" + ShopController.backgroundColor + ";");

			if ( saveRef )
			{
				selectedShopProduct = (Pane) node;
				if ( selectedCartProduct != null )
				{
					selectedCartProduct.setStyle("-fx-background-color:" + ShopController.backgroundColor + ";");
					selectedCartProduct = null;
				}
			}

		}
		else
		{
			if ( highlight )
			{
				node.setStyle("-fx-background-color:" + ShopController.hoveringColor + ";");
			}
			else
			{
				node.setStyle("-fx-background-color:" + ShopController.backgroundColor + ";");
			}

			if ( saveRef )
				hoveringShopProduct = (Pane) node;
		}
	}

	private void highlightCartProduct(Node node, boolean highlight, boolean saveRef, boolean selectOrHover)
	{
		Label lblName = null;
		Label lblQty = null;
		Label currLabel = (Label) node;

		// Find correct nodes references
		if ( currLabel.getText().contains("x") )
		{
			lblQty = currLabel;

			for ( Label lbl : prodNameToQty.keySet() )
			{
				if ( prodNameToQty.get(lbl) == lblQty )
					lblName = lbl;
			}
		}
		else
		{
			lblName = currLabel;

			for ( Label lbl : prodNameToQty.keySet() )
			{
				if ( lbl.getText().equals(lblName.getText()) )
					lblQty = prodNameToQty.get(lbl);
			}
		}

		// Apply actions
		if ( selectOrHover )
		{
			if ( highlight )
			{
				Selector.clearSelectionList(false);
				Selector.addNodeToSelection(lblName);
				Selector.addNodeToSelection(lblQty);

				lblName.setStyle("-fx-background-color:" + ShopController.selectedColor + ";");
				lblQty.setStyle("-fx-background-color:" + ShopController.selectedColor + ";");
			}
			else
			{
				lblName.setStyle("-fx-background-color:" + ShopController.backgroundColor + ";");
				lblQty.setStyle("-fx-background-color:" + ShopController.backgroundColor + ";");
			}

			if ( saveRef )
			{
				selectedCartProduct = lblName;
				if ( selectedShopProduct != null )
				{
					selectedShopProduct.setStyle("-fx-background-color:" + ShopController.backgroundColor + ";");
					selectedShopProduct = null;
				}
			}

		}
		else
		{
			if ( highlight )
			{
				lblName.setStyle("-fx-background-color:" + ShopController.hoveringColor + ";");
				lblQty.setStyle("-fx-background-color:" + ShopController.hoveringColor + ";");
			}
			else
			{
				lblName.setStyle("-fx-background-color:" + ShopController.backgroundColor + ";");
				lblQty.setStyle("-fx-background-color:" + ShopController.backgroundColor + ";");
			}

			if ( saveRef )
				hoveringCartProduct = lblName;
		}
	}

	private void setShopNodeEvents(Node node)
	{
		// Clicking on node
		node.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler <Event>()
		{
			@Override
			public void handle(Event event)
			{
				if ( selectedShopProduct == null )
				{
					executeActionOnNode(node, true, true, true);

					loadProductInfo((ImageView) (selectedShopProduct.getChildren().get(0)));
				}
				else
				{
					if ( selectedShopProduct == ((Pane) node) )
					{
						Selector.clearSelectionList(true);
						enableProductPanel(false);
						selectedShopProduct = null;
					}
					else
					{

						executeActionOnNode(selectedShopProduct, false, false, false);
						executeActionOnNode(node, true, true, true);

						enableProductPanel(true);

						loadProductInfo((ImageView) (selectedShopProduct.getChildren().get(0)));
					}
				}
			}
		});

		// Entering in node event
		node.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler <Event>()
		{
			@Override
			public void handle(Event event)
			{
				if ( selectedShopProduct == null || selectedShopProduct != ((Pane) node) )
				{// Select current node
					executeActionOnNode(node, true, true, false);
				}

				loadProductInfo((ImageView) (((Pane) node).getChildren().get(0)));
			}
		});

		// Exiting from node event
		node.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler <Event>()
		{
			@Override
			public void handle(Event event)
			{
				if ( selectedShopProduct != null )
				{
					executeActionOnNode(node, false, false, false);
					loadProductInfo((ImageView) selectedShopProduct.getChildren().get(0));
				}
				else
				{
					if ( selectedShopProduct == null )
					{
						executeActionOnNode(node, false, false, false);

						if ( selectedCartProduct == null )
						{
							resetProductPanel();
						}
						else
						{
							loadProductInfo((ImageView) productToImage.get(nameToProduct
									.get((selectedCartProduct).getText().toLowerCase().replace(" ", "_"))));
						}

						enableProductPanel(false);
					}
					else if ( GridPane.getRowIndex(node) != GridPane.getRowIndex(selectedCartProduct) )
					{
						executeActionOnNode(node, false, false, false);
						loadProductInfo((ImageView) (selectedShopProduct.getChildren().get(0)));
					}
				}
			}
		});
	}

	private void setCartNodeEvents(Node node)
	{
		// Clicking in node event
		node.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler <Event>()
		{
			@Override
			public void handle(Event event)
			{
				if ( selectedCartProduct == null )
				{
					highlightCartProduct(node, true, true, true);

					loadProductInfo((ImageView) productToImage
							.get(nameToProduct.get(selectedCartProduct.getText().toLowerCase().replace(" ", "_"))));

				}
				else
				{
					if ( GridPane.getRowIndex(node) == GridPane.getRowIndex(selectedCartProduct) )
					{
						Selector.clearSelectionList(true);
						selectedCartProduct = null;
					}
					else
					{
						highlightCartProduct(selectedCartProduct, false, false, true);
						highlightCartProduct(node, true, true, true);

						loadProductInfo((ImageView) productToImage
								.get(nameToProduct.get(selectedCartProduct.getText().toLowerCase().replace(" ", "_"))));
					}
				}

				enableProductPanel(false);
			}
		});

		// Entering in node event
		node.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler <Event>()
		{
			@Override
			public void handle(Event event)
			{
				if ( selectedCartProduct == null )
				{// Select current node

					highlightCartProduct(node, true, true, false);
				}
				else if ( GridPane.getRowIndex(node) != GridPane.getRowIndex(selectedCartProduct) )
				{
					highlightCartProduct(node, true, true, false);
				}

				loadProductInfo((ImageView) productToImage
						.get(nameToProduct.get((hoveringCartProduct).getText().toLowerCase().replace(" ", "_"))));

				enableProductPanel(false);

			}
		});

		// Exiting from node event
		node.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler <Event>()
		{
			@Override
			public void handle(Event event)
			{
				if ( selectedCartProduct == null )
				{
					highlightCartProduct(node, false, false, false);

					if ( selectedShopProduct == null )
					{
						resetProductPanel();
						enableProductPanel(false);
					}
					else
					{
						loadProductInfo((ImageView) (selectedShopProduct.getChildren().get(0)));
					}
				}
				else if ( GridPane.getRowIndex(node) != GridPane.getRowIndex(selectedCartProduct) )
				{
					highlightCartProduct(node, false, false, false);
					loadProductInfo((ImageView) productToImage
							.get(nameToProduct.get((selectedCartProduct).getText().toLowerCase().replace(" ", "_"))));
				}

			}
		});
	}

	private void searchInShop()
	{// Isolates products that match ward and search string

		selectedShopProduct = null;

		Ward toSearchWard = wardSelection.getSelectionModel().getSelectedItem();
		String toSearchProduct = shopSearchbar.getText();

		clearShopGrid();
		resetProductPanel();

		if ( toSearchWard != Ward.ALL && !toSearchProduct.isEmpty() )
		{
			for ( Product p : productToImage.keySet() )
			{
				if ( ((toSearchWard != Ward.ALL) && p.getWard() == toSearchWard) && ((!toSearchProduct.isEmpty())
						&& p.getName().toLowerCase().startsWith(toSearchProduct.toLowerCase())) )
					addItemToShopGrid(p);
			}
		}
		else
		{// Load all products

			if ( toSearchWard == Ward.ALL && toSearchProduct.isEmpty() )
			{
				for ( Product p : productToImage.keySet() )
				{
					addItemToShopGrid(p);
				}
			}
			else
			{
				for ( Product p : productToImage.keySet() )
				{
					if ( toSearchWard != Ward.ALL && p.getWard() == toSearchWard )
						addItemToShopGrid(p);
					if ( ((!toSearchProduct.isEmpty())
							&& p.getName().toLowerCase().startsWith(toSearchProduct.toLowerCase())) )
						addItemToShopGrid(p);
				}
			}
		}
	}

	private void addItemToShopGrid(Product p)
	{// Add item to given pane, in given position

		ImageView b = productToImage.get(p);
		AnchorPane r = new AnchorPane();
		r.getChildren().add(b);
		r.setStyle("-fx-background-color:" + backgroundColor + ";");
		setShopNodeEvents(r);

		GridPane.setFillWidth(r, true);
		GridPane.setFillHeight(r, true);
		GridPane.setConstraints(r, xShop, yShop);
		shopGridPane.getChildren().add(r);

		// Update coords
		xShop++;
		if ( xShop == 6 )
		{
			xShop = 0;
			yShop++;
		}
	}

	private void addItemToCartGrid(Product p, int qtyToAdd)
	{
		// Product name label
		Label lblName = new Label(p.getName());
		lblName.setFont(new Font("Arial", 20));
		lblName.setStyle("-fx-background-color:" + backgroundColor + ";");
		setCartNodeEvents(lblName);

		// GridPane.setFillWidth(lblName, true);
		// GridPane.setFillHeight(lblName, true);
		GridPane.setConstraints(lblName, 0, yCart);
		cartGridPane.getChildren().add(lblName);

		// Product qty label
		Label lblQty = new Label("x" + qtyToAdd);
		lblQty.setFont(new Font("Arial", 20));
		lblQty.setStyle("-fx-background-color:" + backgroundColor + ";");
		setCartNodeEvents(lblQty);

		// GridPane.setFillWidth(lblQty, true);
		// GridPane.setFillHeight(lblQty, true);
		GridPane.setConstraints(lblQty, 1, yCart);
		cartGridPane.getChildren().add(lblQty);

		prodNameToQty.put(lblName, lblQty);

		yCart++;
	}

	private void updateItemInCart(Product p)
	{
		Label lbl;
		int qtyLabelRow = -1;

		// Find product's row index
		for ( Node node : cartGridPane.getChildren() )
		{
			if ( ((Label) node).getText().equals(p.getName()) )
			{
				qtyLabelRow = GridPane.getRowIndex(node);
				break;
			}
		}

		// Reach qty label and add 1
		for ( Node node : cartGridPane.getChildren() )
		{
			if ( GridPane.getRowIndex(node) == qtyLabelRow && GridPane.getColumnIndex(node) == 1 )
			{
				lbl = (Label) node;
				lbl.setText("x" + Integer.toString(shoppingCart.getProducts().get(p)));
			}
		}
	}

	private void loadProductInfo(ImageView imageV)
	{
		Product pToLoad = imageToProduct.get(imageV);

		currProdImage.setImage(imageV.getImage());
		currProdName.setText(pToLoad.getName());
		currProdPrice.setText("Price: " + String.format(Locale.US, "%.2f", pToLoad.getPrice()) + " $");
		currProdBrand.setText("Brand: " + pToLoad.getBrand().toString());
		currProdQty.setText(
				"Quantity per item: " + Float.toString(pToLoad.getQtyPerItem()) + " " + pToLoad.getMeasureUnit());
		currProdQtyAvailable.setText("Items available: " + Integer.toString(pToLoad.getQtyAvailable()));

		if ( pToLoad.getQtyAvailable() == 0 )
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
		if ( value )
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

	private void clearCartGrid()
	{
		yCart = 0;

		cartGridPane.getChildren().removeAll(cartGridPane.getChildren());

		btnOpenCart.setDisable(true);
	}

	private void clearShopGrid()
	{
		xShop = 0;
		yShop = 0;

		shopGridPane.getChildren().removeAll(shopGridPane.getChildren());
	}

	public void setData(Customer customer)
	{
		setCurrentUser(customer);
	}

	public void updateProducts()
	{// Called in remove product

		clearCartGrid();

		ArrayList <Product> products = new ArrayList <Product>();
		products.addAll(shoppingCart.getProducts().keySet());

		for ( Product p : products )
		{
			addItemToCartGrid(p, shoppingCart.getProducts().get(p));
		}

		shoppingCart.recalculateTotalPrice();

		lblTotToPay.setText("Total: " + shoppingCart.getTotalPrice() + "$");

		if ( shoppingCart.getProducts().size() == 0 )
			btnGoToPayment.setDisable(true);
		else
			btnOpenCart.setDisable(false);

		refreshProductPanel();
	}

	public void removeProductFromCart(Product p)
	{
		p.setQtyAvailable(p.getQtyAvailable() + shoppingCart.getProducts().get(p));
		shoppingCart.removeProduct(p);
	}

	private void refreshProductPanel()
	{
		if ( selectedCartProduct != null )
		{
			loadProductInfo((ImageView) productToImage
					.get(nameToProduct.get(selectedCartProduct.getText().toLowerCase().replace(" ", "_"))));
		}
		else if ( selectedShopProduct != null )
		{
			loadProductInfo((ImageView) (selectedShopProduct.getChildren().get(0)));
		}
	}

	private void resetProductPanel()
	{
		currProdImage.setImage(noProductImage);
		currProdName.setText("-");
		currProdPrice.setText("Price: - $");
		currProdBrand.setText("Brand: -");
		currProdQty.setText("Quantity per item: -");
		currProdQtyAvailable.setText("Items available: -");

		isPAvaialble.setText("-");
		isPAvaialble.setStyle("-fx-text-fill: black;");
	}

	private void quitApp()
	{
		clearApp();
		clearShopGrid();

		logInOutUser(false);

		productSelector.quit();
	}

	public void clearApp()
	{
		clearCartGrid();

		shoppingCart.clear();

		// Reset shop grid
		wardSelection.getSelectionModel().select(0);
		shopSearchbar.clear();
		searchInShop();

		selectedShopProduct = null;
		hoveringShopProduct = null;
		selectedCartProduct = null;
		hoveringCartProduct = null;

		resetProductPanel();

		lblTotToPay.setText("Total: 0$");
		btnGoToPayment.setDisable(true);
		wardSelection.getSelectionModel().select(0);
		prodNameToQty.clear();
	}

	public void goToShoppingCartView()
	{
		((ShoppingCartController) openView("/views/ShoppingCart.fxml", "Shopping Cart")).setData(shoppingCart, this);

		ShoppingCartController.showAndWaitStage();
	}

	public void goToPaymentView()
	{
		((PaymentController) openView("/views/Payment.fxml", "Payment")).setData((Customer) getCurrentUser(),
				shoppingCart, (HashMap <String,Product>) products, this);

		PaymentController.showAndWaitStage();
	}

	@FXML
	public void goToCustomerView()
	{
		((CustomerController) openView("/views/Customer.fxml", "Customer")).setData((Customer) getCurrentUser());

		CustomerController.showAndWaitStage();
	}

	@FXML
	public void goToAppInfos()
	{
		openView("/views/AppInfos.fxml", "App Infos");

		AppInfosController.showAndWaitStage();
	}

	@FXML
	public void showLogOutPrompt()
	{
		String msg = "Are you sure you want to logout?";

		if ( !shoppingCart.getProducts().isEmpty() )
		{
			msg += "\nThe current shopping list will be lost.";
		}

		if ( alertPrompt(AlertType.CONFIRMATION, "Logout", msg) )
		{
			quitApp();

			((Stage) container.getScene().getWindow()).close();

			openView("/views/Login.fxml", "Login");

			LoginController.showStage();
		}
	}

	public void setCloseEvent()
	{
		((Stage) container.getScene().getWindow()).setOnCloseRequest(new EventHandler <WindowEvent>()
		{
			@Override
			public void handle(WindowEvent event)
			{
				String msg = "Are you sure you want to quit?";

				if ( !shoppingCart.getProducts().isEmpty() )
				{
					msg += "\nThe current shopping list will be lost.";
				}

				if ( alertPrompt(AlertType.CONFIRMATION, "Quit application", msg) )
				{
					logInOutUser(false);
					productSelector.quit();

					Platform.exit();
				}
				else
				{
					event.consume();
				}
			}
		});
	}
}