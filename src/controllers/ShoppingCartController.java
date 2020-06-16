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
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
	private TableColumn <ProductProperty,Boolean> bioColumn;
	@FXML
	private TableColumn <ProductProperty,Boolean> glutenFreeColumn;
	@FXML
	private TableColumn <ProductProperty,Boolean> madeInItalyColumn;
	@FXML
	private TableColumn <ProductProperty,Boolean> milkFreeColumn;
	@FXML
	private Button btnResetFilters, btnRemoveProduct;
	@FXML
	private ComboBox <Type> cmbType;
	@FXML
	private ComboBox <Brand> cmbBrand;
	@FXML
	private CheckBox chkBio, chkGlutenFree, chkMadeInItaly, chkMilkFree;
	private ShopController shopController;

	private ObservableList <ProductProperty> productsList;

	private ShoppingCart shoppingCart;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		Platform.runLater(() ->
		{
			container.requestFocus();
			setCloseEvent();
		});

		btnRemoveProduct.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));

		productsList = FXCollections.observableArrayList();

		setImageColumn();
		setQuantityColumn();
		setNameColumn();
		setPriceColumn();
		setBrandColumn();
		setTypeColumn();
		setBioColumn();
		setGlutenFreeColumn();
		setMadeInItalyColumn();
		setMilkFreeColumn();

		// Set ComboBox
		ObservableList <Type> types = FXCollections.observableArrayList(Type.values());
		cmbType.setItems(types);

		ObservableList <Brand> brands = FXCollections.observableArrayList(Brand.values());
		cmbBrand.setItems(brands);

		// Products filters
		ObjectProperty <Predicate <ProductProperty>> typeFilter = new SimpleObjectProperty <>();
		ObjectProperty <Predicate <ProductProperty>> brandFilter = new SimpleObjectProperty <>();
		ObjectProperty <Predicate <ProductProperty>> bioFilter = new SimpleObjectProperty <>();
		ObjectProperty <Predicate <ProductProperty>> glutenFreeFilter = new SimpleObjectProperty <>();
		ObjectProperty <Predicate <ProductProperty>> madeInItalyFilter = new SimpleObjectProperty <>();
		ObjectProperty <Predicate <ProductProperty>> milkFreeFilter = new SimpleObjectProperty <>();

		typeFilter.bind(Bindings.createObjectBinding(
				() -> product -> cmbType.getValue() == null || cmbType.getValue() == product.getType(),
				cmbType.valueProperty()));

		brandFilter.bind(Bindings.createObjectBinding(
				() -> product -> cmbBrand.getValue() == null || cmbBrand.getValue() == product.getBrand(),
				cmbBrand.valueProperty()));

		bioFilter.bind(Bindings.createObjectBinding(
				() -> product -> chkBio.isSelected() == false || chkBio.isSelected() == product.isBio(),
				chkBio.selectedProperty()));

		glutenFreeFilter
				.bind(Bindings.createObjectBinding(
						() -> product -> chkGlutenFree.isSelected() == false
								|| chkGlutenFree.isSelected() == product.isGlutenFree(),
						chkGlutenFree.selectedProperty()));

		madeInItalyFilter
				.bind(Bindings.createObjectBinding(
						() -> product -> chkMadeInItaly.isSelected() == false
								|| chkMadeInItaly.isSelected() == product.isMadeInItaly(),
						chkMadeInItaly.selectedProperty()));

		milkFreeFilter.bind(Bindings.createObjectBinding(
				() -> product -> chkMilkFree.isSelected() == false || chkMilkFree.isSelected() == product.isMilkFree(),
				chkMilkFree.selectedProperty()));

		FilteredList <ProductProperty> filteredProducts = new FilteredList <>(productsList);

		filteredProducts.predicateProperty()
				.bind(Bindings
						.createObjectBinding(
								() -> typeFilter.get()
										.and(brandFilter.get()
												.and(bioFilter.get()
														.and(glutenFreeFilter.get().and(
																madeInItalyFilter.get().and(milkFreeFilter.get()))))),
								typeFilter, brandFilter, bioFilter, glutenFreeFilter, madeInItalyFilter,
								milkFreeFilter));

		SortedList <ProductProperty> sortedProducts = new SortedList <>(filteredProducts);

		sortedProducts.comparatorProperty().bind(tableView.comparatorProperty());

		tableView.setItems(sortedProducts);

		btnResetFilters.setOnAction(e ->
		{
			cmbType.setValue(null);
			cmbBrand.setValue(null);
			chkBio.setSelected(false);
			chkGlutenFree.setSelected(false);
			chkMadeInItaly.setSelected(false);
			chkMilkFree.setSelected(false);
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

				alertWarning(AlertType.WARNING, "Warning", "The entered value is not correct!");

				event.getRowValue().setCartQuantity(event.getOldValue());
				tableView.refresh();
			}
			else
			{
				Product pToModify = event.getRowValue().getProduct();

				if ( (event.getNewValue() - event.getOldValue()) > pToModify.getQtyAvailable() )
				{
					alertWarning(AlertType.WARNING, "Warning",
							"The desired quantity exceeds availability:\n" + "Requested: +"
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

	private void setBioColumn()
	{
		bioColumn.setCellValueFactory(new PropertyValueFactory <>("bio"));
	}

	private void setGlutenFreeColumn()
	{
		glutenFreeColumn.setCellValueFactory(new PropertyValueFactory <>("glutenFree"));
	}

	private void setMadeInItalyColumn()
	{
		madeInItalyColumn.setCellValueFactory(new PropertyValueFactory <>("madeInItaly"));
	}

	private void setMilkFreeColumn()
	{
		milkFreeColumn.setCellValueFactory(new PropertyValueFactory <>("milkFree"));
	}

	public void removeProduct()
	{
		ProductProperty productProperty = tableView.getSelectionModel().getSelectedItem();

		if ( productProperty != null )
		{
			shopController.removeProductFromCart(productProperty.getProduct());
			productsList.remove(productProperty);
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
			productsList.add(new ProductProperty(p, shoppingCart.getProducts().get(p)));
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