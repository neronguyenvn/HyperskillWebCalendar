import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;
import webCalendarSpring.ApplicationKt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    private static List<EventForTest> eventsList = new ArrayList<>();
    int count = 0;

    public WebCalendarSpringTest() {

        super(ApplicationKt.class);

    }

    public static final String todayEndPoint = "/event/today";


    CheckResult testEndpoint(String url, int status) {
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

    private String convert(List<String> trs) {
        JsonArray jsonArray = new JsonArray();
        for (String tr : trs) {
            JsonElement jsonObject = JsonParser.parseString(tr);
            jsonArray.add(jsonObject);
        }
        return jsonArray.toString();
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


    @DynamicTest
    DynamicTesting[] dynamicTests = new DynamicTesting[]{


            () -> testEndpoint(todayEndPoint, 200), //#1

    };


}
