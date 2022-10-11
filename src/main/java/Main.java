import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class Main {
    private CamelContext camelContext;

    public Main() {
        this.camelContext = new DefaultCamelContext();
    }

    public void addRouteBuilder(RouteBuilder routeBuilder) throws Exception {
        camelContext.addRoutes(routeBuilder);
    }

    public void run() {
        camelContext.start();
    }

}
