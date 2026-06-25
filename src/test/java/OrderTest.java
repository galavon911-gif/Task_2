import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;
import static org.hamcrest.Matchers.*;

public class OrderTest {
    private OrderClient orderClient;
    private String accessToken;
    // Валидные хеши ингредиентов с сайта
    private final List<String> validIngredients = List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa71");

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        User user = new User(RandomStringUtils.randomAlphanumeric(10) + "@yandex.ru", "password123", "User1");
        Response response = orderClient.createUser(user);
        accessToken = response.path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            orderClient.deleteUser(accessToken);
        }
    }

    @Test
    public void testCreateOrderWithAuthSuccess() {
        orderClient.createOrder(validIngredients, accessToken).then().statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    public void testCreateOrderWithoutAuthSuccess() {

        orderClient.createOrder(validIngredients, null).then().statusCode(200)
                .body("success", is(true));
    }

    @Test
    public void testCreateOrderWithoutIngredientsReturnsError() {
        orderClient.createOrder(new ArrayList<>(), accessToken).then().statusCode(400)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    public void testCreateOrderWithInvalidIngredientHashReturnsError() {
        List<String> invalidIngredients = List.of("invalid_hash_123");
        orderClient.createOrder(invalidIngredients, accessToken).then().statusCode(500);
    }

    @Test
    public void testGetOrdersWithAuthSuccess() {
        orderClient.createOrder(validIngredients, accessToken);

        orderClient.getUserOrders(accessToken).then().statusCode(200)
                .body("success", is(true))
                .body("orders", notNullValue());
    }

    @Test
    public void testGetOrdersWithoutAuthReturnsError() {
        orderClient.getUserOrders(null).then().statusCode(401)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }
}