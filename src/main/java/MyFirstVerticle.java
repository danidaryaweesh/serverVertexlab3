import com.mongodb.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

import java.net.UnknownHostException;
import java.util.Date;

/**
 * Created by dani on 2016-12-08.
 */
public class MyFirstVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        vertx
                .createHttpServer()
                .requestHandler(r -> {
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
                })
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
    }
}
