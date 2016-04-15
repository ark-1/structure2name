
package com.epam.structure2name;

import com.epam.indigo.*;
import java.util.*;

public class Main {
    public static class Bond {
        public final Atom a, b;
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
            if (!Objects.equals(this.a, other.a)) {
                return false;
            }
            if (!Objects.equals(this.b, other.b)) {
                return false;
            }
            return true;
        }
        
        
    }
    
    public static class Atom {
        public final int id;
        private Molecule molecule = null;
        private HashSet<Bond> bonds = new HashSet<>();
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
        
        public Atom(Molecule molecule, int id) {
            this.id = id;
            this.molecule = molecule;
            molecule.addAtom(this);
        }
        
        public Atom(Molecule molecule) {
            this(molecule, molecule.getMaxAtomID());
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
            if (this.id != other.id) {
                return false;
            }
            return true;
        }
        
        
    }
    
    public static class Molecule {
        private int maxAtomID = 0;
        private final HashSet<Atom> atoms = new HashSet<>();
        private final HashSet<Bond> bonds = new HashSet<>();
        private final HashSet<Integer> atomIDs = new HashSet<>();
        private final HashSet<Integer> bondIDs = new HashSet<>();
        public int getMaxAtomID() {
            return maxAtomID;
        }
        public void connect(Atom a, Atom b) {
            if (!atoms.contains(a) | !atoms.contains(b)) {
                throw new IllegalArgumentException("No such atoms here");
            }
            Bond bond = new Bond(a, b);
            a.bonds.add(bond);
            b.bonds.add(bond);
            bonds.add(bond);
        }
        public Molecule(IndigoObject molecule) {
            HashMap<Integer, Atom> mol = new HashMap<>();
            for (IndigoObject atom : molecule.iterateAtoms()) {
                if (atom.symbol().toLowerCase().equals("c")) {
                    Atom last = new Atom(atom.index());
                    addAtom(last);
                    mol.put(last.id, last);
                }
            }
            for (IndigoObject bond : molecule.iterateBonds()) {
                connect(bond, b);
            }
        }
        public void addAtom(Atom atom) {
            if (atoms.contains(atom)) {
                throw new IllegalArgumentException
                    ("Atom with this ID already exists");
            }
            atoms.add(atom);
            atomIDs.add(atom.id);
            maxAtomID = Math.max(atom.id + 1, getMaxAtomID());
        }
    }
    private static IndigoObject deepest(IndigoObject atom) {
        return dfs(atom, 0, null, new HashMap<>());
    }
    private static IndigoObject dfs(IndigoObject atom, int depth,
                                    IndigoObject from,
                                    Map<Integer, Integer> depths) {
        depths.put(atom.index(), depth);
        IndigoObject result = atom;
        for (IndigoObject neighbour : atom.iterateNeighbors()) {
            if (from.index() == neighbour.index()) {
                continue;
            }
            IndigoObject distant = dfs(neighbour, depth + 1, atom, depths);
            if (depths.get(result.index()) < depths.get(distant.index())) {
                result = distant;
            }
        }
        return result;
    }
    private static boolean dfs( IndigoObject atom, ArrayList<IndigoObject> path,
                                IndigoObject to) {
        path.add(atom);
        if (atom == to) {
            return true;
        }
        for (IndigoObject neighbour : atom.iterateNeighbors()) {
            if (path.size() > 0 && path.get(path.size() - 1) == atom) {
                continue;
            }
            if (dfs(neighbour, path, atom)) {
                return true;
            }
        }
        path.remove(path.size() - 1);
        return false;
    }
    private static IndigoObject getAnyAtom(IndigoObject molecule) {
        IndigoObject result = null;
        for (IndigoObject atom : molecule.iterateAtoms()) {
            result = atom;
            break;
        }
        return result;
    }
    /*public static ArrayList<ArrayList<IndigoObject>> centreOfMolecule(
                                                        IndigoObject molecule) {
        IndigoObject deepest = deepest(getAnyAtom(molecule));
        ArrayList<IndigoObject> path = new ArrayList<>();
        dfs(deepest, path, deepest(deepest));
        IndigoObject centre1 = path.get(path.size() / 2);
        IndigoObject centre2 = path.get(path.size() / 2);
    }*/
    
    public static void main(String[] args) {
        Indigo indigo = new Indigo();
        IndigoObject mol1 = indigo.loadMolecule("NC1=CC=CC=C1");
        Map<IndigoObject, ArrayList<IndigoObject>> graph = new HashMap<>();
        for (IndigoObject atom : mol1.iterateAtoms()) {
            graph.put(atom, new ArrayList<>());
        }
        for (IndigoObject bond : mol1.iterateBonds()) {
            
        }
        System.out.println(mol1.smiles());
    }
    
}
