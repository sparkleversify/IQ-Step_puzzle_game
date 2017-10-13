package comp1110.ass2.gui;

import comp1110.ass2.Alphabet;
import comp1110.ass2.SolverForHint;
import comp1110.ass2.StepsGame;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.math.BigDecimal;
import java.util.*;

import static comp1110.ass2.Alphabet.isPeg;
import static comp1110.ass2.StepsGame.notObstruct;

public class Board extends Application implements Runnable  {
    private static final int BOARD_WIDTH = 933;
    private static final int BOARD_HEIGHT = 700;
    private static final String URI_BASE = "assets/";
    private Effect blueshadow = new DropShadow(5, Color.SKYBLUE);

    TextField textField;
    /** message on completion */
    private final Text completionText = new Text("Good Job!");
    private final Text insText = new Text(" Press Q =>to=> Quit game\n\n Click '/' =>to=> Show next piece hint.\n\n Click '/' Again =>to=> Hide next piece hint. \n\n Press S =>to=> Show best score \n\n Mouse Double click =>to=> Flip piece\n\n Mouse scroll =>to=> Rotate.\n\n Slide the \"Difficulty\" =>to=> Change difficulty levels.\n\n Press \"Start\" =>to=> a new game. ");
    private Text timeUsing = new Text("Shows timeUsing");
    private final Slider difficulty = new Slider();
    private final DropShadow dropShadow = new DropShadow();

    private static String newstart = "";
    private static String pastplacement = "";

    private final Group root = new Group();
    private final Group controls = new Group();
    private static final Group pegs = new Group();
    private static final Group blankpegs = new Group();
    private static final Group letters = new Group();
    private final Group startpieces = new Group();
    private final Group invisibleSol = new Group();
    private int count = 0; //initialize

    private boolean invi = false;

    private final Group newpieces = new Group();

    private ArrayList<Peg> peglist = new ArrayList<>();
    private ArrayList<BlankPeg> blankpeglist = new ArrayList<>();

    private ArrayList<Double> diff_0 = new ArrayList<>();
    private ArrayList<Double> diff_1 = new ArrayList<>();
    private ArrayList<Double> diff_2 = new ArrayList<>();
    private ArrayList<Double> diff_3 = new ArrayList<>();
    //private ArrayList<Double> diff_4 = new ArrayList<>();

    private double startMilli = 0;
    private double endMilli = 0;
    private double useTime = 0;
    private BigDecimal UseTime;
    private String done = ""; // initialise the placed pieces

// the idea is suggested by Shenjia Ji (u5869805) and Henan Wang (u6007140)
// https://stackoverflow.com/questions/23202272/how-to-play-sounds-with-javafx
    private AudioClip begin = new AudioClip(getClass().getResource("res/begin~.mp3").toString());
    private AudioClip yohu = new AudioClip(getClass().getResource("res/yohu.mp3").toString());
    private AudioClip ouou = new AudioClip(getClass().getResource("res/ouoh-error.mp3").toString());
    private AudioClip win = new AudioClip(getClass().getResource("res/mar-success.mp3").toString());
    private AudioClip snap = new AudioClip(getClass().getResource("res/snap.mp3").toString());
    private AudioClip hints = new AudioClip(getClass().getResource("res/hints.mp3").toString());
    private AudioClip flip = new AudioClip(getClass().getResource("res/flip.mp3").toString());

    private static final String[][] TaskEleven_OBJECTIVE ={
            //diff_3
            {"HHnBFOGDL", "EEfHALAHS", "HFSGFlBDx", "EFBFCgBGS", "BFqHALAHS",
                    "EFBAFnGFS", "CHSGHnBGK", "DGSGHnBHF", "FBmBCoCEj", "HGnGAREBv",
                    "BGKFCNGFn", "FBgEElBEe", "BGKFCNCAg", "EFBAFnDHS", "GDLCGOEEn"},
            //diff_3
            {"FCLBFqEFjCCW", "AHSEHlBDxFBg", "EFBBFqCHSGHn", "BGSGHnDGQEEf", "EFBHAgDGSAHQ",
                    "BGSAHQEFBHAg", "BHFFCLHBNDFl", "BHFFCLHBNGGn", "BGSAHQEFBHFn", "EFBFCNCHSBBG",
                    "BFOGDLADgDFj", "EEfFBiCCLBGS", "HFSFDbEAoBHD", "DGSBGlEAoCEj", "HFSFDPBGKADg"},
            //diff_2
            {"BFOGDLADgDFjHEQ", "BHFFCLHBNGGnEDI", "EFBFCNCHSBBGADg", "BGKAFjGCNHAPCAg", "HFSFDbEAoBHDGDL",
                    "EFBAFjGCNHAPCAg", "FCLBFqDAWHEjEFF", "AEnCElFFSBHFDGj", "AHSEHlBDxFBgHCP", "FBmBCoCEjABRDCP",
                    "GDLCGOEEnADgDAi", "HHnBFOGDLADgDAi", "BGKFCNEEnDHSGEQ", "BFqEFlHDiAFnCCL", "GDLADgBGODAiHAk"},
            //diff_0
            {"AHSEHlBDxFBgHCPGBi", "EFBHAgDGSAHQCDNGHj", "DGSGHnBHFFDNCAkHCi", "EFBFCNCHSBBGADgHAi", "GDLADgBGODAiHAkCGc",
                    "BHFFCLHBNGGnEDIADg", "HHnBFOGDLADgDAiCCk", "BFqHALAHSFDPCDNDAi", "FBmBCoCEjABRDCPHCN", "CHSGHnBGKFCNADgHAi",
                    "HFSFDbEAoBHDGDLADg", "EEfFBiCCLBGSHANAHQ", "FCLBFqDAWHEjEFFCGl", "BHDHBLADgCGQDFlFEj", "EFBFCNDAgCEnAESGEQ"}
    }; // by Wenjun Yang

    @Override
    public void run() {
        try {
            new Board().start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }/* initialize : preparation for the pegs arrangement & original pieces' arrangement */


    /** Create pegs helper class */
    private static class Peg extends Circle {
        double x,y;
        Alphabet letter;
        Peg (int id){
            setRadius(12);//https://stackoverflow.com/questions/1104975/a-for-loop-to-iterate-over-an-enum-in-java
            for (Alphabet a : Alphabet.values())
                if (isPeg(id) && (a.getId() == id)){
                    letter = a;
                    setCenterX(pegmapX.get(id%10)+80);
                    x = pegmapX.get(id%10) ;
                    setCenterY(pegmapY.get(id/10));
                    y = pegmapY.get(id/10);
                    setStroke(Color.gray(0.6));
                    setFill(Color.gray(0.6));
            }
        }
    }

    private static class BlankPeg extends Circle {
        double x,y;
        Alphabet letter;
        BlankPeg(int id){
            setRadius(12);
            for(Alphabet a : Alphabet.values())
                if (!isPeg(id) && a.getId() == id){
                    letter = a;
                    setCenterX(pegmapX.get(id%10)+80);
                    x = pegmapX.get(id%10) ;
                    setCenterY(pegmapY.get(id/10));
                    y = pegmapY.get(id/10);
                    setStroke(Color.gray(0.8));
                    setFill(Color.gray(0.8));
                    setOpacity(0.1);
                }
        }
    }

//https://stackoverflow.com/questions/17437411/how-to-put-a-text-into-a-circle-object-to-display-it-from-circles-center
    /** Show pegs in the original state */
    private void makePegs() {
        //pane.getChildren().clear();
        pegs.getChildren().clear();
        for(int i = 0;i<=49;i++){
            if (isPeg(i)) {
                Peg onepeg = new Peg(i);
                //System.out.println("peg letter: "+onepeg.letter+" x-pos: "+onepeg.x+" y-pos: "+onepeg.y);
                peglist.add(onepeg);
                Label letter = new Label(onepeg.letter.toString());
                letter.setLayoutX(onepeg.x+73);
                letter.setLayoutY(onepeg.y-18);//https://www.dafont.com/top.php
                letter.setFont(Font.loadFont(MenuApp.class.getResource("res/Meatloaf.ttf").toExternalForm(), 30));
                //https://www.dafont.com/theme.php?cat=117
                // http://docs.oracle.com/javafx/2/text/jfxpub-text.htm
                letter.setTextFill(Color.WHITE);
                letter.setEffect(new DropShadow(30,Color.DEEPPINK));
                letters.getChildren().add(letter);
                letters.toFront();
                pegs.getChildren().add(onepeg);
            }
        }
    }

    private void makeBlankPegs(){
        blankpegs.getChildren().clear();
        for(int i = 0; i<=49;i++){
            if (!isPeg(i)){
                BlankPeg oneblankpeg = new BlankPeg(i);
                blankpeglist.add(oneblankpeg);
                Label letter = new Label(oneblankpeg.letter.toString());
                letter.setLayoutX(oneblankpeg.x+73);
                letter.setLayoutY(oneblankpeg.y-18);
                letter.setFont(Font.loadFont(MenuApp.class.getResource("res/Meatloaf.ttf").toExternalForm(), 30));
                letter.setTextFill(Color.WHITE);
                letter.setEffect(new DropShadow(30,Color.DEEPPINK));
                letters.getChildren().add(letter);
                letters.toFront();
                blankpegs.getChildren().add(oneblankpeg);
            }
        }
    }

//https://stackoverflow.com/questions/41246688/circle-wont-move-position-in-javafx/41247282
//https://stackoverflow.com/questions/21118394/explicitly-positioning-nodes-in-javafx
/** Assign the pieces' original coordinate */
    private static final Map<String,Integer> piecemapY;
    static {
        piecemapY = new HashMap<>();
        piecemapY.put("AA",50);
        piecemapY.put("AE",50);
        piecemapY.put("BA",190);
        piecemapY.put("BE",190);
        piecemapY.put("CA",330);
        piecemapY.put("CE",330);
        piecemapY.put("DA",470);
        piecemapY.put("DE",470);
        piecemapY.put("EA",50);
        piecemapY.put("EE",50);
        piecemapY.put("FA",190);
        piecemapY.put("FE",190);
        piecemapY.put("GA",330);
        piecemapY.put("GE",330);
        piecemapY.put("HA",470);
        piecemapY.put("HE",470);
    }



    /* Helpers function to show the images in assets folder */
    /** Traversal through the assets images and pick up the given piece to show */
    class Picture extends ImageView{
        String piece;
        Picture(String piece){
            for(String p : piecelist){
                if (p.equals(piece)){
                    setImage(new Image(Viewer.class.getResource(URI_BASE+piece+".png").toString()));
                    this.piece = piece;
                    setFitHeight(80);
                    setFitWidth(80);
                    break;}}}}

    /** Assign the coordinate for all pieces */
    class FlippableFXPiece extends Picture{
        double homeX;
        double homeY;
        FlippableFXPiece(String piece) {
            super(piece);//https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Node.html
            if (piecelist.contains(piece)){
            String char1 = String.valueOf(piece.charAt(0));
            homeY = piecemapY.get(piece);
            if ("ABCD".contains(char1)){
                homeX = 80;
            }
            else homeX = 750;
            setLayoutX(homeX);
            setLayoutY(homeY);
            setRotate(0);
            setFitHeight(80);
            setFitWidth(80);}
        }
//https://stackoverflow.com/questions/3290681/initializing-a-double-object-with-a-primitive-double-value
//https://stackoverflow.com/questions/5617175/arraylist-replace-element-if-exists-at-a-given-index
        void flippedPieces(){
            String char1 = String.valueOf(piece.charAt(0));
            String char2 = String.valueOf(piece.charAt(1));
            String newpiece = "";
            if (char2.equals("A") ) newpiece = char1 + "E";
            else if (char2.equals("E")) newpiece = char1 + "A";
            System.out.println(piece + " ->updated-> " +newpiece );
            this.piece = newpiece;
            flip.play();
            setImage(new Image(Viewer.class.getResource(URI_BASE+newpiece+".png").toString()));
            setFitHeight(80);
            setFitWidth(80);

        }
    }

    private ArrayList<String> piecelist = new ArrayList<>();
    private void originalPieces(){
        String temp = "ABCDEFGH";
        System.out.println("make ... home");
        System.out.println("home done~~~"+ done);
        if(piecelist.size()==0){
            for (int j = 0; j < 8; j++) {
                if (!done.contains(String.valueOf(temp.charAt(j)))) {
                    piecelist.add(String.valueOf(temp.charAt(j)) + "A");
                    System.out.println("piecelist: " +piecelist);
                }// else continue;
                //https://stackoverflow.com/questions/8330747/doing-minus-operation-on-string
            }
        }
    }
    private ArrayList<String> placedpieces = new ArrayList<>();

//https://www.mkyong.com/java/how-to-loop-arraylist-in-java/
    /** This method is used to show those draggable pieces on the board. **/
    private void makeOriginalPieces() {
        originalPieces();
        //pieces.getChildren().clear(); // updated piecelist states : including the flipped side
        newpieces.getChildren().clear();
        for (String piece : piecelist) {
            newpieces.getChildren().add(new DraggableFXPiece(piece));
        } // newpieces : original group
        newpieces.toFront();
    }


    // FIXME Task 7: Implement a basic playable Steps Game in JavaFX that only allows pieces to be placed in valid places

    class DraggableFXPiece extends FlippableFXPiece {
        double homeX, homeY;
        double mouseX, mouseY;

        private Board board = new Board();

        DraggableFXPiece(String piece) {
            super(piece);
            if (piecelist.contains(piece))
            for(String p : piecelist) {
                if ( p.equals(piece)) {

                    homeX = new FlippableFXPiece(p).homeX;
                    setLayoutX(homeX);
                    homeY = new FlippableFXPiece(p).homeY;
                    setLayoutY(homeY);
                }
            }

            setOnScroll(event -> {if (!isOnBoard()) {
                hideCompletion();
                hideUsingTime();
                rotate(); // not on board -> enables rotate property
                event.consume();
            }});
            //https://stackoverflow.com/questions/22542015/how-to-add-a-mouse-doubleclick-event-listener-to-the-cells-of-a-listview-in-java
            setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent click) {

                    if (homeX == getLayoutX() & homeY == getLayoutY()){
                        if (click.getClickCount() == 2) { // double click to flip the piece -> only at the origin
                            flippedPieces();
                            hideCompletion();
                            hideUsingTime();}}
                }
            });

            setOnMousePressed(event -> {
                if (canmove()) { // layer checker function
                    hideCompletion();
                    hideUsingTime();
                    mouseX = event.getSceneX();
                    mouseY = event.getSceneY();}
                    });

            setOnMouseDragged(event -> {
                if (canmove()) { // layer checker function
                    hideCompletion();
                    hideUsingTime();
                    setFitHeight(110);
                    setFitWidth(110);
                    setOpacity(0.3);
               // System.out.println("mouse X: "+ mouseX);
              //  System.out.println("mouse Y: "+ mouseY);
                    double movementX = event.getSceneX() - mouseX;
                    double movementY = event.getSceneY() - mouseY;
                    setLayoutX(getLayoutX() + movementX);
                    setLayoutY(getLayoutY() + movementY);
                    mouseX = event.getSceneX();
                    mouseY = event.getSceneY();
                    event.consume();}
                else event.consume();
                        });

            setOnMouseReleased(event -> { if(canmove()){
                if (isOnBoard()) {
                    setOpacity(1);
                    snapToGrid(); // check the validity (whether it's movable)
                    hideCompletion();
                    hideUsingTime();
                    event.consume();
                }
                else if(!(getLayoutX()==homeX && getLayoutY() == homeY)){
                    //ouou.play();
                    snap.play();
                    if(!done.contains(String.valueOf(piece.charAt(0)))) {
                        System.out.println(done);
                        snapToHome();
                        System.out.println("Directly to home! Out board!");}
                    else if (canmove()) // layer constrains
                    {
                        snapToHome();
                        int index = done.indexOf(piece.charAt(0));
                        System.out.println("done test "+ done);
                        System.out.println("index: "+index);
                        System.out.println("placement length "+pastplacement.length());
                        System.out.println("previous placement "+pastplacement);
                        //https://stackoverflow.com/questions/7775364/how-can-i-remove-a-substring-from-a-given-string
                        if (3 * (index+1) == pastplacement.length()){ // the last piece to back
                            pastplacement = pastplacement.substring(0,3*index);}
                        else {
                            System.out.println("qian :" + pastplacement.substring(0,3*index));
                            System.out.println("hou " + pastplacement.substring(3*index+3));
                            System.out.println();
                            pastplacement = pastplacement.substring(0,3*index)+pastplacement.substring(3*index+3);}
                        System.out.println("outboard! reduced pastplacement: " +pastplacement);
                        System.out.println("done before: "+done);
                        String renew = "";
                        for (int i = 0; i < done.length();i++){
                            if (!(done.charAt(i)==piece.charAt(0))) renew += done.charAt(i);
                        }
                        done = renew;
                        System.out.println("done after renew: " + done);
                    }
                    setOpacity(1);}
            }});
                }

        /** To check whether the piece already on board can move:
         * Bottom level probably is constrained by the top level pieces
         * Top layer: notObstruct(reducedplacement,p)  -> canmove -> true
         * Bottom layer: Obstruct ! -> false -> cannot move
         * the precondition is "on board" already!
         * */
        private boolean canmove(){
            String p;
            String reducedplacement;
            // already on board
            done = ""; //  initialise each time
            if (pastplacement.length() == 0) pastplacement = newstart;
            if (pastplacement.length()>0){
                for (int i = 0; i< pastplacement.length();i+=3){
                    done += String.valueOf( pastplacement.charAt(i));
                }
            }
            if (done.contains(String.valueOf(piece.charAt(0)))){
                int index = done.indexOf(piece.charAt(0));
                //https://stackoverflow.com/questions/7775364/how-can-i-remove-a-substring-from-a-given-string
                if (3 * (index+1) == pastplacement.length()){ // the last piece to back
                   // pastplacement = pastplacement.substring(0,3*index);
                    return true;
                }
                else { // not the last to place..
                    System.out.println("canmove checker: !!");
                    p = pastplacement.substring(3*index,3*index+3);
                    reducedplacement = pastplacement.substring(0,3*index)+pastplacement.substring(3*index+3);
                    return  (notObstruct(reducedplacement,p));
                }
            }
            else // not on board
                return true;
        }

        /** Check whether the piece is on board or out of the board boundary. */
        boolean isOnBoard(){
            if ( ((piece.equals("BA") || piece.equals("EE")) && getRotate() == 0) || (piece.equals("BE") || piece.equals("EA")) && getRotate() == 180){
                if (getLayoutX()<270||getLayoutX()>520 || getLayoutY()<220 || getLayoutY()>290){
                    return false;
                }}
            else if ( ((piece.equals("BA") || piece.equals("EE")) && getRotate() == 90) || ((piece.equals("BE") || piece.equals("EA")) && getRotate() == 270)){
                if (getLayoutX()<300 || getLayoutX()>520||getLayoutY()<190 || getLayoutY()>290){
                    return false;
                }}
            else if (((piece.equals("BA") || piece.equals("EE")) && getRotate() == 180) || ((piece.equals("BE") || piece.equals("EA")) && getRotate() == 0)){
                if (getLayoutX()<300 || getLayoutX()>550 || getLayoutY()<220 || getLayoutY()>290){
                    return false;
                }}
            else if (((piece.equals("BA") || piece.equals("EE")) && getRotate() == 270) ||((piece.equals("BE") || piece.equals("EA")) && getRotate() == 90)) {
                if (getLayoutX() < 300 || getLayoutX() > 520 || getLayoutY() < 220 || getLayoutY() > 320) {
                    return false;
                }}

            else if (getLayoutX()<300 || getLayoutX()>520 || getLayoutY()<220 || getLayoutY()>290){
                return false;
            }
            return true;
        }

        /** Make the pieces snap to their original position. */
        private void snapToHome() {
            homeY = piecemapY.get(piece);
            String char1 = String.valueOf(piece.charAt(0));
            if ("ABCD".contains(char1)){
                homeX = 80;}
            else homeX = 750;
            setLayoutX(homeX);
            setLayoutY(homeY);
            setRotate(0);
            setFitHeight(80);
            setFitWidth(80);
        }

        /** The main aim is to test whether the piece is correctly located on the peg rather than the non-peg area.
         * It applies the for-each loop to travel through all pegs to check the validity in terms of the distance.
         * Another feature for this method is to record the end time is the piece is the last one to place correctly.
         * Last, it enables us to communicate with the backend programming since it gives to output of the
         * piece placement (3-char) and the updated board placement String */
        private void snapToGrid(){
            done = ""; //  initialise each time
            if (pastplacement.length()>0){
                for (int i = 0; i< pastplacement.length();i+=3){
                    done += String.valueOf( pastplacement.charAt(i));
                }
            } // if already on board
            if (done.contains(String.valueOf(piece.charAt(0)))){
                int index = done.indexOf(piece.charAt(0));
                //https://stackoverflow.com/questions/7775364/how-can-i-remove-a-substring-from-a-given-string
                if (3 * (index+1) == pastplacement.length()){ // the last piece to back
                    pastplacement = pastplacement.substring(0,3*index);}
                else {
                    pastplacement = pastplacement.substring(0,3*index)+pastplacement.substring(3*index+3);}
                String renew = "";
                for (int i = 0; i < done.length();i++){
                    if (!(done.charAt(i)==piece.charAt(0))) renew += done.charAt(i);
                }
                done = renew; // reduce the done
                System.out.println(" renew done here: " + done);
            }
            // calculated done pieces each time
            String ori = "";
            String pieceplacement;
            int index;
            String char1 = String.valueOf(piece.charAt(0));
            String char2 = String.valueOf(piece.charAt(1));
            int count = 0;
            if(char2.equals("A")) {
                for (Peg a : peglist){
                    if(getLayoutX()-25 <= a.x+20 && getLayoutX()-25 >= a.x-20 && getLayoutY()+ 55 <= a.y+20 && getLayoutY()+55  >= a.y-20){
                        //ouou.play();
                        //snap.play();
                        setLayoutY(a.y - 55); // getLayoutY() - 20 <= pos <= getLayoutY() + 20
                        setLayoutX(a.x + 25);
                        setRotate(getRotate());
                        setImage(new Image(Viewer.class.getResource(URI_BASE+ piece+".png").toString()));// top
                        toFront(); // debugged for layer arrangement
                        System.out.println("placed well");
                        setFitHeight(110);
                        setFitWidth(110);
                        if (char2.equals("E")) { // flipped
                            ori =  String.valueOf((char) ('E'+(getRotate()/90))); // E F G H
                        }
                        else if (char2.equals("A")) {// non-flipped
                            ori = String.valueOf((char) ('A'+(getRotate()/90)));}
                        pieceplacement = char1 + ori + a.letter.toString();
                        //System.out.println("orientation : " + ori);
                        System.out.println("this current placement: " + pieceplacement);

                        if (pastplacement.length()==0) {// first piece to place
                            pastplacement = newstart + pieceplacement; // add the given string: "newstart"
                            // viewNewStart(newstart);
                        }
                            //continue; // -- fixme
                        else // Updated ( try )

                            if (!done.contains(char1))
                                pastplacement = pastplacement + pieceplacement;

                            else  {
                                index = done.indexOf(char1);
                                System.out.println("index: "+index);
                                System.out.println("before: "+pastplacement);
                                //https://stackoverflow.com/questions/7775364/how-can-i-remove-a-substring-from-a-given-string
                                pastplacement = pastplacement.replace(pastplacement.substring(3*index,3*index+3),"");
                                System.out.println("reduced: "+pastplacement);
                                pastplacement = pastplacement + pieceplacement;
                            }

                        System.out.println("initially revised pastplacement: " + pastplacement);
                        // whether complete?
                        if ( StepsGame.notObstruct(pastplacement.substring(0,pastplacement.length()-3),pieceplacement) && pastplacement.length() == 24) {
                            yohu.play();
                            endMilli = System.currentTimeMillis();
                            useTime = endMilli - startMilli;
                            if (useTime != startMilli) {
                                if (difficulty.getValue() == 0) {
                                    diff_0.add(useTime);
                                }
                                else if (difficulty.getValue() == 1) {
                                    diff_1.add(useTime);
                                }
                                else if (difficulty.getValue() == 2) {
                                    diff_2.add(useTime);
                                }
                                else if (difficulty.getValue() == 3) {
                                    diff_3.add(useTime);
                                }
                            }
                            if (useTime > 60000) {
                                UseTime = new BigDecimal(useTime / 60000);
                                useTime = UseTime.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                timeUsing = new Text("use time: " + useTime + " min");}
                            else {
                                UseTime = new BigDecimal(useTime / 1000);
                                useTime = UseTime.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                timeUsing = new Text("use time: " + useTime + " s");}
                            makeUsingTime();
                            showCompletion();
                            win.play();
                            showUsingTime();}
                        //  place well ! -> update the pastplacement
                        else if(StepsGame.notObstruct(pastplacement.substring(0,pastplacement.length()-3),pieceplacement)) {
                            yohu.play();
                            System.out.println("enter not Obstruct here");
                            done += char1;
                            System.out.println("enter comfirmed pastplacement " +pastplacement);
                            //continue;
                        }
                        else if (pieceplacement.equals("")) {
                            ouou.play();
                            snap.play();
                            snapToHome();
                        } //
                        else if(!notObstruct(pastplacement.substring(0,pastplacement.length()-3),pieceplacement)){
                            ouou.play();
                            System.out.println("home here!");
                            System.out.println("before return pastplacement: " + pastplacement);
                            System.out.println("BUG length :" +pastplacement.length());
                            System.out.println("BUG revise :" +pastplacement.substring(0,pastplacement.length()-3));
                            pastplacement = pastplacement.substring(0,pastplacement.length()-3);
                            System.out.println("return to previous pastplacement: " + pastplacement);
                            System.out.println("BUG test");
                            snap.play();
                            snapToHome();
                        }
                        System.out.println("comfirmed pastplacement: " + pastplacement);}
                    else {
                        count += 1;
                        //System.out.println("count pegs: "+count);
                        if (count == 25) {
                            ouou.play();
                            snap.play();
                            snapToHome();
                            System.out.println("not on grid! ->  home");
                            System.out.println("pastplacement-> " + pastplacement);
                        }
                    }// next peg in the traversal -> continue
                }
            }
            else if (char2.equals("E")){
                for (BlankPeg a : blankpeglist){
                    if(getLayoutX()-25 <= a.x+20 && getLayoutX()-25 >= a.x-20 && getLayoutY()+ 55 <= a.y+20 && getLayoutY()+55  >= a.y-20){
                        //ouou.play();
                        //snap.play();
                        setLayoutY(a.y - 55); // getLayoutY() - 20 <= pos <= getLayoutY() + 20
                        setLayoutX(a.x + 25);
                        setRotate(getRotate());
                        setImage(new Image(Viewer.class.getResource(URI_BASE+ piece+".png").toString()));// top
                        System.out.println("placed well");
                        setFitHeight(110);
                        setFitWidth(110);
                        if (char2.equals("E")) { // flipped
                            ori =  String.valueOf((char) ('E'+(getRotate()/90))); // E F G H
                        }
                        else if (char2.equals("A")) {// non-flipped
                            ori = String.valueOf((char) ('A'+(getRotate()/90)));}
                        pieceplacement = char1 + ori + a.letter.toString();
                        //System.out.println("orientation : " + ori);
                        System.out.println("this current placement: " + pieceplacement);

                        if (pastplacement.length()==0) {// first piece to place
                            pastplacement = newstart + pieceplacement; // add the given string: "newstart"
                            // viewNewStart(newstart);
                        }
                        //continue; // -- fixme
                        else // Updated ( try )

                            if (!done.contains(char1))
                                pastplacement = pastplacement + pieceplacement;

                            else  {
                                index = done.indexOf(char1);
                                System.out.println("index: "+index);
                                System.out.println("before: "+pastplacement);
                                //https://stackoverflow.com/questions/7775364/how-can-i-remove-a-substring-from-a-given-string
                                pastplacement = pastplacement.replace(pastplacement.substring(3*index,3*index+3),"");
                                System.out.println("reduced: "+pastplacement);
                                pastplacement = pastplacement + pieceplacement;
                            }

                        System.out.println("initially revised pastplacement: " + pastplacement);
                        // whether complete?
                        if ( StepsGame.notObstruct(pastplacement.substring(0,pastplacement.length()-3),pieceplacement) && pastplacement.length() == 24) {
                            yohu.play();
                            endMilli = System.currentTimeMillis();
                            useTime = endMilli - startMilli;
                            if (useTime != startMilli) {
                                if (difficulty.getValue() == 0) {
                                    diff_0.add(useTime);
                                }
                                else if (difficulty.getValue() == 1) {
                                    diff_1.add(useTime);
                                }
                                else if (difficulty.getValue() == 2) {
                                    diff_2.add(useTime);
                                }
                                else if (difficulty.getValue() == 3) {
                                    diff_3.add(useTime);
                                }
                            }
                            if (useTime > 60000) {
                                UseTime = new BigDecimal(useTime / 60000);
                                useTime = UseTime.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                timeUsing = new Text("use time: " + useTime + " min");}
                            else {
                                UseTime = new BigDecimal(useTime / 1000);
                                useTime = UseTime.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                timeUsing = new Text("use time: " + useTime + " s");}
                            makeUsingTime();
                            showCompletion();
                            win.play();
                            showUsingTime();}
                        //  place well ! -> update the pastplacement
                        else if(StepsGame.notObstruct(pastplacement.substring(0,pastplacement.length()-3),pieceplacement)) {
                            yohu.play();
                            System.out.println("enter not Obstruct here");
                            done += char1;
                            System.out.println("enter comfirmed pastplacement " +pastplacement);
                            //continue;
                        }
                        else if (pieceplacement.equals("")) {
                            ouou.play();
                            snap.play();
                            snapToHome();
                        } //
                        else if(!notObstruct(pastplacement.substring(0,pastplacement.length()-3),pieceplacement)){
                            ouou.play();
                            System.out.println("home here!");
                            System.out.println("before return pastplacement: " + pastplacement);
                            System.out.println("BUG length :" +pastplacement.length());
                            System.out.println("BUG revise :" +pastplacement.substring(0,pastplacement.length()-3));
                            pastplacement = pastplacement.substring(0,pastplacement.length()-3);
                            System.out.println("return to previous pastplacement: " + pastplacement);
                            System.out.println("BUG test");
                            snap.play();
                            snapToHome();
                        }
                        System.out.println("comfirmed pastplacement: " + pastplacement);}
                    else {
                        count += 1;
                        //System.out.println("count pegs: "+count);
                        if (count == 25) {
                            ouou.play();
                            snap.play();
                            snapToHome();
                            System.out.println("not on grid! ->  home");
                            System.out.println("pastplacement-> " + pastplacement);
                        }
                    }// next peg in the traversal -> continue
                }
            }
        }



        /** Make the piece rotate clockwise. Each time rotate 45 degrees. **/
        private void rotate() {
            flip.play();
            setRotate((getRotate() + 90) % 360);}
    }

    // FIXME Task 8: Implement starting placements
// Task 8 is combined with Task 11

    // FIXME Task 10: Implement hints
//https://stackoverflow.com/questions/18265940/should-i-always-not-catch-nullpointerexception
    private String nextMask(String placement){
        try {
           // if(placement != "") {
                invi = true;
                Set<String> solution = SolverForHint.Solutions(placement);
                ArrayList<String> temp = new ArrayList<>(solution);
                int leng = placement.length();
                String result = temp.get(0);
                return result.substring(leng,leng+3);//}
        }
        catch (NullPointerException z){
            new innerStage().display("NullPointerException", "Please press 'Start' first!");
            return null;}
        }


/*    boolean isValidCurrentStep(String pastPlacement,String appendMask){
        Set<String> solution = SolverForHint.Solutions (pastPlacement);
        ArrayList<String> temp = new ArrayList<>(solution);
        int leng = pastPlacement.length();
        String[] validAppendMask = new String[temp.size()];
        for (int i = 0; i < validAppendMask.length; i++){
            validAppendMask[i] = temp.get(i).substring(leng,leng+3);
        }
        for (int j = 0; j < validAppendMask.length; j++){
            return  (validAppendMask[j].equals(appendMask));
        }
        return false;
    }*/

    /* Hints helper functions */

    /** helps to set the effect of text, arranging the text's position, size.
      * When called, record current time automatically.**/
    private void compTextEffect() {
        completionText.setFill(Color.DEEPPINK);
        completionText.setFont(Font.loadFont(MenuApp.class.getResource("res/handwriting-draft_free-version.ttf").toExternalForm(), 20));
        completionText.setLayoutX(400);
        completionText.setLayoutY(70);
        //completionText.setTextAlignment(TextAlignment.CENTER);
       // root.getChildren().add(completionText);
        endMilli = System.currentTimeMillis();
    }

    /** This method helps to show the completion text -> set it front **/
    private void showCompletion() {
        //newPiece.setOpacity(0.3);
        newpieces.setOpacity(0.3);
        startpieces.setOpacity(0.3);
        completionText.toFront();
        completionText.setOpacity(1);
        completionText.setEffect(blueshadow);
    }

    /** helps to hide the completion message  **/
    private void hideCompletion() {
        completionText.toBack();
        completionText.setOpacity(0);
        newpieces.setOpacity(1);
        startpieces.setOpacity(1);
        //newPiece.setOpacity(1);
    }
// Using time relevant methods are based on ideas given by Henan Wang(u6007140) and Shenjia Ji(u5869805)
    /** helps to set properties for the "timeUsing" text
    * the total time to complete the game at the corresponding difficulty. **/
    private void makeUsingTime() {
        timeUsing.setFill(Color.DEEPPINK);
        timeUsing.setFont(Font.loadFont(MenuApp.class.getResource("res/handwriting-draft_free-version.ttf").toExternalForm(), 15));
        timeUsing.setTextAlignment(TextAlignment.LEFT);
        timeUsing.setLayoutY(650);
        //root.getChildren().add(timeUsing);
    }
    /** helps to show the "timeUsing" text. **/
    private void showUsingTime() {
        timeUsing.toFront();
        timeUsing.setOpacity(1);
    }

    /** helps to hide the "timeUsing" text. **/
    private void hideUsingTime() {
        timeUsing.toBack();
        timeUsing.setOpacity(0);
    }

    /**
     * This method helps to catch the keyboard code and activate the corresponding operation.
     * Specifically,
     * SLASH -> hints for the next step appears
     *     Q -> quit the game viewer and back to the menu
     *     S -> inner stage will appear with the best score in current difficulty
     *     I -> the instruction text appears
     * @param scene the current game scene
     */
    // used ideas given by Henan Wang(u6007140) and Shenjia Ji(u5869805)
    private void keyboardHandlers(Scene scene) {
        System.out.println("Handlers up");
        //https://docs.oracle.com/javase/8/javafx/api/javafx/scene/input/KeyEvent.html#KEY_PRESSED
        scene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.Q) {
                        Platform.exit();
                        event.consume();
                    }
                    else if (event.getCode() == KeyCode.SLASH){
                        count +=1;
                        System.out.println("count: " +count);
                        if (count%2 == 1){
                            System.out.println("BUG before" + pastplacement);
                            if (pastplacement.length()==0) pastplacement =newstart;
                            System.out.println("BUG after" + pastplacement);
                            placePieces(nextMask(pastplacement));
                            invisibleSol.setOpacity(0.75);} //invi = true;
                        else
                            invisibleSol.setOpacity(0);
                        event.consume();
                    }
                    else if (event.getCode() == KeyCode.I){
                        showInsturnction();
                        event.consume();
                    } // show an inner stage with the best score in this difficulty
                    else if (event.getCode() == KeyCode.S){
                        if(BestScore(difficulty.getValue())>1000000){
                            double best = BestScore(difficulty.getValue());
                            BigDecimal Best = new BigDecimal(best/6000);
                            best = Best.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                            new innerStage().display("The best score for level "+ difficulty.getValue(),+best+" min!");
                        }
                        else if (BestScore(difficulty.getValue()) == 0){
                            new innerStage().display("The best score for level "+ (int) difficulty.getValue(), "Oops! No record yet:( \n\n Congratulations! \n\nYou're the 1st lucky guy to play this level:)");
                        }
                        else{
                            double best = BestScore(difficulty.getValue());
                            BigDecimal Best = new BigDecimal(best/1000);
                            best = Best.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                            new innerStage().display("The best score for level "+ difficulty.getValue(), +best+ " s!");
                        }
                    }
                }); // based on ideas given by Henan Wang(u6007140) and Shenjia Ji(u5869805)
        //https://docs.oracle.com/javase/8/javafx/api/javafx/scene/input/KeyEvent.html#KEY_RELEASED
        scene.setOnKeyReleased(event -> {
                     if (event.getCode() == KeyCode.I){
                        hideInstruction();
                    }
                });
    }


 /** helper functions to show the hints' text */
    private void InsTextEffect(){
        //insText.setFill(Color.DEEPPINK);
        //insText.setFont(Font.loadFont(MenuApp.class.getResource("res/handwriting-draft_free-version.ttf").toExternalForm(), 10));
        insText.setLayoutX(345);
        insText.setLayoutY(410);
        //insText.setTranslateX(5);
        //insText.setTranslateY(20);
        insText.setFont(Font.loadFont(MenuApp.class.getResource("res/Penumbra-HalfSerif-Std_35114.ttf").toExternalForm(), 10));
        insText.setFill(Color.DEEPSKYBLUE);
        insText.setEffect(blueshadow);

        root.getChildren().add(insText);
    }

   private void showInsturnction (){
        insText.toFront();
        insText.setOpacity(1);
   }

   private void hideInstruction () {
       insText.toBack();
       insText.setOpacity(0);
   }
   // used ideas given by Henan Wang(u6007140) and Shenjia Ji(u5869805)
 /** helper class to show the best score in an inner stage */
//https://stackoverflow.com/questions/38062219/how-can-i-get-textfield-from-inner-stage-in-javafx
   private class innerStage{
       private void display(String title, String message){
           Stage inner = new Stage();
           inner.setTitle(title);
           inner.initModality(Modality.APPLICATION_MODAL);
           inner.setMinWidth(300);
           inner.setMinHeight(200);

           Label label = new Label(message);
           VBox layout = new VBox(10);
           layout.getChildren().addAll(label);
           layout.setAlignment(Pos.CENTER);

           Scene scene = new Scene(layout);
           inner.setScene(scene);
           inner.showAndWait();
       }
   }

    // used ideas given by Henan Wang(u6007140) and Shenjia Ji(u5869805)
  /* add "if-statement" to return the best corresponding to current difficulty */
   private double BestScore(double difficulty){
       double best = 0; // initialise
       if (difficulty == 0){
            best = getBest(diff_0);
       }
       else if (difficulty == 1){
           best = getBest(diff_1);
       }
       else if (difficulty == 2){
           best = getBest(diff_2);
       }
       else if (difficulty == 3){
           best = getBest(diff_3);
       }
       return best;

   } // used ideas given by Henan Wang(u6007140) and Shenjia Ji(u5869805)
    /* find the best result in one certain difficulty */
   private double getBest (ArrayList<Double> list){
       double min = 1000000; //initialise
       if (list.size() == 0){
           min = 0;
       }
       else {
           for (Double i : list){ // traversal through the list to get the min
               if (i<min)  min = i;
           }
       }
       return min; // represents the best score
   }

    /** Coordinates helper functions */
    private static final Map<Integer,Integer> pegmapX;
    static {
        pegmapX = new HashMap<>();
        pegmapX.put(0,250); //
        pegmapX.put(1,280); // 2nd / 4th row ->  1st peg x-Coord
        pegmapX.put(2,310); //
        pegmapX.put(3,340);
        pegmapX.put(4,370);
        pegmapX.put(5,400);
        pegmapX.put(6,430);
        pegmapX.put(7,460);
        pegmapX.put(8,490);
        pegmapX.put(9,520);
    }
    private static final Map<Integer,Integer> pegmapY;
    static {
        pegmapY = new HashMap<>();
        pegmapY.put(0,250); //
        pegmapY.put(1,280); //                  y-Coord
        pegmapY.put(2,310); //
        pegmapY.put(3,340);
        pegmapY.put(4,370);
    }

    // used ideas given by the YouTuber Almas Baimagambetov and the link:
    // https://www.youtube.com/watch?v=N2EmtYGLh4U&index=1&list=PL4h6ypqTi3RQWPZfR6t73rxZK_TFkyURe
    // The source code is from:
    // https://github.com/AlmasB/FXTutorials/tree/master/src/com/almasb/civ6menu
    /** UI helper functions */
    private void addBackground(){
        //ImageView imageView = new ImageView(new Image(getClass().getResource("res/Colourful.jpg").toExternalForm()));
        //ImageView imageView = new ImageView(new Image(getClass().getResource("res/pinkblue.jpg").toExternalForm()));
        ImageView imageView = new ImageView(new Image(getClass().getResource("res/pinkpurple.jpg").toExternalForm()));
        imageView.setFitWidth(BOARD_WIDTH);
        imageView.setFitHeight(BOARD_HEIGHT);
        root.getChildren().add(imageView);
    }

    private void addTitle() {
        Title title = new Title("IQ - STEPS");
        title.setTranslateX(BOARD_WIDTH / 2 - title.getTitleWidth() / 2);
        title.setTranslateY(BOARD_HEIGHT / 5);

        root.getChildren().add(title);
    }

    private void addHints(){
        Text RightCorner = new Text("Press 'I' =>To=> Hide/Show the Game Instructions.");
        RightCorner.setFont(Font.loadFont(MenuApp.class.getResource("res/Penumbra-HalfSerif-Std_35114.ttf").toExternalForm(), 12));
        RightCorner.setEffect(new DropShadow(5,Color.ORANGERED));
        RightCorner.setTranslateX(600);
        RightCorner.setFill(Color.MEDIUMPURPLE);
        RightCorner.setTranslateY(0.98 * BOARD_HEIGHT);
        root.getChildren().add(RightCorner);
    }

    // FIXME Task 11: Generate interesting starting placements
//https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
    /** generates interesting starting placements with only one solution  */
    private void setNewstart (){
        int diff = (int) difficulty.getValue();
        int row = 3 - diff;
        int random = new Random().nextInt(15);
        newstart = TaskEleven_OBJECTIVE[row][random];
}
    /** Place the generated pieces to the board according to the string */
    // similar to Viewer.java
    private void  viewNewStart (String newstart){
        int num = newstart.length()/3;
        for(int i=0;i<num;i++){
            placePieces(newstart.substring(3*i,3*i+3));
            done += String.valueOf(newstart.charAt(3*i));
            System.out.println("initial done: " + done);
            System.out.println("newstart: " + newstart);
        }
    }
    //https://www.quora.com/How-do-you-compare-chars-in-Java
    private void placePieces (String pieceplacement){
        String p = String.valueOf(pieceplacement.charAt(0));
        char ori = pieceplacement.charAt(1);
        char location = pieceplacement.charAt(2);
        //https://docs.oracle.com/javase/8/javafx/api/javafx/scene/image/ImageView.html
        ImageView StartPiece =new ImageView();
        //https://stackoverflow.com/questions/27785917/javafx-mouseposition
        //https://www.cs.cmu.edu/~pattis/15-1XX/common/handouts/ascii.html
        if ("ABCDEFGHIJ".contains(String.valueOf(location))){
            StartPiece.setLayoutY(195);
            StartPiece.setLayoutX(275+30*(location-'A'));
        }
        else if ("KLMNOPQRST".contains((String.valueOf(location)))){
            StartPiece.setLayoutY(225);
            StartPiece.setLayoutX(275+30*(location-'K'));
        }
        else if ("UVWXY".contains((String.valueOf(location)))){
            StartPiece.setLayoutY(255);
            StartPiece.setLayoutX(275+30*(location-'U'));
        }
        else if ("abcde".contains((String.valueOf(location)))){
            StartPiece.setLayoutY(255);
            StartPiece.setLayoutX(425+30*(location-'a'));
        }
        else if ("fghijklmno".contains((String.valueOf(location)))){
            StartPiece.setLayoutY(285);
            StartPiece.setLayoutX(275+30*(location-'f'));
        }
        else if ("pqrstuvwxy".contains((String.valueOf((location))))){
            StartPiece.setLayoutY(315);
            StartPiece.setLayoutX(275+30*(location-'p'));
        }

        if (ori >='A' && ori <'E'){
            StartPiece.setRotate((ori-'A')*90);
            StartPiece.setFitHeight(110);
            StartPiece.setFitWidth(110);
            StartPiece.setImage(new Image(Viewer.class.getResource(URI_BASE+ p+"A"+".png").toString()));
        }
        else if (ori >='E' && ori <='H'){
            StartPiece.setRotate((ori-'E')*90);
            StartPiece.setFitHeight(110);
            StartPiece.setFitWidth(110);
            StartPiece.setImage(new Image(Viewer.class.getResource(URI_BASE+ p+"E"+".png").toString()));
        }
        if (invi){
            System.out.println("here ??????");
            invisibleSol.getChildren().clear();
            StartPiece.setOpacity(0.75);
            System.out.println("next hints: " + pieceplacement);
            invi = false;
            invisibleSol.getChildren().add(StartPiece);}
        else
            {StartPiece.setOpacity(1);
            startpieces.getChildren().add(StartPiece);}
    }

    /**
     * Set slide bar for difficulty
     * "0" represents easy level
     * Similarly,
     * "0" represents medium level
     * "2" represents hard level
     * "3" represents hardest level
     * */
//https://docs.oracle.com/javase/8/javafx/api/javafx/scene/layout/HBox.html
//http://docs.oracle.com/javafx/2/ui_controls/slider.htm
    private void makeControls() {
        Button button = new Button("Start");
        button.setLayoutX(610);
        button.setLayoutY(605);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                startMilli = System.currentTimeMillis();
                begin.play();

                hideCompletion();
                hideUsingTime();
                startpieces.getChildren().clear();
                newpieces.getChildren().clear();
                piecelist = new ArrayList<>();

                setNewstart ();
                done ="";
                pastplacement ="";
                viewNewStart(newstart);
                makeOriginalPieces();
                //pastplacement ="";
            }
        });
        controls.getChildren().add(button);
// used ideas suggested by Chenhao Tong(u5920830)
//https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/slider.htm#CCHDJDFE
        difficulty.setMin(0);
        difficulty.setMax(3);
        difficulty.setValue(0);
        difficulty.setBlockIncrement(1);
        difficulty.setPrefWidth(250);
        difficulty.setShowTickLabels(true);
        difficulty.setShowTickMarks(true);
        difficulty.setMajorTickUnit(4);
        difficulty.setMinorTickCount(3);
        difficulty.setSnapToTicks(true);

        difficulty.setLayoutX(300);
        difficulty.setLayoutY(610);
        controls.getChildren().add(difficulty);

        final Label difficultyCaption = new Label("Difficulty:");
        difficultyCaption.setFont(Font.loadFont(MenuApp.class.getResource("res/Penumbra-HalfSerif-Std_35114.ttf").toExternalForm(), 12));
        difficultyCaption.setEffect(new DropShadow(5,Color.DEEPPINK));
        difficultyCaption.setTextFill(Color.WHITE);
        difficultyCaption.setLayoutX(310);
        difficultyCaption.setLayoutY(590);
        controls.getChildren().add(difficultyCaption);
    }

    /**
     * According to the chosen difficulty, generate a new starting placement. */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("IQ-Steps Viewer");
        Scene scene = new Scene(root, BOARD_WIDTH, BOARD_HEIGHT);
        makePegs();
        makeBlankPegs();
        addBackground();
        addTitle();
        addHints();
        primaryStage.setScene(scene);
        root.getChildren().add(pegs);
        root.getChildren().add(blankpegs);
        root.getChildren().add(letters);
        root.getChildren().add(controls);
        root.getChildren().add(newpieces);
        root.getChildren().add(startpieces);
        root.getChildren().add(invisibleSol);
        keyboardHandlers(scene);
        InsTextEffect();
        compTextEffect();
        makeControls();
        makeUsingTime();

        primaryStage.setScene(scene);
        primaryStage.show();
        }}
