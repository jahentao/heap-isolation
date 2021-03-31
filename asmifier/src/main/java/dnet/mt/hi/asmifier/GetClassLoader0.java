package dnet.mt.hi.asmifier;

import dnet.mt.hi.init.ClassLoaderFacade;

public class GetClassLoader0 {

    private transient String packageName;

    public String getPackageName() { return packageName; }

    ClassLoader getClassLoader0() {
        return ClassLoaderFacade.getClassLoader(getPackageName());
    }

}
