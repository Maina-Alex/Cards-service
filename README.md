# CARD BACKEND SERVICE

## REQUIREMENTS

* Application users are uniquely identified by mail address, have rolt (Member or Admin)
* Members have access to cards they created
* Admins have access to all cards
* Card is created by providing a name (Mandatory), description and color are optional
* Color must conform to 6 Alphanumeric character prefixed by #
* User can search through cards using filters: name,color, status and date of creation
* Results can be paginated, default pagination is 10 items
* Results may be sorted by name,color, dateOfCreation
* A user can update a card's description,color and status
* A user can delete cards they have access to


## RUN SERVICE

* The service has a compose.yaml 
  You can run the compose manually on Prod
   Run : 
*      docker compose pull
*      docker compose up -d
* You can also run the springboot, CardApplication class and the containers will
 be automatically spinned up on dev env

## RUNNING TESTS

On a terminal run
*     ./gradlew test

## API DOCS

package postman contains exported collection of APIs
