package commoncore;

import commoncore.Game.Attack;
import commoncore.Game.Gameboard;

import javax.swing.*;

public interface GUIInterface {

	void 	drawBoard(Gameboard board);
	void 	printToChat(String chatMessage);
	String 	getText(JTextField inputField);
	void 	attackMode();
	void 	regularMode();
	void 	enterGameMode();
	Attack	parseAttack();
	Transmission 	gameRequest(String opponentUsername);
}
