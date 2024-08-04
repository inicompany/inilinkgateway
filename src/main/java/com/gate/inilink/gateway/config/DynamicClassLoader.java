package com.gate.inilink.gateway.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;
import javax.tools.*;

public class DynamicClassLoader extends ClassLoader {

    public DynamicClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> defineClass(String name, String sourceCode) throws Exception {
        System.out.println("Starting defineClass for " + name);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        SimpleJavaFileObject fileObject = new JavaSourceFromString(name, sourceCode);
        Iterable<? extends JavaFileObject> fileObjects = Collections.singletonList(fileObject);

        System.out.println("Compiling source code for " + name);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, Collections.singletonList("-proc:none"), null, fileObjects);
        Boolean result = task.call();

        if (result == null || !result) {
            throw new RuntimeException("Compilation failed.");
        }

        fileManager.close();

        System.out.println("Compilation successful for " + name);

        // 컴파일된 바이트 코드를 가져오기
        byte[] byteCode = compileToByteCode(name, sourceCode);
        if (byteCode.length == 0) {
            throw new RuntimeException("Bytecode is empty. Compilation might have failed.");
        }

        System.out.println("Defining class " + name);
        return defineClass(name, byteCode, 0, byteCode.length);
    }

    private byte[] compileToByteCode(String className, String sourceCode) throws IOException {
        System.out.println("Starting compileToByteCode for " + className);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaFileObject fileObject = new JavaSourceFromString(className, sourceCode);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            SimpleJavaFileManager fileManager = new SimpleJavaFileManager(compiler.getStandardFileManager(diagnostics, null, null), outputStream);
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, Collections.singletonList("-proc:none"), null, Collections.singletonList(fileObject));
            boolean success = task.call();

            if (!success) {
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    System.err.println(diagnostic.getMessage(null));
                }
                throw new RuntimeException("Compilation failed. See diagnostics for details.");
            }

            System.out.println("Bytecode generated for " + className);
            return outputStream.toByteArray();
        }
    }

    private static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;

        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    private static class SimpleJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private final ByteArrayOutputStream outputStream;

        SimpleJavaFileManager(StandardJavaFileManager fileManager, ByteArrayOutputStream outputStream) {
            super(fileManager);
            this.outputStream = outputStream;
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
            return new SimpleJavaClassFileObject(className, outputStream);
        }
    }

    private static class SimpleJavaClassFileObject extends SimpleJavaFileObject {
        private final ByteArrayOutputStream outputStream;

        SimpleJavaClassFileObject(String name, ByteArrayOutputStream outputStream) {
            super(URI.create("byte:///" + name.replace('.', '/') + JavaFileObject.Kind.CLASS.extension), JavaFileObject.Kind.CLASS);
            this.outputStream = outputStream;
        }

        @Override
        public OutputStream openOutputStream() {
            return outputStream;
        }
    }
}