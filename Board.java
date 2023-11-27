import java.util.Iterator;

public class Board {

    public static final int N = 4;

    private long x; // Boolean vector of positions containing X's
    private long o; // Boolean vector of positions containing O's


    // Constructors.

    public Board() {
        this.x = 0;
        this.o = 0;
    }

    public Board(Board board) {
        this.x = board.x;
        this.o = board.o;
    }

    public Board(String s) {
        int position = 0;
        this.x = 0;
        this.o = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case 'x':
                case 'X':
                    this.set(position++, Player.X);
                    break;

                case 'o':
                case 'O':
                    this.set(position++, Player.O);
                    break;

                case '.':
                    position++;
                    break;

                case ' ':
                case '|':
                    break;

                default:
                    throw new IllegalArgumentException("Invalid player: " + c);
            }
        }
    }


    // Empty squares.

    public boolean isEmpty(int position) {
        assert Coordinate.isValid(position);
        return !Bit.isSet(this.x | this.o, position);
    }

    public boolean isEmpty(Coordinate coordinate) {
        return this.isEmpty(coordinate.position());
    }

    public boolean isEmpty(int x, int y, int z) {
        return this.isEmpty(Coordinate.position(x, y, z));
    }

    public int numberEmptySquares() {
        return Bit.countOnes(~(this.x | this.o));
    }


    // Get value of a square on the board.

    public long get(Player player) {
        return switch (player) {
            case EMPTY -> ~(this.x | this.o);
            case X -> this.x;
            case O -> this.o;
            default -> 0;
        };
    }

    public Player get(int position) {
        assert Coordinate.isValid(position);
        if (Bit.isSet(this.x, position)) return Player.X;
        if (Bit.isSet(this.o, position)) return Player.O;
        return Player.EMPTY;
    }

    public Player get(Coordinate coordinate) {
        return this.get(coordinate.position());
    }

    public Player get(int x, int y, int z) {
        return this.get(Coordinate.position(x, y, z));
    }


    // Set value of a square on the board.

    public void set(int position, Player player) {
        assert (isEmpty(position));
        switch (player) {
            case X:
                this.x = Bit.set(this.x, position);
                break;

            case O:
                this.o = Bit.set(this.o, position);
                break;

            default:
                break;
        }
    }

    public void set(Coordinate coordinate, Player player) {
        set(coordinate.position(), player);
    }

    public void set(int x, int y, int z, Player player) {
        set(Coordinate.position(x, y, z), player);
    }

    public void clear(int position) {
        this.x = Bit.clear(this.x, position);
        this.o = Bit.clear(this.o, position);
    }

    public void clear(Coordinate coordinate) {
        clear(coordinate.position());
    }

    public void clear(int x, int y, int z) {
        clear(Coordinate.valueOf(x, y, z));
    }


    // Equality.

    public boolean equals(Board other) {
        return this.o == other.o && this.x == other.x;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Board && this.equals((Board) other);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.x) * Long.hashCode(this.o);
    }


    // Image & printing functions.

    @Override
    public String toString() {
        String result = "";
        String separator = "";

        for (int position = 0; position < 64; position++) {
            result += separator;
            result += this.get(position).toString();
            if (position % 16 == 0) {
                separator = " | ";
            } else if (position % 4 == 0) {
                separator = " ";
            } else {
                separator = "";
            }
        }
        return result;
    }


    public static Board valueOf(String s) {
        return new Board(s);
    }


    public void print() {
        for (int y = N - 1; y >= 0; y--) {
            for (int z = 0; z < N; z++) {
                for (int x = 0; x < N; x++) {
                    System.out.print(this.get(x, y, z));
                }
                System.out.print("    ");
            }
            System.out.println();
        }
    }


    // Generate new board for a given move.

    public Board next(Coordinate move, Player player) {
        assert this.isEmpty(move);
        Board result = new Board(this);
        result.set(move, player);
        return result;
    }

    public Board next(int position, Player player) {
        return next(Coordinate.valueOf(position), player);
        // return next(new Coordinate(position), player);
    }

    public Board next(int x, int y, int z, Player player) {
        return next(Coordinate.valueOf(x, y, z), player);
        // return next (new Coordinate(x, y, z), player);
    }


    // Iterators.

    private class EmptySquareIterator implements Iterator<Coordinate> {

        private final Iterator<Integer> iterator;

        public EmptySquareIterator() {
            this.iterator = Bit.iterator(Board.this.get(Player.EMPTY));
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public Coordinate next() {
            return Coordinate.valueOf(this.iterator.next());

        }
    }

    public Iterator<Coordinate> emptySquareIterator() {
        return new EmptySquareIterator();
    }

    public Iterable<Coordinate> emptySquares() {
        return EmptySquareIterator::new;
    }

    public boolean isTerminal() {
        return wins(Player.X) || wins(Player.O) || numberEmptySquares() == 0;
    }

    public boolean wins(Player player) {
        assert !player.equals(Player.EMPTY);

        long pos = player.equals(Player.X) ? x : o;

        for (Line line : Line.lines) {
            if (Bit.countOnes(line.positions() & pos) == N) return true;
        }
        return false;
    }


    private static final int[] xWeights = {1, 4, 9, 16};
    private static final int[] oWeights = {2, 9, 16, 25};

    public int evaluate(Player player) {
        int[] xMap = new int[N];
        int[] oMap = new int[N];

        for (Line line : Line.lines) {
            int xCount = Bit.countOnes(this.x & line.positions());
            int oCount = Bit.countOnes(this.o & line.positions());

            if (xCount > 0 && oCount == 0) {
                xMap[xCount - 1]++;
            } else if (oCount > 0 && xCount == 0) {
                oMap[oCount - 1]++;
            }
        }

        int xCount = 0;
        int oCount = 0;

        for (int i = 0; i < N; i++) {
            xCount += xWeights[i] * xMap[i];
            oCount += oWeights[i] * oMap[i];
        }


        if (!isTerminal()) {
            return xCount - oCount;
        } else {
            if (wins(Player.O)) {
                return Integer.MIN_VALUE;
            } else if (wins(Player.X)) {
                return Integer.MAX_VALUE;
            } else {
                return 0;
            }

        }
    }
}

