/**
 * Created by dani on 2016-12-08.
 */

import org.vertx.java.core.Handler;
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
        final ArrayList<User> myInstances = new ArrayList<>();
        // websocket on
        serverSocket.websocketHandler(new Handler<ServerWebSocket>() {
            // websocket connected
            @Override
            public void handle(final ServerWebSocket serverWebSocket) {
                JsonArray j = new JsonArray();

                for(int i=0;i<registerdUsers.size();i++){
                    j.add(registerdUsers.get(i));
                }
                System.out.println("Welcome to new Era");
                JsonObject jsonObject = new JsonObject().putArray("registerdUsers", j);
                final StringBuffer username = new StringBuffer();
                final StringBuffer id = new StringBuffer();
                serverWebSocket.writeTextFrame(jsonObject.toString()); // write the text that we got on connection (should be name)

                final Handler<Message> messageHandler = new Handler<Message>() {
                    @Override
                    public void handle(Message message) {
                        serverWebSocket.writeTextFrame(message.body().toString());
                    }
                };

                System.out.println("After handler");
                // if client want to end / close socket
                serverWebSocket.closeHandler(e -> {
                    System.out.println("Before removing: "+registerdUsers.toString());
                    System.out.println("Closed socket... in close handler now");
                   // registerdUsers.remove(username.toString());

                    // remove right user
                    for(int i=0;i<myInstances.size();i++){
                        System.out.println("index i:" +myInstances.get(i));
                        if(myInstances.get(i).getId().equals(id.toString())){
                            myInstances.remove(i);
                            System.out.println("After remove");
                            for(int a=0;a<myInstances.size();a++){
                                System.out.println("instance "+myInstances.get(a).getId());
                            }
                            break;
                        }
                    }

                    System.out.println("after removing the username: "+username.toString());
                    usersBus.publish("chat", new JsonObject().putString("offline", username.toString()));
                    usersBus.unregisterHandler("chat", messageHandler);
                    usersBus.unregisterHandler("chat/" + id.toString(), messageHandler);

                    System.out.println("After removing, the list looks like: "+registerdUsers.toString());
                });

                // handle the data (json object that the user sends)
                serverWebSocket.dataHandler(buffer -> {
                    System.out.println("before jsonobject but in datahandler");
                    JsonObject object = new JsonObject(buffer.toString());
                    System.out.println("After jsonobject datahandler and object is: "+object.toString());
                    // check login
                    System.out.println("Username now is: "+username.toString());
                    //if true --> go on
                    // if false --> call register
                    // then do this...

                    if (username.toString().equals("") ) { // om användaren inte är registrerad

                        username.append(object.getString("name")); // kan append null istället
                        id.append(object.getString("id"));
                        String preferedUser = object.getString("to");
                        System.out.println("Prefered user: "+preferedUser +" and id is from stringbuffer: "+id.toString() + " id in the json object: "+object.getString("id"));
                        User user = new User(username.toString(),  object.getString("id"),object.getString("preferedUser")!=null? object.getString("preferedUser") : "", false);
                        user.setPreferedUser(preferedUser);
                        myInstances.add(user);

                        registerdUsers.add(username.toString());

                        usersBus.publish("chat", new JsonObject().putString("user", object.getString("name")).toString());

                        // changed name to from!
                       //  usersBus.registerHandler("chat/" + object.getString("name"), messageHandler);
                        // registrera id
                        usersBus.registerHandler("chat/"+user.getId(), messageHandler);
                        usersBus.registerHandler("chat", messageHandler);

                        for(int i=0;i<myInstances.size();i++){
                            if(!myInstances.get(i).isBusy() && myInstances.get(i).getTo().equals("") && myInstances.get(i).getPreferedUser().equals(username.toString())){
                                // send my id to the other user
                                System.out.println("IN IF BITCH!");
                                myInstances.get(i).setTo(username.toString());
                                JsonObject message = new JsonObject().putString("toID", id.toString());
                            //    usersBus.publish("chat/"+myInstances.get(i).getId(), message.toString());
                                myInstances.get(i).setBusy(true);
                                System.out.println("THE WHOLE MYINSTANCE THAT I GOT: id:"+myInstances.get(i).getId() + " ,username: "+myInstances.get(i).getUsername() + " ,prefereduser: "+myInstances.get(i).getPreferedUser() + " ,to: "+myInstances.get(i).getTo());
                                System.out.println("Send my id to other user:"+id.toString());

                                // update myself!
                                for(int num=0;num<myInstances.size();num++){
                                    if(id.toString().equals(myInstances.get(num).getId())){
                                        myInstances.get(num).setBusy(true);
                                        myInstances.get(num).setTo(myInstances.get(i).getUsername());
                                        break;
                                    }
                                }
                                // send to myself about the others id
                           /*     JsonObject messageToMe = new JsonObject().putString("to", myInstances.get(i).getId());
                                usersBus.publish("chat/"+id.toString(), messageToMe.toString());
                                System.out.println("Send id to myself: "+myInstances.get(i).getId()); */
                                break;
                            }
                        }

                        System.out.println("Registerd client name as: "+object.getString("name") + "  and with id: "+object.getString("id"));

                        System.out.println("if got: "+messageHandler.toString());
                    }else{ // om användaren är registrerad --> skicka meddelandet direkt
                        JsonObject message = new JsonObject()
                                .putString("from", object.getString("from"))
                                .putString("to", object.getString("to"))
                                .putString("body", object.getString("body"));
                        System.out.println("Else got: "+message.toString());
                        System.out.println("Else got to: "+object.getString("to")  + "  and the current username who is trying to send is : "+username.toString());
                        for(int i=0;i<myInstances.size();i++){
                            if(myInstances.get(i).isBusy() && myInstances.get(i).getTo().equals(object.getString("from")) && myInstances.get(i).getPreferedUser().equals(username.toString()) && object.getString("to").equals(myInstances.get(i).getUsername()) && !object.getString("id").equals(myInstances.get(i).getId())){
                                System.out.println("Alican found with id: "+myInstances.get(i).getId()+ " busy?: "+myInstances.get(i).isBusy());
                                usersBus.publish("chat/" + myInstances.get(i).getId(), message.toString());
                            }
                            System.out.println("THE WHOLE MYINSTANCE THAT I GOT: id:"+myInstances.get(i).getId() + " ,username: "+myInstances.get(i).getUsername() + " ,prefereduser: "+myInstances.get(i).getPreferedUser() + " ,to: "+myInstances.get(i).getTo()+" busy?: "+myInstances.get(i).isBusy());
                        }
                    }
                });
            }
        }).listen(1337, "localhost");
    }
}

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

/*        MongoClient mongo;
        DB db;
        DBCollection table;

        try {
            mongo = new MongoClient( "localhost" , 27017 );
            db= mongo.getDB("firstDatabase");
            table= db.getCollection("user");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
*/