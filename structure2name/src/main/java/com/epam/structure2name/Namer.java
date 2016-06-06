
package com.epam.structure2name;

import com.epam.indigo.*;
import java.io.IOException;

public class Namer {
    public static Molecule initialiseMolecule(IndigoObject molecule) 
            throws IOException {
        Molecule mol = new Molecule(molecule);
        if (Alkane.isAlkane(mol)) {
            Alkane alk = new Alkane(molecule);
            mol = alk;
            if (LinearAlkane.isLinearAlkane(alk)) {
                mol = new LinearAlkane(molecule);
            }
        }
        return mol;
    }
    
    public static String structure2name(IndigoObject molecule) 
            throws IOException {
        return initialiseMolecule(molecule).getName();
    } 
}