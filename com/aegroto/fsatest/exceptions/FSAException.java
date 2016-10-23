/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aegroto.fsatest.exceptions;

/**
 *
 * @author lorenzo
 */
public abstract class FSAException extends Exception{
    public FSAException(String msg) {
        super(msg);
    }
}
