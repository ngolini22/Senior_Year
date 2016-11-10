package chai;

import chesspresso.position.Position;

import java.util.Random;

public class RandomAI implements ChessAI {
	public short getMove(Position position) throws InterruptedException {
//		TimeUnit.SECONDS.sleep(20);
		short [] moves = position.getAllMoves();
		short move = moves[new Random().nextInt(moves.length)];
	
		return move;
	}
}
