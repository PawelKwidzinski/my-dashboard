# My-Dasboard - in progress
## General
A microservice application that delivers data according to the developer's personal preferences such as IT job offers, local news and more.
To visualize the retrieved data through microservices, it is planned to connect to Angular framework and deploy the frontend and backend on a Kubernetes cluster.
## Running microservices:
### Configserver-ms
#### Features:
* Provides necessary configurations for proper operation of microservices.
### JustJoin-It-ms
#### Features:
* Web scraping job offers from justjoin.it and saving them in the database
* Filter by required experience
* Filter bo from/to date
#### Technologies
* Java 21
* Maven
* Spring Boot 3.2
* Spring Data
* Spring Config
* Lombok
* MongoDB
* Jsoup (library for webspraping)
* Jib
### TriCity-news-ms
#### Features:
* Web scraping local news from trojmiasto.pl and saving them in the database
* Filter by from/to date
#### Technologies
* Java 21
* Maven
* Spring Boot 3.2
* Spring Data
* Spring Config
* Lombok
* MongoDB
* Jsoup (library for webspraping)
* Jib
