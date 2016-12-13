/**
 * Created by dani on 2016-12-08.
 */
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.ArrayList;

public class MyFirstVerticle extends Verticle {
    @Override
    public void start() {
        // declarations
        HttpServer serverSocket = vertx.createHttpServer();
        final EventBus usersBus = vertx.eventBus();
        final ArrayList<String> registerdUsers = new ArrayList<>();
/*        MongoClient mongo;
        DB db;
        DBCollection table;

        try {
            mongo = new MongoClient( "localhost" , 27017 );
            db= mongo.getDB("firstDatabase");
            table= db.getCollection("user");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } */

        // websocket on
        serverSocket.websocketHandler(new Handler<ServerWebSocket>() {
            // websocket connected
            @Override
            public void handle(final ServerWebSocket serverWebSocket) {
                // check if path is right --> not sure if right
              /*  if (!serverWebSocket.path().equals("/chat")) {
                    System.out.println("Wrong path?");
                    serverWebSocket.reject();
                    return;
                }*/
                // serverWebSocket.writeTextFrame("Hello world!");

                System.out.println("Welcome to new Era");
                JsonObject jsonObject = new JsonObject().putArray("registerdUsers", new JsonArray(String.valueOf(registerdUsers)));
                final StringBuffer username = new StringBuffer();
                serverWebSocket.writeTextFrame(jsonObject.toString()); // write the text that we got on connection (should be name)

                final Handler<Message> messageHandler = new Handler<Message>() {
                    @Override
                    public void handle(Message message) {
                        serverWebSocket.writeTextFrame(message.body().toString());
                    }
                };

                System.out.println("After handler");
                // if client want to end / close socket
                serverWebSocket.closeHandler(new Handler<Void>() {
                    @Override
                    public void handle(Void e) {
                        System.out.println("Closed socket...");
                        registerdUsers.remove(username.toString());
                        usersBus.publish("chat", new JsonObject().putString("offline", username.toString()));
                        usersBus.unregisterHandler("chat", messageHandler);
                        usersBus.unregisterHandler("chat/" + username, messageHandler);
                    }
                });

                // handle the data (json object that the user sends)
                serverWebSocket.dataHandler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer buffer) {
                        System.out.println("before jsonobject but in datahandler");
                        JsonObject object = new JsonObject(buffer.toString());
                        System.out.println("After jsonobject datahandler and object is: "+object.toString());
                        // check login

                        //if true --> go on
                        // if false --> call register
                        // then do this...
                        if (username.toString().equals("")) { // om användaren inte är registrerad
                            username.append(object.getString("name"));
                            registerdUsers.add(username.toString());

                            usersBus.publish("chat", new JsonObject().putString("user", object.getString("name")).toString());

                            usersBus.registerHandler("chat/" + object.getString("name"), messageHandler);
                            usersBus.registerHandler("chat", messageHandler);
                            System.out.println("if got: "+messageHandler.toString());
                        } else { // om användaren är registrerad --> skicka meddelandet direkt
                            JsonObject message = new JsonObject()
                                    .putString("from", object.getString("from"))
                                    .putString("to", object.getString("to"))
                                    .putString("body", object.getString("body"));
                            System.out.println("Else got: "+message.toString());
                            usersBus.publish("chat/" + object.getString("to"), message.toString());
                        }
                    }
                });
            }
        }).listen(1337, "localhost");
    }
}
/*
    // check if user exist
    public boolean checkUserExistence(String username, String password, DBCollection table){
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("name", username);
        searchQuery.put("password", password);
        DBCursor cursor = table.find(searchQuery);

        if(cursor.size() > 0){
            return true;
        }
        return false;
    }

    // add user
    public boolean addUser(String username, String password, int age ,DBCollection table){
        BasicDBObject user = new BasicDBObject();
        user.put("name", username);
        user.put("age", age);
        user.put("password", password);
        user.put("createdDate", new Date());
        table.insert(user);

        DBCursor cursor = table.find(user);
        if(cursor.size() > 0){
            return true;
        }
        return false;
    }*/


/*   try {
                        System.out.println("Handling mongo!");
                        MongoClient mongo = new MongoClient( "localhost" , 27017 );
                        DB db = mongo.getDB("firstDatabase");
                        DBCollection table = db.getCollection("user");
                        BasicDBObject document = new BasicDBObject();
                        document.put("name", "Alican");
                        document.put("age", 21);
                        document.put("createdDate", new Date());
                        table.insert(document);

                        BasicDBObject searchQuery = new BasicDBObject();
                        searchQuery.put("name", "Alican");

                        DBCursor cursor = table.find(searchQuery);

                        while (cursor.hasNext()) {
                            System.out.println("got: "+cursor.next());
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    r.response().end("<h1>Hello from my first " +
                            "Vert.x 3 application with first changes now!</h1>");
 */