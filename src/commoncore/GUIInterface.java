package commoncore;

import javax.swing.JTextField;

import commoncore.Game.Attack;
import commoncore.Game.Gameboard;

public interface GUIInterface {

	public void 			drawBoard(Gameboard board);
	public void 			printToChat(String chatMessage);
	public String 		getText(JTextField inputField);
	public void 			attackMode();
	public void 			regularMode();
	public void 			enterGameMode();
	public Attack		parseAttack();
	public Transmission 	gameRequest(String opponentUsername);
}
