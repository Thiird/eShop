package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.Customer;
import models.PaymentMethod;
import models.ShoppingCart;
import models.ShoppingCartProperty;

public class CustomerController extends Controller implements Initializable
{
	@FXML
	private Pane pane;
	@FXML
	private TextField searchBar;
	@FXML
	private TableView <ShoppingCartProperty> tableView;
	@FXML
	private TableColumn <ShoppingCartProperty,Date> expectedDateColumn;
	@FXML
	private TableColumn <ShoppingCartProperty,Float> totalPriceColumn;
	@FXML
	private TableColumn <ShoppingCartProperty,PaymentMethod> paymentMethodColumn;
	@FXML
	private Button btnViewProducts, btnEditProfile, btnFidelityCard;

	private ObservableList <ShoppingCartProperty> dataList;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		Platform.runLater(() -> pane.requestFocus());

		dataList = FXCollections.observableArrayList();

		setExpectedDateColumn();
		setTotalPriceColumn();
		setPaymentMethodColumn();

		// Add all shoppingCarts to tableView
		Map <String,ArrayList <ShoppingCart>> shoppingCarts = getShoppingCarts((Customer) getCurrentUser());

		for ( String customer : shoppingCarts.keySet() )
		{
			for ( ShoppingCart shoppingCart : shoppingCarts.get(customer) )
				dataList.add(new ShoppingCartProperty(shoppingCart));
		}

		// 1. Wrap the ObservableList in a FilteredList ( initially display all data )
		FilteredList <ShoppingCartProperty> shoppingFilteredData = new FilteredList <>(dataList, b -> true);

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
		shoppingSortedData.comparatorProperty().bind(tableView.comparatorProperty());

		// 5. Add sorted ( and filtered ) data to the table
		tableView.setItems(shoppingSortedData);

	}

	public void setData(Customer customer)
	{
		setCurrentUser(customer);

		if ( customer.getFidelityCard().getEnabled() == false )
			btnFidelityCard.setVisible(false);
	}

	public void openFidelityCardView()
	{
		((FidelityCardController) openView("/views/FidelityCard.fxml", "Fidelity Card"))
				.setData((Customer) getCurrentUser());
	}

	public void openViewProductsView()
	{
		ShoppingCartProperty shoppingCartProperty = tableView.getSelectionModel().getSelectedItem();

		if ( shoppingCartProperty != null )
		{
			ArrayList <ShoppingCart> customerShoppingCarts = getShoppingCarts((Customer) getCurrentUser())
					.get(shoppingCartProperty.getCustomerEmail());

			for ( ShoppingCart shoppingCart : customerShoppingCarts )
			{
				if ( shoppingCart.getID() == shoppingCartProperty.getID() )

					((ViewProductsController) openView("/views/ViewProducts.fxml", "View Products"))
							.setData(shoppingCart.getID(), (Customer) getCurrentUser());
			}
		}
	}

	public void openEditProfileView()
	{
		((EditProfileController) openView("/views/EditProfile.fxml", "Edit Profile"))
				.setData((Customer) getCurrentUser());
	}

	public void switchToShop()
	{
		((Stage) pane.getScene().getWindow()).close();

		((EditProfileController) openView("/views/Shop.fxml", "Shop")).setData((Customer) getCurrentUser());
	}

	private void setExpectedDateColumn()
	{
		expectedDateColumn.setCellValueFactory(new PropertyValueFactory <>("expectedDate"));
	}

	private void setTotalPriceColumn()
	{
		totalPriceColumn.setCellValueFactory(new PropertyValueFactory <>("totalPrice"));
	}

	private void setPaymentMethodColumn()
	{
		paymentMethodColumn.setCellValueFactory(new PropertyValueFactory <>("paymentMethod"));
	}
}