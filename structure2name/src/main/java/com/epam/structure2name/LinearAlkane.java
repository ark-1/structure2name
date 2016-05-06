package com.epam.structure2name;

import com.epam.indigo.IndigoObject;
import java.io.IOException;

public class LinearAlkane extends Molecule {

    public LinearAlkane(IndigoObject mol) throws IOException {
        super(mol);
    }

    @Override
    public String getName() {
        return (atoms.size() > 3 ? "n-" : "") + roots[atoms.size() - 1] + "ane";
    }
    
    
}
