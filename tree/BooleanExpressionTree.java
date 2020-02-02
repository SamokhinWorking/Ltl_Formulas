package tree;
// Java program to construct an expression tree

import javax.management.loading.MLetMBean;
import java.awt.*;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Random;
// Java program for expression tree

public class BooleanExpressionTree {
    // A utility function to check if 'c'
    // is an operator
    public Node root;

    class Node {

        String value;
        Node left, right;//,parent;
        int weight;


        Node(String item) {

            value = item;
            weight=0;
           // parent=null;
            left = right = null;
        }
    }

    boolean isOperatorBinary(String c) {
        if (c.equals("AND") || c.equals("OR")) {
            return true;
        }
        return false;
    }
    boolean isOperatorUnary(String c)
    {
        if (c.equals("NOT"))
        {
            return true;
        }
        return false;
    }

    boolean isBrackets(String c){
        if(c.equals("(") || c.equals(")")){
            return true;
        }
        return false;
    }
    public String [ ] toStringArray(String str){
        ArrayList<String> nameList = new ArrayList<String>();
        String temp="";
        for(int i=0; i<str.length();i++)
        {
            // Character.toString(str.charAt(i));
            if(str.charAt(i)!=' '){
                temp=temp+Character.toString(str.charAt(i));
            }
            else {
                nameList.add(temp);
                temp = "";
            }
        }
        if(!temp.isEmpty()){
            nameList.add(temp);
        }

        String []result=nameList.toArray(new String[nameList.size()]);
        return result;
    }
    // Utility function to do inorder traversal

    void inorder(Node t) {

        if (t != null) {

            inorder(t.left);
            //System.out.print(t.value + " ");
            String s="";
            if(t.right==null && t.left==null){

                s+=t.value;

            }

            else
            {
                s+=t.value;

            }
            System.out.print(s +" ");
            /*
            if(t.parent!=null) {
                System.out.println(t.parent.value + " <----------parent "+s+ " <-------- children");
            }
            else {
                System.out.println(s + " -----without parent");
            }
            */
            inorder(t.right);


        }


    }
    public void inorder(){
        inorder(root);
        System.out.println();
    }

    // Returns root of constructed tree for given
    // postfix expression
    Node constructTreeHelp(String str) {
        Stack<Node> st = new Stack();
        Node t, t1, t2;
        String [] expresion=toStringArray(str);

        // Traverse through every character of
        // input expression
        int level=0;
        for (int i = 0; i < expresion.length; i++) {

            // If operand, simply push into stack
            if (!isOperatorBinary(expresion[i]) && !isBrackets(expresion[i]) && !isOperatorUnary(expresion[i])) {
                t = new Node(expresion[i]);
                st.push(t);

                level++;

            }
            else if(isOperatorUnary(expresion[i])) {
                t=new Node(expresion[i]);
                i++;
                level++;

                if(expresion[i].equals("(")) {
                    st.push(t);
                }
                else {
                    t1=new Node(expresion[i]);

                    t.left=t1;

                   // t1.parent=t;
                    st.push(t);
                    level++;

                }
            }
            else // operator
            {


                if(expresion[i].equals("(")){
                    continue;
                }
                else if(expresion[i].equals(")"))
                {
                    t1=st.pop();
                    if(st.empty())
                    {
                        st.push(t1);
                       // continue;


                    }
                    else {
                        t2 = st.pop();
                        if (st.empty()) {
                            t2.right = t1;
                          //  t1.parent=t2;
                            st.push(t2);




                        } else {
                            t = st.pop();
                            t2.left = t1;
                         //   t1.parent=t2;
                            t.right = t2;
                         //   t2.parent=t;
                            st.push(t);




                        }
                    }
                }
                //operator
                else {

                    t = new Node(expresion[i]);
                    // Pop two top nodes
                    // Store top

                    t1 = st.pop();
                    t.left = t1;

                    level++;



                  //  t1.parent=t;
                    i++;
                    if (expresion[i].equals("(")) {
                        st.push(t);

                    }
                    else if (isOperatorUnary(expresion[i])) {
                        t2=new Node(expresion[i]);

                        i++;

                        level++;

                        if(expresion[i].equals("(")) {
                            st.push(t);
                            st.push(t2);

                        }else
                        {
                            Node t3;
                            t3=new Node(expresion[i]);
                            t2.left=t3;
                           // t3.parent=t2;
                            t.right=t2;
                          //  t2.parent=t;
                            st.push(t);
                            level++;

                        }
                    }
                    //operand
                    else {
                        t2 = new Node(expresion[i]);


                        //  make them children

                        t.right = t2;
                       // t2.parent=t;

                        // System.out.println(t1 + "" + t2);
                        // Add this subexpression to stack
                        st.push(t);
                        level++;


                    }
                }

            }
        }

        //  only element will be root of expression
        // tree
        t = st.peek();
        st.pop();

        t.weight=level;
        return t;
    }
    public void constructTree(String str){
        this.root=constructTreeHelp(str);
    }

    Node changeTreeOneValue (Node t1, String value){

        Node test=null;

        //take a random under the tree
        Random randomDirection = new Random();
        Random randomNumber = new Random();
        int val=t1.weight;
        int k =randomNumber.nextInt(val+1);
        boolean direction;

        //System.out.println("k= "+k);

        test=t1;
        ArrayList<String> ListDirection = new ArrayList<String>();
        for(int i=0; i<k;i++)
        {
            if(test.left ==null && test.right==null)
            {
                break;
            }
            direction= randomDirection.nextBoolean();

          //  System.out.println("result bool = "+direction);

            if(direction){
                if(test.left!=null)
                {
                    test=test.left;
                    ListDirection.add("l");
                }
                else{
                    test=t1.right;
                    ListDirection.add("r");
                }
            }
            else{
                if(test.right!=null)
                {
                    test=test.right;
                    ListDirection.add("r");
                }
                else{
                    test=test.left;
                    ListDirection.add("l");
                }
            }

        }

       //   System.out.println("what change -> "+test.value);

        String []result=ListDirection.toArray(new String[ListDirection.size()]);

        //the tree t2 with our value
        Node t2=new Node(value);

        Stack<Node> st = new Stack();

        st.push(t1);
        Node tmp=t1;

        //System.out.println("Check my labels");
        /*
        for(int i=0;i<result.length;i++)
        {
            System.out.print(result[i]+" ");
        }
        System.out.println();
        */

        for(int i=0;i<result.length;i++)
        {
            if(result[i].equals("l"))
            {
                tmp=tmp.left;
            }
            else
            {
                tmp=tmp.right;
            }
            st.push(tmp);
        }
        st.pop();


        Node helpT1,helpT2;

        st.push(t2);


        for(int i=result.length-1; i>=0;i--)
        {
            helpT1=st.pop();
            helpT2=st.pop();
            if(result[i].equals("l"))
            {
                helpT2.left=helpT1;
            }
            else
            {
                helpT2.right=helpT1;
            }
            st.push(helpT2);
        }

        helpT2=st.pop();

        return helpT2;


    }
    public void changeTreeOneValue (String t){
        this.root= changeTreeOneValue(root,t);
    }

    Node changeTree(Node t1, Node t2){

        Node test=null;

        //take a random under the tree
        Random randomDirection = new Random();
        Random randomNumber = new Random();
        int val=6;
        int k =randomNumber.nextInt(val+1);
        boolean direction;

        //System.out.println("k= "+k);
        test=t1;
        ArrayList<String> ListDirection = new ArrayList<String>();
        for(int i=0; i<k;i++)
        {
            if(test.left ==null && test.right==null)
            {
                break;
            }
            direction= randomDirection.nextBoolean();
           // System.out.println("result bool = "+direction);
            if(direction){
                if(test.left!=null)
                {
                    test=test.left;
                    ListDirection.add("l");
                }
                else{
                    test=t1.right;
                    ListDirection.add("r");
                }
            }
            else{
                if(test.right!=null)
                {
                    test=test.right;
                    ListDirection.add("r");
                }
                else{
                    test=test.left;
                    ListDirection.add("l");
                }
            }

        }
        //System.out.println("what change -> "+test.value);

        String []result=ListDirection.toArray(new String[ListDirection.size()]);

        //take a random under the tree t2
        int k2 =randomNumber.nextInt(val+1);
        // System.out.println("k2= "+k2);
        test=t1;
        for(int i=0; i<k2;i++)
        {
            if(t2.left ==null && t2.right==null)
            {
                break;
            }
            direction= randomDirection.nextBoolean();
            // System.out.println("result bool = "+direction);
            if(direction){
                if(t2.left!=null)
                {
                    t2=t2.left;
                }
                else{
                    t2=t2.right;
                }
            }
            else{
                if(t2.right!=null)
                {
                    t2=t2.right;
                }
                else{
                    t2=t2.left;
                }
            }

        }
      // System.out.println("for what change -> "+t2.value);
       // System.out.println();

        Stack<Node> st = new Stack();

        st.push(t1);
        Node tmp=t1;

        //System.out.println("Check my labels");
        /*
        for(int i=0;i<result.length;i++)
        {
            System.out.print(result[i]+" ");
        }
        System.out.println();
        */

        for(int i=0;i<result.length;i++)
        {
            if(result[i].equals("l"))
            {
                tmp=tmp.left;
            }
            else
            {
                tmp=tmp.right;
            }
            st.push(tmp);
        }
        st.pop();


        Node helpT1,helpT2;

        st.push(t2);


        for(int i=result.length-1; i>=0;i--)
        {
            helpT1=st.pop();
            helpT2=st.pop();
            if(result[i].equals("l"))
            {
                helpT2.left=helpT1;
            }
            else
            {
                helpT2.right=helpT1;
            }
            st.push(helpT2);
        }

        helpT2=st.pop();

        return helpT2;


    }

    public void changeTree(BooleanExpressionTree t){
        this.root= changeTree(root,t.root);
    }


    String makeString(Node t) {
        String result="";
        if (t != null) {

            result+="( " +makeString(t.right) + " "+ t.value+ " "+makeString(t.left)+ " )";

        }

        return result;
    }
    public String makeString(){
        String s= makeString(root);
        //System.out.println(s);
        //System.out.println();
        s=s.replace("(  ","( ");
        s=s.replace("  )"," )");

      //  System.out.println(s);
      //  System.out.println();
      //  System.out.println();

        return s;

    }


    public static void main(String args[]) {

        BooleanExpressionTree et = new BooleanExpressionTree();
        String expr="NOT pp1 AND NOT pp2 AND pp3 AND vcHome";
        String expr1="pp1 AND pp2";
        String expr2="vcHome AND ( NOT c1Home OR NOT c2Home )";
        String expr3="vcEnd AND NOT ( c1Home AND c2Home )";

        et.constructTree(expr);
        BooleanExpressionTree et2=new BooleanExpressionTree();

        String smth=et.makeString();
        System.out.println("My string : "+smth);

     //   et2.constructTree(expr);
      //  smth=et2.makeString();
      //  System.out.println("My string : "+smth);

        et.changeTreeOneValue("c1Home");

        String k =et.makeString();

        System.out.println("New string : "+k);




    }
}
