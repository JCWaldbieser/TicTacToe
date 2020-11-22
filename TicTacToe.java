// *******************************************************************************
// Josh Waldbieser
// 
// TicTacToe.java
// The driver class for playing 3x3 Tic-Tac-Toe. Uses GameTree.java and Node.java.
// *******************************************************************************

import java.util.*;

public class TicTacToe {

   public static void main(String[] args) {
   
      GameTree tree = new GameTree();
      tree.init_values();
            
      Scanner input = new Scanner(System.in);
      boolean keep_playing = true;
      while(keep_playing) {
         boolean good_choice = false;
         int choice = -1;
         while(!good_choice) {
            System.out.println("Which mode do you want to run (1, 2, 3, or 4):" +
               "\n\t1. optimal" +
               "\n\t2. mixed optimal" +
               "\n\t3. optimal human" +
               "\n\t4. mixed optimal human");
            try {
               choice = input.nextInt();
               // Clear it out so the nextLine later on will work
               input.nextLine();
               if(choice < 1 || choice > 4) {
                  throw new IndexOutOfBoundsException();
               }
               good_choice = true;
            }
            catch(Exception e) {
               System.out.println("ERR: BAD INPUT");
            }
         }
         switch(choice) {
            case 1:
               System.out.println(play(tree.get_root(), true, "optimal"));
               break;
            case 2:
               System.out.println(play(tree.get_root(), true, "mixed optimal"));
               // Make a brand new tree and reinit values so it can have a different choice. Not good
               // practice at all, but it's a temporary fix, and runtime is so fast anyway there's no
               // noticeable difference.
               tree = new GameTree();
               tree.init_values();
               break;
            case 3:
               System.out.println(play(tree.get_root(), true, "optimal human"));
               break;
            case 4:
               System.out.println(play(tree.get_root(), true, "mixed optimal human"));
               tree = new GameTree();
               tree.init_values();
               break;
            default:
               System.out.println("It shouldn't have gotten here...");
         }
         
         boolean good_YN = false;
         String yn = null;
         while(!good_YN) {
            System.out.print("Keep playing? (Y/N): ");
            try {
               yn = input.nextLine();
               yn = yn.toUpperCase();
               if(!yn.equals("Y") && !yn.equals("N")) {
                  throw new IllegalArgumentException();
               }
               good_YN = true;
            }
            catch(Exception e) {
               System.out.println("ERR: BAD INPUT");
            }
         }
         
         if(yn.equals("N")) {
            keep_playing = false;
         }
      }
   }

   // The method that actually plays the game. Will eventually return the result.
   // mode: a string that specifies which config to play the game in.
   public static Node.Values play(Node current, boolean is_X_turn, String mode) {

      // Each player chooses the best move possible for each state. Deterministic; will only
      // have one path.
      if(mode.equals("optimal")) {
         System.out.println(" -----");
         current.print_board();
         if(!current.is_leaf()) {
            return play(current.get_best_move(), !is_X_turn, "optimal");
         }
         else {
            return current.get_value();
         }
      }
      
      // Each player chooses one of the possible moves that will give the best outcome. Implements
      // mixed strategy.
      else if(mode.equals("mixed optimal")) {
         System.out.println(" -----");
         current.print_board();
         if(!current.is_leaf()) {
            return play(current.get_mixed_best_move(), !is_X_turn, "mixed optimal");
         }
         else {
            return current.get_value();
         }
      }
      
      else if(mode.equals("optimal human")) {
         System.out.println(" -----");
         current.print_board();
         if(!current.is_leaf()) {
         
            // Initialize new_move to the computer's move. Will be changed in the while loop
            // if it's the user's turn.
            Node new_move = current.get_best_move();
            Scanner scan = new Scanner(System.in);
            boolean move_on = false;
            
            // If it's the user's turn, get their move.
            while(is_X_turn && !move_on) {
               System.out.print("Which square do you want to fill? (0, 1, ..., 8): ");
               int choice = scan.nextInt();
               System.out.println();
               
               // If the range is bad...
               if(choice < 0 || choice > 8) {
                  System.out.println("ERR: OUT OF BOUNDS");
               }
               
               // If that location is already filled...
               else if(current.get_state()[choice] != 0) {
                  System.out.println("ERR: ALREADY FILLED");
               }
               
               // It's a valid move, so look for the child of current that matches this proposed state.
               else {
                  byte[] new_state = Arrays.copyOf(current.get_state(), 9);
                  new_state[choice] = 1;
                  for(int i = 0; i < current.get_children().size(); i++) {
                     if(Arrays.equals(new_state, current.get_children().get(i).get_state())) {
                        new_move = current.get_children().get(i);
                     }
                  }
                  
                  // Will stop the while loop from looping
                  move_on = true;
               }
              
            }
            
            return play(new_move, !is_X_turn, "optimal human");
            
         }
         
         // If current is a leaf
         else {
            return current.get_value();
         }
      }
      
      else if(mode.equals("mixed optimal human")) {
         System.out.println(" -----");
         current.print_board();
         if(!current.is_leaf()) {
         
            // This initialization is the only difference from optimal human mode.
            Node new_move = current.get_mixed_best_move();
            Scanner scan = new Scanner(System.in);
            boolean move_on = false;
            
            while(is_X_turn && !move_on) {
               System.out.print("Which square do you want to fill? (0, 1, ..., 8): ");
               int choice = scan.nextInt();
               System.out.println();
               
               if(choice < 0 || choice > 8) {
                  System.out.println("ERR: OUT OF BOUNDS");
               }
               
               else if(current.get_state()[choice] != 0) {
                  System.out.println("ERR: ALREADY FILLED");
               }
               
               else {
                  byte[] new_state = Arrays.copyOf(current.get_state(), 9);
                  new_state[choice] = 1;
                  for(int i = 0; i < current.get_children().size(); i++) {
                     if(Arrays.equals(new_state, current.get_children().get(i).get_state())) {
                        new_move = current.get_children().get(i);
                     }
                  }
                  
                  move_on = true;
               }
              
            }
            
            return play(new_move, !is_X_turn, "optimal human");
            
         }
         else {
            return current.get_value();
         }
      }
      
      // dummy return just so it will compile
      else {
         System.out.println("It shouldn't have gotten here...");
         return null;
      }
   }

}