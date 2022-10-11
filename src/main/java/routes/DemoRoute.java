package routes;

import DTO.CSVItem;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.BindyType;

import java.util.Arrays;

public class DemoRoute extends RouteBuilder {
    private static final String HTTP_HEADER_TOKEN_KEY = "Token";
    private static final String HTTP_HEADER_TOKEN_VALUE = "SECRET";
    private static final String HTTP_HEADER_TOKEN_MISSED_MESSAGE = "Access token is missing or invalid.";
    private static final String FROM_URI = "jetty:http://localhost:7000/";
    private static final String TO_URI = "file:src/main/resources?fileExist=append&fileName=items-${date:now:dd-MM-yyyy}.csv";
    private static final String ROUTE_ID = "Test task";
    private static final String NUMBER_OF_ITEMS_MESSAGE = "Number of items = ${body.length}";

    private final String[] multiplier;

    public DemoRoute(String[] multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public void configure() {
        from(FROM_URI)
                .routeId(ROUTE_ID)
                .choice()
                .when(exchange -> exchange.getIn().getHeaders().entrySet().stream()
                        .noneMatch((e) -> e.getKey().equals(HTTP_HEADER_TOKEN_KEY)
                                && e.getValue().equals(HTTP_HEADER_TOKEN_VALUE)))

                .log(LoggingLevel.WARN, HTTP_HEADER_TOKEN_MISSED_MESSAGE)

                .process(exchange -> {
                    exchange.getIn().setBody(HTTP_HEADER_TOKEN_MISSED_MESSAGE);
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 401);
                })

                .stop()
                .otherwise()

                .unmarshal()
                .json(CSVItem[].class)

                .log(LoggingLevel.INFO, NUMBER_OF_ITEMS_MESSAGE)

                .process(exchange -> {
                    if (multiplier.length != 0) {
                        int multiplier = Integer.parseInt(this.multiplier[0]);
                        CSVItem[] csvItems = exchange.getIn().getBody(CSVItem[].class);
                        for (CSVItem csvItem : csvItems) {
                            csvItem.setSum(csvItem.getSum() * multiplier);
                        }
                    }
                })

                .marshal()
                .bindy(BindyType.Csv, CSVItem.class)
                .to(TO_URI);
    }
}
