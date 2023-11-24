This is a Java application which uses the VIDYO (https://www.vidyo.com/help/VidyoConnect/1_Intro/New-to-Vidyo.htm?tocpath=VidyoConnect%7CGet%20started%7C_____1)
platform to create and access the rooms created for video calling.

By using this application you can create the rooms and access all the rooms created on current day.

# How to run the demo

Pre-requisite:

Install Java 17.
Install Gradle 8.4

Do the following from a terminal window:

1. Clone the repo

2. git clone https://github.com/abhinavbhatejagit/moderator.git

3. Navigate to project directory and Run ModeratorApplication file in any IDE like Intellij

4. You can access the application on http://127.0.0.1:8081

5. This API also create & start the DB server on http://127.0.0.1:8081/h2-console/login.do

6. You can check there is one table created with name of room_entity

7. You can call the http://127.0.0.1:8081/getRoom POST api from postaman by providing body as JSON { "roomName" : "test" }

8. The above call will check in the room_entity table if the test room exist or not. If not it will create a new entry in the table & return the json response.

9. You can get the roomUrl from json response & hit it on the browser to join the call.

10. There is another GET API to fetch the rooms created on current day i.e http://127.0.0.1:8081/fetchAllRooms

11. This API will provide all the rooms created in a current single day.
