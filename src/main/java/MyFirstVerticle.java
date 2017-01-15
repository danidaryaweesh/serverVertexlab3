import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.ArrayList;

/**
 * Created by dani on 2016-12-08.
 */

public class MyFirstVerticle extends AbstractVerticle {

    @Override
    public void start() {
        // declarations
        HttpServer serverSocket = vertx.createHttpServer();
        final EventBus usersBus = vertx.eventBus();
        final ArrayList<User> myInstances = new ArrayList<>();
        final ArrayList<String> loggedIDS = new ArrayList<>();

        final JsonObject mongoconfig = new JsonObject().put("connection_string", "mongodb://localhost:27017").put("db_name", "vertxusers");
        final MongoClient mongoClient = MongoClient.createShared(vertx, mongoconfig);

        // websocket on
        serverSocket.websocketHandler(new Handler<ServerWebSocket>() {
            // websocket connected
            @Override
            public void handle(final ServerWebSocket serverWebSocket) {
                JsonArray j = new JsonArray();

                System.out.println("Welcome to new Era");

                final StringBuffer username = new StringBuffer();
                final StringBuffer id = new StringBuffer();
                final StringBuffer loggedin = new StringBuffer();

                // if client want to end / close socket
                serverWebSocket.closeHandler(e -> {
                    System.out.println("Closed socket... in close handler now");

                    // remove right user
                    for (int i = 0; i < myInstances.size(); i++) {
                        System.out.println("index i:" + myInstances.get(i));
                        if (myInstances.get(i).getId().equals(id.toString())) {
                            myInstances.remove(i);
                            System.out.println("After remove");
                            for (int a = 0; a < myInstances.size(); a++) {
                                System.out.println("instance " + myInstances.get(a).getId());
                            }
                            break;
                        }
                    }

                    for(int i=0;i<loggedIDS.size();i++){
                        if(loggedIDS.get(i).equals(id.toString())){
                            loggedIDS.remove(i);
                        }
                    }

                    System.out.println("after removing the username: " + username.toString());
                    usersBus.publish("chat", new JsonObject().put("offline", username.toString()));


                    usersBus.consumer("chat/" + id.toString()).unregister(res -> {
                        if (res.succeeded()) {
                            System.out.println("The handler un-registration has reached all nodes");
                        } else {
                            System.out.println("Un-registration failed!");
                        }
                    });
                });

                // handle the data (json object that the user sends)
                serverWebSocket.handler(buffer -> {
                    System.out.println("before jsonobject but in datahandler");
                    JsonObject object = new JsonObject(buffer.toString());

                    if(object.getString("option").equals("register")) {
                        JsonObject document = new JsonObject().put("username", object.getValue("username")).put("password", object.getValue("password"));

                        mongoClient.find("vertxusers", document, res -> {
                            if (res.succeeded()) {
                                if (res.result().size() > 0) {
                                    System.out.println("Cannot register user already registerd!");
                                    serverWebSocket.writeFinalTextFrame("/error");

                                } else {
                                    System.out.println("Didnt find the user and will register!");
                                    mongoClient.save("vertxusers", document, insertResult -> {
                                        if (insertResult.succeeded()) {
                                            System.out.println("Name registering is: "+object.getValue("username"));
                                            System.out.println("Succeded to insert the user with id: " + insertResult.result());
                                            loggedIDS.add(object.getString("id"));
                                        } else {
                                            System.out.println("Failed to register user and will return");
                                            serverWebSocket.writeFinalTextFrame("/error");
                                        }
                                    });
                                }
                            } else {
                                res.cause().printStackTrace();
                            }
                        });
                    }

                    else if(object.getString("option").equals("login")){
                        JsonObject document = new JsonObject().put("username", object.getValue("name")).put("password", object.getValue("password"));
                        mongoClient.find("vertxusers", document, res -> {
                            if (res.succeeded()) {
                                System.out.println("Succeded to run!");
                                if(res.result().size() > 0){
                                    System.out.println("Found user and continue and will add id..");
                                    loggedIDS.add(object.getString("id"));

                                }else{
                                    System.out.println("Didnt find the user and will return!");
                                    serverWebSocket.writeFinalTextFrame("/wrong password");
                                }
                            } else {
                                res.cause().printStackTrace();
                            }
                        });
                    }

                    else if (username.toString().equals("") && object.getString("option").equals("connect")) { // om användaren inte är registrerad

                        System.out.println("***********************"+ "   in connect   "+"*******************");
                        for (int i = 0; i < loggedIDS.size(); i++) {
                            if (loggedIDS.get(i).equals(object.getString("id"))) {
                                System.out.println("FOUND THE ID!");
                                loggedin.append("true");
                            }
                        }

                        if(loggedin.toString().equals("")){
                            for(int nr=0;nr<myInstances.size();nr++){
                                if(object.getString("name").equals(myInstances.get(nr).getUsername())){
                                    loggedin.append("true");
                                }
                            }
                        }

                        if(loggedin.toString().equals("true")){
                            System.out.println("***********************"+ "   in if   "+"*******************");

                            username.append(object.getString("name"));
                            id.append(object.getString("id"));
                            String preferedUser = object.getString("to");

                            User user = new User(username.toString(), object.getString("id"), object.getString("preferedUser") != null ? object.getString("preferedUser") : "", false);
                            user.setPreferedUser(preferedUser);
                            myInstances.add(user);
                            usersBus.publish("chat", new JsonObject().put("user", object.getString("name")).toString());

                            usersBus.consumer("chat/" + user.getId(), message -> {
                                System.out.println("The conusmer handler have received a message: " + message.body() + "The user id is: " + user.getId());
                                serverWebSocket.writeFinalTextFrame(message.body().toString());
                            }).completionHandler(res -> {
                                if (res.succeeded()) {
                                    System.out.println("The handler registration has reached all nodes 1");
                                } else {
                                    System.out.println("Registration failed!");
                                }
                            });

                            for (int i = 0; i < myInstances.size(); i++) {
                                if (!myInstances.get(i).isBusy() && myInstances.get(i).getTo().equals("") && myInstances.get(i).getPreferedUser().equals(username.toString())) {
                                    // send my id to the other user
                                    System.out.println("FOUND THE USER ON REGISTRATION!");
                                    myInstances.get(i).setTo(username.toString());
                                    myInstances.get(i).setBusy(true);
                                    System.out.println("THE WHOLE MYINSTANCE THAT I GOT while connecting: id:" + myInstances.get(i).getId() + " ,username: " + myInstances.get(i).getUsername() + " ,prefereduser: " + myInstances.get(i).getPreferedUser() + " ,to: " + myInstances.get(i).getTo());

                                    // update myself!
                                    for (int num = 0; num < myInstances.size(); num++) {
                                        if (myInstances.get(num).getId().equals(id.toString())) {
                                            System.out.println("Found myself and i will update myself!");
                                            myInstances.get(num).setBusy(true);
                                            myInstances.get(num).setTo(myInstances.get(i).getUsername());
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }

                    }else if(loggedin.toString().equals("true")){
                        System.out.println("Else got: " + object.getString("from")+":"+object.getString("body"));
                        for (int i = 0; i < myInstances.size(); i++) {
                            if (myInstances.get(i).isBusy() && myInstances.get(i).getTo().equals(object.getString("from")) && myInstances.get(i).getPreferedUser().equals(username.toString()) && object.getString("to").equals(myInstances.get(i).getUsername()) && !object.getString("id").equals(myInstances.get(i).getId())) {
                                System.out.println("found with id: " + myInstances.get(i).getId() + " busy?: " + myInstances.get(i).isBusy());
                                usersBus.publish("chat/" + myInstances.get(i).getId(), object.getString("from")+":"+object.getString("body"));
                            }
                            System.out.println("THE WHOLE MYINSTANCE THAT I GOT: id:" + myInstances.get(i).getId() + " ,username: " + myInstances.get(i).getUsername() + " ,prefereduser: " + myInstances.get(i).getPreferedUser() + " ,to: " + myInstances.get(i).getTo() + " busy?: " + myInstances.get(i).isBusy());
                        }
                    }
                });
            }
        }).listen(1337, "192.168.1.8");
    }
}

