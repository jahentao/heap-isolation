package dnet.mt.hi.analyzer.st;

import java.util.HashSet;
import java.util.Set;

class TypeNode {

    Set<TypeNode> directParents = new HashSet<>();
    Set<TypeNode> directChildren = new HashSet<>();

    Class type;

    TypeNode(Class type) {
        this.type = type;
    }

    Set<Class> getAllParents() {
        Set<Class> result = new HashSet<>();

        for (TypeNode directParent : directParents) {
            result.add(directParent.type);
            result.addAll(directParent.getAllParents());
        }

        return result;
    }

    Set<Class> getAllChildren() {
        Set<Class> result = new HashSet<>();

        for (TypeNode directChild : directChildren) {
            result.add(directChild.type);
            result.addAll(directChild.getAllChildren());
        }

        return result;
    }

}
