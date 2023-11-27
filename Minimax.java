import static java.lang.Math.max;
import static java.lang.Math.min;

public class Minimax{

    public Coordinate bestMove(Board board, Player player) {

        Coordinate bestMove = null;
        int maxV = Integer.MIN_VALUE;
        for (Coordinate c : board.emptySquares()) {
            int v = minValue(board.next(c, player), player.other(), 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (v > maxV) {
                maxV = v;
                bestMove = c;
            } else if (v == maxV && (bestMove == null || (int)(Math.random()*3) == 1)) bestMove = c;
        }
        return bestMove;
    }

    public int maxValue(Board board, Player player, int plies, int a, int b) {
        int maxPlies = plies < 5 ? TicTacToe.PLIES + plies - 4: (int)((1.0/15)*plies);
        if(board.isTerminal() || plies >= maxPlies) return board.evaluate(player);
        int v = Integer.MIN_VALUE;
        for(Coordinate c : board.emptySquares()) {
            v = max(v, minValue(board.next(c,player),player.other(),plies+1,a,b));
            if (v >= b) return v;
            a = max(a,v);
        }
        return v;
    }

    public int minValue(Board board, Player player, int plies, int a, int b) {
        int maxPlies = plies < 5 ? TicTacToe.PLIES + plies - 4: (int)((1.0/15)*plies);
        if(board.isTerminal() || plies >= maxPlies) return board.evaluate(player);
        int v = Integer.MAX_VALUE;
        for(Coordinate c : board.emptySquares()){
            v = min(v,maxValue(board.next(c,player),player.other(),plies+1,a,b));
            if(v <= a) return v;
            b = min(b,v);
        }
        return v;
    }
}
