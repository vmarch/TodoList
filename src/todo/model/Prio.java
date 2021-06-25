package todo.model;

public enum Prio {
    HIGH, LOW, MEDIUM;

    public static Prio intToPrio(int intAsPrio) {
        return switch (intAsPrio) {
            case 1 -> LOW;
            case 2 -> MEDIUM;
            case 3 -> HIGH;
            default -> null;
        };
    }

    public static int prioToInt(Prio prioEnum) {
        if (null != prioEnum) {
            return switch (prioEnum) {
                case LOW -> 1;
                case MEDIUM -> 2;
                case HIGH -> 3;
            };
        } else {
            return 0;
        }
    }
}
