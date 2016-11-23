/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.structure2name;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author ARK1
 */
public class BranchData {
    public ArrayList<Atom> chain = new ArrayList<>();
    private boolean isNegativeInfinity;
    public ArrayList<Integer> branchesCoordinates = new ArrayList<>();
    public ArrayList<Integer> doubleBonds = new ArrayList<>();
    public ArrayList<Integer> multipleBonds = new ArrayList<>();
    
    public BranchData() {
        isNegativeInfinity = true;
    }
    
    public BranchData(Atom leaf) {
        isNegativeInfinity = false;
        chain.add(leaf);
    }
    
    public void append(Atom atom) {
        switch (Bond.getBond(atom, chain.get(chain.size() - 1)).bondOrder) {
            case Bond.DOUBLE_BOND:
                doubleBonds.add(chain.size());
                multipleBonds.add(chain.size());
                break;
            case Bond.TRIPLE_BOND:
                multipleBonds.add(chain.size());
                break;
        }
        isNegativeInfinity = false;
        chain.add(atom);
    }
    
    public void addBranch() {
        isNegativeInfinity = false;
        branchesCoordinates.add(chain.size() - 1);
    }
    
    public void connect(BranchData another) {
        isNegativeInfinity = false;
        ArrayList<Atom> reverseChain = (ArrayList<Atom>) another.chain.clone();
        Collections.reverse(reverseChain);
        for (Atom atom : reverseChain) {
            chain.add(atom);
        }
        for (int i : another.branchesCoordinates) {
            branchesCoordinates.add(chain.size() - 1 - i);
        }
        for (int i : another.doubleBonds) {
            doubleBonds.add(chain.size() - i);
        }
        for (int i : another.multipleBonds) {
            multipleBonds.add(chain.size() - i);
        }
    }

    boolean isNegativeInfinity() {
        return isNegativeInfinity;
    }
}
