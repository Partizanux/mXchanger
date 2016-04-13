package org.partizanux.mXchanger.client_ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.partizanux.mXchanger.client_ui.model.DealerMoney;
import org.partizanux.mXchanger.client_ui.model.UOrder;
import org.partizanux.mXchanger.client_ui.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

@Component
public class ExchangeController {
	
	@Autowired
	private ClientService service;
	@Autowired
	private DealerMoneyController dealerMoneyController;
	@Autowired
	private OrderDialogController orderDialogController;
	@Autowired
	private ClientMain app;
	
	@FXML
	private TextField amountField;
	@FXML
	private ComboBox<String> money1Box; // dealer money to sell
	@FXML
	private ComboBox<String> money2Box; // money to buy in a bank
	@FXML
	private Button eButton;
	
	private String moneyToSell, moneyToBuy;
	private BigDecimal amount;
	
	@FXML
	public void initialize() {
		// get a list of all evident money of the dealer
		List<String> dealerMoneyList = new ArrayList<>();
		dealerMoneyController.getDealerMoney().getItems().forEach(
				item -> dealerMoneyList.add(dealerMoneyController.getMoneyColumn().getCellData(item)));
		
		money1Box.getItems().addAll(dealerMoneyList);
		money2Box.getItems().addAll(service.getAvailableMoney());
		
		money1Box.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> moneyToSell = newValue);
		money2Box.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> moneyToBuy = newValue);

		amountField.textProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue.matches("(([0-9]*)|([0-9]+)\\.([0-9]{0,2}))")) {
					// we need one more if(), because otherwise oldValue could be null
					if (!newValue.equals("")) {
						amount = new BigDecimal(newValue);
					}
				} else {
					amountField.setText(oldValue);
				}
			});
		}
	
	@FXML
	private void handleExchange() {
		if(moneyToSell == null || moneyToBuy == null || amount == null)// in case null text field - do nothing
			return;
		if (moneyToSell.equals(moneyToBuy)) {
			//Alert
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(app.getStage());
			alert.setTitle("Error");
			alert.setHeaderText("You couldn't buy and sell same currency");
			alert.setContentText("Please, select currency properly and try again");
			alert.showAndWait();
			return;
		}
		
		if (!checkIfEnoughMoney()) {
			//Alert
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(app.getStage());
			alert.setTitle("Error");
			alert.setHeaderText("You haven't enough money");
			alert.setContentText("Please, observe your money amount");
			alert.showAndWait();
			return;
		}
		
		long id = app.getDealerId();
		
		UOrder order = service.offerEx(id, moneyToSell, moneyToBuy, amount);
		orderDialogController.setUOrder(order);
		
		amount = null;
		amountField.setText("");
		
		boolean flag = app.showOrderDialog();
		if (flag)
			dealerMoneyController.refreshDealerMoney();
	}
	
	public void setApp(ClientMain app) {
		this.app = app;
	}
	
	private boolean checkIfEnoughMoney() {
		DealerMoney tableRowData;
		ObservableList<DealerMoney> list = dealerMoneyController.getDealerMoney().getItems();
		for (int i = 0; i < list.size(); i++) {
			tableRowData = list.get(i);
			if (tableRowData.getMoney().getValue().equals(moneyToSell)) {
				System.out.println(tableRowData.getMoney().getValue());
				int check = tableRowData.getAmount().getValue().subtract(amount).signum();
				return check >= 0;
			}
		}
		
		return false;//doesn't find
	}
	
}
