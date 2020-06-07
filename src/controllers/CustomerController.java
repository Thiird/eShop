package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.Customer;
import models.PaymentMethod;
import models.ShoppingCart;
import models.ShoppingCartProperty;
import models.State;

public class CustomerController extends Controller implements Initializable
{
	@FXML
	private Pane container;
	@FXML
	private TableView <ShoppingCartProperty> tableView;
	@FXML
	private TableColumn <ShoppingCartProperty,Date> expectedDateColumn;
	@FXML
	private TableColumn <ShoppingCartProperty,Float> totalPriceColumn;
	@FXML
	private TableColumn <ShoppingCartProperty,PaymentMethod> paymentMethodColumn;
	@FXML
	private TableColumn <ShoppingCartProperty,State> stateColumn;

	@FXML
	private Button btnViewProducts, btnEditProfile, btnFidelityCard;

	private ObservableList <ShoppingCartProperty> dataList;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		Platform.runLater(() -> container.requestFocus());
		btnViewProducts.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
		dataList = FXCollections.observableArrayList();

		setExpectedDateColumn();
		setTotalPriceColumn();
		setPaymentMethodColumn();
		setStateColumn();

		// Add all shoppingCarts to tableView
		Map <String,ArrayList <ShoppingCart>> shoppingCarts = getShoppingCarts((Customer) getCurrentUser());

		for ( String customer : shoppingCarts.keySet() )
		{
			for ( ShoppingCart shoppingCart : shoppingCarts.get(customer) )
				dataList.add(new ShoppingCartProperty(shoppingCart));
		}

		// 1. Wrap the FilteredList in a SortedList
		SortedList <ShoppingCartProperty> shoppingSortedData = new SortedList <>(dataList);

		// 2. Bind the SortedList comparator to the TableView comparator
		shoppingSortedData.comparatorProperty().bind(tableView.comparatorProperty());

		// 3. Add sorted ( and filtered ) data to the table
		tableView.setItems(shoppingSortedData);
	}

	public void openFidelityCardView()
	{
		((FidelityCardController) openView("/views/FidelityCard.fxml", "Fidelity Card"))
				.setData((Customer) getCurrentUser());

		FidelityCardController.showAndWaitStage();
	}

	@FXML
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
				{
					((ViewProductsController) openView("/views/ViewProducts.fxml", "View Products"))
							.setData(shoppingCart.getID(), (Customer) getCurrentUser());

					ViewProductsController.showAndWaitStage();

					break;
				}
			}
		}
	}

	public void openEditProfileView()
	{
		((EditProfileController) openView("/views/EditProfile.fxml", "Edit Profile"))
				.setData((Customer) getCurrentUser());

		EditProfileController.showAndWaitStage();
	}

	public void switchToShop()
	{
		((Stage) container.getScene().getWindow()).close();

		((ShopController) openView("/views/Shop.fxml", "Shop")).setData((Customer) getCurrentUser());

		ShopController.showAndWaitStage();
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

	private void setStateColumn()
	{
		stateColumn.setCellValueFactory(new PropertyValueFactory <>("state"));
	}

	public void setData(Customer customer)
	{
		setCurrentUser(customer);

		if ( customer.getFidelityCard().getEnabled() == false )
			btnFidelityCard.setDisable(true);
	}
}