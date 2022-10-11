import routes.DemoRoute;

public class Test {

    public static void main(String[] args) throws Exception {
        Main m = new Main();
        m.addRouteBuilder(new DemoRoute(args));
        m.run();
    }
}
