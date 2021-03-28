package dnet.mt.hi.analyzer;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class NativeClassDetector extends ModifierVisitor<Void> {

    static final Set<String> nativeClassNames = new HashSet<>();

    public Visitable visit(final MethodDeclaration methodDeclaration, final Void arg) {

        if (methodDeclaration.isNative()) {
            Optional<TypeDeclaration> typeDeclaration = methodDeclaration.findAncestor(TypeDeclaration.class);
            if (typeDeclaration.isPresent()) {
                Optional<String> fullyQualifiedName = typeDeclaration.get().getFullyQualifiedName();
                if (fullyQualifiedName.isPresent()) {
                    synchronized (nativeClassNames) {
                        nativeClassNames.add(fullyQualifiedName.get() + "\n");
                    }
                }
            }
        }

        return super.visit(methodDeclaration, arg);

    }

}
