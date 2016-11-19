/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.epam.indigo.Indigo;
import com.epam.indigo.IndigoObject;
import com.epam.structure2name.Alkane;
import com.epam.structure2name.Namer;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.Before;

/**
 *
 * @author ARK1
 */
public class AlkaneTest {
    Indigo indigo;
   
    @Before
    public void setUp() {
        indigo = new Indigo();
    }
   
    @Test
    public void testBasicLinearGetName() throws IOException {
        assertEquals("n-hexane", Namer.structure2name(
                indigo.loadMolecule("CCCCCC")));
    }
    
    @Test
    public void testBasicAlkaneGetName() throws IOException {
        assertEquals("3,4-dimethylhexane", Namer.structure2name(
                indigo.loadMolecule("CCC(C)C(C)CC")));
        assertEquals("4,4,7-trimethyl-6-propyldecane", Namer.structure2name(
                indigo.loadMolecule("CCCC(C)C(CCC)CC(C)(C)CCC")));
        assertEquals("2,4,4,7-tetramethyl-5-(2-methylpentan-3-yl)nonane", 
                Namer.structure2name(
                indigo.loadMolecule("CCC(C)CC(C(CC)C(C)C)C(C)(C)CC(C)C")));
    }
    
    public AlkaneTest() {
    }
    
}
