/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.structure2name;

import java.util.HashSet;

/**
 *
 * @author ARK1
 */
public class Atom {
    
    public final int id;
    public Molecule molecule = null;
    HashSet<Bond> bonds = new HashSet<>();

    public Molecule getMolecule() {
        return molecule;
    }

    public Iterable<Bond> bonds() {
        return bonds;
    }

    public Atom() {
        this(0);
    }

    public Atom(int id) {
        this.id = id;
    }

    public boolean isConnected(Atom atom) {
        return bonds.contains(new Bond(this, atom));
    }

    @Override
    public int hashCode() {
        return id;
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
        final Atom other = (Atom) obj;
        if (molecule != other.molecule) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        return true;
    }
    
    public HashSet<Atom> neighbours() {
        HashSet<Atom> res = new HashSet<>();
        bonds.stream().forEach((bond) -> {
            res.add(this.equals(bond.a) ? bond.b : bond.a);
        });
        return res;
    }
}
