package chai;

import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

import java.util.ArrayList;
import java.util.HashMap;

public class AlphaBetaAI implements ChessAI {
    static int MAXDEPTH = 8;
    static int NEGINF = -10000;
    static int INF = 10000;
    static int RANDMOVE;
    int StatesVisited;
    HashMap<Long, ArrayList<Integer>> transpoTable = new HashMap<>();


    public short getMove(Position position) throws IllegalMoveException {
        short [] moves = position.getAllMoves();
        RANDMOVE = moves.length;
        short move;
//        transpoTable = new HashMap<>();

        // reduces threading errors by passing a temporary variable for the position into the move search
        Position temp = new Position(position);

        move = IDABsearch(temp);

        if(move == -1){
            System.out.println("No move found");
        }
        position.doMove(move);
        int val = UtilityFunc(position);
        System.out.println("Move Chosen: " + position + " Value: " + val);

        if( position.isMate())
            System.out.println("CHECKMATE!! Game is over");

        position.undoMove();

        return move;
    }

    // not iterative deepening
    public short ABSearch(Position position) throws IllegalMoveException {
        int i = 0;
        int currV;
        int currMax = MAXDEPTH;
        int largestV = NEGINF;
        short bestMove = 0;
        Position temp = position;

        StatesVisited = 0;
        for (short currMove : position.getAllMoves()) {
            position.doMove(currMove);
            if (i == 33)
                System.out.println("");
            currV = AlphaBetaSearch(position, 0, NEGINF, INF, false, currMax);
            if (currV > largestV) {
                largestV = currV;
                bestMove = currMove;
            }
            if (currV == 10000)
                System.out.println(position);
            position.undoMove();
            i++;
        }
        return bestMove;
    }

    public short IDABsearch(Position position) throws IllegalMoveException {
        int i;
        int currV;
        int currMax = 0;
        int largestV = NEGINF;
        short bestMove = 0;
        Position temp = position;
        // Maybe use this when a checkmate is found -> OR use when there are fewer than 20 moves for a position or something
        while(currMax <= MAXDEPTH) {
            i = 0;
            StatesVisited = 0;

            position = temp;
            for (short currMove : position.getAllMoves()) {
                position.doMove(currMove);
                if (i == 33)
                    System.out.println("");
                currV = AlphaBetaSearch(position, 0, NEGINF, INF, false, currMax);
                if (currV > largestV) {
                    largestV = currV;
                    bestMove = currMove;
                }
                if(largestV == INF){
                    return bestMove;
                }
                System.out.println("CurrV: " + currV + " i: " + i + " curr depth: " + currMax + " MOVE: " + currMove + " Nodes Visited: " + StatesVisited);
                if (currV == 10000)
                    System.out.println(position);
                position.undoMove();
                i++;
            }
            currMax++;
        }

        return bestMove;
    }

    public int AlphaBetaSearch(Position pos, int depth, int a, int b, boolean maxPlayer, int currMax) throws IllegalMoveException {
        int v;
        StatesVisited++;
        if(transpoTable.containsKey(pos.getHashCode())){
            if((MAXDEPTH - depth) <= transpoTable.get(pos.getHashCode()).get(1)) {
                // if currDepth >= depth
                v = transpoTable.get(pos.getHashCode()).get(0);
                return v;
            }
        } else if(cutoff(pos, depth, currMax)){
            v =  UtilityFunc(pos);
            return v;
        }
        depth++;
        if(maxPlayer){
            v = NEGINF;
            for(short currMove : pos.getAllMoves()){
                Result(pos, currMove);
                v = Math.max(v, AlphaBetaSearch(pos, depth, a, b, !maxPlayer, currMax));
                a = Math.max(a, v);
                pos.undoMove();
                if(b <= a){
                    break;
                }
            }
            ArrayList<Integer> newVals = new ArrayList<>();
            newVals.add(0, v);
            newVals.add(1, MAXDEPTH - depth);
            transpoTable.put(pos.getHashCode(), newVals);
            return a;

        } else{
            v = INF;
            for(short currMove : pos.getAllMoves()){
                Result(pos, currMove);
                v = Math.min(v, AlphaBetaSearch(pos, depth, a, b, !maxPlayer, currMax));
                b = Math.min(b, v);
                pos.undoMove();
                if(b <= a){
                    break;
                }
            }
            ArrayList<Integer> newVals = new ArrayList<>();
            newVals.add(0, v);
            newVals.add(1, MAXDEPTH - depth);
            transpoTable.put(pos.getHashCode(), newVals);
            return b;

        }
    }


    public Position Result(Position curr, short currMove) throws IllegalMoveException {
        curr.doMove(currMove);
        return curr;
    }


    public boolean cutoff(Position curr, int depth, int currMax){
        if(curr.isMate()) { // win
            // return true if there is a king capture
            return true;
        } else if(curr.isTerminal() && !curr.isMate()) { // draw
            return true;
        } else if(depth >= currMax) { // at max depth
            return true;
        } else
            return false;
    }

    public int UtilityFunc(Position curr){
        boolean MAXTURN;
        int v = 0;
        // 0 is blacks turn, 1 is white's turn
        // black(min)'s turn after white moves
        if(curr.getToPlay() == 0)
            MAXTURN = true;
        else { // white's turn
            MAXTURN = false;
        }

        // if someone won
        if(curr.isMate()) {
            if(MAXTURN == false) // mate and it's black's turn, white wins
                v = NEGINF;
            else if(MAXTURN == true) // mate and it's white's turn, black wins
                v = INF;
            return v;
            // if there is a draw
        }else if(curr.isTerminal() && !curr.isMate()){
            v = 0;
            return v;
            // else no win, lose, or draw, return random value
        }else {
            v = Eval(curr);
            // v = (int) (Math.random() * RANDMOVE);
            return v;
        }
    }

    public int Eval(Position curr){
        char c = 'z';
        int p = 0, _p = 0;
        int n = 0, _n = 0;
        int b = 0, _b = 0;
        int r = 0, _r = 0;
        int q = 0, _q = 0;
        int k = 0, _k = 0;
        String str = curr.toString();
        int score = 0;
        boolean done = false;
        int i = 0;
        while(i < str.length() && !done){
            c = str.charAt(i);
            switch (c){
                case 'P':
                    p++;
                    break;
                case 'N':
                    n++;
                    break;
                case 'B':
                    b++;
                    break;
                case 'R':
                    r++;
                    break;
                case 'Q':
                    q++;
                    break;
                case 'K':
                    k++;
                    break;
                case 'p':
                    _p++;
                    break;
                case 'n':
                    _n++;
                    break;
                case 'b':
                    _b++;
                    break;
                case 'r':
                    _r++;
                    break;
                case 'q':
                    _q++;
                    break;
                case 'k':
                    _k++;
                    break;
                case ' ':
                    done = true;
                    break;
                default:
                    // do nothing
            }
            i++;
        }
        score = (_p - p) + 3*(_n - n + _b - b) + 5*(_r - r) + 9*(_q - q) + 200*(_k - k);
        return score;
    }

    public int Mobility(Position curr){

        return 0;
    }

    public int PawnEval(Position curr){


        return 0;
    }

//    public void readBook(){
//        LinkedTransferQueue<Game> openingBook;
//
//        URL url = this.getClass().getResource("book.pgn");
//
//        File f = new File(url.toURI());
//        FileInputStream fis = new FileInputStream(f);
//        pgnReader = new PGNReader(fis, "book.pgn");
//
////hack: we know there are only 120 games in the opening book
//        for (int i = 0; i < 120; i++)  {
//            Game g = new Game(pgnReader.parseGame());
//            openingBook.add(g);
//        }
//    }

}


//    public short AlphaBetaSearch(Position pos) throws IllegalMoveException {
//        int currV;
//        short bestMove = 0;
//        int a = NEGINF;
//        int b = INF;
//        Position temp = pos;
//        int bestV = NEGINF;
//
//        int v = MaxValue(pos, -1, MAXDEPTH, a, b);
//
//        for(int currMax = 0; currMax <= MAXDEPTH; currMax++) {
//            pos = temp;
//            a = NEGINF;
//            b = INF;
//            System.out.println("V: " + v + " at depth: " + currMax);
//
//            for (short currMove : pos.getAllMoves()) {
//                System.out.println("Move: " + currMove);
//                currV = MinValue(Result(pos, currMove), 0,  currMax, a, b);
//                pos.undoMove();
//                System.out.println("CurrV: " + currV);
//                if (currV == v) {
//                    if(currV > bestV) {
//                        bestV = currV;
//                        System.out.println("found V");
//                        bestMove = currMove;
//                    }
////                    return bestMove;
//                }
//            }
//
//        /**
//         * Some moves are not finding the best move ->
//         */
//        }
//        System.out.println("BEST MOVE: " + bestMove);
//        return bestMove;
//    }

//    public int MaxValue(Position curr, int depth, int currMax, int a, int b) throws IllegalMoveException {
//        if(cutoff(curr, depth, currMax)) {
////            System.out.println("MAX alpha: " + a + " beta: " + b);
//            int ut = UtilityFunc(curr);
//            return ut;
//        }
//        int v = NEGINF;
//        depth++;
//        for(short currMove : curr.getAllMoves()){
//            v = Math.max(v, MinValue(Result(curr, currMove), depth, currMax, a, b));
//            if(v >= b){
//                curr.undoMove();
//                return v;
//            }
//            curr.undoMove();
//            a = Math.max(a, v);
//        }
//        return v;
//    }
//
//    public int MinValue(Position curr, int depth, int currMax, int a, int b) throws IllegalMoveException {
//        if(cutoff(curr, depth, currMax)) {
////            System.out.println("MIN alpha: " + a + " beta: " + b);
//            int ut = UtilityFunc(curr);
//            return ut;
//        }
//        int v = INF;
//        depth++;
//        for(short currMove : curr.getAllMoves()){
//            v = Math.min(v, MaxValue(Result(curr, currMove),  depth, currMax, a, b));
//            if(v <= a){
//                curr.undoMove();
//                return v;
//            }
//            curr.undoMove();
//            b = Math.min(b, v);
//        }
//        return v;
//    }