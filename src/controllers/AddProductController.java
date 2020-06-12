package controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.imageio.ImageIO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Brand;
import models.Feature;
import models.MeasureUnit;
import models.Product;
import models.Type;
import models.Ward;

public class AddProductController extends Controller implements Initializable
{
	@FXML
	private Pane container;
	@FXML
	private ComboBox <Ward> ward;
	@FXML
	private ComboBox <Brand> brand;
	@FXML
	private ComboBox <Type> type;
	@FXML
	private ComboBox <MeasureUnit> measureUnit;
	@FXML
	private TextField prodName, qtyPerItem, qtyAvailable, price;
	@FXML
	private CheckBox glutenFree, milkFree, bio, madeInItaly;
	@FXML
	private ImageView imageView;
	private String image;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		clearFields();
		Platform.runLater(() -> container.requestFocus());
		initEventHandlers();

		ObservableList <Ward> wards = FXCollections.observableArrayList(Ward.values());
		ward.setItems(wards);

		ObservableList <Brand> brands = FXCollections.observableArrayList(Brand.values());
		brand.setItems(brands);

		ObservableList <Type> types = FXCollections.observableArrayList(Type.values());
		type.setItems(types);

		ObservableList <MeasureUnit> measureUnits = FXCollections.observableArrayList(MeasureUnit.values());
		measureUnit.setItems(measureUnits);
	}

	private void initEventHandlers()
	{
		imageView.setOnMouseEntered(new EventHandler <MouseEvent>()
		{
			@Override
			public void handle(MouseEvent e)
			{
				Scene scene = container.getScene();
				scene.setCursor(Cursor.HAND);
			}
		});

		imageView.setOnMouseExited(new EventHandler <MouseEvent>()
		{
			@Override
			public void handle(MouseEvent e)
			{
				Scene scene = container.getScene();
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

		if ( file != null )
		{
			try
			{
				BufferedImage bufferedImage = ImageIO.read(file);

				Image imageSelected = SwingFXUtils.toFXImage(bufferedImage, null);
				imageView.setImage(imageSelected);

				image = file.getPath();
				prodName.setText(image.replace("\\", "/"));
				prodName.setText(prodName.getText().split("/")[prodName.getText().split("/").length - 1].split("\\.")[0]
						.replace("_", " ").replace(".png", ""));
				prodName.setText(prodName.getText().substring(0, 1).toUpperCase() + prodName.getText().substring(1));
			}
			catch ( IOException e )
			{
				System.err.println("Image not found !");
			}
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

			Product product = new Product(ward.getValue(), prodName.getText(), brand.getValue(), qtyPerItemAsFloat,
					measureUnit.getValue(), priceAsFloat, image, type.getValue(), features, qtyAvailableAsInt);

			if ( productAlreadyExists(product) )
			{
				clearFields();

				alertWarning(AlertType.WARNING, "Warning", "Product already exists");
			}
			else
			{
				// Insert product into DB
				Map <String,Product> products = getProducts();

				products.put(product.getImage(), product);
				setProducts(products);

				alertWarning(AlertType.INFORMATION, "Information", "Product added");
				
				clearFields();

				((Stage) container.getScene().getWindow()).close();
			}
		}
		else
			alertWarning(AlertType.WARNING, "Warning", "All fields must be filled");
	}

	private final boolean isAllCompiled()
	{
		return (ward.getValue() != null && !prodName.getText().isEmpty() && brand.getValue() != null
				&& !qtyPerItem.getText().isEmpty() && !measureUnit.getSelectionModel().isEmpty()
				&& !price.getText().isEmpty() && image != null && type.getValue() != null
				&& !qtyAvailable.getText().isEmpty());
	}

	private final boolean productAlreadyExists(Product product)
	{
		return (getProducts().get(product.getImage()) == null ? false : true);
	}

	private final void clearFields()
	{
		prodName.clear();
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
}