/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.structure2name;

import com.epam.indigo.*;

/**
 *
 * @author ARK1
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Indigo indigo = new Indigo();
        IndigoObject mol1 = indigo.loadMolecule("CCN");
        for (IndigoObject atom : mol1.iterateAtoms()) {
            System.out.println(atom.symbol());
        }
        System.out.println("helloworld");
    }
    
}
