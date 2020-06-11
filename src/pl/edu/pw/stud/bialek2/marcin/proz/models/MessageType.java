package pl.edu.pw.stud.bialek2.marcin.proz.models;


public enum MessageType {
    NULL_MESSAGE(0),
    TEXT_MESSAGE(1),
    IMAGE_MESSAGE(2);
    
    private final int value;

    private MessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static MessageType fromValue(int value) {
        if(value == 1) {
            return TEXT_MESSAGE;
        }
        else if(value == 2) {
            return IMAGE_MESSAGE;
        }

        return NULL_MESSAGE;
    }
}
