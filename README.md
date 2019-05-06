# Roommates
For finding the perfect roommate. A full stack web development project using Angular 7 and Spring Boot.


## How to run
There are three main components to this project:
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
After cloning the repository, go into `.../roommates/client` and run `npm install` to install all the required packages. When that is complete, run `ng s` and navigate to `http://localhost:4200/`.

### Server
Navigate to `.../roommates/server` and run `mvn package`. Then run `java -jar target/roommates-0.1.0.jar`.

### Chat Server
Navigate to `.../roommates/chat_server` and run `gulp build`. Then run `node dist/index.js`.

### Database
