/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.structure2name;

import java.util.ArrayList;

/**
 *
 * @author ARK1
 */
public class BranchData {
    public int depth = 0, branches = 0, firstBranch = -1, lastBranch = -1;
    public ArrayList<Atom> chain = new ArrayList<>();
    public final boolean isNegativeInfinity;
    
    public static final BranchData NEGATIVE_INFIMITY = new BranchData();
    
    private BranchData() {
        isNegativeInfinity = true;
    }
    
    public BranchData(Atom leaf) {
        isNegativeInfinity = false;
        chain.add(leaf);
    }
}
