package pl.edu.pw.stud.bialek2.marcin.proz.models;


public enum MessageType {
    TEXT_MESSAGE(0);
    
    private final int value;

    private MessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
