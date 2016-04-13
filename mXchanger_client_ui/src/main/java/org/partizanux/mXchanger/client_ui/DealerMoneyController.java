package org.partizanux.mXchanger.client_ui;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.partizanux.mXchanger.client_ui.model.DealerMoney;
import org.partizanux.mXchanger.client_ui.service.ClientService;
import org.partizanux.mXchanger.client_ui.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

@Component
public class DealerMoneyController {
		
	@Autowired
	private ClientService service;
	
	@FXML
	private Label time;
	@FXML
	private TableView<DealerMoney> dealerMoney;
	@FXML
	private TableColumn<DealerMoney, String> moneyColumn;
	@FXML
	private TableColumn<DealerMoney, BigDecimal> amountColumn;
	
	// Initializes the controller
	// automatically called after the fxml file has been loaded
	@FXML
	public void initialize() {
		moneyColumn.setCellValueFactory(cellData -> cellData.getValue().getMoney());
		amountColumn.setCellValueFactory(cellData -> cellData.getValue().getAmount());
		
		dealerMoney.setItems(service.getDealerMoney());
		
		time.setText(DateUtil.format(LocalDateTime.now()));
	}
	
	void refreshDealerMoney() {
		dealerMoney.setItems(service.getDealerMoney());
	}
	
	TableView<DealerMoney> getDealerMoney() {
		return dealerMoney;
	}
	
	TableColumn<DealerMoney, String> getMoneyColumn() {
		return moneyColumn;
	}
	
	TableColumn<DealerMoney, BigDecimal> getAmountColumn() {
		return amountColumn;
	}
	
}
