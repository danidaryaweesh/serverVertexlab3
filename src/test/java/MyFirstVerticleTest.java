/**
 * Created by dani on 2016-12-08.
 */

public class MyFirstVerticleTest {



 /*



import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.ArrayList;


<dependency>
            <groupId>io.vertx</groupId>
            <artifactId>vertx-core</artifactId>
            <version>2.1.2</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>io.vertx</groupId>
            <artifactId>vertx-platform</artifactId>
            <version>2.1.2</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>io.vertx</groupId>
            <artifactId>vertx-platform</artifactId>
            <version>3.0.0</version>
            <scope>provided</scope>
        </dependency>




   try {
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


       MongoClient mongo;
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


}
