package dnet.mt.hi.analyzer.st;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class TypeHierarchyInfoExtractor {

    static Map<Class, TypeNode> allTypeNodes = new ConcurrentHashMap<>();

    void extract() {

        for (Class clazz : Initializer.classToNameMap.keySet()) {
            TypeNode node = new TypeNode(clazz);
            processParent(node, clazz.getSuperclass());
            for (Class i : clazz.getInterfaces()) {
                processParent(node, i);
            }
            allTypeNodes.put(clazz, node);
        }

    }

    void processParent(TypeNode childNode, Class parentClazz) {
        if (parentClazz != null) {
            TypeNode parentNode;
            if (allTypeNodes.containsKey(parentClazz)) {
                parentNode = allTypeNodes.get(parentClazz);
            } else {
                parentNode = new TypeNode(parentClazz);
            }
            parentNode.directChildren.add(childNode);
        }
    }

}
