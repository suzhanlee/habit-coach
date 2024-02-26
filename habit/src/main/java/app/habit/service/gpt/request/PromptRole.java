package app.habit.service.gpt.request;

public enum PromptRole {

    USER("user"), SYSTEM("system");

    private final String value;

    PromptRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


