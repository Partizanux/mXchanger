package org.partizanux.mXchanger.service;

import org.partizanux.mXchanger.service.exception.ParseDataException;

public interface MXchangerService<T> {
	T getAllDealerMoney(T moneyRequest) throws ParseDataException;
	T getDealerMoneyPair(T currencyRequest) throws ParseDataException;
	T requestEx(T offer) throws ParseDataException;
	boolean confirmEx(T confirm) throws ParseDataException;
	T doExchange(T order) throws ParseDataException;
}
