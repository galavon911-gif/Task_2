import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.qameta.allure.restassured.AllureRestAssured;

public class UserClient {
    protected static final String BASE_URL = "https://stellarburgers.education-services.ru/";

    protected RequestSpecification getSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured()) // Логирование запросов в Allure отчет
                .build();
    }

    @Step("Создание пользователя")
    public Response createUser(User user) {
        return RestAssured.given().spec(getSpec()).body(user).post("api/auth/register");
    }

    @Step("Логин пользователя")
    public Response loginUser(User user) {
        return RestAssured.given().spec(getSpec()).body(user).post("api/auth/login");
    }

    @Step("Изменение данных пользователя")
    public Response updateUser(User user, String token) {
        var request = RestAssured.given().spec(getSpec());
        if (token != null) {
            request.header("Authorization", token);
        }
        return request.body(user).patch("api/auth/user");
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String token) {
        return RestAssured.given().spec(getSpec()).header("Authorization", token).delete("api/auth/user");
    }
}