package bdd.steps;

import static io.restassured.RestAssured.given;
//import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.util.Map;
import java.util.UUID;

import org.springframework.boot.context.embedded.LocalServerPort;

import com.js.profile.services.ApplicationFeatureTest;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
// import static org.hamcrest.Matchers.containsInAnyOrder;

public class APISteps extends ApplicationFeatureTest {

	private Response response;
	private ValidatableResponse json;
	private RequestSpecification request;

	private String HOST_ROOT = "http://localhost//jsprofile/v2/profiles";

	@LocalServerPort
	private int port;

	@Given("^I use the pid (\\d+) , ids \"(.*?)\", vt \"(.*?)\"$")
	public void i_use_the_pid_ids_vt(int pid, String ids, String vt) throws Throwable {
		request = given().port(port)
				.params("pid", pid, "ids", ids, "vt", vt)
				.header("JS-Request-Id", UUID.randomUUID().toString());
	}

	@When("^I request the api$")
	public void i_request_the_api() throws Throwable {
		response = request.when().get(HOST_ROOT);
	}

	@Then("^I should get a response with HTTP status code (\\d+)$")
	public void i_should_get_a_response_with_HTTP_status_code(int arg1) throws Throwable {
		json = response.then().statusCode(arg1);
	}

	@Then("^response includes the following in any order$")
	public void response_includes_the_following_in_any_order(DataTable arg1) throws Throwable {

		Map<String, String> m = arg1.asMap(String.class, String.class);

		m.forEach((k, v) -> {
			/*if (v.equals("null")) {
				json.body(k, equalTo(null));
			} else if (StringUtils.indexOfAny(k.toLowerCase(), new String[] { ".count", "errorcode" }) != -1) {
				json.body(k, equalTo(Integer.parseInt(v)));
			}
			else {*/
				json.body(String.valueOf(k), equalTo(String.valueOf(v)));
			//}

		});
	}
}