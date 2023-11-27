public enum Player {

    X,
    O,
    EMPTY;

    public Player other() {
        return switch (this) {
            case X -> O;
            case O -> X;
            default -> EMPTY;
        };
    }

    public static Player valueOf(char c) {
        return switch (c) {
            case 'x', 'X' -> X;
            case 'o', 'O' -> O;
            default -> EMPTY;
        };
    }
            

    @Override
    public String toString() {
        return switch (this) {
            case X -> "X";
            case O -> "O";
            default -> ".";
        };
    }
}
