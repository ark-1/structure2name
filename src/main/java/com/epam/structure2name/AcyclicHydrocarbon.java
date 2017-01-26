package com.epam.structure2name;

import com.epam.indigo.IndigoObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import org.json.simple.parser.ParseException;

public class AcyclicHydrocarbon extends Molecule {
    public final BranchComparator headBranchComparator =
            new BranchComparator(true), tailBranchComparator =
            new BranchComparator(false);
    

    public static ArrayList<Bond> chainBonds(ArrayList<Atom> chain) {
        ArrayList<Bond> res = new ArrayList<>();
        for (int i = 1; i < chain.size(); i++) {
            res.add(Bond.getBond(chain.get(i - 1), chain.get(i)));
        }
        return res;
    }
    
    public String getSuffixes(ArrayList<Atom> chain) {
        ArrayList<Bond> bondChain = chainBonds(chain);
        String res = "", dres = SEPARATOR, 
               tres = SEPARATOR;
        int doubleBonds = 0, tripleBonds = 0;
        for (int i = 0; i < bondChain.size(); i++) {
            switch (bondChain.get(i).bondOrder) {
                case Bond.DOUBLE_BOND:
                    doubleBonds++;
                    dres += i + 1 + NUMBERS_SEPARATOR;
                    break;
                case Bond.TRIPLE_BOND:
                    tripleBonds++;
                    tres += i + 1 + NUMBERS_SEPARATOR;
                    break;
            }
        }
        if (doubleBonds > 1 || tripleBonds > 1) {
            res += CONNECTOR;
        }
        if (doubleBonds == 0 && tripleBonds == 0) {
            return res + hydrocarbonSuffixes[Bond.SINGLE_BOND];
        }
        if (doubleBonds > 0) {
            res += dres.substring(0, dres.length() - 1) + SEPARATOR + 
                   factors[doubleBonds];
            if (tripleBonds == 0) {
                return res + hydrocarbonSuffixes[Bond.DOUBLE_BOND];
            }
            res += shortHydrocarbonSuffixes[Bond.DOUBLE_BOND];
        }
        return res + tres.substring(0, tres.length() - 1) + SEPARATOR + 
               factors[tripleBonds] + hydrocarbonSuffixes[Bond.TRIPLE_BOND];
    }
    
    public String getShortSuffixes(ArrayList<Atom> chain) {
        ArrayList<Bond> bondChain = chainBonds(chain);
        String res = "", dres = SEPARATOR, 
               tres = SEPARATOR;
        int doubleBonds = 0, tripleBonds = 0;
        for (int i = 0; i < bondChain.size(); i++) {
            switch (bondChain.get(i).bondOrder) {
                case Bond.DOUBLE_BOND:
                    doubleBonds++;
                    dres += i + 1 + ",";
                    break;
                case Bond.TRIPLE_BOND:
                    tripleBonds++;
                    tres += i + 1 + ",";
                    break;
            }
        }
        if (doubleBonds > 1 || tripleBonds > 1) {
            res += CONNECTOR;
        }
        if (doubleBonds > 0) {
            res += dres.substring(0, dres.length() - 1) + SEPARATOR + 
                   factors[doubleBonds] + shortHydrocarbonSuffixes[Bond.DOUBLE_BOND];
        }
        if (tripleBonds > 0) {
            res += tres.substring(0, tres.length() - 1) + SEPARATOR + 
                   factors[tripleBonds] + shortHydrocarbonSuffixes[Bond.TRIPLE_BOND];
        }
        return res;
    }

    @Override
    public String getName() {
        Atom[] centers = new Atom[2];
        int j = 0;
        for (Atom center : getCenters()) {
            centers[j++] = center;
        }
        ArrayList<Atom> chain;
        if (centers[1] == null) {
            chain = getParentChain(centers[0]);
        } else {
            chain = getParentChain(centers[0], centers[1]);
        }
        return getShortName(chain) + getSuffixes(chain);
    }
    
    public boolean isComplex(String name) {
        for (char c : name.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return name.contains(SEPARATOR);
    }
    
    public String getShortName(ArrayList<Atom> chain) {
        ArrayList<String> substituents = new ArrayList<>();
        ArrayList<Integer> substituentCoordinates = new ArrayList<>();
        ArrayList<Integer> substituentAdjoiningPoints = new ArrayList<>();
        ArrayList<Boolean> substituentUnsaturated = new ArrayList<>();
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
                Bond removed = Bond.getBond(atom, neighbor);
                neighbor.bonds.remove(removed);
                substituentCoordinates.add(i + 1);
                ArrayList<Atom> groupChain = getGroupParentChain(neighbor);
                substituentAdjoiningPoints.add(
                        groupChain.indexOf(neighbor) + 1);
                String substituentSuffixes = getShortSuffixes(groupChain);
                substituentUnsaturated.add(!substituentSuffixes.equals(""));
                substituents.add(roots[groupChain.size()] + substituentSuffixes 
                                 + "$" + getShortName(groupChain) + 
                                 substituentSuffixes +
                                 (removed.bondOrder == Bond.DOUBLE_BOND ? "=" : 
                                  removed.bondOrder == Bond.TRIPLE_BOND ? "#" :
                                  ""));
                neighbor.bonds.add(removed);
            }
        }
        TreeMap<String, TreeMap<Integer, Integer>> groups = new TreeMap<>();
        for (int i = 0; i < substituents.size(); i++) {
            String substituent = substituents.get(i);
            int adjoiningPoint = substituentAdjoiningPoints.get(i);
            if (adjoiningPoint != 1) {
                substituent += (substituentUnsaturated.get(i) ? "" :
                               shortHydrocarbonSuffixes[Bond.SINGLE_BOND]) +
                               SEPARATOR + adjoiningPoint + SEPARATOR;
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
            group = pushSpecialCharacter(group);
            switch (group.charAt(group.length() - 1)) {
                case '=':
                    group = group.substring(0, group.length() - 1) + 
                            GROUP_SUFFIX + 
                            multivalenceSuffixes[Bond.DOUBLE_BOND];
                    break;
                case '#':
                    group = group.substring(0, group.length() - 1) + 
                            GROUP_SUFFIX +
                            multivalenceSuffixes[Bond.TRIPLE_BOND];
                    break;
                default:
                    group += GROUP_SUFFIX;
                    break;
            }
            if (isComplex(group)) {
                group = complexFactors[j] + "(" + group + ")";
            } else {
                group = factors[j] + group;
            }
            result += SEPARATOR + group;
        }
        return result + roots[chain.size()];
    }
    
    public static String pushSpecialCharacter(String s) {
        if (s.contains("=")) {
            return s.replace("=", "") + "=";
        }
        if (s.contains("#")) {
            return s.replace("#", "") + "#";
        }
        return s;
    }
    
    public AcyclicHydrocarbon(IndigoObject molecule) throws IOException, 
            ParseException {
        super(molecule, true);
    }
    
    public Atom getDeepest(Atom start) {
        return getPathToDeepest(start).get(0);
    }
    
    public ArrayList<Atom> getPathToDeepest(Atom start) {
        BranchDFS deepestDfs = new BranchDFS(headBranchComparator);
        deepestDfs.dfs(start);
        return deepestDfs.result().chain;
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
            return head1.chain;
        } else {
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
    
    public HashSet<Atom> getCenters() {
        return getCenters(getAnyAtom());
    }
    
    public HashSet<Atom> getCenters(Atom start) {
        HashSet<Atom> centers = (HashSet<Atom>) this.atoms.clone();
        for (Atom atom : atoms) {
            if (!centers.contains(atom)) {
                continue;
            }
            centers.retainAll(getPathToDeepest(atom));
            if (centers.isEmpty()) {
                throw new IllegalArgumentException();
            }
            if (centers.size() == 1) {
                return centers;
            }
        }
        return centers; // TODO highly ineffective
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

    public static boolean isAcyclicHydrocarbon(Molecule molecule) {
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
            if (bond.bondOrder != Bond.SINGLE_BOND &&
                bond.bondOrder != Bond.DOUBLE_BOND &&
                bond.bondOrder != Bond.TRIPLE_BOND) {
                return false;
            }
        }
        return atoms - 1 == bonds;
    }
}
