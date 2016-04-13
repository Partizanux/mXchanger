package org.partizanux.mXchanger.client_ui;

import java.math.BigDecimal;

import org.partizanux.mXchanger.client_ui.model.CurrencyRate;
import org.partizanux.mXchanger.client_ui.service.ClientService;
import org.partizanux.mXchanger.client_ui.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

@Component
public class RateController {
	
	@Autowired
	private ClientService service;
	
	@FXML
	private ComboBox<String> money1Box;
	@FXML
	private ComboBox<String> money2Box;
	@FXML
	private Label bidLabel;
	@FXML
	private Label askLabel;
	@FXML
	private Label spreadLabel;
	@FXML
	private Label timeLabel;
	
	private String money1;
	private String money2;
	
	@FXML
	public void initialize() {
		money1Box.getItems().addAll(service.getAvailableMoney());
		money2Box.getItems().addAll(service.getAvailableMoney());
		
		showRate();
		
		money1Box.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> { money1 = newValue; showRate(); });
		money2Box.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> { money2 = newValue; showRate(); });
	}
	
	@FXML
	private void showRate() {
		
		if(money1 != null && money2 != null) {
			CurrencyRate currencyRate = service.getCurrency(money1, money2);
			
			BigDecimal x = currencyRate.getBid().getValue();
			BigDecimal y = currencyRate.getAsk().getValue();
			BigDecimal z = y.add(x.negate());
			
			bidLabel.setText(x.toString());
			askLabel.setText(y.toString());
			spreadLabel.setText(z.toString());
			timeLabel.setText(DateUtil.format(currencyRate.getTime().getValue()));
			
		} else {
			bidLabel.setText("");
			askLabel.setText("");
			spreadLabel.setText("");
			timeLabel.setText("");
		}
		
	}
	
}
