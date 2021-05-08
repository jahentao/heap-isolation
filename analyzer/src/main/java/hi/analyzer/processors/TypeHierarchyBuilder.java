package dnet.mt.hi.analyzer.processors;

import dnet.mt.hi.analyzer.model.TypeNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TypeHierarchyBuilder {

    public static Map<Class, TypeNode> allTypeNodes = new ConcurrentHashMap<>();

    public void build() {

        for (Class clazz : Initializer.classToNameMap.keySet()) {
            TypeNode node = new TypeNode(clazz);
            processParent(node, clazz.getSuperclass());
            for (Class i : clazz.getInterfaces()) {
                processParent(node, i);
            }
            allTypeNodes.put(clazz, node);
        }

    }

    private void processParent(TypeNode childNode, Class parentClazz) {
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
