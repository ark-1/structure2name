package com.epam.structure2name;

import java.util.Comparator;

/**
 *
 * @author Aleksandr_Savelev
 */
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
        int res = Integer.compare(o1.chain.size(), o2.chain.size());
        if (res != 0) {
            return res;
        }
        res = Integer.compare(o1.branches, o2.branches);
        if (res != 0) {
            return res;
        }
        if (head) {
            return -Integer.compare(o1.firstBranch, o2.firstBranch);
        } else {
            return Integer.compare(o1.lastBranch, o2.lastBranch);
        }
    }
    
}
