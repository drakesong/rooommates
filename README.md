# Roommates
For finding the perfect roommate. A full stack web development project using Angular 7 and Spring Boot.


## How to run
There are four main components to this project:
* Client
* Server
* Chat Server
* Database

In order to locally run these components, you must meet the following pre-requisites:
* [NodeJS](https://nodejs.org/en/) @10.15.3
* [Angular CLI](https://github.com/angular/angular-cli) @7.3.7
* [Apache Maven](https://maven.apache.org/download.cgi) @3.6.0
* [Gulp](https://gulpjs.com/docs/en/getting-started/quick-start) @2.2.0
* [Java](https://www.java.com/en/download/) @1.8.0_201
* [MySQL](https://www.mysql.com/downloads/) @8.0.15

### Client
After cloning the repository, go into `.../roommates/client` and run `npm install` to install all the required packages. When that is complete, run `ng s` and navigate to `http://localhost:4200/`. Edit *environment.ts* located in `.../roommates/client/src/environments` as needed with *API_BASE_URL* and *CHAT_URL* created by running the two major components below.

### Server
Navigate to `.../roommates/server`. Edit the *dbparams.txt* file located at `.../roommates/server/src/main/java/roommates/configs` with the first line having *Username* and second line having *Password* to local MySQL database. Go back to `.../roommates/server` and run `mvn package`. Then run `java -jar target/roommates-0.1.0.jar`.

### Chat Server
Navigate to `.../roommates/chat_server` and run `npm install` and `gulp build`. Then run `node dist/index.js`.

### Database
Run the following to create local database.
```mysql
CREATE DATABASE roommates;
USE roommates;

CREATE TABLE Users
(user_id INT NOT NULL AUTO_INCREMENT,
email VARCHAR(64) NOT NULL UNIQUE,
password CHAR(255) NOT NULL,
first_name VARCHAR(64),
last_name VARCHAR(64),
gender CHAR(1),
zipcode CHAR(5),
birthdate DATE,
description TEXT,
picture TEXT,
sleep INT,
eat INT,
neat INT,
social INT,
desired_zipcode CHAR(5),
desired_gender CHAR(1),
desired_rent INT,
PRIMARY KEY (user_id));

CREATE TABLE Messages
(message_id INT NOT NULL AUTO_INCREMENT,
user_id INT NOT NULL,
message TEXT,
PRIMARY KEY (message_id),
FOREIGN KEY (user_id) REFERENCES Users(user_id));

CREATE TABLE Matches
(user1_id INT NOT NULL,
user2_id INT NOT NULL,
chat_id INT AUTO_INCREMENT,
PRIMARY KEY (chat_id),
FOREIGN KEY (user1_id) REFERENCES Users(user_id),
FOREIGN KEY (user2_id) REFERENCES Users(user_id));

CREATE TABLE ChatRooms
(chat_id INT NOT NULL,
message_id INT NOT NULL,
FOREIGN KEY (chat_id) REFERENCES Matches(chat_id),
FOREIGN KEY (message_id) REFERENCES Messages(message_id));

CREATE TABLE Likes
(user1_id INT NOT NULL,
user2_id INT NOT NULL,
FOREIGN KEY (user1_id) REFERENCES Users(user_id),
FOREIGN KEY (user2_id) REFERENCES Users(user_id));

CREATE TABLE Dislikes
(user1_id INT NOT NULL,
user2_id INT NOT NULL,
FOREIGN KEY (user1_id) REFERENCES Users(user_id),
FOREIGN KEY (user2_id) REFERENCES Users(user_id));
```

Afterwards, clone the following repository: `https://github.com/drakesong/roommates_database.git`. Edit the *dbparams.txt* file located at `.../roommates_database/src/main/java/roommates_database/configs` with the first line having *Username* and second line having *Password* to local MySQL database. Go back to `.../roommates_database` and run `mvn package`. Then run `java -jar target/roommates_database-0.1.0.jar` which populates the *User* table with 5000 mock users (*will take awhile*).
