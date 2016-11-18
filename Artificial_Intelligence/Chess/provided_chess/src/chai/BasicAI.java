package chai;

import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class BasicAI implements ChessAI {
    static int MAXDEPTH = 4;
    static int NEGINF = -10000;
    static int INF = 10000;
    static int RANDMOVE;
    int StatesVisited;
//    static short bestMove;

    public short getMove(Position position) throws IllegalMoveException {
        Position temp = position;
        short [] moves = position.getAllMoves();
        short choice = -1;
        RANDMOVE = moves.length;
        choice = MiniMax(temp);
        if(choice == -1){
            System.out.println("No move found");
            choice = 0;
        }
        position.doMove(choice);
        int val = UtilityFunc(position);
        System.out.println("Move Chosen: " + position + " Value: " + val);
        position.undoMove();

        short move = choice;

        return move;
    }

    public short MiniMax(Position pos) throws IllegalMoveException {
        int currV;
        int largestV = NEGINF;
        int i;
        short bestMove = 0;
        int currMax = 0;
        Position temp = pos;

        while(currMax <= MAXDEPTH) {
            StatesVisited = 0;
            i = 0;
            pos = temp;
            System.out.println("New max depth: " + currMax);
            for (short currMove : pos.getAllMoves()) {
                pos.doMove(currMove);
                if(i == 33)
                    System.out.println("");
                currV = MinValue(pos, 0, currMax);
                if (currV > largestV) {
                    largestV = currV;
                    bestMove = currMove;
                }
                System.out.println("CurrV: " + currV + " i: " + i + " curr depth: " + currMax + " MOVE: " + currMove + "Nodes visited: " + StatesVisited);
                if(currV == 10000)
                    System.out.println(pos);
                pos.undoMove();
                i++;
            }
            currMax++;
        }

        System.out.println("Best Move: " + bestMove);
        // returns the move corresponding to the best action
        return bestMove;
    }

    public int MaxValue(Position curr, int depth, int currMax) throws IllegalMoveException {
        StatesVisited++;
        if(cutoff(curr, depth, currMax)) {
            int ut = UtilityFunc(curr);
            return ut;
        }
        int v = NEGINF;
        depth++;
        for(short currMove : curr.getAllMoves()){
            v = Math.max(v, MinValue(Result(curr, currMove), depth, currMax));
            curr.undoMove();
        }
        return v;
    }

    public int MinValue(Position curr, int depth, int currMax) throws IllegalMoveException {
        StatesVisited++;
        if(cutoff(curr, depth, currMax)) {
            int ut = UtilityFunc(curr);
            return ut;
        }
        int v = INF;
        depth++;
        for(short currMove : curr.getAllMoves()){
            v = Math.min(v, MaxValue(Result(curr, currMove),  depth, currMax));
            curr.undoMove();
        }
        return v;
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

    public boolean kingCaptured(Position curr){
        String str = curr.toString();
        int i = 0;
        int k = 0;
        int _k = 0;
        boolean done = false;
        char c = 'z';
        while(i < str.length() && !done) {
            c = str.charAt(i);
            switch (c) {
                case ' ':
                    done = true;
                    break;
                case 'k':
                    k++;
                    break;
                case 'K':
                    _k++;
                    break;
                default:
                    // nothing
            }
        }

        // if one of the kings is missing
        if(k == 0 || _k == 0){
            return true;
        } else{
            return false;
        }
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
}


//        if(score >= 3) {
//            System.out.println(curr + " " + score + " " + _p + "bp" + _n + "bn" + _b + "bb" + _r + "br" + _q + "bq"+ p + "wp" + n + "wn" + b + "wb" + r + "wr" + q+ "wq");
//        }