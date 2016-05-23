package com.epam.structure2name;

import com.epam.indigo.IndigoObject;
import java.io.IOException;

public class LinearAlkane extends Alkane {
    
    
    public LinearAlkane(IndigoObject mol) throws IOException {
        super(mol);
    }

    @Override
    public String getName() {
        return (atoms.size() > 3 ? linearityPrefix : "") + 
                roots[atoms.size() - 1] + suffixes[0];
    }
    
    
}
