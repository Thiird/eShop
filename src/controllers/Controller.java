package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.Customer;
import models.Product;
import models.ShoppingCart;
import models.User;

public class Controller
{
	private User currentUser;

	public Controller openView(String viewPath, String viewTitle)
	{
		Parent parent = null;
		FXMLLoader loader = null;

		try
		{
			loader = new FXMLLoader(getClass().getResource(viewPath));
			parent = loader.load();

			// sendDataToController(loader, viewTitle, user, shoppingCart, products);
		}
		catch ( IOException e )
		{
			System.err.println("switchToView IOException");
		}

		Scene scene = new Scene(parent);
		Stage stage = new Stage();

		stage.centerOnScreen();
		stage.setResizable(false);
		stage.setTitle(viewTitle);
		stage.setScene(scene);
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/generics/eShop.png")));
		stage.show();

		return loader.getController();
	}

	public User getCurrentUser()
	{
		return currentUser;
	}

	public void setCurrentUser(User user)
	{
		this.currentUser = user;
	}

	@SuppressWarnings ( "unchecked" )
	public Map <String,Customer> getCustomers()
	{
		Map <String,User> users = null;
		Map <String,Customer> customers = new HashMap <>();

		URL resource = getClass().getResource("/databases/users.txt");
		File file = null;

		try
		{
			file = new File(resource.toURI());
		}
		catch ( URISyntaxException e )
		{
			System.err.println("getCustomers URISyntaxException");
		}

		try ( FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis); )
		{
			users = (HashMap <String,User>) ois.readObject();
		}
		catch ( IOException e )
		{
			System.err.println("getCustomers IOException");
		}
		catch ( ClassNotFoundException e )
		{
			System.err.println("getCustomers ClassNotFoundException");
		}

		if ( users != null )
		{
			for ( User user : users.values() )
				if ( user instanceof Customer )
					customers.put(user.getEmail(), (Customer) user);
		}

		return customers;
	}

	@SuppressWarnings ( "unchecked" )
	public static final Map <String,User> getUsers()
	{
		Map <String,User> users = null;

		URL resource = Class.class.getClass().getResource("/databases/users.txt");
		File file = null;

		try
		{
			file = new File(resource.toURI());
		}
		catch ( URISyntaxException e )
		{
			System.err.println("getUsers URISyntaxException");
		}

		try ( FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis); )
		{
			users = (HashMap <String,User>) ois.readObject();
		}
		catch ( IOException e )
		{
			System.err.println("getUsers IOException");
		}
		catch ( ClassNotFoundException e )
		{
			System.err.println("getUsers ClassNotFoundException");
		}

		// File is empty, return an empty database
		if ( users == null )
			return new HashMap <String,User>();

		return users;
	}

	public static final void setUsers(Map <String,User> users)
	{
		URL resource = Class.class.getClass().getResource("/databases/users.txt");
		File file = null;

		try
		{
			file = new File(resource.toURI());
		}
		catch ( URISyntaxException e )
		{
			System.err.println("setUsers URISyntaxException");
		}

		try ( FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(fos); )
		{
			oos.writeObject(users);
		}
		catch ( IOException e )
		{
			System.err.println("setUsers IOException");
		}
	}

	@SuppressWarnings ( "unchecked" )
	public static final Map <String,Product> getProducts()
	{
		Map <String,Product> products = null;

		URL resource = Class.class.getClass().getResource("/databases/products.txt");
		File file = null;

		try
		{
			file = new File(resource.toURI());
		}
		catch ( URISyntaxException e )
		{
			System.err.println("getProducts URISyntaxException");
		}

		try ( FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis); )
		{
			products = (HashMap <String,Product>) ois.readObject();
		}
		catch ( IOException e )
		{
			System.err.println("getProducts IOException");
			return new HashMap <String,Product>();
		}
		catch ( ClassNotFoundException e )
		{
			System.err.println("getProducts ClassNotFoundException");
		}

		// File is empty, return an empty db
		if ( products == null )
			return new HashMap <String,Product>();

		Map <String,Product> newProducts = adjustPath(products);

		return newProducts;
	}

	public static final void setProducts(Map <String,Product> products)
	{

		URL resource = Class.class.getClass().getResource("/databases/products.txt");
		File file = null;

		try
		{
			file = new File(resource.toURI());
		}
		catch ( URISyntaxException e1 )
		{
			e1.printStackTrace();
		}

		try ( FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(fos); )
		{
			oos.writeObject(products);
		}
		catch ( IOException e )
		{
			System.err.println("products.txt not found !");
		}
	}

	@SuppressWarnings ( "unchecked" )
	public Map <String,ArrayList <ShoppingCart>> getShoppingCarts(Customer customer)
	{
		Map <String,ArrayList <ShoppingCart>> allShoppingCarts = null;

		URL resource = getClass().getResource("/databases/shoppingCarts.txt");
		File file = null;

		try
		{
			file = new File(resource.toURI());
		}
		catch ( URISyntaxException e )
		{
			System.err.println("getShoppingCarts URISyntaxException");
		}

		try ( FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis); )
		{
			allShoppingCarts = (HashMap <String,ArrayList <ShoppingCart>>) ois.readObject();
		}
		catch ( IOException e )
		{
			return new HashMap <String,ArrayList <ShoppingCart>>();

			// System.err.println("getShoppingCarts IOException");
		}
		catch ( ClassNotFoundException e )
		{
			System.err.println("getShoppingCarts ClassNotFoundException");
		}

		if ( allShoppingCarts == null )
			return new HashMap <String,ArrayList <ShoppingCart>>();

		if ( Objects.nonNull(customer) )
		{
			Map <String,ArrayList <ShoppingCart>> customerShoppingCarts = new HashMap <>();
			ArrayList <ShoppingCart> shoppingCarts = new ArrayList <>();

			for ( String s : allShoppingCarts.keySet() )
			{
				for ( ShoppingCart shoppingCart : allShoppingCarts.get(s) )
					shoppingCarts.add(shoppingCart);
			}

			customerShoppingCarts.put(customer.getEmail(), shoppingCarts);

			return customerShoppingCarts;
		}

		return allShoppingCarts;
	}

	public void setShoppingCarts(Map <String,ArrayList <ShoppingCart>> shoppingCarts)
	{
		URL resource = getClass().getResource("/databases/shoppingCarts.txt");
		File file = null;

		try
		{
			file = new File(resource.toURI());
		}
		catch ( URISyntaxException e )
		{
			System.err.println("setShoppingCarts URISyntaxException");
		}

		try ( FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(fos); )
		{
			oos.writeObject(shoppingCarts);
		}
		catch ( IOException e )
		{
			System.err.println("setShoppingCarts IOException");
		}
	}

	private Comparator <Customer> getComparatorByID()
	{
		return new Comparator <Customer>()
		{
			@Override
			public int compare(Customer u1, Customer u2)
			{
				return u1.getFidelityCard().getID() - u2.getFidelityCard().getID();
			}
		};
	}

	public int getNextFidelityCardID()
	{
		Map <String,Customer> customers = getCustomers();

		if ( customers != null )
		{
			Collection <Customer> customersAsCollection = customers.values();
			List <Customer> customersAsList = new ArrayList <>(customersAsCollection);
			customersAsList.sort(getComparatorByID());
			if ( customersAsList.size() == 0 )
				return 0;

			return customersAsList.get(customersAsList.size() - 1).getFidelityCard().getID() + 1;
		}

		return 0;
	}

	public static final Optional <ButtonType> alertWarning(AlertType type, String title, String header)
	{
		Alert alert = new Alert(type);

		alert.setTitle(title);
		alert.setHeaderText(header);

		return alert.showAndWait();
	}

	public static boolean alertPrompt(AlertType type, String title, String content)
	{
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setContentText(content);

		if ( alert.showAndWait().get() == ButtonType.OK )
			return true;
		else
			return false;
	}

	private static final Map <String,Product> adjustPath(Map <String,Product> products)
	{
		Map <String,Product> newProducts = new HashMap <String,Product>();

		for ( String key : products.keySet() )
		{
			if ( key.contains("bin") )
				newProducts.put(key.split("bin")[1].replace("\\", "/"), products.get(key));
			else
				newProducts.put(key.replace("\\", "/"), products.get(key));
		}

		return newProducts;
	}

}