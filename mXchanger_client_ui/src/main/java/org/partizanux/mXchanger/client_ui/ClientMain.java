package org.partizanux.mXchanger.client_ui;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class ClientMain extends Application {
	
	private final static Logger logger = LoggerFactory.getLogger(ClientMain.class);
	
	private static ApplicationContext ctx;
	
	{
		DefaultListableBeanFactory parentBeanFactory = new DefaultListableBeanFactory();
		parentBeanFactory.registerSingleton("ClientMain", this);
		GenericApplicationContext parentContext = new GenericApplicationContext(parentBeanFactory);
		parentContext.refresh();
		if(ctx == null) {
			ctx = new ClassPathXmlApplicationContext(
			        new String[] {"client-context.xml", "ui-context.xml"}, parentContext);
			//Shutting down the Spring IoC container gracefully in non-web applications
			((AbstractApplicationContext) ctx).registerShutdownHook();
		}
	}
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	// demo version :)
	private long dealerId = 1L;
	
	
	public ClientMain() {
		//starting app ctx...
	}
	
	@Override
	public void start(Stage stage) {
		this.primaryStage = stage;
		stage.setTitle("Money exchange client");
		
		initRootLayout();
		
		showDealerLayout();
	}

	private void initRootLayout() {
		try {
			// Load root layout from fxml file
			FXMLLoader loader = getLoader();
			loader.setLocation(ClientMain.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			loader.getController();
			
			// Show the scene containing the root layout
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			logger.error("error while loading view/RootLayout.fxml\n", e);
		}
	}
	
	//show DealerLayout inside rootLayout
	private void showDealerLayout() {
		try {
			FXMLLoader loader = getLoader();
			loader.setLocation(ClientMain.class.getResource("view/DealerLayout.fxml"));
			AnchorPane dealerView = (AnchorPane) loader.load();
			
			rootLayout.setCenter(dealerView);

			// Load controller
			loader.getController();
						
		} catch (IOException e) {
			logger.error("error while loading view/DealerLayout.fxml\n", e);
		}
		
	}
	
	//show order dialog
	public boolean showOrderDialog() {
		try {
			FXMLLoader loader = getLoader();
			loader.setLocation(ClientMain.class.getResource("view/OrderDialog.fxml"));
			AnchorPane order = (AnchorPane) loader.load();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Order");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			dialogStage.setResizable(false);
			
			Scene scene = new Scene(order);
			dialogStage.setScene(scene);

			OrderDialogController controller = loader.getController();
			
			controller.setStage(dialogStage);
			
			dialogStage.showAndWait();
			
			return controller.isOkClicked();
			
		} catch (IOException e) {
			logger.error("error while loading view/OrderDialog.fxml\n", e);
			return false;
		}
	}
	
	private FXMLLoader getLoader() {
		FXMLLoader loader = new FXMLLoader();
		loader.setControllerFactory(clazz -> ctx.getBean(clazz));
		return loader;
	}

	Stage getStage() {
		return primaryStage;
	}
	
	public long getDealerId() {
		return dealerId;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}