package dnet.mt.hi.asmifier;

import java.util.Properties;

public class SetProperties {

    private static volatile SecurityManager security;
    private static Properties props;

    public static SecurityManager getSecurityManager() {
        return security;
    }

    private static native Properties initProperties(Properties props);

    public static void setProperties(Properties props) {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPropertiesAccess();
        }
        if (props == null) {
            props = new Properties();
            initProperties(props);
        }
        SetProperties.props = props;
    }

}
