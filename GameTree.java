// ************************************************************************************
// Josh Waldbieser
//
// GameTree.java
// Represents the game tree of 3x3 Tic-Tac-Toe. Uses Node.java; used by TicTacToe.java.
// This is where the actual "solving" of the game happens.
// ************************************************************************************

import java.util.*;

public class GameTree {

   // The state at the beginning of the tree is a blank board. The
   // depth of the root node is 0.
   private Node root = new Node(new byte[9], 0);
   
   // These may not be helpful.
   private int leaf_count = 0;
   private int node_count = 0;
   private int loss_count = 0;

   // The constructor. Calls the recursive helper method generate_tree. Once it's finished,
   // GameTree should hold the entire game tree, referenceable from root.
   public GameTree() {
      generate_tree(root, true);
   }
   
   public Node get_root() {
      return root;
   }
   
   public int get_leaf_count() {
      count_leaves(root);
      return leaf_count;
   }
   
   public int get_node_count() {
      return node_count;
   }
   
   public int get_loss_count() {
      return loss_count;
   }   
   
   // NOTE: This method generates the ENTIRE tree without taking all ending positions into account.
   // If one player wins the game before the whole board is covered, this doesn't notice and continues to
   // generate possible moves past that. This should only serve as a building block.
   private void generate_tree(Node current, boolean is_X_turn) {
   
         if(is_X_turn) {
         
            byte[] current_state = current.get_state().clone();
            byte[] new_state = current_state.clone();
         
            for(int i = 0; i < 9; i++) {
               if(current_state[i] == 0) {
               
                  new_state[i] = 1;
                  
                  Node new_node = new Node(new_state, current.get_depth() + 1);
                  node_count++;
                  generate_tree(new_node, false);
                  current.add_child(new_node);
                  
                  // reset the change to new_state
                  new_state = current_state.clone();
               }
            }
         }
         else {
         
            byte[] current_state = current.get_state().clone();
            byte[] new_state = current_state.clone();
         
            for(int j = 0; j < 9; j++) {
               if(current_state[j] == 0) {
               
                  new_state[j] = 2;
                  
                  Node new_node = new Node(new_state, current.get_depth() + 1);
                  node_count++;
                  generate_tree(new_node, true);
                  current.add_child(new_node);
                  
                  // reset the change to new_state
                  new_state = current_state.clone();
               }
            }
         
         }
   
   }
   
   public void init_values() {
      root.set_value();
   }
      
   private void count_leaves(Node current) {
      if(current.is_leaf()) {
         leaf_count++;
         if(current.get_value().compareTo(Node.Values.LOSS) == 0) {
            loss_count++;
         }
         return;
      }
      else {
         for(int i = 0; i < current.get_children().size(); i++) {
            count_leaves(current.get_children().get(i));
         }
         return;
      }
   }

}