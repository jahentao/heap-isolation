package dnet.mt.hi.analyzer.model;

import java.util.HashSet;
import java.util.Set;

public class TypeNode {

    public Class type;
    public Set<TypeNode> directParents = new HashSet<>();
    public Set<TypeNode> directChildren = new HashSet<>();

    public TypeNode(Class type) {
        this.type = type;
    }

    public Set<Class> getAllParents() {
        Set<Class> result = new HashSet<>();

        for (TypeNode directParent : directParents) {
            result.add(directParent.type);
            result.addAll(directParent.getAllParents());
        }

        return result;
    }

    public Set<Class> getAllChildren() {
        Set<Class> result = new HashSet<>();

        for (TypeNode directChild : directChildren) {
            result.add(directChild.type);
            result.addAll(directChild.getAllChildren());
        }

        return result;
    }

}
