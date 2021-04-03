package dnet.mt.hi.analyzer.st;

import java.lang.reflect.*;
import java.util.*;

class SharedTypeListExpander {

    static List<Class> sharedTypes = new LinkedList<>();

    void init(Set<String> initialSeed) {
        initialSeed.forEach(s -> {
            sharedTypes.add(TypeHierarchyInfoExtractor.nameToClassMap.get(s));
        });
    }

    void expand() {

        ListIterator<Class> itr = sharedTypes.listIterator();
        Class clazz;
        while (itr.hasNext()) {
            clazz = itr.next();
            if (!Modifier.isPrivate(clazz.getModifiers())) {
                Set<Class> newlyFoundReachableTypes = findNewReachableTypes(clazz);
                newlyFoundReachableTypes.forEach(itr::add);
            }
        }

    }

    private Set<Class> findNewReachableTypes(Class clazz) {
        Set<Class> result = new HashSet<>();

        result.addAll(findNewReachableTypesFromFields(clazz));
        result.addAll(findNewReachableTypesFromMethods(clazz));
        result = applyInheritance(result);

        return result;
    }

    private Set<Class> applyInheritance(Set<Class> initialSet) {
        Set<Class> result = new HashSet<>();

        initialSet.forEach(c -> {
            result.add(c);
            Set<Class> allInHierarchy = TypeHierarchyInfoExtractor.allTypeNodes.get(c).getAllParents();
            allInHierarchy.addAll(TypeHierarchyInfoExtractor.allTypeNodes.get(c).getAllChildren());
            allInHierarchy.forEach(aih -> {
                if (!sharedTypes.contains(aih)) {
                    result.add(aih);
                }
            });
        });

        return result;
    }

    private Set<Class> findNewReachableTypesFromMethods(Class clazz) {
        Set<Class> result = new HashSet<>();

        Method[] methods = clazz.getDeclaredMethods();

        Set<Type> signatureTypes;
        for (Method method : methods) {
            if (!Modifier.isPrivate(method.getModifiers())) {
                signatureTypes = new HashSet<>();
                signatureTypes.add(method.getGenericReturnType());
                for (Type parameterType : method.getGenericParameterTypes()) {
                    signatureTypes.add(parameterType);
                }
                for (Type exceptionType : method.getGenericExceptionTypes()) {
                    signatureTypes.add(exceptionType);
                }
                Set<Class> signatureClasses = new HashSet<>();
                signatureTypes.forEach(s -> signatureClasses.addAll(extractClasses(s)));
                signatureClasses.forEach(t -> {
                    if (!sharedTypes.contains(t)) {
                        result.add(t);
                    }
                });
            }
        }

        return result;
    }

    private Set<Class> findNewReachableTypesFromFields(Class clazz) {
        Set<Class> result = new HashSet<>();

        Field[] fields = clazz.getDeclaredFields();

        Set<Class> signatureClasses;
        for (Field field : fields) {
            if (!Modifier.isPrivate(field.getModifiers())) {
                signatureClasses = extractClasses(field.getGenericType());
                signatureClasses.forEach(t -> {
                    if (!sharedTypes.contains(t)) {
                        result.add(t);
                    }
                });
            }
        }

        return result;
    }

    private Set<Class> extractClasses(Type genericType) {
        Set<Class> result = new HashSet<>();

        Class clazz;
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            clazz = TypeHierarchyInfoExtractor.nameToClassMap.get(pt.getRawType().getTypeName());
            for (Type t : pt.getActualTypeArguments()) {
                result.addAll(extractClasses(t));
            }
        } else {
            clazz = TypeHierarchyInfoExtractor.nameToClassMap.get(genericType.getTypeName());
        }

        if (clazz != null) {
            result.add(clazz);
        }


        return result;
    }

}
