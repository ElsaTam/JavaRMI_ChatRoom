# ChatRoom

## Note

This was a student project that was made in a short time. I did not have the time to correct all bugs, focus too much on ergonomic and intuitive GUI, and I don't plan (for now) to improve the project in the future.

The project is mainly an exercice to use Java RMI (remote method invocation) API to create a client-server program, using a TCP transport.

The project is a ChatRoom tool to send files and written messages between different client applications.

## Compilation

### With NetBeans
Open the project and build it with the IDE (built with Java 8, JavaFX21).

### With command line
Simply run:
```bash
$ cd ChatRoom
$ ant -f build.xml
```

## Running

### Server

Start the server in one terminal:
```bash
$ cd ChatRoom/build/classes
$ start rmiregistry
$ java chatroom.model.serveur.ServeurApp
```
It will display the IP adress on which the server is running.

### Client

Start a client run in an other terminal:
```bash
$ cd ChatRoom/build/classes
$ java chatroom.ui.client.ChatRoom
```

## Usage

### Connect to server

Once the client's window is opened, connect to the server (`Serveurs > Se connecter au serveur`) and copy paste the server IP adress. Choose a username (`pseudo`) for your connection, and click `Se connecter`.

You can end connection with `Serveurs > Se déconnecter du serveur`.

### Join a chat room

Create a new chat room with `Salles > Créer une salle`.

- `Nom de la salle` will be the name other users will use to connect to your chat room;
- `Capacité max` is the maximum number of users allowed in the room;
- `Visibilité` can be either public (no password) or public (with password)
  
Once the chat room is created, you will be loged in automatically. As the owner of the room you created, if you leave it, it will be destroyed and removed from available rooms.


Join an existing chat room with `Salles > Se connecter à une salle`. A list of available chat rooms will be shown. Just select the one you want to join. If it is a private room, you will need a password.
You can join as many rooms as you want, each of them will be opened in a new tab.


Log out from the current chat room with `Salles > Se déconnecter de la salle`.

Log out from all chat rooms with `Salles > Se déconnecter de toutes les salles`.


### Send messages

Once loged in a chat room, you can send messages to everyone using the text field.

You can to send a private message to an other user with `Envois > Message privé`.

You can also send a file with `Envois > Fichier à la salle` to share it with all connected users, or `Envois > Fichier privé` to send it to only one user.