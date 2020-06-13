package pl.edu.pw.stud.bialek2.marcin.proz;

import java.util.Locale;
import java.util.ResourceBundle;


public enum Language {
    DEFAULT("pl");

    private ResourceBundle bundle;
    
    private Language(String language) {
        final Locale locale = new Locale(language);
        this.bundle = ResourceBundle.getBundle("resources.Language", locale);
    }

    public String getString(String key) {
        return this.bundle.getString(key);
    }
}