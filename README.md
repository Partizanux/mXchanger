# mXchanger

# About

The project consists of two parts: backend server app and frontend client desktop app.
Server app represents a bank which store information about dealers and their bills and provides money exchange service.  
Client is a desktop application which allows user information about his money in bank, provides him bid and ask info and allows him to perform money exchange operations.


# Architecture

Server application consists of 4 layers: domain, dao, service and server. Spring IoC wires this layers together.
As a datastore I used HSQLDB in in-memory mode. You can find self descriptive sql initialization file in dao resources.
A domain layer classes represents entities persisted in db.
A dao layer uses spring JdbcTemplate to access db.
A service layer methods takes input data of some request in JSON format and returns JSON message with data (currency data for example)
A server layer apache mina server, which handles client request.

Client application consist of 2 layers: client and client_ui, wired by spring.
A client layer uses plain sockets to communicate with server.
A client_ui layer represents javaFX desktop application.

# Technologies

java, javaFX, spring, apache mina, Junit, EasyMock, gradle

# How to build and try

Gradle was used as a build tool, so you need instaled gradle build tool. I build and try application using Linux, so in Linux it worked fine.

- Download the code

- Open terminal from project directory

- First, let's assemble a server part. Type in terminal and run gradle task: `gradle :mXchanger_server:build`
This command will build a fat executable jar file, and run tests.  
NOTE: after tests you should wait 2*MSL (default values: 1 min for Linux, 4 min for Windows) before applying next step, because during this period a TCP-connection will be in TIME_WAIT state.

- Second, let's assemble a client part. Type in terminal and run gradle task: `gradle :mXchanger_client_ui:build`
This command will build a fat executable jar file, and run tests. Also wait  2*MSL after tests, before next step.

- Run  server by typing in terminal: `java -jar mXchanger_server-1.0.jar`

- Run  client by typing in terminal: `java -jar mXchanger_client_ui-1.0.jar`

You should see such window:

![mXchanger-img](https://cloud.githubusercontent.com/assets/6363051/14510554/c8a6640e-01da-11e6-95c9-0b260448f555.png)
