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
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author ARK1
 */
public class Molecule {
    
    protected int maxAtomID = 0;
    protected final HashSet<Atom> atoms = new HashSet<>();
    protected final HashSet<Bond> bonds = new HashSet<>();

    protected final String[] roots;
    protected final String[] shortHydrocarbonSuffixes;
    protected final String[] hydrocarbonSuffixes;
    protected final String[] factors;
    protected final String[] multivalenceSuffixes;
    protected final String[] complexFactors;
    protected final JSONObject language;
    protected final Map<String, String> trivial_radical_names;
    
    public static final String LANGUAGE_FILE = "language.json";
    public static final String TRIVIAL_RADICAL_NAMES_FILE = 
            "trivial_radical_names.json";
    public final String SEPARATOR = "-", NUMBERS_SEPARATOR = ",",
                        GROUP_SUFFIX, CONNECTOR;
    
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
    
    private String[] parseJSONArrayOfStrings(JSONArray array) {
        String[] res = new String[array.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = array.get(i).toString();
        }
        return res;
    }
    
    public Molecule() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        language = (JSONObject) parser.parse(new FileReader(LANGUAGE_FILE));
        roots = parseJSONArrayOfStrings((JSONArray) language.get("roots"));
        shortHydrocarbonSuffixes = parseJSONArrayOfStrings(
                (JSONArray) language.get("short_hydrocarbon_suffixes"));
        hydrocarbonSuffixes = parseJSONArrayOfStrings(
                (JSONArray) language.get("hydrocarbon_suffixes"));
        factors = parseJSONArrayOfStrings((JSONArray) language.get("factors"));
        complexFactors = parseJSONArrayOfStrings(
                (JSONArray) language.get("complex_factors"));
        multivalenceSuffixes = parseJSONArrayOfStrings(
                (JSONArray) language.get("multivalence_suffixes"));
        GROUP_SUFFIX = language.get("group_suffix").toString();
        CONNECTOR = language.get("connector").toString();
        trivial_radical_names = new HashMap<>();
        for (Object e : ((JSONObject) parser.parse(new FileReader(
                TRIVIAL_RADICAL_NAMES_FILE))).entrySet()) {
            trivial_radical_names.put((String) ((Map.Entry) e).getKey(), 
                                      (String) ((Map.Entry) e).getValue());
        }
    }
    
    public Molecule(IndigoObject molecule) throws IOException, ParseException {
        this(molecule, false);
    }
    
    public Molecule(IndigoObject molecule, boolean onlyCarbons) 
            throws IOException, ParseException {
        this();
        HashMap<Integer, Atom> mol = new HashMap<>();
        for (IndigoObject atom : molecule.iterateAtoms()) {
            if ((onlyCarbons && atom.symbol().toLowerCase().equals("c")) ||
                !onlyCarbons) {
                Atom last = new Atom(atom.index());
                addAtom(last);
                mol.put(last.id, last);
            }
        }
        for (IndigoObject bond : molecule.iterateBonds()) {
            addBond(mol.get(bond.source().index()),
                    mol.get(bond.destination().index()),
                    bond.bondOrder());
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
