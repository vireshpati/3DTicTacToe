import java.io.Console;

public class TicTacToe {


    public static int PLIES = 3;
    public static Player player = Player.X;

    public static void main(String[] args) {

        Board board = new Board();
        boolean first = true;

        Minimax minimax = new Minimax();

        for (String arg : args) {
            try {
                TicTacToe.PLIES = Integer.parseInt(arg);
                continue;
            } catch (NumberFormatException ignored) {
            }

            switch (arg.toLowerCase()) {
                case "-x":
                    player = Player.X;
                    break;
                case "-o":
                    player = Player.O;
                    break;
                case "-first":
                    first = true;
                    break;
                case "-second":
                    first = false;
                    break;
                default:
                    board = new Board(arg);
                    break;
            }
        }

        boolean flag = false;
        while (!board.isTerminal()) {

            board.print();

            Console console = System.console();

            if (first || flag) {
                Coordinate bestMove = minimax.bestMove(board, player);
                board = board.next(bestMove, player);

                if (board.wins(Player.X)) {
                    System.out.println("I win!!!");
                    break;
                }

                System.out.println(bestMove + "\n");

                board.print();
            }
            player = player.other();

            int x = 0;
            int y = 0;
            int z = 0;
            String line;
            do {
                do {
                    line = console.readLine("Row: ");
                    try {
                        x = Integer.parseInt(line) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid row: " + line);
                    }
                    if (x >= 0 && x < 4) break;
                    System.out.println("Invalid row: " + line);
                } while (true);

                do {
                    line = console.readLine("Col: ");
                    try {
                        y = Integer.parseInt(line) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid column: " + line);
                    }
                    if (y >= 0 && y < 4) break;
                    System.out.println("Invalid col: " + line);
                } while (true);

                do {
                    line = console.readLine("Level: ");
                    try {
                        z = Integer.parseInt(line) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid level: " + line);
                    }
                    if (z >= 0 && z < 4) break;
                    System.out.println("Invalid level: " + line);
                } while (true);

                if (board.isEmpty(x, y, z)) break;
                System.out.println("Square is not empty");
            } while (true);

            board = board.next(Coordinate.valueOf(x, y, z), player);

            player = player.other();
            flag = true;
        }

        if (board.wins(Player.O)) {
            System.out.println("You win :(");
        }

    }

}

