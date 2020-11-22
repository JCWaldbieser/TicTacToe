// ******************************************************
// Josh Waldbieser
//
// Node.java
// Represents a node in the game tree of 3x3 Tic-Tac-Toe. 
// Used by GameTree.java and TicTacToe.java.
// ******************************************************

import java.util.*;

public class Node {

   // This is the state at this node. The pattern of subscripts is:
   // 0 1 2
   // 3 4 5
   // 6 7 8
   // A 1 represents an X, and a 2 represents an O.
   private byte[] state = new byte[9];

   private Node parent;
   // The size of children can be from 0 (if it is a final state) to 9.
   private ArrayList<Node> children = new ArrayList<Node>();
   
   // Determined in the set_value method. If there are multiple "best" paths to take,
   // this will always be the first of them.
   private Node best_move;
   
   // Also determined in the set_value method. If there are multiple "best" paths to take,
   // one will be chosen at random, at a pseudo-uniform distribution. This is a mixed strategy.
   private Node mixed_best_move;
   
   public enum Values {
      LOSS,
      DRAW,
      WIN
   }
   
   // The value is from X's perspective.
   private Values value;
   private boolean is_leaf;
   private int depth;
   
   // Some of these methods may not be useful. Adding them anyway just because.
   
   // The nodes in this tree are designed to be two-way to make operations easier.
   
   public Node(byte[] new_state, int node_depth) {
      state = new_state;
      depth = node_depth;
      
      // Checking if this is a leaf node. It's not a leaf if any squares are blank and
      // it hasn't been won or lost yet.
      is_leaf = true;
      // If it hasn't yet been won or lost...
      if(is_win_loss() == null) {
         // If it has at least one blank square...
         for(int i = 0; i < 9; i++) {
            if(state[i] == 0) {
               is_leaf = false;
               break;
            }
         }
      }
   }
   
   public void set_parent(Node new_parent) {
      if(parent != null) {
         parent.remove_child(this);
      }
      parent = new_parent;
   }
   
   public ArrayList<Node> get_children() {
      return children;
   }
   
   public void add_child(Node child) {
      children.add(child);
      child.set_parent(this);
   }
   
   public void remove_child(Node child) {
      if(children.contains(child)) {
         children.remove(child);
      }
      else {
         System.out.println("ERR: This node does not have that child. Nothing was done.");
      }
   }
   
   public Values get_value() {
      return value;
   }
   
   // Note: set_state() is covered with the constructor.
   public byte[] get_state() {
      return state;
   }
   
   public int get_depth() {
      return depth;
   }
   
   public Node get_best_move() {
      return best_move;
   }
   
   public Node get_mixed_best_move() {
      return mixed_best_move;
   }
   
   // Two nodes are equal iff their states are identical.
   public boolean equals(Node other) {
      if(Arrays.equals(state, other.get_state())) {
         return true;
      }
      else {
         return false;
      }
   }
   
   public boolean is_leaf() {
      return is_leaf;
   }
   
   // Assigns the correct value to this node. If value has already been assigned (as part of the
   // is_win_loss() method), do nothing since the work has already been done. If this node is a leaf,
   // it can be considered a basis case for the recursive call. If it is not a leaf, do the recursive call,
   // setting this node's value to the best (or worst, depending on whose turn it is) value of the children. 
   // This should only really be called once, on the root node.
   public void set_value() {
      if(value == null) {
         if(is_leaf) {
            if(
               (state[0] == 1 && state[3] == 1 && state[6] == 1) ||
               (state[1] == 1 && state[4] == 1 && state[7] == 1) ||
               (state[2] == 1 && state[5] == 1 && state[8] == 1) ||
               (state[0] == 1 && state[1] == 1 && state[2] == 1) ||
               (state[3] == 1 && state[4] == 1 && state[5] == 1) ||
               (state[6] == 1 && state[7] == 1 && state[8] == 1) ||
               (state[0] == 1 && state[4] == 1 && state[8] == 1) ||
               (state[2] == 1 && state[4] == 1 && state[6] == 1)) {
            
               value = Values.WIN;  
            }
            else if(
               (state[0] == 2 && state[3] == 2 && state[6] == 2) ||
               (state[1] == 2 && state[4] == 2 && state[7] == 2) ||
               (state[2] == 2 && state[5] == 2 && state[8] == 2) ||
               (state[0] == 2 && state[1] == 2 && state[2] == 2) ||
               (state[3] == 2 && state[4] == 2 && state[5] == 2) ||
               (state[6] == 2 && state[7] == 2 && state[8] == 2) ||
               (state[0] == 2 && state[4] == 2 && state[8] == 2) ||
               (state[2] == 2 && state[4] == 2 && state[6] == 2)) {
               
               value = Values.LOSS;
            }
            else {
               value = Values.DRAW;
            }
         } // end if(is_leaf)
      
         // If this node is not a leaf...
         else {
         
            // Initialize each to the first child.
            best_move = children.get(0);
            mixed_best_move = children.get(0);
            
            // A subset of children; all children that give the best path.
            // mixed_best_move will be randomly chosen from this.
            ArrayList<Node> best_children = new ArrayList<Node>();
         
            // If the depth is even, it is X's turn, so it should look for the
            // best possible value.
            if(depth % 2 == 0) {
               Values max_child_value = Values.LOSS;
               // Go through each of this node's children and set each's value. If that
               // child has the best value so far, that value will be remembered and assigned after
               // all children have been examined. The best move will similarly be determined.
               for(int i = 0; i < children.size(); i++) {
                  children.get(i).set_value();
                  if(children.get(i).get_value().compareTo(max_child_value) > 0) {
                     max_child_value = children.get(i).get_value();
                     best_move = children.get(i);
                  }
               }
               
               // Go through all children again, and put all that had the best result in best_children. Then
               // choose one at random to be mixed_best_move.
               for(int i = 0; i < children.size(); i++) {
                  if(children.get(i).get_value().compareTo(max_child_value) == 0) {
                     best_children.add(children.get(i));
                  }
               }
               
               Random randy = new Random();
               mixed_best_move = best_children.get(randy.nextInt(best_children.size()));
            
               value = max_child_value;
            }
            
            // If the depth is odd, it is O's turn. This will work the same as if it were
            // X's turn, except it will look for the worst possible value (from X's perspective).
            else {
               Values min_child_value = Values.WIN;
               for(int i = 0; i < children.size(); i++) {
                  children.get(i).set_value();
                  if(children.get(i).get_value().compareTo(min_child_value) < 0) {
                     min_child_value = children.get(i).get_value();
                     best_move = children.get(i);
                  }
               }
               
               for(int i = 0; i < children.size(); i++) {
                  if(children.get(i).get_value().compareTo(min_child_value) == 0) {
                     best_children.add(children.get(i));
                  }
               }
               
               Random rand = new Random();
               mixed_best_move = best_children.get(rand.nextInt(best_children.size()));
               
               value = min_child_value;
            }
         }
         
      } // end if(value == null)

   }
   
   // Tests if this node represents a win or loss. Sets value to WIN or LOSS (but not DRAW), and
   // returns that value. If neither has occurred yet, return null.
   public Values is_win_loss() {
      if(
         (state[0] == 1 && state[3] == 1 && state[6] == 1) ||
         (state[1] == 1 && state[4] == 1 && state[7] == 1) ||
         (state[2] == 1 && state[5] == 1 && state[8] == 1) ||
         (state[0] == 1 && state[1] == 1 && state[2] == 1) ||
         (state[3] == 1 && state[4] == 1 && state[5] == 1) ||
         (state[6] == 1 && state[7] == 1 && state[8] == 1) ||
         (state[0] == 1 && state[4] == 1 && state[8] == 1) ||
         (state[2] == 1 && state[4] == 1 && state[6] == 1)) {
         
         value = Values.WIN;
         return Values.WIN;  
      }
      else if(
         (state[0] == 2 && state[3] == 2 && state[6] == 2) ||
         (state[1] == 2 && state[4] == 2 && state[7] == 2) ||
         (state[2] == 2 && state[5] == 2 && state[8] == 2) ||
         (state[0] == 2 && state[1] == 2 && state[2] == 2) ||
         (state[3] == 2 && state[4] == 2 && state[5] == 2) ||
         (state[6] == 2 && state[7] == 2 && state[8] == 2) ||
         (state[0] == 2 && state[4] == 2 && state[8] == 2) ||
         (state[2] == 2 && state[4] == 2 && state[6] == 2)) {
         
         value = Values.LOSS;
         return Values.LOSS;
      }
      else {
         return null;
      }
   }
   
   // Prints out the board at the state represented by this node. Example:
   // X O X
   // O    
   //     X
   public void print_board() {
      System.out.println(" " + translate_char(0) + " " + translate_char(1) + " " + translate_char(2));
      System.out.println(" " + translate_char(3) + " " + translate_char(4) + " " + translate_char(5));
      System.out.println(" " + translate_char(6) + " " + translate_char(7) + " " + translate_char(8));
   }
   
   // Just returns the text representation of some square in the state. Helper method
   // for print_board.
   private char translate_char(int i) throws ArrayIndexOutOfBoundsException{
   
      // Initializes it to the character for if state[i] == 0, so don't have to
      // check that later.
      char to_return = ' ';
      
      // Checking the bounds...
      if(i < 0 || i > 8) {
         throw new ArrayIndexOutOfBoundsException("Pull it together, Josh");
      }
      else {
         if(state[i] == 1) {
            to_return = 'X';
         }
         else if(state[i] == 2) {
            to_return = 'O';
         }
      }
      return to_return;
   }
   
   public Node get_this_child(byte[] new_state) {
      Node to_find = new Node(new_state, depth + 1);
      Node match = null;
      for(int i = 0; i < children.size(); i++) {
         if(Arrays.equals(to_find.get_state(), children.get(i).get_state())) {
            match = children.get(i);
            break;
         }
      }
      return match;
   }
   
}