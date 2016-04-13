DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS currency;
DROP TABLE IF EXISTS DealerMoney;
DROP TABLE IF EXISTS Money;
DROP TABLE IF EXISTS Dealer;

CREATE TABLE Dealer (
dealerID integer PRIMARY KEY,
firstName varchar(20),
lastName varchar(20),
info text
);
INSERT INTO Dealer VALUES (1, 'John', 'Doe', 'super dealer');

CREATE TABLE Money (
moneyID char(3) PRIMARY KEY,
description varchar(20)
);
INSERT INTO Money VALUES ('eur', 'euro');
INSERT INTO Money VALUES ('usd', 'american dollar');

CREATE TABLE DealerMoney (
dealerID integer REFERENCES Dealer (dealerID),
moneyID char(3) REFERENCES Money (moneyID),
amount NUMERIC(15, 2),
PRIMARY KEY (dealerID, moneyID)
);
INSERT INTO DealerMoney VALUES (1, 'eur', 1000.00);
INSERT INTO DealerMoney VALUES (1, 'usd', 1000.00);

CREATE TABLE currency (
money1 char(3) REFERENCES Money (moneyID),
money2 char(3) REFERENCES Money (moneyID),
timestamp TIMESTAMP,
bid NUMERIC(10, 4),
ask NUMERIC(10, 4),
PRIMARY KEY (money1, money2)
);
INSERT INTO currency VALUES ('eur', 'usd', '2016-02-04 23:00:00', '1.0919', '1.0921');

CREATE TABLE orders (
orderID BIGSERIAL PRIMARY KEY,
dealerID integer REFERENCES dealer (dealerID),
moneyToSell char(3) REFERENCES Money (moneyID),
moneyToBuy char(3) REFERENCES Money (moneyID),
buyAmount NUMERIC(15, 2),
sellAmount NUMERIC(15, 2),
rate NUMERIC(10, 4),
timestamp TIMESTAMP
);
