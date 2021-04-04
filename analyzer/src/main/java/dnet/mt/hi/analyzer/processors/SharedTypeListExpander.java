package dnet.mt.hi.analyzer.processors;

import java.lang.reflect.*;
import java.util.*;

public class SharedTypeListExpander {

    public static List<Class> sharedTypes = new LinkedList<>();

    public void init(Set<String> initialSeed) {
        initialSeed.forEach(s -> {
            Class clazz = Initializer.nameToClassMap.get(s);
            if (clazz == null) {
                System.out.println(s);
            }
            sharedTypes.add(clazz);
        });
    }

    public void expand() {

        addAllAnnotations();
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

    private void addAllAnnotations() {
        for (Class clazz : Initializer.classToNameMap.keySet()) {
            if (clazz.isAnnotation() && !Modifier.isPrivate(clazz.getModifiers())) {
                sharedTypes.add(clazz);
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
            Set<Class> allInHierarchy = TypeHierarchyBuilder.allTypeNodes.get(c).getAllParents();
            allInHierarchy.addAll(TypeHierarchyBuilder.allTypeNodes.get(c).getAllChildren());
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
            clazz = Initializer.nameToClassMap.get(pt.getRawType().getTypeName());
            for (Type t : pt.getActualTypeArguments()) {
                result.addAll(extractClasses(t));
            }
        } else {
            clazz = Initializer.nameToClassMap.get(genericType.getTypeName());
        }

        if (clazz != null) {
            result.add(clazz);
        }


        return result;
    }

}
