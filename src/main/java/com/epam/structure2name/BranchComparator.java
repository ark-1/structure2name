package com.epam.structure2name;

import java.util.ArrayList;
import java.util.Comparator;

public class BranchComparator implements Comparator<BranchData> {
    public final boolean head;
    
    
    
    public BranchComparator(boolean head) {
        this.head = head;
    }
    
    @Override
    public int compare(BranchData o1, BranchData o2) {
        if (o1.isNegativeInfinity() && o2.isNegativeInfinity()) {
            return 0;
        }
        if (o1.isNegativeInfinity() ^ o2.isNegativeInfinity()) {
            return o1.isNegativeInfinity() ? -1 : 1;
        }
        int res = Integer.compare(o1.multipleBonds.size(),
                                  o2.multipleBonds.size());
        if (res != 0) {
            return res;
        }
        res = Integer.compare(o1.chain.size(), o2.chain.size());
        if (res != 0) {
            return res;
        }
        res = Integer.compare(o1.doubleBonds.size(), o2.doubleBonds.size());
        if (res != 0) {
            return res;
        }
        res = compare(o1.multipleBonds, o2.multipleBonds);
        if (res != 0) {
            return res;
        }
        res = compare(o1.doubleBonds, o2.doubleBonds);
        if (res != 0) {
            return res;
        }
        res = Integer.compare(o1.branchesCoordinates.size(),
                              o2.branchesCoordinates.size());
        if (res != 0) {
            return res;
        }
        return compare(o1.branchesCoordinates, o2.branchesCoordinates);
    }

    public int compare(ArrayList<Integer> o1, ArrayList<Integer> o2) {
        int res;
        if (head) {
            for (int i = 0; i < o1.size(); i++) {
                res = -Integer.compare(o1.get(i), o2.get(i));
                if (res != 0) {
                    return res;
                }
            }
            return 0;
        }
        for (int i = o1.size() - 1; i >= 0; i--) {
            res = Integer.compare(o1.get(i), o2.get(i));
            if (res != 0) {
                return res;
            }
        }
        return 0;
    }
    
}
