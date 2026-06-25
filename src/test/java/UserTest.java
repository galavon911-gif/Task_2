import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

public class UserTest {
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = new User(
                RandomStringUtils.randomAlphanumeric(10) + "@yandex.ru",
                "qwerty1234",
                "TestName"
        );
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    public void testCreateUniqueUserSuccess() {
        Response response = userClient.createUser(user);
        response.then().statusCode(200).body("success", is(true));
        accessToken = response.path("accessToken");
    }

    @Test
    public void testCreateExistingUserReturnsError() {
        Response response1 = userClient.createUser(user);
        accessToken = response1.path("accessToken");

        Response response2 = userClient.createUser(user);
        response2.then().statusCode(403)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @Test
    public void testCreateUserWithoutFieldReturnsError() {
        user.setEmail("");
        userClient.createUser(user).then().statusCode(403)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    public void testLoginExistingUserSuccess() {
        userClient.createUser(user);

        Response response = userClient.loginUser(user);
        response.then().statusCode(200).body("success", is(true));
        accessToken = response.path("accessToken");
    }

    @Test
    public void testLoginWithIncorrectCredentialsReturnsError() {
        userClient.createUser(user);
        user.setPassword("wrong_password");

        userClient.loginUser(user).then().statusCode(401)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }
}