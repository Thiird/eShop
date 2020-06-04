package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.converter.IntegerStringConverter;
import models.Brand;
import models.Product;
import models.ProductProperty;
import models.ShoppingCart;
import models.Type;

public class ShoppingCartController extends Controller implements Initializable
{
	@FXML
	private Pane container;
	@FXML
	private TextField searchBar;
	@FXML
	private TableView <ProductProperty> tableView;
	@FXML
	private TableColumn <ProductProperty,String> imageColumn, nameColumn;
	@FXML
	private TableColumn <ProductProperty,Integer> quantityColumn;
	@FXML
	private TableColumn <ProductProperty,Float> priceColumn;
	@FXML
	private TableColumn <ProductProperty,Brand> brandColumn;
	@FXML
	private TableColumn <ProductProperty,Type> typeColumn;
	@FXML
	private TableColumn <ProductProperty,Boolean> madeInItalyColumn;
	@FXML
	private Button btnRemoveProduct, btnApplyChanges;
	@FXML
	private ComboBox <String> cmbType, cmbBrand;

	private ShopController shopController;

	private ObservableList <ProductProperty> dataList;

	private ShoppingCart shoppingCart;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		Platform.runLater(() ->
		{
			container.requestFocus();
			setCloseEvent();
		});

		ObservableList <String> types = FXCollections.observableArrayList(Type.valuesAsString());
		cmbType.setItems(types);
		/*
		 * ObservableList <Brand> brands =
		 * FXCollections.observableArrayList(Brand.values()); cmbBrand.setItems(brands);
		 */
		dataList = FXCollections.observableArrayList();
		setImageColumn();
		setQuantityColumn();
		setNameColumn();
		setPriceColumn();
		setBrandColumn();
		setTypeColumn();
		setMadeInItalyColumn();

		FilteredList <ProductProperty> filteredDataByType = new FilteredList <>(dataList, b -> true);

		cmbType.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
		{
			filteredDataByType.setPredicate(product ->
			{
				// If filter text is empty, display all products
				if ( newValue.equalsIgnoreCase("ALL") )
					return true;

				// Compare ... of every product with filter
				if ( product.getType().toString().equals(newValue) )
					// Filter matches type
					return true;
				else
					// Does not match
					return false;
			});
		});

		// 3. Wrap the FilteredList in a SortedList
		SortedList <ProductProperty> sortedDataByType = new SortedList <>(filteredDataByType);

		// 4. Bind the SortedList comparator to the TableView comparator
		sortedDataByType.comparatorProperty().bind(tableView.comparatorProperty());

		// 5. Add sorted ( and filtered ) data to the table
		tableView.setItems(sortedDataByType);
	}

	@FXML
	public void applyChanges()
	{
		Controller.alertWarning(AlertType.INFORMATION, "Information", "Applied changes");
	}

	private void setImageColumn()
	{
		imageColumn.setCellValueFactory(new PropertyValueFactory <>("imageView"));
	}

	private void setQuantityColumn()
	{
		quantityColumn.setCellValueFactory(new PropertyValueFactory <>("cartQuantity"));
		quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()
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

		quantityColumn.setOnEditCommit(event ->
		{
			if ( event.getNewValue() == null || event.getNewValue() == Integer.MIN_VALUE || event.getNewValue() <= 0 )
			{

				Controller.alertWarning(AlertType.WARNING, "Warning", "The entered value is not correct !");

				event.getRowValue().setCartQuantity(event.getOldValue());
				tableView.refresh();
			}
			else
			{
				Product pToModify = event.getRowValue().getProduct();

				if ( (event.getNewValue() - event.getOldValue()) > pToModify.getQtyAvailable() )
				{
					Controller.alertWarning(AlertType.WARNING, "Warning",
							"The desired quantity exceeds availability" + "\nRequested: +"
									+ (event.getNewValue() - event.getOldValue()) + "\nAvailability: "
									+ pToModify.getQtyAvailable());

					event.getRowValue().setCartQuantity(event.getOldValue());
				}
				else
				{// Update quantity

					pToModify
							.setQtyAvailable(pToModify.getQtyAvailable() - (event.getNewValue() - event.getOldValue()));
					event.getRowValue().setCartQuantity(event.getNewValue());
					shoppingCart.getProducts().put(pToModify, event.getRowValue().getCartQuantity());

					shopController.updateProducts();
				}

				tableView.refresh();
			}
		});
	}

	private void setNameColumn()
	{
		nameColumn.setCellValueFactory(new PropertyValueFactory <>("name"));
	}

	private void setPriceColumn()
	{
		priceColumn.setCellValueFactory(new PropertyValueFactory <>("price"));
	}

	private void setBrandColumn()
	{
		brandColumn.setCellValueFactory(new PropertyValueFactory <>("brand"));
	}

	private void setTypeColumn()
	{
		typeColumn.setCellValueFactory(new PropertyValueFactory <>("type"));
	}

	private void setMadeInItalyColumn()
	{
		madeInItalyColumn.setCellValueFactory(new PropertyValueFactory <>("madeInItaly"));
	}

	public void removeProduct()
	{
		ProductProperty productProperty = tableView.getSelectionModel().getSelectedItem();

		if ( productProperty != null )
		{
			shopController.removeProductFromCart(productProperty.getProduct());
			dataList.remove(productProperty);
			shoppingCart.getProducts().remove(productProperty.getProduct());
			tableView.refresh();
		}

		shopController.updateProducts();
	}

	public void setData(ShoppingCart shoppingCart, ShopController shopController)
	{
		this.shoppingCart = shoppingCart;
		this.shopController = shopController;

		for ( Product p : shoppingCart.getProducts().keySet() )
			dataList.add(new ProductProperty(p, shoppingCart.getProducts().get(p)));
	}

	public void setCloseEvent()
	{
		((Stage) container.getScene().getWindow()).setOnCloseRequest(new EventHandler <WindowEvent>()
		{
			@Override
			public void handle(WindowEvent event)
			{
				// shopController.updateProducts();
			}
		});
	}
}