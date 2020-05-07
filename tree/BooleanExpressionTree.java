package  tree;
// Java program to construct an expression tree

import ec.EvolutionState;
import ec.gp.GPTree;
import ec.util.MersenneTwisterFast;
import automaton.Automaton;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Random;
// Java program for expression tree

public class BooleanExpressionTree {
    // A utility function to check if 'c'
    // is an operator
    public Node root;

    private List<Node> nodes = new ArrayList<>();
    private List<Node> leaves = new ArrayList<>();

    class Node {
        String value;
        Node left, right;
        int weight;

        public Node(String item) {
            value = item;
            weight = 0;
            left = right = null;
        }
    }

    public BooleanExpressionTree(String s) {
      


        Stack<Node> st = new Stack<>();
        Node t, t1, t2;
        String[] expression = toStringArray(s);

        //System.out.println("Watch string");
        /*
        for(int i=0;i<expression.length;i++){
            System.out.println("i= "+i+" Line:"+expression[i]);
        }

         */

        // Traverse through every character of input expression
        int level = 0;
        for (int i = 0; i < expression.length; i++) {
            // If operand, simply push into stack
            if (!isOperatorBinary(expression[i]) && !isBrackets(expression[i]) && !isOperatorUnary(expression[i])) {
                t = new Node(expression[i]);
                st.push(t);
                nodes.add(t);
                level++;
            } else if (isOperatorUnary(expression[i])) {
                t = new Node(expression[i]);
                nodes.add(t);
                i++;
                level++;

                if (expression[i].equals("(")) {
                    st.push(t);
                } else {
                    t1 = new Node(expression[i]);
                    nodes.add(t1);
                    t.left = t1;
                    st.push(t);
                    level++;
                }
            } else /* operator */ {
                if (expression[i].equals("(")) {
                    continue;
                } else if (expression[i].equals(")")) {
                    t1 = st.pop();
                    if (st.empty()) {
                        st.push(t1);
                    } else {
                        t2 = st.pop();
                        if (st.empty()) {
                            t2.right = t1;
                            st.push(t2);
                        } else {
                            t = st.pop();
                            t2.left = t1;
                            t.right = t2;
                            st.push(t);
                        }
                    }
                } else /* operator */ {
                    t = new Node(expression[i]);
                    nodes.add(t);
                    // Pop two top nodes
                    // Store top

                    t1 = st.pop();
                    t.left = t1;
                    level++;
                    i++;
                    if (expression[i].equals("(")) {
                        st.push(t);
                    } else if (isOperatorUnary(expression[i])) {
                        t2 = new Node(expression[i]);
                        nodes.add(t2);
                        i++;
                        level++;
                        if (expression[i].equals("(")) {
                            st.push(t);
                            st.push(t2);
                        } else {
                            Node t3;
                            t3 = new Node(expression[i]);
                            nodes.add(t3);
                            t2.left = t3;
                            t.right = t2;
                            st.push(t);
                            level++;
                        }
                    } else /* operand */ {
                        t2 = new Node(expression[i]);
                        nodes.add(t2);
                        //make them children
                        t.right = t2;
                        // t2.parent=t;

                        // Add this subexpression to stack
                        st.push(t);
                        level++;
                    }
                }
            }
        }

        //  only element will be root of expression
        t = st.peek();
        st.pop();
        t.weight = level;
        root = t;

        for (Node node : nodes) {
      //      System.out.println("Node value: " + node.value);
       //     System.out.println("Node children: " + node.left+ ", " + node.right);
            if(node.left!=null){
         //       System.out.println("Node left children: " + node.left.value);
            }
            if (node.right!=null){
         //       System.out.println("Node right children: " + node.right.value);

            }
            if (node.left == null && node.right == null) {
                leaves.add(node);
            }
        }
    }

    public void mutateLeaf(MersenneTwisterFast random) {
        Node randomLeaf = leaves.get(random.nextInt(leaves.size()));
        //System.out.println("Selected leaf: " + randomLeaf.value);
        String oldValue = randomLeaf.value;
        while (randomLeaf.value.equals(oldValue)) {
            randomLeaf.value = Automaton.automaton.getInputVars()[random.nextInt(Automaton.automaton.getInputVars().length)];
        }
    }
    public void mutateLeaf(Random random) {
        Node randomLeaf = leaves.get(random.nextInt(leaves.size()));
        System.out.println("leves = "+leaves.size());
        System.out.println("Selected leaf: " + randomLeaf.value);
        String oldValue = randomLeaf.value;
        while (randomLeaf.value.equals(oldValue)) {
            randomLeaf.value = Automaton.automaton.getInputVars()[random.nextInt(Automaton.automaton.getInputVars().length)];
        }
    }

    boolean isOperatorBinary(String c) {
        if (c.equals("AND") || c.equals("OR") || c.equals("&") || c.equals("|")) {
            return true;
        }
        return false;
    }

    boolean isOperatorUnary(String c) {
        if (c.equals("NOT") || c.equals("!")) {
            return true;
        }
        return false;
    }

    boolean isBrackets(String c) {
        if(c.equals("(") || c.equals(")")){
            return true;
        }
        return false;
    }

    public String[] toStringArray(String str) {
        ArrayList<String> nameList = new ArrayList<String>();
        String temp = "";
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != ' ') {
                temp = temp + str.charAt(i);
            } else {
                nameList.add(temp);
                temp = "";
            }
        }
        if (!temp.isEmpty()) {
            nameList.add(temp);
        }

        String[] result = nameList.toArray(new String[nameList.size()]);
        return result;
    }

    // Utility function to do inorder traversal
    void inorder(Node t) {
        if (t != null) {
            inorder(t.left);
            String s = "";
            if (t.right == null && t.left == null) {
                s += t.value;
            } else {
                s += t.value;
            }
            System.out.print(s +" ");
            inorder(t.right);
        }
    }

    public void inorder() {
        inorder(root);
        System.out.println();
    }

//    public Node changeTreeOneValue(Node t1, String value, MersenneTwisterFast random) {
//        int val = t1.weight;
//        int k = random.nextInt(val + 1);
//        boolean direction;
//        Node test = t1;
//        ArrayList<String> listDirection = new ArrayList<String>();
//        for (int i = 0; i < k; i++) {
//            if (test.left == null && test.right == null) {
//                break;
//            }
//            direction = random.nextBoolean();
//
//            if (direction) {
//                if (test.left != null) {
//                    test = test.left;
//                    listDirection.add("l");
//                } else{
//                    test = t1.right;
//                    listDirection.add("r");
//                }
//            } else{
//                if (test.right != null) {
//                    test = test.right;
//                    listDirection.add("r");
//                } else{
//                    test = test.left;
//                    listDirection.add("l");
//                }
//            }
//
//        }
//
//        String[] result = listDirection.toArray(new String[listDirection.size()]);
//
//        //the tree t2 with our value
//        Node t2 = new Node(value);
//        Stack<Node> st = new Stack();
//        st.push(t1);
//        Node tmp = t1;
//
//        for (int i = 0; i < result.length; i++) {
//            if (result[i].equals("l")) {
//                tmp = tmp.left;
//            } else {
//                tmp = tmp.right;
//            }
//            st.push(tmp);
//        }
//        st.pop();
//
//        Node helpT1, helpT2;
//        st.push(t2);
//
//        for (int i = result.length - 1; i >= 0; i--) {
//            helpT1 = st.pop();
//            helpT2 = st.pop();
//            if (result[i].equals("l")) {
//                helpT2.left = helpT1;
//            } else {
//                helpT2.right = helpT1;
//            }
//            st.push(helpT2);
//        }
//
//        helpT2 = st.pop();
//        return helpT2;
//    }

//    public void changeTreeOneValue(String t, MersenneTwisterFast random) {
//        this.root = changeTreeOneValue(root, t, random);
//    }

//    public Node changeTree(Node t1, Node t2, MersenneTwisterFast random) {
//        int val = 6;
//        int k =random.nextInt(val + 1);
//
//        Node test = t1;
//        ArrayList<String> listDirection = new ArrayList<String>();
//        boolean direction;
//
//        for (int i = 0; i < k; i++) {
//            if(test.left == null && test.right == null) {
//                break;
//            }
//            direction= random.nextBoolean();
//            if (direction) {
//                if (test.left != null) {
//                    test = test.left;
//                    listDirection.add("l");
//                } else {
//                    test = t1.right;
//                    listDirection.add("r");
//                }
//            } else {
//                if (test.right != null) {
//                    test = test.right;
//                    listDirection.add("r");
//                } else {
//                    test = test.left;
//                    listDirection.add("l");
//                }
//            }
//
//        }
//
//        String[] result = listDirection.toArray(new String[listDirection.size()]);
//
//        int k2 = random.nextInt(val + 1);
//        test=t1;
//        for (int i = 0; i < k2; i++) {
//            if (t2.left == null && t2.right == null) {
//                break;
//            }
//            direction= random.nextBoolean();
//            if (direction) {
//                if (t2.left != null) {
//                    t2 = t2.left;
//                } else {
//                    t2 = t2.right;
//                }
//            } else {
//                if (t2.right != null) {
//                    t2 = t2.right;
//                } else {
//                    t2 = t2.left;
//                }
//            }
//        }
//
//        Stack<Node> st = new Stack();
//        st.push(t1);
//        Node tmp = t1;
//
//        for (int i = 0; i < result.length; i++) {
//            if (result[i].equals("l")) {
//                tmp = tmp.left;
//            } else {
//                tmp = tmp.right;
//            }
//            st.push(tmp);
//        }
//        st.pop();
//
//        Node helpT1, helpT2;
//        st.push(t2);
//        for (int i = result.length - 1; i >= 0; i--) {
//            helpT1 = st.pop();
//            helpT2 = st.pop();
//            if (result[i].equals("l")) {
//                helpT2.left = helpT1;
//            } else {
//                helpT2.right = helpT1;
//            }
//            st.push(helpT2);
//        }
//
//        helpT2 = st.pop();
//        return helpT2;
//    }

//    public void changeTree(BooleanExpressionTree t, MersenneTwisterFast random) {
//        this.root = changeTree(root, t.root, random);
//    }

    public String makeString(Node t) {
        String result = "";
        if (t != null) {
            result += "( " + makeString(t.right) + " " + t.value + " " + makeString(t.left) + " )";
        }
        return result;
    }

    public String makeString() {
        String s = makeString(root);
        s = s.replace("(  ","( ");
        s = s.replace("  )"," )");
        return s;
    }

    public static void main(String args[]) {
//        BooleanExpressionTree et = new BooleanExpressionTree();
//        String expr="NOT pp1 AND NOT pp2 AND pp3 AND vcHome";
//        String expr1="pp1 AND pp2";
//        String expr2="vcHome AND ( NOT c1Home OR NOT c2Home )";
//        String expr3="vcEnd AND NOT ( c1Home AND c2Home )";
//
//        et.constructTree(expr);
//        BooleanExpressionTree et2 = new BooleanExpressionTree();
//
//        String smth = et.makeString();
//        System.out.println("My string : "+smth);
//
////        et.changeTreeOneValue("c1Home");
//
//        String k =et.makeString();
//        System.out.println("New string : "+k);

        String expression = "rest & ( ( k1 | mdp ) & ! ( another & facePalm ) )";
        BooleanExpressionTree tree =new BooleanExpressionTree(expression);
        System.out.println(tree.makeString());
        Random randomNumber = new Random();
        tree.mutateLeaf(randomNumber);
        System.out.println(tree.makeString());
       /*
        BooleanExpressionTree t1= new BooleanExpressionTree (k);
        String ls=t1.makeString();
        System.out.println(t1.makeString());
        BooleanExpressionTree t2= new BooleanExpressionTree (ls);
        System.out.println(t2.makeString());

        */
    }
}
