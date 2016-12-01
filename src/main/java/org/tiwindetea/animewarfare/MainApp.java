////////////////////////////////////////////////////////////
//
// Anime Warfare
// Copyright (C) 2016 TiWinDeTea - contact@tiwindetea.org
//
// This software is provided 'as-is', without any express or implied warranty.
// In no event will the authors be held liable for any damages arising from the use of this software.
//
// Permission is granted to anyone to use this software for any purpose,
// including commercial applications, and to alter it and redistribute it freely,
// subject to the following restrictions:
//
// 1. The origin of this software must not be misrepresented;
//    you must not claim that you wrote the original software.
//    If you use this software in a product, an acknowledgment
//    in the product documentation would be appreciated but is not required.
//
// 2. Altered source versions must be plainly marked as such,
//    and must not be misrepresented as being the original software.
//
// 3. This notice may not be removed or altered from any source distribution.
//
////////////////////////////////////////////////////////////

package org.tiwindetea.animewarfare;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.lomadriel.lfc.event.EventDispatcher;
import org.lomadriel.lfc.statemachine.DefaultStateMachine;
import org.tiwindetea.animewarfare.gui.event.AskMenuStateUpdateEvent;
import org.tiwindetea.animewarfare.gui.event.AskMenuStateUpdateEventListener;
import org.tiwindetea.animewarfare.gui.event.QuitApplicationEvent;
import org.tiwindetea.animewarfare.gui.event.QuitApplicationEventListener;
import org.tiwindetea.animewarfare.gui.menu.MainMenuState;
import org.tiwindetea.animewarfare.util.PropertiesReader;

import java.io.IOException;

/**
 * Main application class.
 * This class contains the main function to start the game.
 *
 * @author Benoît CORTIER
 */
public class MainApp extends Application implements AskMenuStateUpdateEventListener, QuitApplicationEventListener {
	private static final PropertiesReader PROPERTIES_READER
			= new PropertiesReader("org.tiwindetea.animewarfare.MainApp");

	private Stage primaryStage;

	private BorderPane rootLayout;

	private DefaultStateMachine menuStateMachine;

	@Override
	public void start(Stage stage) {
		EventDispatcher.getInstance().addListener(AskMenuStateUpdateEvent.class, this);
		EventDispatcher.getInstance().addListener(QuitApplicationEvent.class, this);

		this.primaryStage = stage;
		this.primaryStage.setTitle(PROPERTIES_READER.getString("title"));

		initRootLayout();

		this.menuStateMachine = new DefaultStateMachine(new MainMenuState(this.rootLayout));

		this.primaryStage.setOnCloseRequest(this::onQuit);
	}

    private void initRootLayout() {
		// Load root layout from fxml file.
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("RootLayout.fxml"));
		try {
			this.rootLayout = loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(this.rootLayout);
			this.primaryStage.setScene(scene);
			this.primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    private void onQuit(WindowEvent event) {
		EventDispatcher.getInstance().removeListener(AskMenuStateUpdateEvent.class, this);
		EventDispatcher.getInstance().removeListener(QuitApplicationEvent.class, this);
	}

	@Override
	public void handleAskMenuStateUpdate() {
		this.menuStateMachine.update();
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void handleQuitApplication() {
		this.primaryStage.close();
	}
}
