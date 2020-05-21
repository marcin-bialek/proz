package pl.edu.pw.stud.bialek2.marcin.proz.services;


public class UserService {
    private UserServiceListener listener;

    public UserService() {

    }

    public void setListener(UserServiceListener listener) {
        this.listener = listener;
    }

    public void loadUser() {
        this.listener.userServiceNeedsUser();
    }
}

