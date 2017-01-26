
package com.epam.structure2name;

import com.epam.indigo.*;
import java.io.IOException;
import org.json.simple.parser.ParseException;

public class Namer {
    public static Molecule initialiseMolecule(IndigoObject molecule) 
            throws IOException, ParseException {
        Molecule mol = new Molecule(molecule);
        if (AcyclicHydrocarbon.isAcyclicHydrocarbon(mol)) {
            AcyclicHydrocarbon ah = new AcyclicHydrocarbon(molecule);
            mol = ah;
            if (Alkane.isAlkane(ah)) {
                Alkane alk = new Alkane(molecule);
                mol = alk;
                if (LinearAlkane.isLinearAlkane(alk)) {
                    mol = new LinearAlkane(molecule);
                }
            }
        }
        return mol;
    }
    
    public static String structure2name(IndigoObject molecule) 
            throws IOException, ParseException {
        return initialiseMolecule(molecule).getName();
    } 
}