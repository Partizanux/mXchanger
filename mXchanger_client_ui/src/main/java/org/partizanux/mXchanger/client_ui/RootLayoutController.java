package org.partizanux.mXchanger.client_ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class RootLayoutController {
	
	@Autowired
	private ClientMain app;
	
	@FXML
	private void handleExit() {
		System.exit(0);
	}
	
	@FXML
	private void handleAbout() {
		Stage about = new Stage();
		about.setTitle("About");
		about.initModality(Modality.WINDOW_MODAL);
		about.initOwner(app.getStage());
		about.setResizable(false);
		
		Pane content = getAboutContent();
		
		Scene scene = new Scene(content);
		about.setScene(scene);
		
		about.show();
	}
	
	private Pane getAboutContent() {
		VBox vbox = new VBox();
	    vbox.setPadding(new Insets(20));
	    vbox.setSpacing(10);
	    
	    Text name = new Text("mXchanger v1.0");
	    name.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
	    name.setTextAlignment(TextAlignment.CENTER);
	    
	    VBox.setMargin(name, new Insets(0, 0, 0, 8));
        vbox.getChildren().add(name);
        
        Text author = new Text("author: Vitaliy Filipchuk");
        author.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        author.setTextAlignment(TextAlignment.CENTER);
	    
	    VBox.setMargin(name, new Insets(0, 0, 0, 12));
        vbox.getChildren().add(author);
        
        return vbox;
	}
	
}
