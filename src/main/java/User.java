/**
 * Created by dani on 2016-12-15.
 */
public class User {

    private String username;
    private String preferedUser;
    private String to;
    private String otherUserID;
    private String id;
    private boolean busy;

    public User(String username, String id, String preferedUser,boolean busy){
        this.username = username;
        this.id=id;
        this.preferedUser = preferedUser;
        this.busy = busy;
        to="";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPreferedUser() {
        return preferedUser;
    }

    public void setPreferedUser(String preferedUser) {
        this.preferedUser = preferedUser;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getOtherUserID() {
        return otherUserID;
    }

    public void setOtherUserID(String otherUserID) {
        this.otherUserID = otherUserID;
    }
}
