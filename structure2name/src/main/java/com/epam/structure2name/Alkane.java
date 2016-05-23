package com.epam.structure2name;

import com.epam.indigo.IndigoObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class Alkane extends Molecule {
    
    public final static String SUFFIXES_FILE = "suffixes.txt",
            LINEARITY_PREFIX_FILE = "linearity prefix.txt";
    
    protected final String[] suffixes;
    public final String linearityPrefix;
    
    public Alkane(IndigoObject mol) throws IOException {
        suffixes = loadFromFile(SUFFIXES_FILE);
        linearityPrefix = loadFromFile(LINEARITY_PREFIX_FILE)[0];
    }

    @Override
    public String getName() {
        return null; // TODO 
    }
    
    public ArrayList<Atom> getPath(Atom start, Atom target, Atom prev) {
        ArrayList<Atom> res;
        if (start.equals(target)) {
            res = new ArrayList<>();
            res.add(start);
            return res;
        }
        for (Atom neighbour : start.neighbours()) {
            if (prev.equals(neighbour)) {
                continue;
            }
            res = getPath(neighbour, target, start);
            if (res != null) {
                res.add(start);
                return res;
            }
        }
        return null;
    }
    
    public int getDepth(Atom start, Atom prev, int currentDepth) {
        int res = 0;
        for (Atom neighbour : start.neighbours()) {
            if (prev.equals(neighbour)) {
                continue;
            }
            res = Integer.max(res, getDepth(neighbour, start, currentDepth + 1));
        }
        return res;
    }
    
    public Atom getDeepest(Atom start, Atom prev, int currentDepth, int depth) {
        if (currentDepth == depth) {
            return start;
        }
        for (Atom neighbour : start.neighbours()) {
            if (prev.equals(neighbour)) {
                continue;
            }
            Atom res = getDeepest(neighbour, start, currentDepth + 1, depth);
            if (res != null) {
                return res;
            }
        }
        return null;
    }
    
    public Atom getDeepest(Atom start) {
        return getDeepest(start, null, 0, getDepth(start, null, 0));
    }
    
    public Atom getAnyAtom() {
        for (Atom atom : atoms) {
            return atom;
        }
        return null;
    }
    
    public HashSet<Atom> getCentres() {
        HashSet<Atom> res = new HashSet<>();
        Atom s = Alkane.this.getDeepest(getAnyAtom());
        ArrayList<Atom> path = getPath(s, Alkane.this.getDeepest(s), null);
        res.add(path.get(path.size() / 2));
        res.add(path.get(path.size() - path.size() / 2));
        return res;
    }
}
