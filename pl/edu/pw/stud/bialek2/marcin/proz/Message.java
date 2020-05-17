package pl.edu.pw.stud.bialek2.marcin.proz;

import java.time.LocalDateTime;


public class Message {
    private String text;
    private LocalDateTime date;

    public Message(String text, LocalDateTime date) {
        this.text = text;
        this.date = date;
    }

    public String getText() {
        return this.text;
    }

    public LocalDateTime getDate() {
        return this.date;
    }
}
