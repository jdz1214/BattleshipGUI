package client;

import commoncore.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

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
    @FXML private List<Button> gridAttackHistoryBtnList;
    @FXML private List<Text> gridFleetBtnList;
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
    private int gridRowLength; //0 is a spot, so val of 5 means 6 spots. This setup enables direct iteration.
    private int gridColLength;

    @FXML
    void init(Main m, String opponentUsername, Fleet fleet) {
        this.m = m;
        titledPane.setText("Battleship - Game: " + m.getUsername());
        lblAttackHistory.setText("Attack History vs. " + opponentUsername);
        btnAttack.setDisable(true);
        attackSelected = false;
        gridRowLength = 5; //5 because it's 0-5, so 6 total.
        gridColLength = 5;
        this.fleet = fleet;
        gridAttackHistoryBtnList = gridAttackHistory.getChildren().stream()
                .filter(btnNode -> btnNode instanceof Button)
                .map(btnNode -> (Button) btnNode)
                .map(btn -> {
                    btn.setDisable(true);
                    btn.setText("~");
                    return btn;})
                .collect(Collectors.toCollection(ArrayList::new));
        gridAttackHistoryBtnList.stream()
                .map(button -> button.getId().substring(11,13));
        gridFleetBtnList = gridFleet.getChildren().stream()
                .filter(txtNode -> txtNode instanceof Text)
                .map(txtNode -> (Text) txtNode)
                .map(t -> {
                    t.setText("~");
                    t.setDisable(true);
                    return t;})
                .collect(Collectors.toCollection(ArrayList::new));
        placeShips(fleet);

        List<Integer> gridSizes = gridFleetBtnList.stream()
                .map(t -> t.getId().substring(9,11))
                .map(w -> Integer.parseInt(w))
                .collect(Collectors.toList());
        OptionalInt max = gridSizes.stream()
                .mapToInt(Integer::intValue).max();
        gridRowLength = (max.getAsInt()+11) / 10; //Adding 11 so that 55 becomes 66 -> corrects to .size() and .length() results from list positions.
        System.out.println("Maximum grid row length: " + gridRowLength);
        gridColLength = (max.getAsInt()+11) % 10;
        System.out.println("Maximum grid col length: " + gridColLength);
    }

    @FXML
    public void youAreUp() {
        for (Button b : gridAttackHistoryBtnList) { b.setDisable(false);}
        btnAttack.setDisable(false);
        lblInfo.setText("You are up!");
    }

    @FXML
    public void youAreNotUp() {
        disableGrid();
        btnAttack.setDisable(true);
        lblInfo.setText(lblInfo.getText() + " :: Awaiting opponent's move.");
    }

    @FXML
    private void attackSelection(ActionEvent event) {
        if (attackSelected) {
            attackSelectionId = null;
            attackSelected = false;
            enableGrid();
        } else {
            attackSelectionBtn = (Button) event.getSource();
            attackSelectionId = attackSelectionBtn.getId();
            System.out.println("Attack selection ID: " + attackSelectionId);
            attackSelected = true;
            disableGrid(attackSelectionId);
        }
    }

    @FXML
    public void disableGrid () {
        gridAttackHistoryBtnList.stream().forEach(btn -> {
            btn.setDisable(true);
        });
    }

    @FXML
    public void disableGrid (String disableAllExceptThisBtnID) {
        gridAttackHistoryBtnList.stream()
                .filter(btn -> btn.getId() != disableAllExceptThisBtnID)
                .forEach(btn -> btn.setDisable(true));
    }

    @FXML
    public void enableGrid () {
        gridAttackHistoryBtnList.stream().filter(btn -> btn.getText().equals("~"))
                .forEach(btn -> btn.setDisable(false));
    }

    @FXML
    public void processAttackResult(AttackResult attackResult) {
        System.out.println("In gameController method handling attackResult.");
        gridAttackHistoryBtnList.stream()
                .filter(btn -> btn.getId().substring(11,13).equals(attackResult.getSpotRowCol()))
                .map(btn -> {if (attackResult.wasHit()) { btn.setText("X");} else {btn.setText("O");} return btn;});
        updateInfo(attackResult.wasHit() ? attackResult.sunkShip() ? ("You sunk their battleship!") : ("Your shot hit!") : ("Your shot missed!"));
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
    public void attack(ActionEvent event) {
        System.out.println("Attack button clicked.");
        if (attackSelected) {
            System.out.println("Attack selected --> transmitting attack.");
            String attackRowColStr = attackSelectionId.substring(11,13);
            m.send(new Transmission(new GameObject(new Attack(attackRowColStr))));
            disableGrid();
            System.out.println("Attack sent.");
            lblInfo.setText("Attack Sent");
        }
    }

    @FXML
    public AttackResult evaluateAttackReceived(Attack attackReceived) {
        return fleet.evaluateAttackReceived(attackReceived);
    }

    @FXML
    public void updateInfo(String infoText) {
        lblInfo.setText(infoText);
    }

    @FXML //Generate ship locations
    public void placeShips(Fleet fleet) {
        //Grid 0,0 is top left-most corner.
        //First, place the largest (5) ship locations. Use these locations in a list to instantiate the new ship.
        //Determine orientation (vertical or horizontal).
        List<Spot> allSpots = gridFleetBtnList.stream()
                .map(Spot::new)
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> spotHistory = new ArrayList<>();

        for (Ship s : fleet) {
            //Building allSpots

            //forEach row/col, loop the number of segments based on calculation from shipLength
            Map<Integer, List<Spot>> spotsByRow = new TreeMap<>(allSpots.stream().sorted((Spot s1, Spot s2) -> s1.getRow().compareTo(s2.getRow()))
                    .collect(Collectors.groupingBy(Spot::getRow))); //Return these as TreeMap so that it is sorted!
            Map<Integer, List<Spot>> spotsByCol = new TreeMap<>(allSpots.stream().sorted((Spot s1, Spot s2) -> s1.getCol().compareTo(s2.getCol()))
                    .collect(Collectors.groupingBy(Spot::getCol)));
            //Done building allSpots.

            //Building allSets
            List<Set<Spot>> allSets = new ArrayList<>();
            allSets.addAll(spotSetGenerator(spotsByRow, s));
            allSets.addAll(spotSetGenerator(spotsByCol, s));
            //Done building allSets.
            List<Set<Spot>> toRemove = new ArrayList<>();
            for (Set<Spot> spotSet : allSets) {
                for (Spot spot : spotSet) {
                    for (String rowColHistory : spotHistory) {
                        if (rowColHistory.equals(spot.getRowColStr())) {
                            toRemove.add(spotSet);
                            break;
                        }
                    }
                }
            }

            if (toRemove.size() > 0) {
                System.out.println("Removing " + toRemove.size() + " sets from allSpots.");
                allSets.removeAll(toRemove);
            }
            toRemove.clear();

            System.out.println("Placing ship of length " + s.getShipLength());
            //Randomly select a set for assignment to the ship's locations and remove it from allSets (no longer available).
            ArrayList<Spot> chosenLocations = allSets.get(randGenerator(0, allSets.size()-1)).stream()
                    .collect(Collectors.toCollection(ArrayList::new));
            assert chosenLocations.size() == s.getShipLength();
            s.setLocations(chosenLocations);
            spotHistory.addAll(chosenLocations.stream().map(spot -> spot.getRowColStr()).collect(Collectors.toSet()));
            //Ship locations are now set.

            //Marking locations with the ship number.
            s.getShipLocations().stream().forEach(spot -> spot.updateGrid(Integer.toString(s.getShipLength())));
            //Grid locations are now marked with the length of the ship.
        }
    }

    private List<Set<Spot>> spotSetGenerator(Map<Integer, List<Spot>> spotsMap, Ship ship) {
        List<Set<Spot>> returnList = new ArrayList<>();
        //We are going to receive a row or col of spots, create sets, add the sets to the list, and return the list.
        //Example with shipLength 5. How do we calculate how many times to iterate? 5 --> 2. Think of the equation.
        //spotsToOrganize must be sorted in order for this to work. Treemap + sorted in above method should guarantee this.

        for(Map.Entry<Integer, List<Spot>> e : spotsMap.entrySet()) {
            List<Spot> spotsToOrganizeIntoSets = e.getValue();
            int numSets = spotsToOrganizeIntoSets.size() + 1 - ship.getShipLength(); //+1 because size will return 6 for 6 spots, and 7-5==2 which is what we need for shipLength of 5..
            assert numSets > 0;

            for (int i = 0; i < numSets; i++) {
                Set<Spot> newSet = new HashSet<>();
                for (int j = i; j < ship.getShipLength() + i; j++) { //This is the key. j=i. Because the spot number we are on dictates the first spot location of the current set.
                    newSet.add(spotsToOrganizeIntoSets.get(j));
                }
                returnList.add(newSet);
            }
        }
        assert returnList.size() != 0;
        return returnList;
    }

    private int randGenerator(int min, int max) {
        Random rand = new Random();
        int nextRand = rand.nextInt((max-min) +1) + min;
        return nextRand;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}