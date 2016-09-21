package com.epam.structure2name;

import java.util.Objects;

public class Bond {
    
    public final Atom a;
    public final Atom b;
    public final int bondOrder;
    
    public static final int SINGLE_BOND = 1, DOUBLE_BOND = 2, TRIPLE_BOND = 3,
                            AROMATIC_BOND = 4;
    
    
    public Bond(Atom a, Atom b, int bondOrder) {
        this.bondOrder = bondOrder;
        if (a.id < b.id) {
            this.a = a;
            this.b = b;
        } else {
            this.b = a;
            this.a = b;
        }
    }

    public Bond(Atom a, Atom b) {
        this(a, b, SINGLE_BOND);
    }
    
    @Override
    public int hashCode() {
        return 997 * a.id + b.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Bond other = (Bond) obj;
        return (Objects.equals(a, other.a) && Objects.equals(b, other.b)) ||
               (Objects.equals(a, other.b) && Objects.equals(b, other.a));
    }
    
}
