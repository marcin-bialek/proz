package pl.edu.pw.stud.bialek2.marcin.proz.services;

import pl.edu.pw.stud.bialek2.marcin.proz.models.User;


public interface UserServiceListener {
    public void userServiceNeedsUser();
    public void userServiceNeedsPassword();
    public void userServiceWrongPassword();
    public void userServiceDidCreateUser(User user);
    public void userServiceDidLoadUser(User user);
}
