package dnet.mt.hi.analyzer;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class StaticFieldPropertiesExtractor extends ModifierVisitor<Void> {

    private Set<String> bootstrapClassNames;
    static final Set<StaticFieldProperties> fields = new HashSet<>();

    StaticFieldPropertiesExtractor(Set<String> bootstrapClassNames) {
        this.bootstrapClassNames = bootstrapClassNames;
    }

    public Visitable visit(final ClassOrInterfaceDeclaration classOrInterfaceDeclaration, final Void arg) {

        Optional<String> fullyQualifiedName = classOrInterfaceDeclaration.getFullyQualifiedName();
        if (fullyQualifiedName.isPresent() && isBootstrapClass(fullyQualifiedName.get())) {
            classOrInterfaceDeclaration.findAll(FieldDeclaration.class).
                    stream().filter(fd -> fd.isStatic()).forEach(fd -> {
                        fd.getVariables().forEach(v -> addProperties(fd, v));
                    });
        }

        return super.visit(classOrInterfaceDeclaration, arg);
    }

    private void addProperties(FieldDeclaration fd, VariableDeclarator v) {
        Optional<TypeDeclaration> typeDeclaration = fd.findAncestor(TypeDeclaration.class);
        if (typeDeclaration.isPresent()) {

            StaticFieldProperties properties = new StaticFieldProperties();

            Optional<String> fullyQualifiedName = typeDeclaration.get().getFullyQualifiedName();
            properties.owner = fullyQualifiedName.orElseGet(() -> typeDeclaration.get().getNameAsString());

            properties.isFinal = fd.isFinal();
            properties.isArray = v.getType().isArrayType();
            properties.access = retrieveAccess(fd);
            properties.type = v.getTypeAsString();
            properties.name = v.getNameAsString();

            synchronized (fields) {
                fields.add(properties);
            }

        }
    }

    private FieldAccess retrieveAccess(FieldDeclaration fd) {
        return fd.isPublic() ? FieldAccess.PUBLIC :
                fd.isProtected() ? FieldAccess.PROTECTED :
                        fd.isPrivate() ? FieldAccess.PRIVATE : FieldAccess.PACKAGE;
    }

    private boolean isBootstrapClass(String fullyQualifiedName) {
        if (bootstrapClassNames.contains(fullyQualifiedName)) {
            return true;
        }
        return bootstrapClassNames.stream().anyMatch(s -> fullyQualifiedName.startsWith(s + "$"));
    }

}
