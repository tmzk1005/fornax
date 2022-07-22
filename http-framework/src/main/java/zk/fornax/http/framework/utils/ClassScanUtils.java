package zk.fornax.http.framework.utils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanUtils {

    private static final String RESOURCE_PROTOCOL_FILE = "file";
    private static final String RESOURCE_PROTOCOL_JAR = "jar";
    private static final String CLASS_FILE_SUFFIX = ".class";

    private ClassScanUtils() {
    }

    public static List<String> getClassNames(String pkgName) throws IOException, URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String pkgPath = pkgName.replace(".", File.separator);
        URL resource = classLoader.getResource(pkgPath);
        List<String> classNames = new ArrayList<>();
        if (null != resource) {
            String protocol = resource.getProtocol();
            if (RESOURCE_PROTOCOL_FILE.equals(protocol)) {
                classNames = getClassNamesFromDirectory(resource.toURI());
                for (int index = 0; index < classNames.size(); ++index) {
                    classNames.set(index, pkgName + "." + classNames.get(index));
                }
            } else if (RESOURCE_PROTOCOL_JAR.equals(protocol)) {
                try (JarFile jarFile = ((JarURLConnection) resource.openConnection()).getJarFile()) {
                    classNames = getClassNamesFromJar(jarFile, pkgPath);
                }

            }
        }
        return classNames;
    }

    public static List<String> getClassNamesFromDirectory(URI dirPath) {
        List<String> classNames = new ArrayList<>();
        File directory = new File(dirPath);
        String[] classFileNames = directory.list();
        if (null == classFileNames) {
            return classNames;
        }
        for (String fn : classFileNames) {
            if (!fn.endsWith(CLASS_FILE_SUFFIX)) {
                continue;
            }
            Path fnPath = Paths.get(directory.toString(), fn);
            if (Files.isDirectory(fnPath)) {
                List<String> subDirClassNames = getClassNamesFromDirectory(fnPath.toUri());
                classNames.addAll(subDirClassNames);
            } else {
                fn = fn.substring(0, fn.length() - CLASS_FILE_SUFFIX.length());
                classNames.add(fn);
            }
        }
        return classNames;
    }

    public static List<String> getClassNamesFromJar(JarFile jarFile, String pkgPath) {
        List<String> classNames = new ArrayList<>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            String entryPath = entries.nextElement().getName();
            if (!entryPath.startsWith(pkgPath) || !entryPath.endsWith(CLASS_FILE_SUFFIX)) {
                continue;
            }
            entryPath = entryPath.substring(0, entryPath.length() - CLASS_FILE_SUFFIX.length());
            entryPath = entryPath.replace(File.separator, ".");
            classNames.add(entryPath);
        }
        return classNames;
    }

}
