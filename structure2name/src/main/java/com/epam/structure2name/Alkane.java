package com.epam.structure2name;

import com.epam.indigo.IndigoObject;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

public class Alkane extends Molecule {
    
    public final static String SUFFIXES_FILE = "suffixes.txt",
            LINEARITY_PREFIX_FILE = "linearity prefix.txt";
    public final BranchCompartor headBranchComparator =
            new BranchCompartor(true), tailBranchCompartor =
            new BranchCompartor(false);
    
    protected final String[] suffixes;
    public final String linearityPrefix;
    
    public Alkane(IndigoObject molecule) throws IOException {
        super(molecule, true);
        suffixes = loadFromFile(SUFFIXES_FILE);
        linearityPrefix = loadFromFile(LINEARITY_PREFIX_FILE)[0];
    }
    
    private Alkane(IndigoObject molecule, boolean onlyCarbons) 
            throws IllegalAccessException, IOException {
        throw new IllegalAccessException();
    }

    @Override
    public String getName() {
        return null; // TODO 
    }
    
    public ArrayList<Atom> getPath(Atom start, final Atom target) {
        DFS<ArrayList<Atom>> pathDfs = new DFS<ArrayList<Atom>>() {
            private boolean found = false;
            private final ArrayList<Atom> res = new ArrayList<>();
            
            @Override
            public void enter(Atom v) {
                if (!found) {
                    res.add(v);
                }
                if (v.equals(v)) {
                    found = true;
                }
            }

            @Override
            public void exit(Atom v) {
                if (!found) {
                    res.remove(res.size() - 1);
                }
            }

            @Override
            public ArrayList<Atom> result() {
                return res;
            }
        };
        pathDfs.dfs(start);
        return pathDfs.result();
    }

    public int getDepth(Atom start) {
        DFS<Integer> depthDfs = new DFS() {
            private int currentDepth = 0, maxDepth = 0;
            
            @Override
            public void enter(Atom v) {
                currentDepth++;
                maxDepth = Integer.max(maxDepth, currentDepth);
            }

            @Override
            public void exit(Atom v) {
                currentDepth--;
            }

            @Override
            public Integer result() {
                return maxDepth;
            }
        };
        depthDfs.dfs(start);
        return depthDfs.result();
    }
    
    public Atom getDeepest(Atom start) {
        DFS<Atom> deepestDfs = new DFS<Atom>() {
            private int maxDepth = -1, currentDepth = 0;
            private Atom res = null;
            
            @Override
            public void enter(Atom v) {
                currentDepth++;
                if (currentDepth > maxDepth) {
                    maxDepth = currentDepth;
                    res = v;
                }
            }

            @Override
            public void exit(Atom v) {
                currentDepth--;
            }

            @Override
            public Atom result() {
                return res;
            }
        };
        deepestDfs.dfs(start);
        return deepestDfs.result();
    }
    
    public HashSet<Atom> getCentres() {
        HashSet<Atom> res = new HashSet<>();
        Atom s = Alkane.this.getDeepest(getAnyAtom());
        ArrayList<Atom> path = getPath(s, Alkane.this.getDeepest(s));
        res.add(path.get(path.size() / 2));
        res.add(path.get(path.size() - path.size() / 2));
        return res;
    }
    
    public BranchData getBranchData(Atom centre, Atom secondCentre) {
        DFS<BranchData> branchDataDfs = new DFS<BranchData>() {
            private final ArrayDeque<Atom>  stack       = new ArrayDeque<>(),
                                            stackPrevs  = new ArrayDeque<>();
            
            @Override
            public void enter(Atom v) {
                stackPrevs.add(stack.peekLast());
                stack.add(v);
            }

            @Override
            public void exit(Atom v) {
                stackPrevs.removeLast();
                stack.removeLast();
            }

            @Override
            public BranchData result() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        return null;//TODO
    }
    
    public static boolean isAlkane(Molecule molecule) {
        int atoms = 0, bonds = 0;
        for (Atom atom : molecule.atoms()) {
            atoms++;
            if (atom.atomicNumber != Atom.CARBON_NUMBER &&
                atom.atomicNumber != Atom.HYDROGEN_NUMBER) {
                return false;
            }
        }
        for (Bond bond : molecule.bonds()) {
            bonds++;
            if (bond.bondOrder != Bond.SINGLE_BOND) {
                return false;
            }
        }
        return atoms - 1 == bonds;
    }
}
