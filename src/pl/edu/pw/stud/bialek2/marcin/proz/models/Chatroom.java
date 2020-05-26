package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.nio.ByteBuffer;
import java.util.UUID;


public class Chatroom {
    private int id;
    private UUID uuid;
    private String name;

    public Chatroom(int id, UUID uuid, String name) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
    }

    public Chatroom(UUID uuid, String name) {
        this(0, uuid, name);
    }

    public Chatroom(String name) {
        this(UUID.randomUUID(), name);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public Message getLastMessage() {
        return FAKE_MESSAGES[FAKE_MESSAGES.length - 1];
    }

    public static final Message[] FAKE_MESSAGES = {
        new Message("Nostrud exercitation est elit deserunt minim reprehenderit fugiat do labore elit.", false),
        new Message("Nisi ipsum proident do ex amet pariatur nulla in cupidatat laboris.", false),
        new Message("no siemaQuis anim amet ullamco esse exercitation reprehenderit consequat veniam est ipsum.", true),
        new Message("Nulla ut sunt sit culpa incididunt. Duis eiusmod quis sint consectetur occaecat tempor quis ea ea. Non velit adipisicing sunt amet qui magna eiusmod eu do anim cillum elit adipisicing esse. Elit labore eu dolore elit magna esse cillum qui. Velit in id adipisicing anim duis enim mollit veniam elit non excepteur dolor esse aliquip. Ea consequat laboris sit laborum. Aute est aliqua minim ipsum cillum duis eiusmod culpa dolor nisi sint.", false),
        new Message("Deserunt exercitation tempor nisi reprehenderit est minim sunt Lorem quis anim adipisicing magna.", true),
        new Message("Adipisicing exercitation mollit consequat velit.", true),
        new Message("Amet aliqua reprehenderit nulla sint.", false),
        new Message("Cillum magna dolor in officia commodo enim culpa laborum anim ad.", false),
        new Message("Commodo proident occaecat voluptate nulla esse ad aute quis Lorem esse sunt do. Velit veniam esse aliqua aute magna amet mollit exercitation cupidatat cupidatat. Culpa irure aliqua adipisicing aute reprehenderit.", true),
    };
}
