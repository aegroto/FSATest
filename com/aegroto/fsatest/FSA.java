/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aegroto.fsatest;

import static com.aegroto.fsatest.FSATest.consolePrint;
import com.aegroto.fsatest.exceptions.FSAException;
import com.aegroto.fsatest.exceptions.FSATransitionFormatException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author lorenzo
 */
public class FSA {
    /*class LambdaDefine {
        protected String state,symbol;
    }*/
    
    @Override
    public String toString() {
        return "FSA data:\nSymbols:"+symbols+
                "\nStates:"+states+
                "\nFunction name:"+transitionFunctionName+
                "\nTransitions:"+transitions+
                "\nInitial State:"+initialState+
                "\nFinal states:"+finalStates;
    }    
    
    protected ArrayList<String> 
            symbols=new ArrayList(),            
            states=new ArrayList(),
            finalStates=new ArrayList();
    protected String initialState,transitionFunctionName;
    
    protected HashMap<String,String> transitions=new HashMap();
    
    public FSA(String strSymbols,String strStates,String strTransitionFunctionName,String strInitialState,String strFinalStates) {
        for(String symbol:strSymbols.split(",")) 
            symbols.add(symbol);
        for(String state:strStates.split(","))
            states.add(state);
        for(String finalState:strFinalStates.split(",")) 
            finalStates.add(finalState);
        
        transitionFunctionName=strTransitionFunctionName;
        initialState=strInitialState;
    }
    
    public String elaborateInput(String input) {
        String lastState=initialState,
               isFinal=" [NOT FINAL]";
        
        char[] chars=input.toCharArray();
        for(char nextChar:chars) {
            if(symbols.contains(Character.toString(nextChar))) {
                String args=lastState+","+nextChar;
                if(transitions.containsKey(args)) lastState=transitions.get(args);
                else return "WARNING! ("+args+") not defined! ";
            } else consolePrint("WARNING!"+nextChar+" is not a valid symbol! ignoring...");
        }
        if(finalStates.contains(lastState)) isFinal=" [FINAL]";
        return lastState.concat(isFinal);
    }
    
    public void setTransitions(String strTransitions) throws FSAException {
        strTransitions=strTransitions.replaceAll("\n", "");
        
        for(String transition:strTransitions.split(";")) {
            if(!transition.isEmpty()) {
                if(transition.contains("(")&&transition.contains(")")&&transition.contains("=")) {
                    String funcName=transition.split("\\(")[0],
                           args=transition.split("\\(")[1].split("\\)")[0],
                           state=transition.split("=")[1];
                    if(!funcName.equals(transitionFunctionName))
                        throw new FSATransitionFormatException("Invalid transition:"+transition+" - Function name doesn't match.");
                    else if(!args.contains(","))
                        throw new FSATransitionFormatException("Invalid transition:"+transition+" - arguments must be split by ','");
                    else if(!states.contains(state))
                        throw new FSATransitionFormatException("Invalid transition:"+transition+" - FSA doesn't have any state '"+state+"'");
                
                    transitions.put(args, state);
                } else throw new FSATransitionFormatException("Invalid transition:"+transition+" - incorrect syntax");
                //consolePrint("Got valid rule:"+transition);
            }
        }        
        //consolePrint("Successfully registered transitions");
    }
}
