package dnet.mt.hi.framework;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiTenantPrintStream extends PrintStream {

    private Map<String, PrintStream> tenantStreams = new ConcurrentHashMap<>();

    InheritableThreadLocal<String> tenantId = new InheritableThreadLocal<>();

    MultiTenantPrintStream(OutputStream out) {
        super(out);
    }

    void registerTenant(String tenantId, PrintStream tenantStream) {
        tenantStreams.putIfAbsent(tenantId, tenantStream);
    }

    @Override
    public void flush() {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).flush();
        } else {
            super.flush();
        }
    }

    @Override
    public void close() {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).close();
        } else {
            super.close();
        }
    }

    @Override
    public boolean checkError() {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            return tenantStreams.get(threadOwnerId).checkError();
        } else {
            return super.checkError();
        }
    }

    @Override
    public void write(int b) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).write(b);
        } else {
            super.write(b);
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).write(buf, off, len);
        } else {
            super.write(buf, off, len);
        }
    }

    @Override
    public void print(boolean b) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).print(b);
        } else {
            super.print(b);
        }
    }

    @Override
    public void print(char c) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).print(c);
        } else {
            super.print(c);
        }
    }

    @Override
    public void print(int i) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).print(i);
        } else {
            super.print(i);
        }
    }

    @Override
    public void print(long l) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).print(l);
        } else {
            super.print(l);
        }
    }

    @Override
    public void print(float f) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).print(f);
        } else {
            super.print(f);
        }
    }

    @Override
    public void print(double d) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).print(d);
        } else {
            super.print(d);
        }
    }

    @Override
    public void print(char[] s) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).print(s);
        } else {
            super.print(s);
        }
    }

    @Override
    public void print(String s) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).print(s);
        } else {
            super.print(s);
        }
    }

    @Override
    public void print(Object obj) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).print(obj);
        } else {
            super.print(obj);
        }
    }

    @Override
    public void println() {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).println();
        } else {
            super.println();
        }
    }

    @Override
    public void println(boolean x) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).println(x);
        } else {
            super.println(x);
        }
    }

    @Override
    public void println(char x) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).println(x);
        } else {
            super.println(x);
        }
    }

    @Override
    public void println(int x) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).println(x);
        } else {
            super.println(x);
        }
    }

    @Override
    public void println(long x) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).println(x);
        } else {
            super.println(x);
        }
    }

    @Override
    public void println(float x) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).println(x);
        } else {
            super.println(x);
        }
    }

    @Override
    public void println(double x) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).println(x);
        } else {
            super.println(x);
        }
    }

    @Override
    public void println(char[] x) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).println(x);
        } else {
            super.println(x);
        }
    }

    @Override
    public void println(String x) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).println(x);
        } else {
            super.println(x);
        }
    }

    @Override
    public void println(Object x) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).println(x);
        } else {
            super.println(x);
        }
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            return tenantStreams.get(threadOwnerId).printf(format, args);
        } else {
            return super.printf(format, args);
        }
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            return tenantStreams.get(threadOwnerId).printf(l, format, args);
        } else {
            return super.printf(l, format, args);
        }
    }

    @Override
    public PrintStream format(String format, Object... args) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            return tenantStreams.get(threadOwnerId).format(format, args);
        } else {
            return super.format(format, args);
        }
    }

    @Override
    public PrintStream format(Locale l, String format, Object... args) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            return tenantStreams.get(threadOwnerId).format(l, format, args);
        } else {
            return super.format(l, format, args);
        }
    }

    @Override
    public PrintStream append(CharSequence csq) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            return tenantStreams.get(threadOwnerId).append(csq);
        } else {
            return super.append(csq);
        }
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            return tenantStreams.get(threadOwnerId).append(csq, start, end);
        } else {
            return super.append(csq, start, end);
        }
    }

    @Override
    public PrintStream append(char c) {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            return tenantStreams.get(threadOwnerId).append(c);
        } else {
            return super.append(c);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        String threadOwnerId = tenantId.get();
        if (threadOwnerId != null && tenantStreams.containsKey(threadOwnerId)) {
            tenantStreams.get(threadOwnerId).write(b);
        } else {
            super.write(b);
        }
    }
}
