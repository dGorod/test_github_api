package example.com.testgithub.api;

import java.io.Serializable;

/**
 * Created by Dmitriy Gorodnytskiy on 29-Oct-15.
 */
public class UserModel implements Serializable {
    private static final long serialVersionUID = 3057337376756216970L;

    private int id;
    private String login;
    private String avatar_url;
    private String html_url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatarUrl() {
        return avatar_url;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatar_url = avatar_url;
    }

    public String getHtmlUrl() {
        return html_url;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.html_url = html_url;
    }
}
