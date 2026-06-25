import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.Map;
import java.util.List;

public class OrderClient extends UserClient {

    @Step("Создание заказа")
    public Response createOrder(List<String> ingredients, String token) {
        var request = RestAssured.given().spec(getSpec());
        if (token != null) {
            request.header("Authorization", token);
        }
        return request.body(Map.of("ingredients", ingredients)).post("api/orders");
    }

    @Step("Получение заказов конкретного пользователя")
    public Response getUserOrders(String token) {
        var request = RestAssured.given().spec(getSpec());
        if (token != null) {
            request.header("Authorization", token);
        }
        return request.get("api/orders");
    }
}