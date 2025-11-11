package main.java.com.cafepos.command;

public interface Command {
    void execute();

    default void undo() {
        // optional undo operation
    }
}

