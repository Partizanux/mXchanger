package org.partizanux.mXchanger.client_ui;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

@Component
public class DealerController {
	
	@FXML
	private AnchorPane dealerMoneyPane;
	@FXML
	private AnchorPane ratePane;
	@FXML
	private AnchorPane exchangePane;
	
	// naming scheme: not a partialy lowercased class name, but fx:id + Controller word
	@FXML
	private DealerMoneyController dealerMoneyPaneController;
	@FXML
	private RateController ratePaneController;
	@FXML
	private ExchangeController exchangePaneController;
		
}
