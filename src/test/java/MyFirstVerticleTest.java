import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by dani on 2016-12-08.
 */
@RunWith(VertxUnitRunner.class)
public class MyFirstVerticleTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
      /*  vertx = Vertx.vertx();
        vertx.deployVerticle(MyFirstVerticle.class.getName(),
                context.asyncAssertSuccess());

    */
        System.out.printf("bob");
    }

    @After
    public void tearDown(TestContext context) {
        //vertx.close(context.asyncAssertSuccess());
        System.out.printf("bob after");
    }

    @Test
    public void testMyApplication(TestContext context) {
       /* final Async async = context.async();

        vertx.createHttpClient().getNow(8090, "localhost", "/",
                response -> {
                    response.handler(body -> {
                        context.assertTrue(body.toString().contains("Hello"));
                        async.complete();
                    });
                });*/
        System.out.printf("bob test");
    }

}
