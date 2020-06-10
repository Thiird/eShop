package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import models.Employee;
import models.PaymentMethod;
import models.Product;
import models.ProductProperty;
import models.ShoppingCart;
import models.ShoppingCartProperty;
import models.State;
import models.Ward;

public class EmployeeController extends Controller implements Initializable
{
	@FXML
	private Pane container;
	@FXML
	private TabPane tabPane;
	@FXML
	private Button btnAddProduct, btnRemoveProduct;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		Platform.runLater(() -> setCloseEvent());

		initializeModifyProductsTab();
		tabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener <Number>()
		{
			@Override
			public void changed(ObservableValue <? extends Number> observable, Number oldValue, Number newValue)
			{
				int selectedIndex = newValue.intValue();
				// Where index of the first tab is 0, while that of the second tab is 1 and so
				// on
				if ( selectedIndex == 0 )
					initializeModifyProductsTab();
				else
					initializeViewShoppingTab();
			}
		});

		cmbSearchFilter.getItems().addAll("Customer email", "ID");
	}

	// Modify product Tab
	@FXML
	private TextField searchBar;
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
	private ObservableList <ProductProperty> dataList;
	private Map <String,Product> newProducts;

	private void initializeModifyProductsTab()
	{
		Platform.runLater(() -> tabPane.requestFocus());
		newProducts = getProducts();
		dataList = FXCollections.observableArrayList();

		setImageColumn();
		setImagePathColumn();
		setWardColumn();
		setNameColumn();
		setQtyPerItemColumn();
		setPriceColumn();
		setQtyAvailableColumn();

		// Add all products to tableView
		for ( Product p : newProducts.values() )
			dataList.add(new ProductProperty(p, 0));

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
			setProducts ( newProducts );
		});
	}

	private void setNameColumn()
	{
		nameColumn.setCellValueFactory(new PropertyValueFactory <>("name"));
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
				inputAlert();
				event.getRowValue().setQtyPerItem(event.getOldValue());
				tableView.refresh();
			}
			else
			{
				event.getRowValue().setQtyPerItem(event.getNewValue());
				Product toModify = newProducts.get(event.getRowValue().getImagePath());
				toModify.setQtyPerItem(event.getNewValue());
				newProducts.replace(toModify.getImage(), toModify);
				setProducts ( newProducts );
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
				inputAlert();
				event.getRowValue().setPrice(event.getOldValue());
				tableView.refresh();
			}
			else
			{
				event.getRowValue().setPrice(event.getNewValue());
				Product toModify = newProducts.get(event.getRowValue().getImagePath());
				toModify.setPrice(event.getNewValue());
				newProducts.replace(toModify.getImage(), toModify);
				setProducts ( newProducts );
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
				inputAlert();
				event.getRowValue().setQtyAvailable(event.getOldValue());
				tableView.refresh();
			}
			else
			{
				event.getRowValue().setQtyAvailable(event.getNewValue());
				Product toModify = newProducts.get(event.getRowValue().getImagePath());
				toModify.setQtyAvailable(event.getNewValue());
				newProducts.replace(toModify.getImage(), toModify);
				setProducts ( newProducts );
			}
		});
	}

	public void removeProduct()
	{
		ProductProperty productProperty = tableView.getSelectionModel().getSelectedItem();

		if ( productProperty != null )
		{
			dataList.remove(productProperty);
			newProducts.remove(productProperty.getProduct().getImage());
			setProducts(newProducts);
			tableView.refresh();
		}
	}

	private void inputAlert()
	{
		alertWarning(AlertType.WARNING, "Warning", "The entered value is not correct !");
	}

	// View shopping Tab
	@FXML
	private TextField searchBarTab2;
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
	@FXML
	private TableColumn <ShoppingCartProperty,State> stateColumn;
	@FXML
	private TableColumn <ShoppingCartProperty,Integer> pointsColumn;
	@FXML
	private Button btnViewProducts;

	@FXML
	private ComboBox <String> cmbSearchFilter;

	private ObservableList <ShoppingCartProperty> shoppingDataList;

	private void initializeViewShoppingTab()
	{
		btnViewProducts.disableProperty()
				.bind(Bindings.isEmpty(shoppingTableView.getSelectionModel().getSelectedItems()));

		shoppingDataList = FXCollections.observableArrayList();

		setIDColumn();
		setExpectedDateColumn();
		setCustomerEmailColumn();
		setTotalPriceColumn();
		setPaymentMethodColumn();
		setStateColumn();
		setPointsColumn();

		// Add all shoppingCarts to shoppingTableView
		Map <String,ArrayList <ShoppingCart>> shoppingCarts = getShoppingCarts(null);

		for ( String customer : shoppingCarts.keySet() )
		{
			for ( ShoppingCart shoppingCart : shoppingCarts.get(customer) )
				shoppingDataList.add(new ShoppingCartProperty(shoppingCart));
		}

		// 1. Wrap the ObservableList in a FilteredList ( initially display all data )
		FilteredList <ShoppingCartProperty> shoppingFilteredData = new FilteredList <>(shoppingDataList, b -> true);

		// 2. Set the filter Predicate whenever the filter changes
		searchBarTab2.textProperty().addListener((observable, oldValue, newValue) ->
		{
			shoppingFilteredData.setPredicate(shoppingCart ->
			{
				// If filter text is empty, display all products
				if ( newValue == null || newValue.isEmpty() )
					return true;

				if ( cmbSearchFilter.getValue() == null )
					return true;

				String lowerCaseFilter = newValue.toLowerCase();

				if ( cmbSearchFilter.getValue().equalsIgnoreCase("ID") )
					return (Integer.toString(shoppingCart.getID()).toLowerCase().indexOf(lowerCaseFilter) != -1) ? true
							: false;
				else if ( cmbSearchFilter.getValue().equalsIgnoreCase("Customer email") )
					return (shoppingCart.getCustomerEmail().toLowerCase().indexOf(lowerCaseFilter) != -1) ? true
							: false;
				else
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

	private void setPaymentMethodColumn()
	{
		paymentMethodColumn.setCellValueFactory(new PropertyValueFactory <>("paymentMethod"));
	}

	private void setStateColumn()
	{
		stateColumn.setCellValueFactory(new PropertyValueFactory <>("state"));
	}

	private void setPointsColumn()
	{
		pointsColumn.setCellValueFactory(new PropertyValueFactory <>("points"));
	}

	public void viewProducts()
	{
		ShoppingCartProperty shoppingCartProperty = shoppingTableView.getSelectionModel().getSelectedItem();

		if ( shoppingCartProperty != null )
		{
			ArrayList <ShoppingCart> customerShoppingCarts = getShoppingCarts(null)
					.get(shoppingCartProperty.getCustomerEmail());

			for ( ShoppingCart shoppingCart : customerShoppingCarts )
			{
				if ( shoppingCart.getID() == shoppingCartProperty.getID() )
				{
					((ViewProductsController) openView("/views/ViewProducts.fxml", "View Products"))
							.setData(shoppingCart.getID(), shoppingCart.getCustomer());

					ViewProductsController.showAndWaitStage();

					break;
				}
			}
		}
	}

	public void goToAddProduct()
	{
		openView("/views/AddProduct.fxml", "Add Product");

		AddProductController.showAndWaitStage();

		initializeModifyProductsTab();
	}

	@FXML
	public void goToAppInfos()
	{
		openView("/views/AppInfos.fxml", "App Info");

		AppInfosController.showAndWaitStage();
	}

	@FXML
	public void showLogOutPrompt()
	{
		if ( alertPrompt(AlertType.CONFIRMATION, "Logout", "Are you sure you want to logout?") )
		{
			((Stage) container.getScene().getWindow()).close();

			setProducts(newProducts);

			logInOutUser(false);

			openView("/views/Login.fxml", "Login");

			LoginController.showStage();
		}
	}

	public void setData(Employee customer)
	{
		setCurrentUser(customer);
	}

	public void setCloseEvent()
	{
		((Stage) container.getScene().getWindow()).setOnCloseRequest(new EventHandler <WindowEvent>()
		{
			@Override
			public void handle(WindowEvent event)
			{
				if ( alertPrompt(AlertType.CONFIRMATION, "Quit application", "Are you sure you want to quit?") )
				{
					setProducts(newProducts);
					logInOutUser(false);
				}
				else
					event.consume();
			}
		});
	}
}
