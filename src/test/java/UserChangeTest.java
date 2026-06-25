import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

public class UserChangeTest {
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = new User(RandomStringUtils.randomAlphanumeric(10) + "@yandex.ru", "password123", "User1");
        Response response = userClient.createUser(user);
        accessToken = response.path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    public void testUpdateUserWithAuthSuccess() {
        user.setName("NewUpdatedName");
        user.setEmail(RandomStringUtils.randomAlphanumeric(10) + "@yandex.ru");

        userClient.updateUser(user, accessToken).then().statusCode(200)
                .body("success", is(true))
                .body("user.name", is("NewUpdatedName"));
    }

    @Test
    public void testUpdateUserWithoutAuthReturnsError() {
        user.setName("UnauthorizedName");

        userClient.updateUser(user, null).then().statusCode(401)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }
}