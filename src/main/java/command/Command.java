package main.java.command;

public interface Command {
    void execute();

    default void undo() {
        // optional undo operation
    }
}