package client;

import commoncore.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static commoncore.Ship.shipName.BIGFIVER;
import static commoncore.Ship.shipName.FOUR;


public class GameController implements Initializable {
    @FXML private TitledPane titledPane;
    @FXML private Label lblAttackHistory;
    @FXML private Label lblYourFleet;
    @FXML private Label lblInfo;
    @FXML private GridPane gridAttackHistory;
    @FXML private GridPane gridFleet;
    @FXML private TextArea txtGameChat;
    @FXML private TextField txtInput;
    @FXML private Button btnQuit;
    @FXML private Button btnAttack;
    @FXML private Button gridHistory00;
    @FXML private Button gridHistory01;
    @FXML private Button gridHistory02;
    @FXML private Button gridHistory03;
    @FXML private Button gridHistory04;
    @FXML private Button gridHistory05;
    @FXML private Button gridHistory10;
    @FXML private Button gridHistory11;
    @FXML private Button gridHistory12;
    @FXML private Button gridHistory13;
    @FXML private Button gridHistory14;
    @FXML private Button gridHistory15;
    @FXML private Button gridHistory20;
    @FXML private Button gridHistory21;
    @FXML private Button gridHistory22;
    @FXML private Button gridHistory23;
    @FXML private Button gridHistory24;
    @FXML private Button gridHistory25;
    @FXML private Button gridHistory30;
    @FXML private Button gridHistory31;
    @FXML private Button gridHistory32;
    @FXML private Button gridHistory33;
    @FXML private Button gridHistory34;
    @FXML private Button gridHistory35;
    @FXML private Button gridHistory40;
    @FXML private Button gridHistory41;
    @FXML private Button gridHistory42;
    @FXML private Button gridHistory43;
    @FXML private Button gridHistory44;
    @FXML private Button gridHistory45;
    @FXML private Button gridHistory50;
    @FXML private Button gridHistory51;
    @FXML private Button gridHistory52;
    @FXML private Button gridHistory53;
    @FXML private Button gridHistory54;
    @FXML private Button gridHistory55;
    @FXML private ArrayList<Button> gridAttackHistoryBtnList;
    @FXML private ArrayList<Text> gridFleetBtnList;
    @FXML private Text gridFleet00;
    @FXML private Text gridFleet01;
    @FXML private Text gridFleet02;
    @FXML private Text gridFleet03;
    @FXML private Text gridFleet04;
    @FXML private Text gridFleet05;
    @FXML private Text gridFleet10;
    @FXML private Text gridFleet11;
    @FXML private Text gridFleet12;
    @FXML private Text gridFleet13;
    @FXML private Text gridFleet14;
    @FXML private Text gridFleet15;
    @FXML private Text gridFleet20;
    @FXML private Text gridFleet21;
    @FXML private Text gridFleet22;
    @FXML private Text gridFleet23;
    @FXML private Text gridFleet24;
    @FXML private Text gridFleet25;
    @FXML private Text gridFleet30;
    @FXML private Text gridFleet31;
    @FXML private Text gridFleet32;
    @FXML private Text gridFleet33;
    @FXML private Text gridFleet34;
    @FXML private Text gridFleet35;
    @FXML private Text gridFleet40;
    @FXML private Text gridFleet41;
    @FXML private Text gridFleet42;
    @FXML private Text gridFleet43;
    @FXML private Text gridFleet44;
    @FXML private Text gridFleet45;
    @FXML private Text gridFleet50;
    @FXML private Text gridFleet51;
    @FXML private Text gridFleet52;
    @FXML private Text gridFleet53;
    @FXML private Text gridFleet54;
    @FXML private Text gridFleet55;
    private Main m;
    private String opponentUsername;
    private String attackSelectionId;
    private Button attackSelectionBtn;
    private Boolean attackSelected;
    private Fleet fleet;
    private ArrayList<Spot> spotPool;


    @FXML
    public void youAreUp() {
        for (Button b : gridAttackHistoryBtnList) { b.setDisable(false);}
        btnAttack.setDisable(false);
        lblInfo.setText("You are up!");
    }

    @FXML
    public void youAreNotUp() {
        for (Button b : gridAttackHistoryBtnList) { b.setDisable(true); }
        btnAttack.setDisable(true);
        lblInfo.setText("Awaiting opponent's move.");
    }

    @FXML
    private void attackSelection(ActionEvent event) {
        if (attackSelected == false) {
            Button thisBtn = (Button) event.getSource();
            attackSelectionBtn = thisBtn;
            attackSelectionId = thisBtn.getId();
            System.out.println(thisBtn.getId());
            gridAttackHistoryBtnList.stream().filter(btn -> btn.getId() != thisBtn.getId()).forEach(btn -> {
                btn.setDisable(true);
            });
            attackSelected = true;
        } else {
            attackSelectionId = null;
            attackSelected = false;
            for (Button b : gridAttackHistoryBtnList) {
                b.setDisable(false);
            }
        }
    }

    @FXML
    public void disableGrid () {
        gridAttackHistoryBtnList.stream().forEach(btn -> {
            btn.setDisable(true);
        });
    }

    @FXML
    public void enableGrid () {
        gridAttackHistoryBtnList.stream().filter(btn -> btn.getText().equals('~')).forEach(btn -> {
            btn.setDisable(false);
        });
    }

    @FXML
    public void processHit() {
        attackSelectionBtn.setText("X");
    }

    @FXML
    public void processMiss() {
        attackSelectionBtn.setText("O");
    }

    @FXML
    private void quit() {
        m.showGUI();
    }

    @FXML
    private void onEnter() {
        sendMessage();
    }

    @FXML
    void init(Main m, String opponentUsername) {
        this.m = m;
        titledPane.setText("Battleship - Game: " + m.getUsername());
        lblAttackHistory.setText("Attack History vs. " + opponentUsername);
        btnAttack.setDisable(true);
    }

    @FXML
    public void updateHistory(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String btnName = btn.getId();
        //Todo
    }

    @FXML
    private void sendMessage() {
        String msg = txtInput.getText();
        if (msg.length() > 0) {
            m.send(msg);
            txtInput.setText("");
            updateChat(m.getUsername() + ": " + msg);
        }
    }

    @FXML
    void updateChat(String msg) {
        txtGameChat.appendText(msg + "\n");
    }

    @FXML
    void disableChat() {
        txtInput.setEditable(false);
        txtInput.setDisable(true);
    }

    @FXML
    public void attack(ActionEvent actionEvent) {
        if (attackSelected) {
            // TODO get x and y
            int x = Integer.parseInt((attackSelectionId.substring(11,12)));
            int y = Integer.parseInt((attackSelectionId.substring(12,13)));
            String z = attackSelectionBtn.getText().substring(0,1);
            System.out.println("x: " + x + " y: " + y + " z: " + z);
            Spot attackSpot = new Spot(x, y, z);
            Attack attack = new Attack(attackSpot);
            m.send(new Transmission(new GameObject(attack)));
            disableGrid();
            lblInfo.setText("Attack Sent");
        }
    }

    @FXML
    public void updateInfo(String infoText) {
        lblInfo.setText(infoText);
    }

    @FXML
    public ArrayList<Spot> getSpotPool (ArrayList<Button> buttonList) {
        ArrayList<Spot> spotList = new ArrayList<>();
        for (Button b : buttonList) {
            String id = b.getId();
            String substring = id.substring(Math.max(id.length() - 2, 0));
            int x = substring.charAt(0);
            int y = substring.charAt(1);
            String txt = b.getText();
            if (txt.length() != 1) {
                System.out.println("Button length error.");
            } else {
                String z = b.getText().substring(0,1);
                spotList.add(new Spot(x, y, z));
            }
        }
        return spotList;
    }

    //Generate ship locations
    @FXML
    public void placeShips() {
        Fleet fleet = new Fleet();
        Boolean fiveDone = false;
        Boolean fourDone = false;
        Boolean threeDone = false;
        Boolean twoDone = false;
        //Grid 0,0 is top left-most corner.
        //First, place the largest (5) ship locations. Use these locations in a list to instantiate the new ship.
        //Determine orientation (vertical or horizontal).
        if (!fiveDone) {
            int orientation = (Math.random() < 0.5) ? 0 : 1;
            System.out.println("Orientation of bigfiver: " + orientation);
            if (orientation == 0) { //Horizontal
                int row = randGenerator(0, 5);
                int leftMostColumn = randGenerator(0, 1);
                ArrayList<Spot> shipLocations = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    String ss = String.valueOf(row) + String.valueOf(leftMostColumn + i);
                    System.out.println("ss horiz: " + ss);
                    for (Text txt : gridFleetBtnList) {
                        if (txt.getId().substring(9, 11).equals((ss))) {
                            txt.setText("5");
                            Spot newSpot = new Spot(Integer.parseInt(ss.substring(0, 1)), Integer.parseInt(ss.substring(1, 2)), "5");
                            shipLocations.add(newSpot);
                            newSpot.printMe();
                        }
                    }
                    if (shipLocations.size() == 5) {
                        Ship bigFive = new Ship(BIGFIVER, 5);
                        bigFive.setLocations(shipLocations);
                        bigFive.setOrientation(orientation);
                        fleet.add(bigFive);
                        fiveDone = true;
                        System.out.println("Bigfiver done.");

                    }
                }
            } else { //Vertical
                int column = randGenerator(0, 5);
                int lowestRow = randGenerator(0, 1);
                ArrayList<Spot> shipLocations = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    String ss = String.valueOf(lowestRow + i) + String.valueOf(column);
                    System.out.println("ss vert: " + ss);
                    for (Text txt : gridFleetBtnList) {
                        if (txt.getId().substring(9, 11).equals(ss)) {
                            txt.setText("5");
                            Spot newSpot = new Spot(Integer.parseInt(ss.substring(0, 1)), Integer.parseInt(ss.substring(1, 2)), "5");
                            shipLocations.add(newSpot);
                            newSpot.printMe();
                        }
                    }
                    if (shipLocations.size() == 5) {
                        Ship bigFive = new Ship(BIGFIVER, 5);
                        bigFive.setLocations(shipLocations);
                        bigFive.setOrientation(orientation);
                        fleet.add(bigFive);
                        fiveDone = true;
                        System.out.println("Bigfiver done.");
                    }
                }
            }
        }

        // Fours

        if (!fourDone) {
            int shipLength = 4;
            ArrayList<Spot> unplaceables = fleet.getUnplaceableLocations();
            ArrayList<ArrayList<Spot>> contiguousSections = new ArrayList<>();


            int orientation = (Math.random() < 0.5) ? 0 : 1;
            System.out.println("Orientation of FOUR: " + orientation);

            if (orientation == 0) { //Horizontal
                Boolean sufficientSpaceInRow = false;
                while(!sufficientSpaceInRow) {
                    int chosenRow = randGenerator(0, 5);
                    int contiguous = 1; // Must be one because a spot is always at least contiguous with itself, and I check for unplaceable spots.
                    contiguousSections = new ArrayList<>();
                    ArrayList<Spot> chosenRowSpots = new ArrayList<>();
                    for (int i = 0; i < 5; i++) { // Populate chosenRowSpots with all spots in the chosen row.
                        for (Text txt : gridFleetBtnList) {
                            Spot spot = new Spot(txt);
                            if (spot.getRow() == chosenRow && spot.getCol() == i) {
                                chosenRowSpots.add(spot);
                            }
                        }
                    }

                    //Remove any unplaceables from chosenRowSpots.
                    for (Spot spot : chosenRowSpots) {
                        if (unplaceables.contains(spot)) {
                            chosenRowSpots.remove(spot);
                        }
                    }

                    //Check for contiguous spots
                    String previous = chosenRowSpots.get(0).getRowColStr();
                    ArrayList<Spot> thisSection = new ArrayList<>();
                    for (int i = 0; i < chosenRowSpots.size(); i++) {
                        Spot spot = chosenRowSpots.get(i);
                        String current = spot.getRowColStr();
                        if (current.equals(previous)) {
                            if (chosenRowSpots.size() - i <= 0) {
                                break;
                            } else {
                                continue;
                            }
                        } else {
                            if (Math.abs((((Integer.parseInt(current) - Integer.parseInt(previous))))) == 1) {
                                contiguous += 1;
                                thisSection.add(spot);
                                previous = current;
                            } else {
                                if (contiguous >= shipLength) {
                                    contiguousSections.add(thisSection);
                                    System.out.println("Added contiguous section near line 361 in GameController.");
                                    thisSection = new ArrayList<>();
                                    contiguous = 1;
                                    if ((chosenRowSpots.size() - i) >= shipLength) {
                                        previous = chosenRowSpots.get(i + 1).getRowColStr();
                                        i++;
                                    } else {
                                        break;
                                    }
                                } else {
                                    thisSection = new ArrayList<>();
                                    contiguous = 1;
                                    previous = current;
                                }
                            }
                        }
                    }
                    if (contiguousSections.size() > 0) {
                        sufficientSpaceInRow = true;
                    }
                }
                //Place ship somewhere in available space
                System.out.println("About to place FOUR ship at line 401.");
                ArrayList<Spot> csec = new ArrayList<>();
                if (contiguousSections.size() > 1) {
                    csec = contiguousSections.get(randGenerator(0, contiguousSections.size()));
                } else if (contiguousSections.size() == 1) {
                        csec = contiguousSections.get(0);
                } else { System.out.println("Error setting up ship placement: wrong contiguousSection size. Line 402 of GameController."); }

                if (csec.size() == shipLength) {
                    Ship ship = new Ship(FOUR, 4);
                    ship.setOrientation(orientation);
                    ship.setLocations(csec);
                    fleet.add(ship);
                    fourDone = true;
                } else { //Choose leftmost spot from possible frame (uppermost for vertical implementation)
                    int startingSpotNum = randGenerator(0, csec.size()-shipLength);
                    ArrayList<Spot> locations = new ArrayList<>();
                    for (int i = 0; i < shipLength; i++) {
                        locations.add(csec.get(startingSpotNum + i));
                    }
                    assert locations.size() == shipLength;
                    Ship ship = new Ship(FOUR, 4);
                    ship.setOrientation(orientation);
                    ship.setLocations(locations);
                    fleet.add(ship);
                    fourDone = true;
                }
                for (Text txt : gridFleetBtnList) {
                    if (fleet.getShipByName(FOUR).getShipLocations().contains(txt)) {
                        txt.setText("4");
                    }
                }
            } else { //Vertical
                System.out.println("Vertical orientation of FOUR (line 434)");
                Boolean sufficientSpaceInCol = false;
                while(!sufficientSpaceInCol) {
                    System.out.println("calculating column (line 437).");
                    int chosenCol = randGenerator(0, 5);
                    int contiguous = 1; // Must be one because a spot is always at least contiguous with itself, and I check for unplaceable spots.
                    contiguousSections = new ArrayList<>();
                    ArrayList<Spot> chosenColSpots = new ArrayList<>();
                    for (int i = 0; i < 5; i++) { // Populate chosenRowSpots with all spots in the chosen row.
                        for (Text text : gridFleetBtnList) {
                            Spot spot = new Spot(text);
                            if (spot.getCol() == chosenCol && spot.getRow() == i) {
                                chosenColSpots.add(spot);
                            }
                        }
                    }
                    System.out.println("Line 450.");
                    //Remove any unplaceables from chosenRowSpots.
                    for (Spot spot : chosenColSpots) {
                        if (unplaceables.contains(spot)) {
                            chosenColSpots.remove(spot);
                        }
                    }
                    System.out.println("Line 457.");
                    //Check for contiguous spots
                    String previous = chosenColSpots.get(0).getRowColStr();
                    ArrayList<Spot> thisSection = new ArrayList<>();
                    for (int i = 0; i < chosenColSpots.size(); i++) {
                        System.out.println("Chosen spot " + i);
                        Spot spot = chosenColSpots.get(i);
                        String current = spot.getRowColStr();
                        if (current.equals(previous)) {
                            if (chosenColSpots.size() - i <= 0) {
                                break;
                            } else {
                                continue;
                            }
                        } else {
                            System.out.println("Line 472.");
                            if (Math.abs((((Integer.parseInt(current) - Integer.parseInt(previous))))) == 10) { // 10 Because of row digit changing
                                contiguous += 1;
                                thisSection.add(spot);
                                previous = current;
                            } else {
                                if (contiguous >= shipLength) {
                                    contiguousSections.add(thisSection);
                                    System.out.println("Added contiguous section near line 436 in GameController.");
                                    thisSection = new ArrayList<>();
                                    contiguous = 1;
                                    if ((chosenColSpots.size() - i) >= shipLength) {
                                        previous = chosenColSpots.get(i + 1).getRowColStr();
                                        i++;
                                    } else {
                                        break;
                                    }
                                } else {
                                    thisSection = new ArrayList<>();
                                    contiguous = 1;
                                    previous = current;
                                }
                            }
                        }
                    }
                    System.out.println(contiguousSections.size());
                    if (contiguousSections.size() > 0) {
                        sufficientSpaceInCol = true;
                    }
                }
                //Place ship somewhere in available space
                ArrayList<Spot> csec = new ArrayList<>();
                if (contiguousSections.size() > 1) {
                    csec = contiguousSections.get(randGenerator(0, contiguousSections.size()));
                } else if (contiguousSections.size() == 1) {
                    csec = contiguousSections.get(0);
                } else { System.out.println("Error setting up ship placement: wrong contiguousSection size. Line 402 of GameController."); }

                if (csec.size() == shipLength) {
                    Ship ship = new Ship(FOUR, 4);
                    ship.setOrientation(orientation);
                    ship.setLocations(csec);
                    fleet.add(ship);
                    fourDone = true;
                } else { //Choose leftmost spot from possible frame (uppermost for vertical implementation)
                    int startingSpotNum = randGenerator(0, csec.size()-shipLength);
                    ArrayList<Spot> locations = new ArrayList<>();
                    for (int i = 0; i < shipLength; i++) {
                        locations.add(csec.get(startingSpotNum + i));
                    }
                    assert locations.size() == shipLength;
                    Ship ship = new Ship(FOUR, 4);
                    ship.setOrientation(orientation);
                    ship.setLocations(locations);
                    fleet.add(ship);
                    fourDone = true;

                }
                for (Text text : gridFleetBtnList) {
                    Spot spot = new Spot(text);
                    if (fleet.getShipByName(FOUR).getShipLocations().contains(spot)) {
                        getTextFromSpot(spot).setText("4");
                    }
                }
            }
        }
    }

    public Text getTextFromSpot(Spot spot) {
        Text txt = null;
        String id = spot.getRowColStr();
        for (Text t : gridFleetBtnList) {
            if (t.getId().substring(9,11).equals(id)) {
                txt = t;
            }
        }
        assert (txt!=null);
        return txt;
    }

    private int randGenerator(int min, int max) {
        Random rand = new Random();
        int nextRand = rand.nextInt((max-min) +1) + min;
        return nextRand;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        attackSelected = false;
        gridAttackHistoryBtnList = gridAttackHistory.getChildren().stream().filter(btnNode -> btnNode instanceof Button).map(btnNode -> (Button) btnNode).collect(Collectors.toCollection(ArrayList::new));
        gridFleetBtnList = gridFleet.getChildren().stream().filter(txtNode -> txtNode instanceof Text).map(txtNode -> (Text) txtNode).collect(Collectors.toCollection(ArrayList::new));
        for (Text t : gridFleetBtnList) { t.setDisable(true);  t.setText("~");}
        for (Button b : gridAttackHistoryBtnList) { b.setDisable(true); b.setText("~");}
        System.out.println("About to execute placeShips.");
        placeShips();
    }


}