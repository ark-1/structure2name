package com.epam.structure2name;

import java.util.Objects;

public class Bond {
    
    public final Atom a;
    public final Atom b;

    public Bond(Atom a, Atom b) {
        if (a.id < b.id) {
            this.a = a;
            this.b = b;
        } else {
            this.b = a;
            this.a = b;
        }
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
