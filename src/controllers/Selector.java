package controllers;

import java.awt.Color;
import java.util.ArrayList;
import javafx.scene.Node;

public class Selector implements Runnable
{
	private volatile boolean quit = false;
	private static long start = System.currentTimeMillis();
	private static float upperOverlayLimit = 0.6f;
	private static float lowerOverlayLimit = 0.2f;
	private static boolean goUpOrDown = true;
	private static float transparencyValue = 0f;
	private static float step = 0.02f;
	private static ArrayList <Node> nodesToHighlight = new ArrayList <Node>();

	@Override
	public void run()
	{
		while ( !quit )
		{
			if ( (System.currentTimeMillis() - start) / 1000f > 0.05f )
			{
				// Update value
				if ( goUpOrDown )
					transparencyValue += step;
				else
					transparencyValue -= step;

				// Clamp value and invert effect
				if ( transparencyValue > upperOverlayLimit )
				{
					transparencyValue = upperOverlayLimit;
					goUpOrDown = false;
				}
				else if ( transparencyValue < lowerOverlayLimit )
				{
					transparencyValue = lowerOverlayLimit;
					goUpOrDown = true;
				}

				Color c = Color.decode(ShopController.selectedColor);

				// Highlight nodes
				for ( Node node : nodesToHighlight )
				{
					node.setStyle("-fx-background-color: rgba(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue()
							+ ", " + transparencyValue + ");");
				}

				start = System.currentTimeMillis();
			}
		}
	}

	public static void addNodeToSelection(Node n)
	{
		nodesToHighlight.add(n);
	}

	public static void clearSelectionList(boolean leaveSelected)
	{
		if ( !leaveSelected )
		{
			for ( Node node : nodesToHighlight )
			{
				node.setStyle("-fx-background-color:" + ShopController.backgroundColor + ";");
			}
		}

		nodesToHighlight.clear();
	}

	public void quit()
	{
		this.quit = true;
	}
}