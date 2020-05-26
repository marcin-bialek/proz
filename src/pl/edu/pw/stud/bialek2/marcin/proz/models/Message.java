package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.time.LocalDateTime;


public class Message {
    private boolean sentByMe;
    private String text;
    private LocalDateTime date;

    public Message(String text, boolean sentByMe) {
        this(text, sentByMe, LocalDateTime.now());
    }

    public Message(String text, boolean sentByMe, LocalDateTime date) {
        this.text = text;
        this.sentByMe = sentByMe;
        this.date = date;
    }

    public String getText() {
        return this.text;
    }

    public boolean isSentByMe() {
        return this.sentByMe;
    }

    public LocalDateTime getDate() {
        return this.date;
    }
}
