package com.epam.structure2name;

import com.epam.indigo.IndigoObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeMap;

public class Alkane extends Molecule {
    
    public final BranchComparator headBranchComparator =
            new BranchComparator(true), tailBranchComparator =
            new BranchComparator(false);
    
    public static final String  SUFFIX = "ane", SHORT_SUFFIX = "an",
                                GROUP_SUFFIX = "yl", LINEARITY_PREFIX = "n", 
                                SEPARATOR = "-", NUMBERS_SEPARATOR = ",";
    
    public Alkane(IndigoObject molecule) throws IOException {
        super(molecule, true);
    }
    
    private Alkane(IndigoObject molecule, boolean onlyCarbons) 
            throws IllegalAccessException, IOException {
        throw new IllegalAccessException();
    }

    @Override
    public String getName() {
        Atom[] centers = new Atom[2];
        int i = 0;
        for (Atom center : getCenters()) {
            centers[i++] = center;
        }
        if (centers[1] == null) {
            return getShortName(getParentChain(centers[0])) + SUFFIX;
        }
        return getShortName(getParentChain(centers[0], centers[1])) + SUFFIX;
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
                if (v.equals(target)) {
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
    
    public HashSet<Atom> getCenters() {
        return getCenters(getAnyAtom());
    }
    
    public HashSet<Atom> getCenters(Atom start) {
        HashSet<Atom> res = new HashSet<>();
        Atom s = Alkane.this.getDeepest(start);
        ArrayList<Atom> path = getPath(s, Alkane.this.getDeepest(s));
        res.add(path.get(path.size() / 2));
        res.add(path.get(path.size() - 1 - path.size() / 2));
        return res;
    }
    
    public boolean isComplex(String name) {
        for (char c : name.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        if (name.contains(SEPARATOR)) {
            return true;
        }
        return false;
    }
    
    public String getShortName(ArrayList<Atom> chain) {
        ArrayList<String> substituents = new ArrayList<>();
        ArrayList<Integer> substituentCoordinates = new ArrayList<>();
        ArrayList<Integer> substituentAdjoiningPoints = new ArrayList<>();
        ArrayList<String> substituentBaseNames = new ArrayList<>();
        for (int i = 0; i < chain.size(); i++) {
            Atom atom = chain.get(i);
            HashSet<Atom> neighbors = atom.neighbors();
            if (i > 0) {
                neighbors.remove(chain.get(i - 1));
            }
            if (i < chain.size() - 1) {
                neighbors.remove(chain.get(i + 1));
            }
            for (Atom neighbor : neighbors) {
                neighbor.bonds.remove(new Bond(atom, neighbor));
                substituentCoordinates.add(i + 1);
                ArrayList<Atom> groupChain = getGroupParentChain(neighbor);
                substituentAdjoiningPoints.add(
                        groupChain.indexOf(neighbor) + 1);
                substituents.add(roots[groupChain.size()] + "$" +
                                 getShortName(groupChain));
                neighbor.bonds.add(new Bond(atom, neighbor));
            }
        }
        TreeMap<String, TreeMap<Integer, Integer>> groups = new TreeMap<>();
        for (int i = 0; i < substituents.size(); i++) {
            String substituent = substituents.get(i);
            int adjoiningPoint = substituentAdjoiningPoints.get(i);
            if (adjoiningPoint != 1) {
                substituent += SHORT_SUFFIX + SEPARATOR + adjoiningPoint +
                               SEPARATOR;
            }
            int coordinate = substituentCoordinates.get(i);
            if (!groups.containsKey(substituent)) {
                groups.put(substituent, new TreeMap<>());
            }
            TreeMap<Integer, Integer> group = groups.get(substituent);
            if (!group.containsKey(coordinate)) {
                groups.get(substituent).put(coordinate, 0);
            }
            group.put(coordinate, group.get(coordinate) + 1);
        }
        int i = 0;
        String result = "";
        for (String group : groups.keySet()) {
            TreeMap<Integer, Integer> coordinates = groups.get(group);
            if (i != 0) {
                result += SEPARATOR;
            }
            i++;
            int j = 0;
            for (int coordinate : coordinates.keySet()) {
                for (int k = 0; k < coordinates.get(coordinate); k++) {
                    if (j != 0) {
                        result += NUMBERS_SEPARATOR;
                    }
                    j++;
                    result += coordinate;
                }
            }
            group = group.substring(group.indexOf("$") + 1);
            group += GROUP_SUFFIX;
            if (isComplex(group)) {
                group = complexFactors[j] + "(" + group + ")";
            } else {
                group = factors[j] + group;
            }
            result += SEPARATOR + group;
        }
        return result + roots[chain.size()];
    }
    
    public ArrayList<Atom> getParentChain(Atom center, Atom secondCenter) {
        BranchDFS branchDFS = new BranchDFS(center, 
                                            headBranchComparator);
        branchDFS.dfs(secondCenter);
        BranchData head1 = branchDFS.result();
        branchDFS = new BranchDFS(secondCenter, headBranchComparator);
        branchDFS.dfs(center);
        BranchData head2 = branchDFS.result();
        branchDFS = new BranchDFS(center, tailBranchComparator);
        branchDFS.dfs(secondCenter);
        BranchData tail1 = branchDFS.result();
        branchDFS = new BranchDFS(secondCenter, tailBranchComparator);
        branchDFS.dfs(center);
        BranchData tail2 = branchDFS.result();
        head1.connect(tail2);
        head2.connect(tail1);
        if (headBranchComparator.compare(head1, head2) > 0) {
            Collections.reverse(head1.chain);
            return head1.chain;
        } else {
            Collections.reverse(head2.chain);
            return head2.chain;
        }
    }
    
    public ArrayList<Atom> getParentChain(Atom center) {
        BranchDFS branchDFS = new BranchDFS(headBranchComparator);
        branchDFS.dfs(center);
        BranchData head = branchDFS.result();
        if (head.chain.size() > 1) {
            head.chain.remove(head.chain.size() - 1);
            branchDFS = new BranchDFS(  head.chain.get(head.chain.size() - 1), 
                                        tailBranchComparator);
            branchDFS.dfs(center);
            head.connect(branchDFS.result());
        }
        return head.chain;
    }

    public ArrayList<Atom> getGroupParentChain(Atom start) {
        HashSet<Atom> c = getCenters(start);
        if (c.size() == 1 && c.iterator().next().equals(start)) {
            return getParentChain(start);
        }
        BranchDFS branchDFS = new BranchDFS(tailBranchComparator);
        branchDFS.dfs(start);
        BranchData tail = branchDFS.result();
        BranchData head;
        if (tail.chain.size() > 1) {
            tail.chain.remove(tail.chain.size() - 1);
            branchDFS = new BranchDFS(  tail.chain.get(tail.chain.size() - 1), 
                                        headBranchComparator);
            branchDFS.dfs(start);
            head = branchDFS.result();
            head.connect(tail);
        } else {
            head = tail;
        }
        return head.chain;
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
