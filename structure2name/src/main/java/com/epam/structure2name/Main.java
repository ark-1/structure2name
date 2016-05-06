
package com.epam.structure2name;

import com.epam.indigo.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Indigo indigo = new Indigo();
        IndigoObject mol1 = indigo.loadMolecule("CCCCCC");
        LinearAlkane a = new LinearAlkane(mol1);
        System.out.println(a.getName());
    }
    
}
