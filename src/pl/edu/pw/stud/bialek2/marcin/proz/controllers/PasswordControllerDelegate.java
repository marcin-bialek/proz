package pl.edu.pw.stud.bialek2.marcin.proz.controllers;


public interface PasswordControllerDelegate {
    public void passwordControllerDidExit(PasswordController sender);
    public void passwordControllerDidEnterPassword(PasswordController sender, char[] password);
}
