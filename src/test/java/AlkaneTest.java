/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.epam.indigo.Indigo;
import com.epam.indigo.IndigoObject;
import com.epam.structure2name.Alkane;
import com.epam.structure2name.Atom;
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
    public void testBasicGetName() throws IOException {
        IndigoObject mol1 = indigo.loadMolecule("CCCCCC");
        assertEquals("n-hexane", Namer.structure2name(mol1));
//        mol1 = indigo.loadMolecule("CCC(C)C(C)CC");
//        assertEquals("3,4-dimethylhexane", Namer.structure2name(mol1));
    }
    
    @Test
    public void testBasicAlkaneGetName() throws IOException {
        Alkane alk = new Alkane(indigo.loadMolecule("CCC(C)C(C)CC"));
        assertEquals("3,4-dimethylhexane", 
                alk.getName());
        alk = new Alkane(indigo.loadMolecule("CCCC(C)C(CCC)CC(C)(C)CCC"));
        System.out.println(alk.getName());
        assertEquals("4,4,7-trimethyl-6-propyldecane", 
                alk.getName());
    }
    
    public AlkaneTest() {
    }
    
}
