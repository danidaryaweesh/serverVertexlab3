/**
 * Created by dani on 2016-12-15.
 */
public class ChatRoom {

    private String defaultUser;
    private String otherUser;
    private String defaultUserPrefer;
    private boolean full;

    public String getDefaultUser() {
        return defaultUser;
    }

    public void setDefaultUser(String defaultUser) {
        this.defaultUser = defaultUser;
    }

    public String getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(String otherUser) {
        this.otherUser = otherUser;
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }
}
