package com.epam.structure2name;

import com.epam.indigo.IndigoObject;
import java.io.IOException;

public class LinearAlkane extends Alkane {
    
    
    public LinearAlkane(IndigoObject mol) throws IOException {
        super(mol);
    }

    @Override
    public String getName() {
        return (atoms.size() > 3 ? LINEARITY_PREFIX + SEPARATOR : "") + 
                roots[atoms.size()] + SUFFIX;
    }
    
    public static boolean isLinearAlkane(Alkane alkane) {
        for (Atom atom : alkane.atoms()) {
            if (atom.neighbors().size() > 2) {
                return false;
            }
        }
        return true;
    }
}
