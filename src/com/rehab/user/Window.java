package com.rehab.user;

import com.rehab.world.Actor;
import com.rehab.world.Arena;
import com.rehab.world.Entity;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@SuppressWarnings("unused")
public class Window {

	// The only instance of a Window allowed
	private static Window mWindow;
	
	private double height;
	private double width;

	Stage primaryStage;
	Canvas canvas;
	GraphicsContext graphics;

	Actor character;
	HealthBar health;
	MainMenu menu;
	Movement movement;

	public static Window getInstance(Stage primaryStage) {
		synchronized (Window.class) {
			if (mWindow == null)
				mWindow = new Window(primaryStage);
			return mWindow;
		}
	}

	public static Window getInstance() {
		synchronized (Window.class) {
			if (mWindow == null)
				throw new IllegalStateException("getInstance(Stage primaryStage) must be called first");
			return mWindow;
		}
	}
	
	public static boolean windowExists(){
		return mWindow != null;
	}

	/**
	 * Constructor to initialize the window
	 * 
	 * @param primaryStage
	 *            the Stage given by JavaFX
	 */
	private Window(Stage primaryStage) {
		this.primaryStage = primaryStage;
		setupWindow(primaryStage);
	}

	/**
	 * Sets up the game Window and displays it for visualizing the game's state.
	 * 
	 * @param primary
	 *            the Stage given by JavaFX
	 * @return the Canvas needed for drawing.
	 */
	private Canvas setupWindow(Stage primaryStage) {
		canvas = new Canvas(Resolution.HDW, Resolution.HDH);
		Group group = new Group();
		group.getChildren().add(canvas);
		primaryStage.setScene(new Scene(group, Color.DARKBLUE));
		primaryStage.setFullScreen(true);
		width = 1366;
		height = 768;
		/*while(height == 0 || width == 0){
		height = primaryStage.getHeight();
		width = primaryStage.getWidth();
		}*/
		return canvas;
	}

	/**
	 * A method to return the Canvas
	 * 
	 * @return the canvas
	 */
	public Canvas getCanvas() {
		return canvas;
	}

	/**
	 * A method to set the width and height of the screen.
	 * 
	 * @param w
	 *            the width given
	 * @param h
	 *            the height given
	 */
	public void setScreen(int w, int h) {
		if (w < 640 || h < 480) {
			throw new IllegalArgumentException("Invalid width or height input");
		}
		Canvas canvas = new Canvas(w, h);
		Group group = new Group();
		group.getChildren().add(canvas);
		primaryStage.setScene(new Scene(group, w, h, Color.DARKBLUE));
	}

	/**
	 * A method to return the width of the window.
	 * 
	 * @return the width of the window.
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * A method to return the height of the window.
	 * 
	 * @return the height of the window.
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * A method to initialize the title of the primary stage.
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		primaryStage.setTitle(title);
	}

	/**
	 * A method to display the window.
	 */
	public void show() {
		primaryStage.show();
	}

}
