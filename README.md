
Application is build as micro service which consumes REST-HTTP request and process it to store in MONGODB

Main tools used to build application spray and reactive Mongo . This build using scala language .

Code base has 2 types of application using spray .

1. Using Service container which is a lightweight framework that provides the ability to build microservices.It contains a built-in Http server and utilizes both Akka and Spray at it's core. The service container is fully SaaS ready as it provides functionality for recording metrics and for tracking the health of your service.

http://localhost:9092/customer                            
                              
2. Simple Spray for rest-HTTP routing 

http://localhost:8080/person

Both application uses reactive mongo as framework to store json in mongoDB

MongoDB is default configured to run only locally with "plugin" DB