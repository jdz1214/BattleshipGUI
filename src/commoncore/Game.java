package commoncore;

import server.ClientRunnable;
import server.Watchtower;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static commoncore.Game.Gamestate.youAreNotUp;

public class Game implements Runnable {
	private Watchtower w;
	private ClientRunnable p1;
	private ClientRunnable p2;
	private ClientRunnable winner;
	private Boolean gameOver;
	private ClientRunnable playerUp;
	private Gameboard gameboard;
	private Gamestate gamestate;
	public enum Gamestate { gameOn, gameOver, youAreUp, youAreNotUp, preGame }
	
	//Constructors
	public Game (ClientRunnable p1, ClientRunnable p2, Watchtower w) {
	    this.w = w;
		this.p1 = p1;
		this.p2 = p2;
        init();
		gameOver = false;
        //TODO gameboard development in progress.
		//gameboard = new Gameboard();
        determineFirst();
		gamestate = Gamestate.gameOn;
		run();
	}
	
	//Methods
	@Override
	public void run() {
        notifyPlayers();
	}

	private void init() {
	    p1.enterGameMode(this, p2.getUsername(), 1);
        p2.enterGameMode(this, p1.getUsername(), 2);
    }

	private void determineFirst() {
		//Choose random player to go first
		int rand = ThreadLocalRandom.current().nextInt(0, 2);
		int first = rand + 1;
        if (first == 1) {
            playerUp = p1;
        } else {
            playerUp = p2;
        }
	}
	
	public void determineGamestate () {
		int player1HitsRemaining = 0;
        Fleet p1Fleet = p1.getFleet();
        Fleet p2Fleet = p2.getFleet();
		int player2HitsRemaining = 0;
		for (int i = 0; i < p1Fleet.size(); i++) {
			Ship ship = p2Fleet.get(i);
			int hitsRemaining = ship.getHitsRemaining();
			player1HitsRemaining = player1HitsRemaining + hitsRemaining;
		}
		
		for (int i = 0; i < p2Fleet.size(); i++) {
			Ship ship = p2Fleet.get(i);
			int hitsRemaining = ship.getHitsRemaining();
			player2HitsRemaining = player2HitsRemaining + hitsRemaining;
		}
		
		if (player1HitsRemaining == 0 || player2HitsRemaining == 0) {
			gamestate = Gamestate.gameOver;
			winner = determineWinner(player1HitsRemaining, player2HitsRemaining);
		}
	}
	
	private ClientRunnable determineWinner(int player1HitsRemaining, int player2HitsRemaining) {
		winner = null;
		if (player1HitsRemaining == 0) {
			gamestate = Gamestate.gameOver;
			winner = p2;
		}
		else if (player2HitsRemaining == 0) {
			gamestate = Gamestate.gameOver;
			winner = p1;
		}
		return winner;
	}

	public void relayAttack(Attack attack, ClientRunnable attackingPlayer) {
	    if (attackingPlayer.equals(playerUp)) {




        }

    }

	private void notifyPlayers() {
	    ClientRunnable otherPlayer = (playerUp.equals(p1)) ? p2 : p1;
        try {
            playerUp.getObjectOutputStream().writeObject(new Transmission(new GameObject(Gamestate.youAreUp)));
            otherPlayer.getObjectOutputStream().writeObject(new Transmission(new GameObject(youAreNotUp)));
        } catch (IOException e) {System.out.println("Error writing client 'your turn' transmissions.");}
    }
	
	//Classes
	public class Gameboard extends ArrayList<String> {
		private static final long serialVersionUID = -543995990855427415L;
		private int defaultCols = 5;
		private int defaultRows = 5;
		private String defaultRow = "~~~~~";
		
		//Constructor
		public Gameboard() {}

		//Methods
		public Gameboard buildClientBoard(int playerNumber, Gameboard serverboard) {
		    //TODO needs rebuilding.
			Gameboard clientBoard = new Gameboard();
			clientBoard.resetClientBoard(clientBoard);
			if (playerNumber == 1) {
				for (int i = 0; i < 4; i++) {
					clientBoard.add(i, serverboard.get(i));
				}
			}
			else if (playerNumber == 2) {
				for (int i = 0; i < 4; i++) {
					clientBoard.add(i, serverboard.get(i + 5));
				}
			}
			return clientBoard;
		}

		public void resetClientBoard(Gameboard clientBoard) {
			for (int i = 0; i < 4; i++) {
				clientBoard.add(i, defaultRow);
			}
		}
	}
	
    public ClientRunnable getClientRunnable (String crUsername) {
        ClientRunnable cr = null;
        if (crUsername.equals(p1.getUsername())) {
            cr = p1;
        } else if (crUsername.equals(p2.getUsername())) {
            cr = p2;
        }
        assert cr!= null;
        return cr;
    }
	
	class Spot {
		private int x = 0;
		private int y = 0;
        private char chr = '~';
		//Constructors
		public Spot () {};
		public Spot (int x, int y) {
			this.x = x;
			this.y = y;
		}

		//Methods
		public int getX() {
			return x;
		}
		public int getY() {
			return y;
		}
	}
	
	public class Attack extends Spot {
		int targetedPlayerNumber;
		public Spot attackSpot;
		//Constructor
		public Attack () {}

		public Attack (Spot spotToAttack) throws IOException {
			Attack attack = new Attack();
			attack.setSpot(spotToAttack);
			this.attackSpot = spotToAttack;
			ClientRunnable targetedPlayer = (playerUp.equals(p1) ? p2 : p1);
			GameObject gameObject = new GameObject(attack);
			Transmission attackTransmission = new Transmission (gameObject);
			playerUp.getObjectOutputStream().writeObject(attackTransmission);
            playerUp.setMyTurn(false);
			playerUp = (playerUp.equals(p1) ? p2 : p1);
            notify();
            //TODO untested -- might not be working properly. Esp. the notify part.
		};
		
		public Spot getSpot() {
			return attackSpot;
		}
		
		public void setSpot(Spot attackSpot) {
			this.attackSpot = attackSpot;
		}
		
		public Spot convertFromUserView(ClientRunnable player, Spot spot) {
			if (player.getUsername().equals("p1" )) {
				Spot adjustedCoordinates = new Spot(spot.getX()+4, spot.getY()-1);
				return adjustedCoordinates;
			}
			else if (player.getUsername().equals("p2")) {
				Spot adjustedCoordinates = new Spot(spot.getX()-1, spot.getY()-1);
				return adjustedCoordinates;
			}
			else {
				System.out.println("Error adjusting spot coordinates");
				return null;
			}
		}
	}
	
	public class AttackResult {
		Boolean result = false;
		Boolean sunkShip = false;
		//Constructor
		public AttackResult() {}
		public AttackResult(Boolean wasShipHit, Boolean wasShipSunk) {
			this.result = wasShipHit;
			this.sunkShip = wasShipSunk;
		}
	}
	
	public class Fleet extends ArrayList<Ship> {
		private static final long serialVersionUID = 5959358336090731180L;
		//one fleet per player
		int numberOfShips = 4; //number of ships in fleet
		int playerNumber = -1;
		ArrayList<Spot> fleetLocations = new ArrayList<>();
		Fleet fleet;
		
		//Constructors
		public Fleet () {}
		public Fleet (int playerNumber) {
			this.playerNumber = playerNumber;
			generateShips();
			generateLocations(fleet, playerNumber);
		}
		
		//Methods
		public Fleet generateShips() {
			Fleet fleet = new Fleet();
			Ship ship1 = new Ship(2);
			Ship ship2 = new Ship(3);
			Ship ship3 = new Ship(4);
			Ship ship4 = new Ship(5);
			fleet.add(ship1);
			fleet.add(ship2);
			fleet.add(ship3);
			fleet.add(ship4);
			return fleet;
		}
		
		public AttackResult validateAttack(Attack attack) {
			AttackResult attackResult = new AttackResult();
			Spot attackSpot = attack.getSpot();
			Boolean repeatAttack = false;
			for (int i = 0; i < playerUp.getAttackHistory().size(); i++) {
				Spot pastAttackSpot = playerUp.getAttackHistory().get(i);
				if (pastAttackSpot.equals(attackSpot)) {
					repeatAttack = true;
				}
			}
			
			if (!repeatAttack) {
				for (int i = 0; i < fleetLocations.size(); i++) {
					Spot fleetSpot = fleetLocations.get(i);
					if (attackSpot.equals(fleetSpot)) {
						attackResult.result = true;
						for (int j = 0; j < fleet.size(); j++) {
							Ship ship = fleet.get(j);
							for (int k = 0; k < ship.locations.size(); k++) {
								Spot shipSpot = ship.locations.get(k);
								if (shipSpot.equals(attackSpot)) {
									int hitsLeft = ship.getHitsRemaining();
									if (hitsLeft == 1) {
										ship.hitsRemaining--;
										attackResult.sunkShip = true;
									}
								}
							}
						}
					}
				}
			}
			return attackResult;
		}
		
		public void generateLocations(Fleet fleet, int playerNumber) {
			ArrayList<Spot> spotPool = new ArrayList<>();
			ArrayList<Spot> fleetLocations = new ArrayList<>();
			
			for (int i = 0; i < 5; i++) { //Initialize spotPool
				for (int j = 0; j < 5; j++) {
					Spot newSpot = new Spot(i, j);
					spotPool.add(newSpot);
				}
			}
			for (int i = 0; i < fleet.size(); i++) { //Place each ship on the board
				Ship ship = fleet.get(i);
				spotPool = ship.placeShip(spotPool);
				if (playerNumber == 2) {
					for (int j = 0; j < ship.locations.size(); j++) {
						Spot spot = ship.locations.get(j);
						spot.x = spot.x+5;
					}
				}
			}
			for (int i = 0; i < fleet.size(); i++) { //Aggregate all ship spots into fleetLocations 
				Ship ship = fleet.get(i);
				for (int j = 0; j < ship.locations.size(); j++) {
					Spot spot = ship.locations.get(j);
					fleetLocations.add(spot);
				}
			}
		}
	}
	
	class Ship {
		int shipLength; //Ship length, in number of Spots. Must be greater than 1 && less than 5.
		int hitsRemaining;
		ArrayList<Spot> locations = null; //Each ship has the board coordinates it occupies as its location.
		//Constructor
		public Ship (int shipLength) {
			this.shipLength = shipLength;
			this.hitsRemaining = shipLength;
			locations = new ArrayList<Spot>(shipLength);
		}
		
		//Methods
		public int getShipLength() {
			return this.shipLength;
		}
		
		int getHitsRemaining() {
			return this.hitsRemaining;
		}
		
		ArrayList<Spot> placeShip(ArrayList<Spot> spotPool) {
			ArrayList<Spot> updatedSpotPool = spotPool;
			String alignment;
			ArrayList<Spot> tempLocations = new ArrayList<>();
			ArrayList<Spot> rowSpots = new ArrayList<>();
			ArrayList<Spot> rowGroup = new ArrayList<>();
			ArrayList<Spot> colSpots = new ArrayList<>();
			ArrayList<Spot> colGroup = new ArrayList<>();
			ArrayList<ArrayList<Spot>> Groups = new ArrayList<>(2);
			Boolean cannotFitInColumn = true;
			Boolean cannotFitInRow = true;
			Random rand = new Random();
			int alignmentRand = rand.nextInt(2);
			if (alignmentRand == 0) {
				alignment = "h";
			} else {alignment = "v";}
			
			
			//Choose ship spots
			if (alignment.equals("h")) {
				int leftMostColumn = -1;
				while (cannotFitInRow) {
					int nextRow = rand.nextInt(5);
					for (int i = 0; i < spotPool.size(); i++) { //Put all row spots into single arraylist
						Spot spot = spotPool.get(i);
						if (spot.y == nextRow) {
							rowSpots.add(spot);
						}
					}
					//Break rowSpots into contiguous Groups
					for (int i = 0; i < rowSpots.size(); i++) {
						Spot rowSpot = rowSpots.get(i);
						
						if (rowGroup.size() == 0) {
							rowGroup.add(rowSpot);
							Groups.add(rowGroup);
						}
						else {
							for (int j = 0; j < Groups.size(); j++) {
								ArrayList<Spot> thisGroup = Groups.get(i);
								for (int k = 0; k < thisGroup.size(); k++) {
									Spot thisGroupSpot = thisGroup.get(k);
									if (Math.abs(rowSpot.getX() - thisGroupSpot.getX()) == 1) {
										thisGroup.add(rowSpot);
									}
									else { //If no existing group has any x-coordinate contiguous with the current rowSpot, put rowSpot in a new group.
										ArrayList<Spot> newGroup = new ArrayList<>();
										newGroup.add(rowSpot);
										Groups.add(newGroup);
									}
								}
							}
						}
					} //Now, Groups contains all groups of contiguous spots. Time to eliminate those less than shipLength in size.
					for (int i = 0; i < Groups.size(); i++) {
						ArrayList<Spot> thisGroup = Groups.get(i);
						if (thisGroup.size() < shipLength) {
							Groups.remove(i);
							i--;
						}
					}
					
					if (Groups.size() == 0) {continue;} //This row does not have a contiguous group of sufficient size for this ship.
					
					if (Groups.size() > 1) { //Now, Groups contains only contiguous groups of spots that could fit this ship. If more than one, time to choose randomly.
						int pickGroup = rand.nextInt(Groups.size()); //will return an index, because it will only return up to Groups.size()-1.
						ArrayList<Spot> chosenGroup = Groups.get(pickGroup);
						for (int i = 0; i < chosenGroup.size(); i++) {
							Spot pickedSpot = chosenGroup.get(i);
							tempLocations.add(pickedSpot);
						}
					} else if (Groups.size() == 1) { //This means there was only one contiguous group remaining.
						ArrayList<Spot> chosenGroup = Groups.get(0);
						for (int j = 0; j < chosenGroup.size(); j++) {
							Spot pickedSpot = chosenGroup.get(j);
							tempLocations.add(pickedSpot);
						}
					}
					
					if (tempLocations.size() >= shipLength) {
						cannotFitInRow = false;
					} else {System.out.println("Might want to check your looping and logic in placeShip for Horizontal Ship Alignment.");}
				}
				//Now we have the contiguous group of spots in this row. Must determine the left-most starting point for the column.
				if (tempLocations.size() == shipLength) { //No determining necessary
					this.locations = tempLocations;
					for (int i = 0; i < locations.size(); i++) {
						Spot tempSpot = locations.get(i);
						for (int j = 0; j < updatedSpotPool.size(); j++) {
							Spot spotPoolSpot = updatedSpotPool.get(j);
							if (spotPoolSpot == tempSpot) {
								updatedSpotPool.remove(j);
								j--;
							}
						}
					}
				}
				else {
					leftMostColumn = rand.nextInt(6 - shipLength); //6** required to get correct col positions b/c req at least 1 to get col position 0.
					for (int i = leftMostColumn; i < tempLocations.size(); i++) {
						while (locations.size() < shipLength) {
							Spot spotToAdd = tempLocations.get(i);
							if (spotToAdd.getX() >= leftMostColumn && spotToAdd.getX() <= (leftMostColumn + shipLength-1)) { //accounts for size vs column position
								locations.add(spotToAdd);
								for (int j = 0; j < updatedSpotPool.size(); j++) {
									Spot spotPoolSpot = updatedSpotPool.get(j);
									if (spotPoolSpot == spotToAdd) {
										updatedSpotPool.remove(j);
										j--;
									}
								}
							}
						}
					}
				}
			}
			if (alignment.equals("v")) {
				int bottomRow = -1;
				while (cannotFitInColumn) {
					int nextCol = rand.nextInt(5);
					for (int i = 0; i < spotPool.size(); i++) { //Put all row spots into single arraylist
						Spot spot = spotPool.get(i);
						if (spot.x == nextCol) {
							colSpots.add(spot);
						}
					}
					//Break colSpots into contiguous Groups
					for (int i = 0; i < colSpots.size(); i++) {
						Spot colSpot = colSpots.get(i);
						
						if (colGroup.size() == 0) {
							colGroup.add(colSpot);
							Groups.add(colGroup);
						}
						else {
							for (int j = 0; j < Groups.size(); j++) {
								ArrayList<Spot> thisGroup = Groups.get(i);
								for (int k = 0; k < thisGroup.size(); k++) {
									Spot thisGroupSpot = thisGroup.get(k);
									if (Math.abs(colSpot.getY() - thisGroupSpot.getY()) == 1) {
										thisGroup.add(colSpot);
									}
									else { //If no existing group has any x-coordinate contiguous with the current colSpot, put colSpot in a new group.
										ArrayList<Spot> newGroup = new ArrayList<>();
										newGroup.add(colSpot);
										Groups.add(newGroup);
									}
								}
							}
						}
					} //Now, Groups contains all groups of contiguous spots. Time to eliminate those less than shipLength in size.
					for (int i = 0; i < Groups.size(); i++) {
						ArrayList<Spot> thisGroup = Groups.get(i);
						if (thisGroup.size() < shipLength) {
							Groups.remove(i);
							i--;
						}
					}
					
					if (Groups.size() == 0) {continue;} //This row does not have a contiguous group of sufficient size for this ship.
					
					if (Groups.size() > 1) { //Now, Groups contains only contiguous groups of spots that could fit this ship. If more than one, time to choose randomly.
						int pickGroup = rand.nextInt(Groups.size()); //will return an index, because it will only return up to Groups.size()-1.
						ArrayList<Spot> chosenGroup = Groups.get(pickGroup);
						for (int i = 0; i < chosenGroup.size(); i++) {
							Spot pickedSpot = chosenGroup.get(i);
							tempLocations.add(pickedSpot);
						}
					} else if (Groups.size() == 1) { //This means there was only one contiguous group remaining.
						ArrayList<Spot> chosenGroup = Groups.get(0);
						for (int j = 0; j < chosenGroup.size(); j++) {
							Spot pickedSpot = chosenGroup.get(j);
							tempLocations.add(pickedSpot);
						}
					}
					
					if (tempLocations.size() >= shipLength) {
						cannotFitInColumn = false;
					} else {System.out.println("Might want to check your looping and logic in placeShip for Horizontal Ship Alignment.");}
				}
				//Now we have the contiguous group of spots in this row. Must determine the left-most starting point for the column.
				if (tempLocations.size() == shipLength) { //No determining necessary
					this.locations = tempLocations;
					for (int i = 0; i < locations.size(); i++) {
						Spot tempSpot = locations.get(i);
						for (int j = 0; j < updatedSpotPool.size(); j++) {
							Spot spotPoolSpot = updatedSpotPool.get(j);
							if (spotPoolSpot == tempSpot) {
								updatedSpotPool.remove(j);
								j--;
							}
						}
					}
				}
				else {
					bottomRow = rand.nextInt(6 - shipLength); //**6 required to get correct col positions b/c req at least 1 to get col position 0.
					for (int i = bottomRow; i < tempLocations.size(); i++) {
						while (locations.size() < shipLength) {
							Spot spotToAdd = tempLocations.get(i);
							if (spotToAdd.getY() >= bottomRow && spotToAdd.getY() <= (bottomRow + shipLength-1)) { //accounts for size vs column position
								locations.add(spotToAdd);
								for (int j = 0; j < updatedSpotPool.size(); j++) {
									Spot spotPoolSpot = updatedSpotPool.get(j);
									if (spotPoolSpot == spotToAdd) {
										updatedSpotPool.remove(j);
										j--;
									}
								}
							}
						}
					}
				}
			}
			else {System.out.println("Error determining ship alignment (horizontal or vertical).");}
			
			return updatedSpotPool;
		}
	}

	public class History extends ArrayList<Spot>{

		private static final long serialVersionUID = 6046904576120105528L;
		History history;
		
		//Constructor
		public History () {
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {

				}
			}
		}
	}
}