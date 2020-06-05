package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
	private Button btnResetFilters, btnRemoveProduct;
	@FXML
	private ComboBox <Type> cmbType;
	@FXML
	private ComboBox <Brand> cmbBrand;

	private ShopController shopController;

	private ObservableList <ProductProperty> dataList;// TODO what is this

	private ShoppingCart shoppingCart;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		Platform.runLater(() ->
		{
			container.requestFocus();
			setCloseEvent();
		});

		setImageColumn();
		setQuantityColumn();
		setNameColumn();
		setPriceColumn();
		setBrandColumn();
		setTypeColumn();
		setMadeInItalyColumn();

		dataList = FXCollections.observableArrayList();

		// Set ComboBox
		ObservableList <Type> types = FXCollections.observableArrayList(Type.values());
		cmbType.setItems(types);

		ObservableList <Brand> brands = FXCollections.observableArrayList(Brand.values());
		cmbBrand.setItems(brands);

		// Products filters
		ObjectProperty <Predicate <ProductProperty>> typeFilter = new SimpleObjectProperty <>();
		ObjectProperty <Predicate <ProductProperty>> brandFilter = new SimpleObjectProperty <>();

		typeFilter.bind(Bindings.createObjectBinding(() -> product -> cmbType.getValue() == null
				|| cmbType.getValue() == Type.ALL || cmbType.getValue() == product.getType(), cmbType.valueProperty()));

		brandFilter.bind(Bindings.createObjectBinding(() -> product -> cmbBrand.getValue() == null
				|| cmbBrand.getValue() == Brand.ALL || cmbBrand.getValue() == product.getBrand(),
				cmbBrand.valueProperty()));

		FilteredList <ProductProperty> filteredProducts = new FilteredList <>(dataList);

		tableView.setItems(filteredProducts);
		filteredProducts.predicateProperty().bind(
				Bindings.createObjectBinding(() -> typeFilter.get().and(brandFilter.get()), typeFilter, brandFilter));

		btnResetFilters.setOnAction(e ->
		{
			cmbType.setValue(null);
			cmbBrand.setValue(null);
			tableView.refresh();
		});
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

	public void setData(ShoppingCart sc, ShopController shopController)
	{
		this.shoppingCart = sc;
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