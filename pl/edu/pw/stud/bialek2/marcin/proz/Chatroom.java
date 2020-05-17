package pl.edu.pw.stud.bialek2.marcin.proz;

import java.time.LocalDateTime;


public class Chatroom {
    private String name;

    public Chatroom(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Message getLastMessage() {
        return new Message(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed iaculis metus risus, at sodales tortor elementum eu. Nunc posuere aliquam vestibulum. Suspendisse sed euismod massa.", 
            LocalDateTime.of(2020, 5, 17, 12, 31)
        );
    }
}
