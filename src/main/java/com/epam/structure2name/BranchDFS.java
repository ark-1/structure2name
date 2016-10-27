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
    
    public BranchDFS(BranchComparator cmp) {
        this.cmp = cmp;
    }
    
    private final ArrayDeque<Atom>  stackLocal       = new ArrayDeque<>(),
            stackPrevs  = new ArrayDeque<>();
    private final HashMap<Atom, BranchData> branches = new HashMap<>();
    private final BranchComparator cmp;
    private Atom start = null;
    
    public void exclude(Atom v) {
        if (v != null) {
            visited.add(v);
        }
    }
    
    @Override
    public void enter(Atom v) {
        Atom parent = stackLocal.peek();
        if (parent != null) {
            stackPrevs.addFirst(parent);
        }
        stackLocal.addFirst(v);
    }
    
    @Override
    public void exit(Atom v) {
        if (branches.get(v) == null) {
            branches.put(v, new BranchData(v));
        }
        BranchData current = branches.get(v);
        Atom parent = stackPrevs.peek();
        stackLocal.pop();
        if (parent == null) {
            return;
        }
        stackPrevs.pop();
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
    public void dfs(Atom start) {
        this.start = start;
        super.dfs(start);
    }
    
    @Override
    public BranchData result() {
        return branches.get(start);
    }
}
