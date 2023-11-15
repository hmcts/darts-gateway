package uk.gov.hmcts.darts.workflow.command;

public class CommandException extends RuntimeException {
    private final int response;

    CommandException(int response, Throwable throwable) {
        super("Command Exception", throwable);
        this.response = response;
    }

    CommandException(int response) {
        super("Command Exception");
        this.response = response;
    }

    public int getResponse() {
        return response;
    }
}
