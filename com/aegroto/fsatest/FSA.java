package com.aegroto.fsatest;

import static com.aegroto.fsatest.FSATest.consolePrint;
import com.aegroto.fsatest.exceptions.FSAException;
import com.aegroto.fsatest.exceptions.FSATransitionFormatException;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;

/**
 *
 * @author lorenzo
 */
public class FSA {
    @Override
    public String toString() {
        return "FSA data:\nSymbols:"+symbols+
                "\nStates:"+states+
                "\nFunction name:"+transitionFunctionName+
                "\nTransitions:"+transitions+
                "\nInitial State:"+initialState+
                "\nFinal states:"+finalStates;
    }    
    
    @Getter protected ArrayList<String> 
            symbols=new ArrayList(),            
            states=new ArrayList(),
            finalStates=new ArrayList();
    @Getter protected String initialState,transitionFunctionName;
    
    @Getter protected HashMap<String,String> transitions=new HashMap();
    
    @Getter protected int maxStringSize=0;
    
    public FSA(String strSymbols,String strStates,String strTransitionFunctionName,String strInitialState,String strFinalStates) {
        for(String symbol:strSymbols.split(",")) {
            if(symbol.length()>maxStringSize) maxStringSize=symbol.length();
            symbols.add(symbol);
        }
        for(String state:strStates.split(",")) {
            if(state.length()>maxStringSize) maxStringSize=state.length();
            states.add(state);
        }
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
                           argsBlock=transition.split("\\(")[1].split("\\)")[0],
                           state=transition.split("=")[1];
                    String[] args=argsBlock.split(",");
                    if(!funcName.equals(transitionFunctionName))
                        throw new FSATransitionFormatException("Invalid transition:"+transition+" - Function name doesn't match.");
                    else if(!argsBlock.contains(","))
                        throw new FSATransitionFormatException("Invalid transition:"+transition+" - arguments must be split by ','");                   
                    else if(!states.contains(args[0]))
                        throw new FSATransitionFormatException("Invalid transition:"+transition+" - state argument is not a valid state");   
                    else if(!symbols.contains(args[1]))
                        throw new FSATransitionFormatException("Invalid transition:"+transition+" - symbol argument is not a valid symbol (extended transitions still not supported)");   
                    else if(!states.contains(state))
                        throw new FSATransitionFormatException("Invalid transition:"+transition+" - FSA doesn't have any state '"+state+"'");
                
                    transitions.put(argsBlock, state);
                } else throw new FSATransitionFormatException("Invalid transition:"+transition+" - incorrect syntax");
                //consolePrint("Got valid rule:"+transition);
            }
        }        
        //consolePrint("Successfully registered transitions");
    }
}
