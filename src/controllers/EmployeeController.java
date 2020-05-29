package controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.imageio.ImageIO;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import models.Brand;
import models.Employee;
import models.Feature;
import models.MeasureUnit;
import models.PaymentMethod;
import models.Product;
import models.ProductProperty;
import models.ShoppingCart;
import models.ShoppingCartProperty;
import models.Type;
import models.Ward;

public class EmployeeController extends Controller <Employee> implements Initializable
{
	@FXML
	private TabPane tabPane;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		initializeAddProductTab();

		tabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener <Number>()
		{
			@Override
			public void changed(ObservableValue <? extends Number> observable, Number oldValue, Number newValue)
			{
				int selectedIndex = newValue.intValue();
				// Where index of the first tab is 0, while that of the second tab is 1 and so on
				if ( selectedIndex == 0 )
					initializeAddProductTab();
				else if ( selectedIndex == 1 )
					initializeModifyProductsTab();
				else
					initializeViewShoppingTab();
			}
		});
	}

	// Add product Tab
	@FXML
	private ComboBox <Ward> ward;
	@FXML
	private ComboBox <Brand> brand;
	@FXML
	private ComboBox <Type> type;
	@FXML
	private ComboBox <MeasureUnit> measureUnit;
	@FXML
	private TextField name, qtyPerItem, qtyAvailable, price, searchBar;
	@FXML
	private CheckBox glutenFree, milkFree, bio, madeInItaly;
	@FXML
	private ImageView imageView;
	private String image;

	private void initializeAddProductTab()
	{
		clearFields();
		Platform.runLater(() -> tabPane.requestFocus());
		initAddProductTabEventHandlers();

		ObservableList <Ward> wards = FXCollections.observableArrayList(Ward.values());
		ward.setItems(wards);

		ObservableList <Brand> brands = FXCollections.observableArrayList(Brand.values());
		brand.setItems(brands);

		ObservableList <Type> types = FXCollections.observableArrayList(Type.values());
		type.setItems(types);

		ObservableList <MeasureUnit> measureUnits = FXCollections.observableArrayList(MeasureUnit.values());
		measureUnit.setItems(measureUnits);
	}

	private void initAddProductTabEventHandlers()
	{
		imageView.setOnMouseEntered(new EventHandler <MouseEvent>()
		{
			@Override
			public void handle(MouseEvent e)
			{
				Scene scene = tabPane.getScene();
				scene.setCursor(Cursor.HAND);
			}
		});

		imageView.setOnMouseExited(new EventHandler <MouseEvent>()
		{
			@Override
			public void handle(MouseEvent e)
			{
				Scene scene = tabPane.getScene();
				scene.setCursor(Cursor.DEFAULT);
			}
		});
	}

	@FXML
	public void selectImage()
	{
		FileChooser fileChooser = new FileChooser();

		// Set extension filters
		FileChooser.ExtensionFilter png = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");

		fileChooser.getExtensionFilters().add(png);

		// Show open file dialog
		File file = fileChooser.showOpenDialog(null);

		try
		{
			BufferedImage bufferedImage = ImageIO.read(file);

			Image imageSelected = SwingFXUtils.toFXImage(bufferedImage, null);
			imageView.setImage(imageSelected);
			image = file.getPath();
		}
		catch ( IOException e )
		{
			System.err.println("Image not found !");
		}
	}

	@FXML
	public void addProductToDatabase()
	{
		if ( isAllCompiled() )
		{
			float qtyPerItemAsFloat = Float.parseFloat(qtyPerItem.getText());
			float priceAsFloat = Float.parseFloat(price.getText());
			int qtyAvailableAsInt = Integer.parseInt(qtyAvailable.getText());

			Set <Feature> features = new HashSet <>();

			if ( glutenFree.isSelected() )
				features.add(Feature.GLUTEN_FREE);

			if ( milkFree.isSelected() )
				features.add(Feature.MILK_FREE);

			if ( bio.isSelected() )
				features.add(Feature.BIO);

			if ( madeInItaly.isSelected() )
				features.add(Feature.MADE_IN_ITALY);

			Product product = new Product(ward.getValue(), name.getText(), brand.getValue(), qtyPerItemAsFloat,
					measureUnit.getValue(), priceAsFloat, image, type.getValue(), features, qtyAvailableAsInt);

			if ( alreadyExists(product) )
			{
				clearFields();

				alert(AlertType.WARNING, "Warning", "Product already exists");
			}
			else
			{
				// Insert product into DB
				Map <String,Product> products = getProducts();

				if ( products == null )
					products = new HashMap <String,Product>();

				products.put(product.getImage(), product);
				setProducts(products);

				if ( alert(AlertType.INFORMATION, "Information", "Product added").get() == ButtonType.OK )
					clearFields();
			}
		}
		else
			alert(AlertType.WARNING, "Warning", "Missing fields");

	}

	private final boolean isAllCompiled()
	{
		if ( ward.getValue() == null )
			return false;

		if ( name.getText().isEmpty() )
			return false;

		if ( brand.getValue() == null )
			return false;

		if ( qtyPerItem.getText().isEmpty() )
			return false;

		if ( measureUnit.getSelectionModel().isEmpty() )
			return false;

		if ( price.getText().isEmpty() )
			return false;

		if ( image == null )
			return false;

		if ( type.getValue() == null )
			return false;

		if ( qtyAvailable.getText().isEmpty() )
			return false;

		return true;
	}

	private final boolean alreadyExists(Product product)
	{
		Map <String,Product> products = getProducts();

		if ( products != null && products.get(product.getImage()) != null )
			return true;

		return false;
	}

	private final void clearFields()
	{
		name.clear();
		ward.setValue(null);
		brand.setValue(null);
		qtyPerItem.clear();
		qtyAvailable.clear();
		measureUnit.setValue(null);
		price.clear();
		type.setValue(null);
		glutenFree.setSelected(false);
		milkFree.setSelected(false);
		bio.setSelected(false);
		madeInItaly.setSelected(false);
		imageView.setImage(new Image(getClass().getResourceAsStream("/icons/generics/select.png")));
	}

	// Modify product Tab
	@FXML
	private TableView <ProductProperty> tableView;
	@FXML
	private TableColumn <ProductProperty,String> imageColumn;
	@FXML
	private TableColumn <ProductProperty,String> imagePathColumn;
	@FXML
	private TableColumn <ProductProperty,Ward> wardColumn;
	@FXML
	private TableColumn <ProductProperty,String> nameColumn;
	@FXML
	private TableColumn <ProductProperty,Float> qtyPerItemColumn;
	@FXML
	private TableColumn <ProductProperty,Float> priceColumn;
	@FXML
	private TableColumn <ProductProperty,Integer> qtyAvailableColumn;
	@FXML
	private Button btnApplyChanges;
	private ObservableList <ProductProperty> dataList;

	private Map <String,Product> newProducts;

	private void initializeModifyProductsTab()
	{
		newProducts = getProducts();
		dataList = FXCollections.observableArrayList();
		initModifyProductsTabEventHandlers();

		setImageColumn();
		setImagePathColumn();
		setWardColumn();
		setNameColumn();
		setQtyPerItemColumn();
		setPriceColumn();
		setQtyAvailableColumn();

		// Add all products to tableView
		Map <String,Product> products = getProducts();

		for ( Product p : products.values() )
			dataList.add(new ProductProperty(p));

		// 1. Wrap the ObservableList in a FilteredList ( initially display all data )
		FilteredList <ProductProperty> filteredData = new FilteredList <>(dataList, b -> true);

		// 2. Set the filter Predicate whenever the filter changes
		searchBar.textProperty().addListener((observable, oldValue, newValue) ->
		{
			filteredData.setPredicate(product ->
			{
				// If filter text is empty, display all products
				if ( newValue == null || newValue.isEmpty() )
					return true;

				// Compare ... of every product with filter text
				String lowerCaseFilter = newValue.toLowerCase();

				if ( product.getName().toLowerCase().indexOf(lowerCaseFilter) != -1 )
					// Filter matches brand
					return true;
				else
					// Does not match
					return false;
			});
		});

		// 3. Wrap the FilteredList in a SortedList
		SortedList <ProductProperty> sortedData = new SortedList <>(filteredData);

		// 4. Bind the SortedList comparator to the TableView comparator
		sortedData.comparatorProperty().bind(tableView.comparatorProperty());

		// 5. Add sorted ( and filtered ) data to the table
		tableView.setItems(sortedData);
	}

	private void setImageColumn()
	{
		imageColumn.setCellValueFactory(new PropertyValueFactory <>("imageView"));
	}

	private void setImagePathColumn()
	{
		imagePathColumn.setCellValueFactory(new PropertyValueFactory <>("imagePath"));
	}

	private void setWardColumn()
	{
		wardColumn.setCellValueFactory(new PropertyValueFactory <>("ward"));
		wardColumn.setCellFactory(ComboBoxTableCell.forTableColumn(Ward.values()));
		wardColumn.setOnEditCommit(event ->
		{
			Product toModify = newProducts.get(event.getRowValue().getImagePath());
			toModify.setWard(event.getNewValue());
			newProducts.replace(toModify.getImage(), toModify);
		});
	}

	private void setNameColumn()
	{
		nameColumn.setCellValueFactory(new PropertyValueFactory <>("name"));
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		nameColumn.setOnEditCommit(event ->
		{
			event.getRowValue().setName(event.getNewValue());
			Product toModify = newProducts.get(event.getRowValue().getImagePath());
			toModify.setName(event.getNewValue());
			newProducts.replace(toModify.getImage(), toModify);
		});
	}

	private void setQtyPerItemColumn()
	{
		qtyPerItemColumn.setCellValueFactory(new PropertyValueFactory <>("qtyPerItem"));
		qtyPerItemColumn.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()
		{
			@Override
			public Float fromString(String value)
			{
				try
				{
					return super.fromString(value);
				}
				catch ( NumberFormatException e )
				{
					return Float.NaN;
				}
			}
		}));
		
		qtyPerItemColumn.setOnEditCommit(event ->
		{
			if ( event.getNewValue() == null || event.getNewValue().isNaN() || event.getNewValue() <= 0 )
			{
				showWarningAlert();
				event.getRowValue().setQtyPerItem(event.getOldValue());
				tableView.refresh();
			}
			else
			{
				event.getRowValue().setQtyPerItem(event.getNewValue());
				Product toModify = newProducts.get(event.getRowValue().getImagePath());
				toModify.setQtyPerItem(event.getNewValue());
				newProducts.replace(toModify.getImage(), toModify);
			}
		});
	}

	private void setPriceColumn()
	{
		priceColumn.setCellValueFactory(new PropertyValueFactory <>("price"));
		priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()
		{
			@Override
			public Float fromString(String value)
			{
				try
				{
					return super.fromString(value);
				}
				catch ( NumberFormatException e )
				{
					return Float.NaN;
				}
			}
		}));
		priceColumn.setOnEditCommit(event ->
		{
			if ( event.getNewValue() == null || event.getNewValue().isNaN() || event.getNewValue() <= 0 )
			{
				showWarningAlert();
				event.getRowValue().setPrice(event.getOldValue());
				tableView.refresh();
			}
			else
			{
				event.getRowValue().setPrice(event.getNewValue());
				Product toModify = newProducts.get(event.getRowValue().getImagePath());
				toModify.setPrice(event.getNewValue());
				newProducts.replace(toModify.getImage(), toModify);
			}
		});
	}

	private void setQtyAvailableColumn()
	{
		qtyAvailableColumn.setCellValueFactory(new PropertyValueFactory <>("qtyAvailable"));
		qtyAvailableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()
		{
			@Override
			public Integer fromString(String value)
			{
				try
				{
					return super.fromString(value);
				}
				catch ( NumberFormatException e )
				{
					return Integer.MIN_VALUE;
				}
			}
		}));
		qtyAvailableColumn.setOnEditCommit(event ->
		{
			if ( event.getNewValue() == null || event.getNewValue() == Integer.MIN_VALUE || event.getNewValue() <= 0 )
			{
				showWarningAlert();
				event.getRowValue().setQtyAvailable(event.getOldValue());
				tableView.refresh();
			}
			else
			{
				event.getRowValue().setQtyAvailable(event.getNewValue());
				Product toModify = newProducts.get(event.getRowValue().getImagePath());
				toModify.setQtyAvailable(event.getNewValue());
				newProducts.replace(toModify.getImage(), toModify);
			}
		});
	}

	private void initModifyProductsTabEventHandlers()
	{
		btnApplyChanges.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler <Event>()
		{
			@Override
			public void handle(Event event)
			{
				setProducts(newProducts);
				alert(AlertType.INFORMATION, "Information", "The changes were applied to the database products.txt");
			}
		});

		btnApplyChanges.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler <KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if ( event.getCode() == KeyCode.ENTER )
				{
					setProducts(newProducts);
					alert(AlertType.INFORMATION, "Information",
							"The changes were applied to the database products.txt");
				}
			}
		});
	}

	private void showWarningAlert()
	{
		alert(AlertType.WARNING, "Warning", "The entered value is not correct !");
	}

	// View shopping Tab
	@FXML
	private TableView <ShoppingCartProperty> shoppingTableView;
	@FXML
	private TableColumn <ShoppingCartProperty,Integer> IDColumn;
	@FXML
	private TableColumn <ShoppingCartProperty,Date> expectedDateColumn;
	@FXML
	private TableColumn <ShoppingCartProperty,String> customerEmailColumn;
	@FXML
	private TableColumn <ShoppingCartProperty,Float> totalPriceColumn;
	@FXML
	private TableColumn <ShoppingCartProperty,PaymentMethod> paymentMethodColumn;
	private ObservableList <ShoppingCartProperty> shoppingDataList;
	
	private void initializeViewShoppingTab()
	{
		shoppingDataList = FXCollections.observableArrayList();
		
		setIDColumn();
		setExpectedDateColumn();
		setCustomerEmailColumn();
		setTotalPriceColumn();
		setpaymentMethodColumn();

		// Add all shoppingCarts to shoppingTableView
		Map <String,ArrayList<ShoppingCart>> shoppingCarts = getShoppingCarts();

		for ( String customer : shoppingCarts.keySet() )
		{	
			for ( ShoppingCart shoppingCart : shoppingCarts.get(customer) )
				shoppingDataList.add(new ShoppingCartProperty ( shoppingCart ));
		}

		// 1. Wrap the ObservableList in a FilteredList ( initially display all data )
		FilteredList <ShoppingCartProperty> shoppingFilteredData = new FilteredList <>(shoppingDataList, b -> true);

		// 2. Set the filter Predicate whenever the filter changes
		searchBar.textProperty().addListener((observable, oldValue, newValue) ->
		{
			shoppingFilteredData.setPredicate(shoppingCart ->
			{
				// If filter text is empty, display all products
				if ( newValue == null || newValue.isEmpty() )
					return true;

				// Compare ... of every product with filter text
				String lowerCaseFilter = newValue.toLowerCase();

				if ( shoppingCart.getCustomerEmail().toLowerCase().indexOf(lowerCaseFilter) != -1 )
					// Filter matches brand
					return true;
				else
					// Does not match
					return false;
			});
		});

		// 3. Wrap the FilteredList in a SortedList
		SortedList <ShoppingCartProperty> shoppingSortedData = new SortedList <>(shoppingFilteredData);

		// 4. Bind the SortedList comparator to the TableView comparator
		shoppingSortedData.comparatorProperty().bind(shoppingTableView.comparatorProperty());

		// 5. Add sorted ( and filtered ) data to the table
		shoppingTableView.setItems(shoppingSortedData);
	}
	
	private void setIDColumn()
	{
		IDColumn.setCellValueFactory(new PropertyValueFactory <>("ID"));
	}
	
	private void setExpectedDateColumn()
	{
		expectedDateColumn.setCellValueFactory(new PropertyValueFactory <>("expectedDate"));
	}
	
	private void setCustomerEmailColumn()
	{
		customerEmailColumn.setCellValueFactory(new PropertyValueFactory <>("customerEmail"));
	}
	private void setTotalPriceColumn()
	{
		totalPriceColumn.setCellValueFactory(new PropertyValueFactory <>("totalPrice"));
	}
	
	private void setpaymentMethodColumn()
	{
		paymentMethodColumn.setCellValueFactory(new PropertyValueFactory <>("paymentMethod"));
	}
	
	public void viewProducts()
	{
		
	}
}