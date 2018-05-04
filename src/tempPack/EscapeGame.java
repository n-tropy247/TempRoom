/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tempPack;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javax.swing.UIManager.LookAndFeelInfo;

/**
 * TEMP
 *
 * @author Ryan Castelli
 */
public class EscapeGame extends JFrame {

    private static ActionEvent sendOverride;

    private static int code;
    private static int option;
    private static int turnCount;
    private static int x;
    private static int y;

    int[][] room;

    private static JFrame jfrm;

    private static JTextArea jtaDisplay;

    private static JTextField jtfInput;

    private static String input;

    private JButton jbtnSend;

    private JScrollPane jscrlp;

    //doors
    private static boolean eastDoor;
    private static boolean northDoor;
    private static boolean southDoor;
    private static boolean westDoor;

    //speed
    private static boolean runToggle = false;

    //first room boolean
    private static boolean start = true;

    //var for pos
    private static int currentIntX;
    private static int currentIntY;

    //storage for last pos
    private static int tempIntX;
    private static int tempIntY;

    //trophies
    private static int trophyCount = 0;
    private static boolean trophy = false;

    private EscapeGame() {
        init();
    }

    public static void main(String args[]) {
        EventQueue.invokeLater(() -> {
            jfrm = new EscapeGame();
            jfrm.setVisible(true);
        });
    }

    private void init() {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException exe) {
            System.err.println("Nimbus unavailable");
        }

        option = 0;
        setTitle("Escape!");
        setLayout(new BorderLayout());
        setSize(700, 600);

        jtaDisplay = new JTextArea(20, 40);
        jtaDisplay.setEditable(false);
        jtaDisplay.setLineWrap(true);

        jscrlp = new JScrollPane(jtaDisplay);

        jtfInput = new JTextField(30);

        jbtnSend = new JButton("Send");

        jbtnSend.addActionListener(new handler());

        KeyListener key = new handler();

        jtfInput.addKeyListener(key);

        add(jscrlp, BorderLayout.PAGE_START);

        sendOverride = new ActionEvent(jbtnSend, 1001, "Send");

        JPanel p1 = new JPanel();

        p1.setLayout(new FlowLayout());
        p1.add(jtfInput, BorderLayout.LINE_END);
        p1.add(jbtnSend, BorderLayout.LINE_END);

        add(p1, BorderLayout.PAGE_END);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initGame();
    }

    private void initGame() {

    }

    private void createRoom(int x, int y) {
        EscapeGame.x = x;
        EscapeGame.y = y;
        if (turnCount != 0) {
            jtaDisplay.setText(jtaDisplay.getText() + "\nYou exit the room successfully and continue your journey through the building. \nEventually you enter a new room, only to be locked in again! Didn't you learn the first time?");
        }

        turnCount++;

        trophy = true;

        //storage for value of room
        int tempRoom = 1;

        int rand;
        int randXCode;
        int randYCode;

        //code for new room
        code = (int) (Math.random() * 8999) + 1000;

        //gets code digits for placement
        int digit0 = code / 1000;
        int digit1 = (code - digit0 * 1000) / 100;
        int digit2 = (code - digit0 * 1000 - digit1 * 100) / 10;
        int digit3 = (code - digit0 * 1000 - digit1 * 100 - digit2 * 10);

        int deskCounter = 0;

        //creates int array for new room
        room = new int[y][x];
        for (int j = 0; j < y; j++) //Y
        {
            for (int k = 0; k < x; k++) //X
            {
                rand = (int) (Math.random() * 100) + 1;
                if ((((j == 0 && k > 0 && k < (x - 1)) || (j == (y - 1) && k > 0 && k < (x - 1)) || (k == 0 && j > 0 && j < (y - 1)) || (k == (x - 1)) && j > 0 && j < (y - 1))) && rand > 90) //doors
                {
                    room[j][k] = 2;
                } else if (!(j == 0 && k == 1) && room[j][k] != 2 && (j == 0 || k == 0 || j == (y - 1) || k == (x - 1))) //walls
                {
                    room[j][k] = 9;
                } else if (!(j == 0 && k == 1) && room[j][k] != 2 && room[j][k] != 9 && room[j + 1][k] != 2 && room[j - 1][k] != 2 && room[j][k + 1] != 2 && room[j][k - 1] != 2 && rand > 85
                        && room[j + 1][k] != 8 && room[j - 1][k] != 8 && room[j][k + 1] != 8 && room[j][k - 1] != 8 && deskCounter < 10) {
                    room[j][k] = 8;
                    deskCounter++;
                } else //empty space
                {
                    room[j][k] = 1;
                }
                if (start) //starting values for game
                {
                    room[1][1] = 3;
                    currentIntX = 1;
                    currentIntY = 1;
                    tempIntX = 0;
                    tempIntY = 0;
                }
            }//end room creation
        }
        for (int j = 0; j <= 3; j++) {
            randXCode = (int) (Math.random() * (x - 3)) + 2;
            randYCode = (int) (Math.random() * (y - 3)) + 2;
            if (room[randYCode][randXCode] == 1) {
                room[randYCode][randXCode] = 4 + j;
            } else {
                j--;
            }
        }

        if (northDoor) //handles continuity of pos if players enter door on north wall
        {
            //sets starting pos
            room[y - 1][tempIntX] = 2;
            room[y - 2][tempIntX] = 3;

            //sets ints for current tile
            currentIntX = tempIntX;
            currentIntY = (y - 2);

            //boolean for door
            northDoor = false;
        }
        if (southDoor) //handles continuity of pos if players enter door on south wall
        {
            room[0][tempIntX] = 2;
            room[1][tempIntX] = 3;
            currentIntX = tempIntX;
            currentIntY = 1;
            southDoor = false;
        }
        if (westDoor) //handles continuity of pos if players enter door on west wall
        {
            room[tempIntY][x - 1] = 2;
            room[tempIntY][x - 2] = 3;
            currentIntX = x - 2;
            currentIntY = tempIntY;
            westDoor = false;
        }
        if (eastDoor) //handles continuity of pos if players enter door on east wall
        {
            room[tempIntY][0] = 2;
            room[tempIntY][1] = 3;
            currentIntX = 1;
            currentIntY = tempIntY;
            eastDoor = false;
        }

        if (turnCount == 5 && !(input.equalsIgnoreCase("exit")) && !(input.equalsIgnoreCase("exity"))) //end of game after 5 rooms
        {
            jtaDisplay.setText(jtaDisplay.getText() + "\nYou shield your eyes as they adjust to the bright light...You have escaped.");//end message

            input = "exit";
        }

        jtaDisplay.setText(jtaDisplay.getText() + "\nYou are in a room. You can't really see.");
        jtaDisplay.setText(jtaDisplay.getText() + "\nWhat direction do you want to move?: "); //prints start of game
    }

    private class handler implements ActionListener, KeyListener {

        /**
         * If button pressed
         *
         * @param ae ActionEvent invoked
         */
        @Override
        public void actionPerformed(ActionEvent ae) {
            //storage for value of room
            int tempRoom = 1;

            //resets random number
            if (ae.getActionCommand().equals("Send")) {
                input = jtfInput.getText();
                jtaDisplay.setText(jtaDisplay.getText() + "\nYou: " + input);
                switch (option) {
                    case 0:
                        //temp values for position
                        tempIntX = currentIntX;
                        tempIntY = currentIntY;

                        start = false; //boolean for start of game

                        //handles direction
                        //upper left is 0,0
                        if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("north") || input.equalsIgnoreCase("up")) {
                            if (!(currentIntY - 1 <= 0)) //handles y movement
                            {
                                if (runToggle && (currentIntY - 1 <= 0)) {
                                    currentIntY--;
                                } else if (runToggle && !(currentIntY - 1 <= 0)) {
                                    currentIntY -= 2;
                                } else {
                                    currentIntY--;
                                }
                            } else if (!(currentIntY - 1 < 0) && room[currentIntY - 1][currentIntX] == 2) //handles doors
                            {
                                jtaDisplay.setText(jtaDisplay.getText() + "\nThere's a door there. Go through it?\n");
                                option = 1;
                            } else if (room[currentIntY][currentIntX] == 8) {
                                if (rand <= 50) {
                                    out.println("You tripped over a table in the dark. Hah.");
                                } else if (rand > 95) {
                                    out.println("You run into a bookshelf and something falls off and hits you in the head. #rekt");

                                    userInput = "exit";

                                } else {
                                    out.println("You run into a bookshelf.");
                                    currentIntY++;
                                }
                            } else //moved into wall
                            {
                                out.print("There's a wall there.\n");
                            }
                        } else if (userInput.equalsIgnoreCase("s") || userInput.equalsIgnoreCase("south") || userInput.equalsIgnoreCase("down")) {
                            if (!(currentIntY + 1 >= (y - 1))) {
                                if (runToggle && (currentIntY + 1 <= 0)) {
                                    currentIntY++;
                                } else if (runToggle && !(currentIntY + 1 <= 0)) {
                                    currentIntY += 2;
                                } else {
                                    currentIntY++;
                                }
                            } else if (!(currentIntY + 1 > (y - 1))) {
                                if (room[currentIntY + 1][currentIntX] == 2) {
                                    out.print("There's a door there. Go through it?\n");
                                    userInput = kbReader.next();
                                    if ((userInput.equalsIgnoreCase("Y") || userInput.equalsIgnoreCase("yes"))) {
                                        out.print("\nEnter the code: ");
                                        userInputCode = kbReader.nextInt();
                                        if (userInputCode == code) {
                                            int NewLimX = (int) (Math.random() * (x + 2)) + x;
                                            int NewLimY = (int) (Math.random() * (y + 2)) + y;
                                            southDoor = true;
                                            game(NewLimY, NewLimX);
                                            userInput = "exit";
                                        } else {
                                            out.println("Lol, no");
                                        }
                                    }
                                } else {
                                    out.print("There's a wall there.\n");
                                }
                            } else {
                                out.print("There's a wall there.\n");
                            }
                            if (room[currentIntY][currentIntX] == 8) {
                                if (rand <= 50) {
                                    out.println("You tripped over a table in the dark. Hah.");
                                } else if (rand > 99) {
                                    out.println("You run into a bookshelf and something falls off and hits you in the head. #rekt");
                                    for (int j = 0; j < turnCount; j++) {
                                        userInput = "exit";
                                    }
                                } else {
                                    out.println("You run into a bookshelf. Idiot.");
                                    currentIntY--;
                                }
                            }

                        } else if (userInput.equalsIgnoreCase("w") || userInput.equalsIgnoreCase("west") || userInput.equalsIgnoreCase("left")) {
                            if (!(currentIntX - 1 <= 0)) //handles movement in X
                            {
                                if (runToggle && (currentIntX - 1 <= 0)) {
                                    currentIntX--;
                                } else if (runToggle && !(currentIntX - 1 <= 0)) {
                                    currentIntX -= 2;
                                } else {
                                    currentIntX--;
                                }
                            } else if (!(currentIntX - 1 < 0) && room[currentIntY][currentIntX - 1] == 2) {
                                out.print("There's a door there. Go through it?\n");
                                userInput = kbReader.next();
                                if ((userInput.equalsIgnoreCase("Y") || userInput.equalsIgnoreCase("yes"))) {
                                    out.print("\nEnter the code: ");
                                    userInputCode = kbReader.nextInt();
                                    if (userInputCode == code) {
                                        out.print("\nEnter the code: ");
                                        int NewLimX = (int) (Math.random() * (x + 2)) + x;
                                        int NewLimY = (int) (Math.random() * (y + 2)) + y;
                                        westDoor = true;
                                        game(NewLimY, NewLimX);
                                        userInput = "exit";
                                    } else {
                                        out.println("Lol, no");
                                    }
                                }
                            } else {
                                out.print("There's a wall there.\n");
                            }
                            if (room[currentIntY][currentIntX] == 8) {
                                if (rand <= 50) {
                                    out.println("You tripped over a table in the dark. Hah.");
                                } else if (rand > 95) {
                                    out.println("You run into a bookshelf and something falls off and hits you in the head. #rekt");
                                    for (int j = 0; j < turnCount; j++) {
                                        userInput = "exit";
                                    }
                                } else {
                                    out.println("You run into a bookshelf. Idiot.");
                                    currentIntX++;
                                }
                            }

                        } else if (userInput.equalsIgnoreCase("e") || userInput.equalsIgnoreCase("east") || userInput.equalsIgnoreCase("right")) {
                            if ((currentIntX + 1) < (x - 1)) {
                                if (runToggle && (currentIntX + 1 <= 0)) {
                                    currentIntX++;
                                } else if (runToggle && !(currentIntX + 1 <= 0)) {
                                    currentIntX += 2;
                                } else {
                                    currentIntX++;
                                }
                            } else if ((currentIntX + 1 >= (x - 2)) && room[currentIntY][currentIntX + 1] == 2) {
                                {
                                    out.print("There's a door there. Go through it?\n");
                                    userInput = kbReader.next();
                                    if ((userInput.equalsIgnoreCase("Y") || userInput.equalsIgnoreCase("yes"))) {
                                        out.print("\nEnter the code: ");
                                        userInputCode = kbReader.nextInt();
                                        if (userInputCode == code) {
                                            out.print("\nEnter the code: ");
                                            int NewLimX = (int) (Math.random() * (x + 2)) + x;
                                            int NewLimY = (int) (Math.random() * (y + 2)) + y;
                                            eastDoor = true;
                                            game(NewLimY, NewLimX);
                                            userInput = "exit";
                                        } else {
                                            out.println("Lol, no");
                                        }
                                    }
                                }
                            } else if (currentIntX + 1 > (x - 2)) {
                                out.print("There's a wall there.\n");
                            }
                            if (room[currentIntY][currentIntX] == 8) {
                                if (rand <= 50) {
                                    out.println("You tripped over a table in the dark. Hah.");
                                } else if (rand > 95) {
                                    out.println("You run into a bookshelf and something falls off and hits you in the head. #rekt");
                                    for (int j = 0; j < turnCount; j++) {
                                        userInput = "exit";
                                    }
                                } else {
                                    out.println("You run into a bookshelf. Idiot.");
                                    currentIntX--;
                                }
                            }
                        } else if (userInput.equalsIgnoreCase("search") || userInput.equalsIgnoreCase("look")) //allows player to check current and adjacent tiles
                        {
                            int randomSearch = (int) (Math.random() * 100) + 1;
                            if (tempRoom != 1) //checks if tile isn't default
                            {
                                if (tempRoom == 4 || tempRoom == 5 || tempRoom == 6 || tempRoom == 7) //checks if tile has code
                                {
                                    if (randomSearch >= 2) {
                                        if (tempRoom == 4) {
                                            out.println("You see a number scrawled out on a note!\n" + digit0 + "\nOn the back it says, \"Millennials, amirite?\"");
                                        }
                                        if (tempRoom == 5) {
                                            out.println("You see a number scrawled out on a note!\n" + digit1 + "\nOn the back it says, \"All about those Benjamins\"");
                                        }
                                        if (tempRoom == 6) {
                                            out.println("You see a number scrawled out on a note!\n" + digit2 + "\nOn the back it says, \"7 ate 9, who's next?\"");
                                        }
                                        if (tempRoom == 7) {
                                            out.println("You see a number scrawled out on a note!\n" + digit3 + "\nOn the back it says, \"You\"");
                                        }
                                        tempRoom = 1; //resets tile once code is picked up
                                    } else if (randomSearch < 1) //trapped!
                                    {
                                        out.print("You don't find anything. Maybe there's something elsewhere...\n");
                                        out.print("Despite this, you get the strange feeling you're not getting out of here...");
                                        tempRoom = 1; //no code
                                    } else {
                                        out.println("Nothing nearby...");
                                    }
                                } else if ((room[currentIntY + 1][currentIntX] != 1 && room[currentIntY + 1][currentIntX] != 9)
                                        || (room[currentIntY - 1][currentIntX] != 1 && room[currentIntY - 1][currentIntX] != 9)
                                        || (room[currentIntY][currentIntX + 1] != 1 && room[currentIntY][currentIntX + 1] != 9)
                                        || (room[currentIntY][currentIntX - 1] != 1 && room[currentIntY][currentIntX - 1] != 9)) //end of elseif for search
                                {
                                    out.println("There's something nearby..."
                                            + "\nCan't quite make it out.");
                                } else if ((room[currentIntY + 1][currentIntX] != 1 && (room[currentIntY + 1][currentIntX] == 9 || room[currentIntY + 1][currentIntX] == 8))
                                        || (room[currentIntY - 1][currentIntX] != 1 && (room[currentIntY - 1][currentIntX] == 9 || room[currentIntY - 1][currentIntX] == 8))
                                        || (room[currentIntY][currentIntX + 1] != 1 && (room[currentIntY][currentIntX + 1] == 9 || room[currentIntY][currentIntX + 1] == 8))
                                        || (room[currentIntY][currentIntX - 1] != 1 && (room[currentIntY][currentIntX - 1] == 9 || room[currentIntY][currentIntX - 1] == 8))) {
                                    int randomItemSearch = (int) (Math.random() * 100) + 1;
                                    if (randomItemSearch <= 95) {
                                        out.println("Nothing nearby...");
                                    }
                                    if (randomItemSearch == 100 && trophy) {
                                        out.println("You got a trophy. Wow.");
                                        trophy = false;
                                    } else if (randomItemSearch > 95 && randomItemSearch <= 99) {
                                        out.println("In your greed, you forgot that the devs are mean and this game is unforgiving. The object you were searching in has collapsed and fell on you. You dead.");
                                        for (int j = 0; j < turnCount; j++) {
                                            userInput = "exit";
                                        }
                                    }
                                } else {
                                    out.println("Nothing nearby...");
                                }
                            } else {
                                out.println("Nothing nearby...");
                            }
                        } else if (userInput.equalsIgnoreCase("apple")) {
                            out.println("Okay. What you're doing there is jumping. You just... you just jumped. \nBut nevermind. Say \'Apple\'. \'Aaaapple\'.");
                        } else if (userInput.equalsIgnoreCase("Rules") || userInput.equalsIgnoreCase("Instructions")) //gives rules for play
                        {
                            System.out.println("To move one of the four directions type up, down, left, or right. \nTo search the tile you are on type search or look. \nYour objective is to escape the series of rooms by gathering clues and tools.");
                            System.out.println("There may be some secrets hidden within each room, finding them will increase your score.");
                        } else if (userInput.equalsIgnoreCase("suicide")) //another way to exit 0_0
                        {
                            System.out.println("You bash your head into the ground until you pass out and die.");
                            for (int j = 0; j < turnCount; j++) {
                                userInput = "exit";
                            }
                        } else if (userInput.equalsIgnoreCase("run")) {
                            if (runToggle) {
                                runToggle = false;
                                out.println("You stop running");
                            } else {
                                runToggle = true;
                                out.println("You start to run");
                            }
                        } else if (userInput.equalsIgnoreCase("walk")) {
                            runToggle = false;
                            out.println("You begin to walk");
                        } else if (userInput.equalsIgnoreCase("help") || userInput.equalsIgnoreCase("let me out")) //easter egg
                        {
                            out.println("Your cries echo against the cold, unforgiving walls. There is no one to hear them.");
                        } else if (userInput.equalsIgnoreCase("debug")) //debug values
                        {
                            //current x/y values
                            out.println("X: " + currentIntX);
                            out.println("Y: " + currentIntY);

                            out.println(tempRoom); //prints tile's index

                            out.println("Running: " + runToggle);

                            out.println("Code: " + code);

                            out.println("Digits: " + digit0 + " " + digit1 + " " + digit2 + " " + digit3);
                        } else if (userInput.equalsIgnoreCase("Map")) //temp map for debugging
                        {
                            out.println("1 - Empty Space"
                                    + "\n2 - Door"
                                    + "\n9 - Wall"
                                    + "\n3 - Player"); //key

                            for (int j = 0; j < y; j++) //Y
                            {
                                for (int k = 0; k < x; k++) //X
                                {
                                    if (k == (x - 1)) {
                                        out.print(room[j][k]); //prints room value for last in line
                                        out.println(""); //new line
                                    } else {
                                        out.print(room[j][k]); //prints room value
                                    }
                                }
                            }
                        } else if (!(userInput.equalsIgnoreCase("exit")) && !(userInput.equalsIgnoreCase("exity"))) //handles unrecognized command
                        {
                            out.println("What was I doing again? I can't remember...");
                        }
                        if (tempRoom == 1 && !(userInput.equalsIgnoreCase("exit")) && !(userInput.equalsIgnoreCase("exity"))) {
                            out.println("Still in the room.");
                        }
                        if (!(userInput.equalsIgnoreCase("exit")) && !(userInput.equalsIgnoreCase("exity"))) //saves room's temp value and allows player to advance w/o affecting that value
                        {
                            room[tempIntY][tempIntX] = tempRoom; //resets room to value saved when tile moved onto

                            tempRoom = room[currentIntY][currentIntX]; //sets temp value to new move

                            room[currentIntY][currentIntX] = 3; //sets player location
                        }
                        break;
                    case 1:
                        if ((input.equalsIgnoreCase("Y") || input.equalsIgnoreCase("yes"))) {
                            jtaDisplay.setText(jtaDisplay.getText() + "\nEnter the code: ");
                            option = 2;
                        } else {//moved into wall
                            jtaDisplay.setText(jtaDisplay.getText() + "\nThere's a wall there.");
                        }
                        option = 0;
                        break;

                    case 2:
                        if (Integer.valueOf(input) == code) {
                            //new limits for room
                            int NewLimX = (int) (Math.random() * (x + 2)) + x;
                            int NewLimY = (int) (Math.random() * (y + 2)) + y;

                            //door wall
                            northDoor = true;

                            //recursively generates new room
                            createRoom(NewLimY, NewLimX);

                            input = "exit";
                        } else {
                            jtaDisplay.setText(jtaDisplay.getText() + "Lol, no");
                        }
                        option = 0;
                        break;
                }
                jtfInput.setText("");
            }
        }

        /**
         * Listens for enter key pressed
         *
         * @param ke KeyEvent invoked
         */
        @Override
        public void keyPressed(KeyEvent ke) {
            if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                actionPerformed(sendOverride);
            }
        }

        /**
         * Necessary override
         *
         * @param ke KeyEvent invoked
         */
        @Override
        public void keyTyped(KeyEvent ke) {
        }

        /**
         * Necessary override
         *
         * @param ke KeyEvent invoked
         */
        @Override
        public void keyReleased(KeyEvent ke) {
        }
    }
}
