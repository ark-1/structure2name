/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.structure2name;

import java.util.ArrayDeque;
import java.util.HashMap;
public class BranchDFS extends DFS<BranchData> {
    public BranchDFS(Atom otherCenter, BranchComparator cmp) {
        this.cmp = cmp;
        visited.add(otherCenter);
    }
    
    private final ArrayDeque<Atom>  stack       = new ArrayDeque<>(),
            stackPrevs  = new ArrayDeque<>();
    private final HashMap<Atom, BranchData> branches = new HashMap<>();
    private final BranchComparator cmp;
    
    @Override
    public void enter(Atom v) {
        stackPrevs.add(stack.peekLast());
        stack.add(v);
        branches.put(v, BranchData.NEGATIVE_INFINITY);
    }
    
    @Override
    public void exit(Atom v) {
        if (branches.get(v) == null) {
            branches.put(v, new BranchData(v));
        }
        BranchData current = branches.get(v);
        Atom parent = stackPrevs.peekLast();
        stackPrevs.removeLast();
        stack.removeLast();
        if (parent == null) {
            return;
        }
        current.append(parent);
        if (branches.get(parent) == null) {
            branches.put(parent, current);
            return;
        }
        if (cmp.compare(current, branches.get(parent)) > 0) {
            branches.put(parent, current);
        }
        branches.get(parent).addBranch();
    }

    @Override
    public BranchData result() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
