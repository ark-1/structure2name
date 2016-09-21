/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.structure2name;

import com.epam.indigo.IndigoObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author ARK1
 */
public class Molecule {
    
    protected int maxAtomID = 0;
    protected final HashSet<Atom> atoms = new HashSet<>();
    protected final HashSet<Bond> bonds = new HashSet<>();

    protected final String[] roots;
    
    public static final String ROOTS_FILE = "roots.txt";
    
    public int getMaxAtomID() {
        return maxAtomID;
    }

    public Iterable<Atom> atoms() {
        return (Iterable<Atom>) atoms;
    }
    
    public Iterable<Bond> bonds() {
        return (Iterable<Bond>) bonds;
    }
    
    public String getName() {
        return null; //TODO not implemented yet
    }
    
    public void addBond(Atom a, Atom b) {
        addBond(a, b, Bond.SINGLE_BOND);
    }
    
    public void addBond(Atom a, Atom b, int bondOrder) {
        if (!atoms.contains(a) | !atoms.contains(b)) {
            throw new IllegalArgumentException("No such atoms here");
        }
        Bond bond = new Bond(a, b, bondOrder);
        a.bonds.add(bond);
        b.bonds.add(bond);
        bonds.add(bond);
    }

    public static final String[] loadFromFile(String fileName) 
            throws IOException {
        String[] r = {null};
        int i = 0;
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        for (String s = in.readLine(); s != null; s = in.readLine()) {
            if (i >= r.length) {
                r = Arrays.copyOf(r, 2 * r.length);
            }
            r[i++] = s;
        }
        Arrays.copyOf(r, i);
        return r;
    }
    
    public Atom getAnyAtom() {
        for (Atom atom : atoms) {
            return atom;
        }
        return null;
    }
    
    public Molecule() throws IOException {
        roots = loadFromFile(ROOTS_FILE);
    }
    
    public Molecule(IndigoObject molecule) throws IOException {
        this(molecule, false);
    }
    
    public Molecule(IndigoObject molecule, boolean onlyCarbons) 
            throws IOException {
        this();
        HashMap<Integer, Atom> mol = new HashMap<>();
        for (IndigoObject atom : molecule.iterateAtoms()) {
            if (onlyCarbons && atom.symbol().toLowerCase().equals("c")) {
                Atom last = new Atom(atom.index());
                addAtom(last);
                mol.put(last.id, last);
            }
        }
        for (IndigoObject bond : molecule.iterateBonds()) {
            addBond(mol.get(bond.source().index()),
                    mol.get(bond.destination().index()));
        }
    }

    public void addAtom(Atom atom) {
        if (atoms.contains(atom)) {
            throw new IllegalArgumentException(
                    "Atom with this ID already exists");
        }
        atoms.add(atom);
        atom.molecule = this;
        maxAtomID = Math.max(atom.id + 1, getMaxAtomID());
    }
}
