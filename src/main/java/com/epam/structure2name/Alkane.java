package com.epam.structure2name;

import com.epam.indigo.IndigoObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import org.json.simple.parser.ParseException;

public class Alkane extends AcyclicHydrocarbon {
    public static final String  SUFFIX = "ane", SHORT_SUFFIX = "an", 
                                LINEARITY_PREFIX = "n";
    
    public Alkane(IndigoObject molecule) throws IOException, ParseException {
        super(molecule);
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
    
    @Override
    public HashSet<Atom> getCenters(Atom start) {
        HashSet<Atom> res = new HashSet<>();
        Atom s = getDeepest(start);
        ArrayList<Atom> path = getPath(s, getDeepest(s));
        res.add(path.get(path.size() / 2));
        res.add(path.get(path.size() - 1 - path.size() / 2));
        return res;
    }
    
    @Override
    public String getShortName(ArrayList<Atom> chain) {
        ArrayList<String> substituents = new ArrayList<>();
        ArrayList<Integer> substituentCoordinates = new ArrayList<>();
        ArrayList<Integer> substituentAdjoiningPoints = new ArrayList<>();
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

    
    public static boolean isAlkane(AcyclicHydrocarbon molecule) {
        for (Bond bond : molecule.bonds()) {
            if (bond.bondOrder != Bond.SINGLE_BOND) {
                return false;
            }
        }
        return true;
    }
}
