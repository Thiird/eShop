package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import models.Customer;
import models.Product;
import models.ProductProperty;
import models.ShoppingCart;
import models.Ward;

public class ViewProductsController extends Controller implements Initializable
{
	@FXML
	private Pane container;
	@FXML
	private TextField searchBar;
	@FXML
	private TableView <ProductProperty> tableView;
	@FXML
	private TableColumn <ProductProperty,String> imageColumn;
	@FXML
	private TableColumn <ProductProperty,Ward> wardColumn;
	@FXML
	private TableColumn <ProductProperty,String> nameColumn;
	@FXML
	private TableColumn <ProductProperty,Float> priceColumn;
	@FXML
	private TableColumn <ProductProperty,Integer> quantityColumn;

	private ObservableList <ProductProperty> dataList;
	@SuppressWarnings ( "unused" )
	private ShoppingCart shoppingCart;
	private Map <Product,Integer> products;

	public void setData(int shoppingCartID, Customer customer)
	{
		ArrayList <ShoppingCart> shoppingCarts = getShoppingCarts(null).get(customer.getEmail());

		for ( ShoppingCart shoppingCart : shoppingCarts )
		{
			if ( shoppingCart.getID() == shoppingCartID )
			{
				this.shoppingCart = shoppingCart;

				products = shoppingCart.getProducts();
				for ( Product p : products.keySet() )
					dataList.add(new ProductProperty(p, products.get(p)));
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		Platform.runLater(() -> container.requestFocus());

		dataList = FXCollections.observableArrayList();
		setImageColumn();
		setQuantityColumn();
		setWardColumn();
		setNameColumn();
		setPriceColumn();

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

	private void setQuantityColumn()
	{
		quantityColumn.setCellValueFactory(new PropertyValueFactory <>("cartQuantity"));
	}

	private void setWardColumn()
	{
		wardColumn.setCellValueFactory(new PropertyValueFactory <>("ward"));
	}

	private void setNameColumn()
	{
		nameColumn.setCellValueFactory(new PropertyValueFactory <>("name"));
	}

	private void setPriceColumn()
	{
		priceColumn.setCellValueFactory(new PropertyValueFactory <>("price"));
	}
}