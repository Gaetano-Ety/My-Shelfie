# MY SHELFIE - Software Engineering Project

This game was developed as a computer game for the final examination of the Software engineering course at Politecnico di Milano (A.Y. 2022/2023) 
<br>
## Game Introduction
You’ve just taken home your new bookshelf, and it’s now time
to put your favourite items in display: books, board portraits… Who will show the most organized shelfie?

## Group members
- **Gaetano Giambra** <br>
- **Sara Naima D'Angelo** <br>
- **Davide Cattivelli** <br>

## Project Requirements
Develop an online multiplayer board game with Java using the MVC pattern.<br>
We developed the server and the client, with both CLI and GUI (JavaFX) interfaces.<br>
To transmit data we used TCP protocol between server and clients along with JSON format and Java serialization.<br><br>

## Screenshots

## Gui 
![image2](resources/readMeImages/guiImage.png)<br> <br>

![image2](resources/readMeImages/gameImages.png)<br> <br>

![image1](resources/readMeImages/goalAchived.png) <br>

## Cli

![image3](resources/readMeImages/cliImage.png) <br>

![image3](resources/readMeImages/cliImage2.png) <br>



## Functionalities Implemented

| Functionality                |                        State                         |
|:-----------------------------|:----------------------------------------------------:|
| Simple rules                 | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Complete rules               | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Socket                       | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| CLI                          | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| GUI                          | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Multiple games               |  [![RED](https://placehold.it/15/f03c15/f03c44)](#)  |
| Persistence                  |  [![RED](https://placehold.it/15/f03c15/f03c44)](#)  |
| Resilience to disconnections |  [![RED](https://placehold.it/15/f03c15/f03c44)](#)  |
| Chat                         | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |

## Execution of the jar file
To execute the application you'll need to download the TopipDiLibreria.jar

### *Server*
To execute the **Server**  insert the following command into the terminal:
``` 
java -jar TopipDiLibreria.jar --server
``` 

### *Client*
To execute the **Client with GUI** insert the following command into the terminal, followed by the IP address of the server:
``` 
java -jar client.jar --gui "IP server"
``` 

To execute the **Client with CLI**  insert the following command into the terminal, followed by the IP address of the server:
```
java -jar client.jar --cli "IP server"
``` 
you can also execute the **Client with CLI** with this command: 

```
java -jar client.jar "IP server"
``` 
<!--
[![RED](https://placehold.it/15/f03c15/f03c15)](#)
[![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#)
[![GREEN](https://placehold.it/15/44bb44/44bb44)](#)
-->
