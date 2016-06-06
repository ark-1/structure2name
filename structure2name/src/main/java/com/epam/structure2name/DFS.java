
package com.epam.structure2name;

import java.util.ArrayDeque;
import java.util.HashSet;

public abstract class DFS {

    public abstract void enter(Atom v);
    public abstract void exit(Atom v);
    
    public HashSet<Atom> visited = new HashSet<>();
    
    public void reset() {
        visited = new HashSet<>();
    }
    
    public void dfs(Atom start) {
        if (visited.contains(start)) {
            return;
        }
        ArrayDeque<Atom> stack = new ArrayDeque<>();
        stack.add(start);
        while (!stack.isEmpty()) {
            Atom v = stack.peek();
            if (visited.contains(v)) {
                exit(v);
                stack.remove();
                continue;
            }
            visited.add(v);
            enter(v);
            for (Atom neighbour : v.neighbours()) {
                if (!visited.contains(neighbour)) {
                    stack.addFirst(neighbour);
                }
            }
        }
    }
}
