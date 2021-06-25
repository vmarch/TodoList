package todo.model;

public enum State {
    WAIT, IN_PROGRESS, DONE;

    public static State intToState(int anInt) {
        return switch (anInt) {
            case 1 -> WAIT;
            case 2 -> IN_PROGRESS;
            case 3 -> DONE;
            default -> null;
        };
    }

    public static int stateToInt(State stateEnum) {
        if (null != stateEnum) {
            return switch (stateEnum) {
                case WAIT -> 1;
                case IN_PROGRESS -> 2;
                case DONE -> 3;
            };
        } else {
            return 0;

        }
    }
}