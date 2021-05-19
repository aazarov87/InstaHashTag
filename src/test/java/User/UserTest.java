package User;

import org.junit.Assert;
import org.junit.Test;
import ru.azarov.instahashtag_bot.InstaHelper.InstaUser;

public class UserTest {

    @Test
    public void setLogin (){
        String login = "Login";

        InstaUser instaUser = new InstaUser();
        instaUser.setLoginAndPass("Login Password");

        Assert.assertEquals(login, instaUser.getLogin());
    }

    @Test
    public void setLogin1 (){
        String login = "Login";

        InstaUser instaUser = new InstaUser();
        instaUser.setLoginAndPass("Login");

        Assert.assertEquals(login, instaUser.getLogin());
    }

    @Test
    public void setPassword (){
        String password = "Password";

        InstaUser instaUser = new InstaUser();
        instaUser.setLoginAndPass("Login Password");

        Assert.assertEquals(password, instaUser.getPassword());
    }

    @Test
    public void setPassword_NULL (){
        InstaUser instaUser = new InstaUser();
        instaUser.setLoginAndPass("Login");

        Assert.assertNull(instaUser.getPassword());
    }
}
