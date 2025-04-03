import com.google.gson.*;
import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.junit.Before;
import webCalendarSpring.ApplicationKt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hyperskill.hstest.common.JsonUtils.getJson;
import static org.hyperskill.hstest.testing.expect.Expectation.expect;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isObject;

class EventForTest {
    int id;
    String event;
    String date;

    public EventForTest(int id, String event, String date) {
        this.id = id;
        this.event = event;
        this.date = date;
    }

    public EventForTest() {
    }

    @Override
    public String toString() {
        return "{ \"id\":" + "\"" + id + "\"" +
                ", \"event\":" + "\"" + event + "\"" +
                ", \"date\":" + "\"" + date + "\"" + "}";
    }


}

public class WebCalendarSpringTest extends SpringTest {
    EventForTest eventForTest;

    int count = 0;

    public WebCalendarSpringTest() {

        super(ApplicationKt.class, "../d.mv.db");

    }

    public static final String todayEndPoint = "/event/today";

    public static final String eventEndPoint = "/event";

    private static final Gson gson = new Gson();

    private static List<EventForTest> eventsList = new ArrayList<>();
    Map<String, String> justToday = Map.of(
            "event", "Today is a good Day ",
            "date", LocalDate.now().toString());
    Map<String, String> newYear = Map.of(
            "event", "New Year's Day",
            "date", "2024-01-01");
    Map<String, String> goodFriday = Map.of(
            "event", "Good Friday",
            "date", "2023-04-07");
    Map<String, String> janHusDay = Map.of(
            "event", "Jan Hus Day",
            "date", "2023-07-06");

    Map<String, String> justaPerfectDay = Map.of(
            "event", "Perfect Day",
            "date", randomDate(-20, 15));
    Map<String, String> anotherGoodDay = Map.of(
            "event", "Another Good Day",
            "date", randomDate(-10, 5));
    List<Map<String, String>> listOfEvents = new ArrayList<>();

    {
        listOfEvents.add(newYear);
        listOfEvents.add(goodFriday);
        listOfEvents.add(janHusDay);
        listOfEvents.add(justaPerfectDay);
        listOfEvents.add(anotherGoodDay);

    }

    Map<String, String> emptyEvent1 = Map.of("event", "", "date", LocalDate.now().toString());
    Map<String, String> blankEvent2 = Map.of("event", "     ", "date", LocalDate.now().toString());

    Map<String, String> nullEvent3 = Map.of("date", LocalDate.now().toString());

    Map<String, String> nullDate4 = Map.of("event", "New Year Party");
    Map<String, String> emptyEventNullDate5 = Map.of("event", "");

    Map<String, String> emptyEventEmptyDate6 = Map.of("event", "", "date", "");

    Map<String, String> blankDateEmptyEvent7 = Map.of("date", "    ", "event", "");

    Map<String, String> blankDate8 = Map.of("date", "    ", "event", "New Year Party");
    Map<String, String> blankDate9 = Map.of("event", "New Year Party", "date", "    ");

    Map<String, String> emptyDate10 = Map.of("date", "", "event", "New Year Party");
    Map<String, String> emptyDate11 = Map.of("event", "New Year Party", "date", "");

    CheckResult todayEndPointTest(String url, int status) {
        HttpResponse response = get(url).send();

        checkStatusCode(response, status);

        if (!response.getJson().isJsonArray()) {
            return CheckResult.wrong("Wrong object in response, expected JSON Array but was \n" +
                    response.getContent().getClass());

        }

        System.out.println(response.getContent() + "\n " + response.getStatusCode() +
                "\n " + response.getRequest().getLocalUri() + "\n " + response.getRequest().getMethod());


        List<String> eventsToString;

        eventsToString = eventsList.stream().filter(it -> it.date.equals(LocalDate.now().toString())).map(it -> it.toString()).collect(Collectors.toList());


        eventsToString.stream().forEach(System.out::println);

        String convertJsonToString = convert(eventsToString);
        JsonArray correctJson = getJson(convertJsonToString).getAsJsonArray();

        JsonArray responseJson = getJson(response.getContent()).getAsJsonArray();

        if (responseJson.size() != correctJson.size()) {
            return CheckResult.wrong("Correct json array size should be " +
                    correctJson.size() + "\n" +
                    "Response array size is: " + responseJson.size() + "\n");
        }


        for (int i = 0; i < responseJson.size(); i++) {


            expect(responseJson.get(i).getAsJsonObject().toString()).asJson()
                    .check(isObject()
                            .value("id", correctJson.get(i).getAsJsonObject().get("id").getAsInt())
                            .value("event", correctJson.get(i).getAsJsonObject().get("event").getAsString())
                            .value("date", correctJson.get(i).getAsJsonObject().get("date").getAsString()));

        }


        return CheckResult.correct();
    }

    CheckResult eventEndPointTest(String url, int status) {
        HttpResponse response = get(url).send();

        checkStatusCode(response, status);

        if (response.getStatusCode() == 200) {

            if (!response.getJson().isJsonArray()) {
                return CheckResult.wrong("Wrong object in response, expected array of JSON but was \n" +
                        response.getContent().getClass());
            }

            List<String> eventsToString;

            eventsToString = eventsList.stream().map(it -> it.toString()).collect(Collectors.toList());

            eventsToString.stream().forEach(System.out::println);

            String convertJsonToString = convert(eventsToString);
            JsonArray correctJson = getJson(convertJsonToString).getAsJsonArray();

            JsonArray responseJson = getJson(response.getContent()).getAsJsonArray();

            if (responseJson.size() != correctJson.size()) {
                return CheckResult.wrong("Correct json array size should be " +
                        correctJson.size() + "\n" +
                        "Response array size is: " + responseJson.size() + "\n");
            }


            for (int i = 0; i < responseJson.size(); i++) {


                expect(responseJson.get(i).getAsJsonObject().toString()).asJson()
                        .check(isObject()
                                .value("id", correctJson.get(i).getAsJsonObject().get("id").getAsInt())
                                .value("event", correctJson.get(i).getAsJsonObject().get("event").getAsString())
                                .value("date", correctJson.get(i).getAsJsonObject().get("date").getAsString()));

            }

        }


        return CheckResult.correct();
    }


    private static void checkStatusCode(HttpResponse resp, int status) {
        if (resp.getStatusCode() != status) {
            throw new WrongAnswer(
                    resp.getRequest().getMethod() + " " +
                            resp.getRequest().getLocalUri() +
                            " should respond with status code " + status + ", " +
                            "responded: " + resp.getStatusCode() + "\n\n" +
                            "Response body:\n\n" + resp.getContent()
            );
        }
    }

    private String convert(List<String> trs) {
        JsonArray jsonArray = new JsonArray();
        for (String tr : trs) {
            JsonElement jsonObject = JsonParser.parseString(tr);
            jsonArray.add(jsonObject);
        }
        return jsonArray.toString();
    }

    CheckResult testEndpointWithParams(String url, String startDay, String endDay) {
        HttpResponse response = get(url + "?start_time=" + startDay + "&end_time=" + endDay).send();


        System.out.println(response.getContent() + "\n " + response.getStatusCode()
                + "\n " + response.getRequest().getLocalUri()
                + "\n " + response.getRequest().getMethod());


        if (eventsList.size() == 0 && +response.getStatusCode() != 204) {
            return CheckResult.wrong(response.getRequest().getMethod() + " " +
                    response.getRequest().getLocalUri() +
                    " should respond with status code 204, " +
                    "responded: " + response.getStatusCode() + "\n\n" +
                    "Response body:\n\n" + response.getContent());

        }


        if (response.getStatusCode() == 200) {

            if (!response.getJson().isJsonArray()) {
                return CheckResult.wrong("Wrong object in response, expected array of JSON but was \n" +
                        response.getContent().getClass());
            }

            List<String> eventsToString;


            eventsToString = eventsList.stream().filter(it -> LocalDate.parse(it.date).equals(LocalDate.parse(startDay))
                    || LocalDate.parse(it.date).isAfter(LocalDate.parse(startDay))
                    && (LocalDate.parse(it.date).equals(LocalDate.parse(endDay))
                    || LocalDate.parse(it.date).isBefore(LocalDate.parse(endDay)))
            ).map(it -> it.toString()).collect(Collectors.toList());

            eventsToString.stream().forEach(System.out::println);

            if (eventsToString.size() == 0) {
                checkStatusCode(response, 204);
            }
            String convertJsonToString = convert(eventsToString);
            JsonArray correctJson = getJson(convertJsonToString).getAsJsonArray();

            JsonArray responseJson = getJson(response.getContent()).getAsJsonArray();

            if (responseJson.size() != correctJson.size()) {
                return CheckResult.wrong("Correct json array size should be " +
                        correctJson.size() + "\n" +
                        "Response array size is: " + responseJson.size() + "\n");
            }


            for (int i = 0; i < responseJson.size(); i++) {


                expect(responseJson.get(i).getAsJsonObject().toString()).asJson()
                        .check(isObject()
                                .value("id", correctJson.get(i).getAsJsonObject().get("id").getAsInt())
                                .value("event", correctJson.get(i).getAsJsonObject().get("event").getAsString())
                                .value("date", correctJson.get(i).getAsJsonObject().get("date").getAsString()));

            }
        }

        return CheckResult.correct();
    }

    CheckResult testEndpoinById(String url, int id) {
        HttpResponse response = get(url + "/" + id).send();

        if (eventsList.stream().filter(it -> it.id == id).map(it -> it.toString()).collect(Collectors.toList()).size() == 1) {
            checkStatusCode(response, 200);
        }

        if (eventsList.stream().filter(it -> it.id == id).map(it -> it.toString()).collect(Collectors.toList()).size() == 0) {
            checkStatusCode(response, 404);
        }

        System.out.println(response.getContent() + " \n" + response.getStatusCode()
                + "\n " + response.getRequest().getLocalUri()
                + "\n " + response.getRequest().getMethod()
                + "\n " + response.getRequest().getContent());

        if (response.getStatusCode() == 404) {

            expect(response.getContent()).asJson().check(
                    isObject()
                            .value("message", "The event doesn't exist!")

            );

        }

        if (response.getStatusCode() == 200) {

            if (!response.getJson().isJsonObject()) {
                return CheckResult.wrong("Wrong object in response, expected array of JSON but was \n" +
                        response.getContent().getClass());
            }

            List<String> eventsToString;


            eventsToString = eventsList.stream().filter(it -> it.id == id).map(it -> it.toString()).collect(Collectors.toList());

            eventsToString.stream().forEach(System.out::println);

            String convertJsonToString = eventsToString.get(0).toString();


            JsonObject correctJson = getJson(convertJsonToString).getAsJsonObject();

            JsonObject responseJson = getJson(response.getContent()).getAsJsonObject();


            expect(responseJson.toString()).asJson()
                    .check(isObject()
                            .value("id", correctJson.getAsJsonObject().get("id").getAsInt())
                            .value("event", correctJson.getAsJsonObject().get("event").getAsString())
                            .value("date", correctJson.getAsJsonObject().get("date").getAsString()));


        }


        return CheckResult.correct();
    }

    CheckResult testEndpointDeleteById(String url, int status, int id) {
        HttpResponse response = delete(url + "/" + id).send();
        checkStatusCode(response, status);
        System.out.println(response.getContent() + " \n" + response.getStatusCode()
                + "\n " + response.getRequest().getLocalUri()
                + "\n " + response.getRequest().getMethod()
                + "\n " + response.getRequest().getContent());

        String content = response.getContent();
        if (content == null || content.isBlank()) {
            throw new WrongAnswer("The JSON response is empty.");
        }

        JsonObject responseJson = getJson(content).getAsJsonObject();

        if (status == 200) {
            List<String> eventsToString;


            eventsToString = eventsList.stream().filter(it -> it.id == id).map(it -> it.toString()).collect(Collectors.toList());

            eventsToString.forEach(System.out::println);

            String convertJsonToString = eventsToString.get(0);


            JsonObject correctJson = getJson(convertJsonToString).getAsJsonObject();


            expect(responseJson.toString()).asJson()
                    .check(isObject()
                            .value("id", correctJson.getAsJsonObject().get("id").getAsInt())
                            .value("event", correctJson.getAsJsonObject().get("event").getAsString())
                            .value("date", correctJson.getAsJsonObject().get("date").getAsString()));

        }

        if (status == 404) {
            expect(responseJson.toString()).asJson()
                    .check(isObject()
                            .value("message", "The event doesn't exist!")
                    );
        }
        eventsList = eventsList.stream().filter(it -> it.id != id).collect(Collectors.toList());

        return CheckResult.correct();
    }

    CheckResult testEndpointDeleteAllById(String url) {
        for (EventForTest it : eventsList
        ) {


            HttpResponse response = delete(url + "/" + it.id).send();
            checkStatusCode(response, 200);
            System.out.println(response.getContent() + " \n" + response.getStatusCode()
                    + "\n " + response.getRequest().getLocalUri()
                    + "\n " + response.getRequest().getMethod()
                    + "\n " + response.getRequest().getContent());

            JsonObject responseJson = getJson(response.getContent()).getAsJsonObject();


            eventsList = eventsList.stream().filter(i -> i.id != it.id).collect(Collectors.toList());
        }
        return CheckResult.correct();
    }

    CheckResult testPostEvent(Map<String, String> body, int status) {

        String jsonBody = gson.toJson(body);

        HttpResponse response = post(eventEndPoint, jsonBody).send();
        checkStatusCode(response, status);
        System.out.println(response.getContent() + " \n" + response.getStatusCode()
                + "\n " + response.getRequest().getLocalUri()
                + "\n " + response.getRequest().getMethod()
                + "\n " + response.getRequest().getContent());

        if (status == 200) {
            count++;
            EventForTest event = new EventForTest(count, body.get("event"), body.get("date"));
            eventsList.add(event);
            expect(response.getContent()).asJson()
                    .check(

                            isObject()
                                    .value("message", "The event has been added!")
                                    .value("event", getJson(jsonBody).getAsJsonObject().get("event").getAsString())
                                    .value("date", getJson(jsonBody).getAsJsonObject().get("date").getAsString())

                    );
        }


        if (status == 400 && String.valueOf(response.getContent()).length() != 0) {

            throw new WrongAnswer(response.getRequest().getMethod() + " " +
                    response.getRequest().getLocalUri() +
                    " responded with status code " + status + " and empty Response body, " +
                    "responded: " + response.getStatusCode() +
                    " Response body: " + response.getContent());
        }
        return CheckResult.correct();
    }

    private int randomReturn(List<Map<String, String>> list) {
        int toReturn = (int) Math.round(Math.random() * (list.size() - 1));

        return toReturn;
    }

    private String randomDate(int maxDays, int mindays) {

        LocalDate now = LocalDate.now();

        return now.plusDays((int) Math.round(Math.random() * (maxDays - mindays) + mindays)).toString();
    }

    @DynamicTest
    DynamicTesting[] dynamicTests = new DynamicTesting[]{


            () -> todayEndPointTest(todayEndPoint, 200), //#1
            () -> eventEndPointTest(eventEndPoint, 204), //#2
            () -> testEndpointWithParams(eventEndPoint,
                    randomDate(-300, -5), randomDate(10, 5)),//#3
            () -> testPostEvent(justToday, 200), //#4
            () -> testPostEvent(justToday, 200), //#5
            () -> testPostEvent(listOfEvents.get(randomReturn(listOfEvents)), 200), //#6
            () -> todayEndPointTest(todayEndPoint, 200),//#7
            () -> eventEndPointTest(eventEndPoint, 200),//#8
            () -> testEndpointWithParams(eventEndPoint,
                    randomDate(-300, -5), randomDate(10, 5)),//#9

            //incorrect body for Post request
            () -> testPostEvent(emptyEvent1, 400), //#10
            () -> testPostEvent(blankEvent2, 400), //#11
            () -> testPostEvent(nullEvent3, 400), //#12
            () -> testPostEvent(nullDate4, 400), //#13
            () -> testPostEvent(emptyEventNullDate5, 400), //#14
            () -> testPostEvent(emptyEventEmptyDate6, 400), //#15
            () -> testPostEvent(blankDateEmptyEvent7, 400), //#16
            () -> testPostEvent(blankDate8, 400), //#17
            () -> testPostEvent(blankDate9, 400), //#18
            () -> testPostEvent(emptyDate10, 400), //#19
            () -> testPostEvent(emptyDate11, 400), //#20

            () -> todayEndPointTest(todayEndPoint, 200),//#21
            () -> eventEndPointTest(eventEndPoint, 200),//#22

            () -> testEndpointWithParams(eventEndPoint,
                    randomDate(-300, -5), randomDate(10, 5)),//#23
            () -> testEndpointWithParams(eventEndPoint,
                    randomDate(-10, -5), randomDate(200, 5)),//#24
            () -> testEndpointWithParams(eventEndPoint,
                    randomDate(-8, -5), randomDate(20, 5)),//#25

            () -> testEndpoinById(eventEndPoint, 1),//#26
            () -> testEndpointDeleteById(eventEndPoint, 200, 2),//#27
            () -> testEndpoinById(eventEndPoint, 2),//#28
            () -> testEndpoinById(eventEndPoint, 1),//#29
            () -> todayEndPointTest(todayEndPoint, 200),//#30
            () -> eventEndPointTest(eventEndPoint, 200),//#31
            () -> testEndpointDeleteById(eventEndPoint, 200, 1),//#32
            () -> testEndpoinById(eventEndPoint, 1),//#33
            () -> testEndpointDeleteAllById(eventEndPoint),//#34
            () -> eventEndPointTest(eventEndPoint, 204),//35

            () -> todayEndPointTest(todayEndPoint, 200),//#36

            () -> testPostEvent(justToday, 200), //#37
            () -> testPostEvent(justToday, 200), //#38
            () -> todayEndPointTest(todayEndPoint, 200),//#39

            () -> testPostEvent(listOfEvents.get(randomReturn(listOfEvents)), 200),//40
            () -> testPostEvent(listOfEvents.get(randomReturn(listOfEvents)), 200),//41
            () -> testPostEvent(listOfEvents.get(randomReturn(listOfEvents)), 200),//42
            () -> testPostEvent(listOfEvents.get(randomReturn(listOfEvents)), 200),//43
            () -> testPostEvent(listOfEvents.get(randomReturn(listOfEvents)), 200),//44
            () -> testPostEvent(listOfEvents.get(randomReturn(listOfEvents)), 200),//45

            () -> eventEndPointTest(eventEndPoint, 200),//#46
            () -> testEndpointWithParams(eventEndPoint,
                    randomDate(-300, -5), randomDate(10, 5)),//#47
            () -> testEndpointWithParams(eventEndPoint,
                    randomDate(-10, -5), randomDate(200, 5)),//#48
            //this::reloadServer,//49
            () -> testEndpointWithParams(eventEndPoint,
                    randomDate(-8, -5), randomDate(20, 5)),//#50
            () -> testEndpointDeleteById(eventEndPoint, 404, 1),//#51
            () -> testEndpointDeleteAllById(eventEndPoint),//#52
            () -> eventEndPointTest(eventEndPoint, 204),//#53
            () -> todayEndPointTest(todayEndPoint, 200)//#54

    };

    @Before
    public void createEvents() {

        listOfEvents.stream().forEach(System.out::println);

    }

    private CheckResult reloadServer() {
        try {
            reloadSpring();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return CheckResult.correct();
    }

}
