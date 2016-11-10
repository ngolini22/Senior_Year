package chai;

import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public interface ChessAI {
	public short getMove(Position position) throws IllegalMoveException, InterruptedException;
}
