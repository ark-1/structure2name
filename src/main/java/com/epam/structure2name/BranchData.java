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
    public int branches = 0, firstBranch = -1, lastBranch = -1;
    public ArrayList<Atom> chain = new ArrayList<>();
    private boolean isNegativeInfinity;
    
    public BranchData() {
        isNegativeInfinity = true;
    }
    
    public BranchData(Atom leaf) {
        isNegativeInfinity = false;
        chain.add(leaf);
    }
    
    public void append(Atom atom) {
        isNegativeInfinity = false;
        chain.add(atom);
    }
    
    public void addBranch() {
        isNegativeInfinity = false;
        branches++;
        lastBranch = chain.size() - 1;
        if (firstBranch == -1) {
            firstBranch = lastBranch;
        }
    }
    
    public void connect(BranchData another) {
        isNegativeInfinity = false;
        ArrayList<Atom> reverseChain = (ArrayList<Atom>) another.chain.clone();
        Collections.reverse(reverseChain);
        for (Atom atom : reverseChain) {
            chain.add(atom);
        }
        if (another.branches == 0) {
            return;
        }
        branches += another.branches;
        lastBranch =    chain.size() - 1 - another.firstBranch;
        if (firstBranch == -1) {
            firstBranch = chain.size() - 1 - another.lastBranch;
        }
    }

    boolean isNegativeInfinity() {
        return isNegativeInfinity;
    }
}
