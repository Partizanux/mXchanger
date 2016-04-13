package org.partizanux.mXchanger.client_ui;

import org.partizanux.mXchanger.client_ui.model.UOrder;
import org.partizanux.mXchanger.client_ui.service.ClientService;
import org.partizanux.mXchanger.client_ui.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

@Component
public class OrderDialogController {
	
	@Autowired
	private ClientService service;
	
	@FXML
	private Label dealerLabel;
	@FXML
	private Label sellLabel;
	@FXML
	private Label sellAmountLabel;
	@FXML
	private Label buyLabel;
	@FXML
	private Label buyAmountLabel;
	@FXML
	private Label rateLabel;
	@FXML
	private Label timestampLabel;
	
	
	private Stage dialogStage;
	private UOrder uorder;
	private boolean isOkClicked;

	@FXML
	public void initialize() {
		dealerLabel.setText(uorder.getDealer().getValue().toString());
		sellLabel.setText(uorder.getMoneyToSell().getValue().toString());
		sellAmountLabel.setText(uorder.getAmountToSell().getValue().toString());
		buyLabel.setText(uorder.getMoneyToBuy().getValue().toString());
		buyAmountLabel.setText(uorder.getAmountToBuy().getValue().toString());
		rateLabel.setText(uorder.getRate().getValue().toString());
		timestampLabel.setText(DateUtil.format(uorder.getTimestamp().getValue()));
	}
	
	void setStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	void setUOrder(UOrder uorder) {
		this.uorder = uorder;
	}
	
	@FXML
	private void handleOk() {
		service.confirmEx(true);
		isOkClicked = true;
		dialogStage.close();
	}
	
	@FXML
	private void handleCancel() {
		service.confirmEx(false);
		isOkClicked = false;
		dialogStage.close();
	}
	
	public boolean isOkClicked() {
		return isOkClicked;
	}
}
