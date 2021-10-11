# Product management and currency conversion Website Demo

Microservices system written in react typescript(client side) and spring boot webflux java(server side).
Both microservices use reactive pattern to communicate with each other and with the web client, this offers flexibility in big data load scenarios.

## The react web page (crud-react-webpage)
It offers a product formulary for edition and a product list to manage products in euros and dollars.
## The first microservice (spring-boot-webflux-client)
It delivers all the product data converted to dollars to the website and use the second
microservice to request for product data in euros. It also serves static content from the website.
## The seconde microservice (spring-boot-webflux-api-rest)
It delivers all the product data in euros to the first microservice and use a mongodb connection to fetch for that data in first place.
