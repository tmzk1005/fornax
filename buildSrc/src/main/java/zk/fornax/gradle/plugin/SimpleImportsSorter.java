package zk.fornax.gradle.plugin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SimpleImportsSorter {

    public static final String KEYWORD_IMPORT_PREFIX = "import ";

    public static final String LINE_SEP = "\n";

    public static final String PREFIX_JAVA = KEYWORD_IMPORT_PREFIX + "java";

    public static final String PREFIX_JAVAX = KEYWORD_IMPORT_PREFIX + "javax";

    public static final String PREFIX_STATIC = KEYWORD_IMPORT_PREFIX + "static ";

    private final String selfPackagePrefix;

    private final String prefixSelf;

    public SimpleImportsSorter() {
        String packageName = this.getClass().getPackageName();
        String[] packageNameSegments = packageName.split("\\.");
        if (packageNameSegments.length > 2) {
            selfPackagePrefix = packageNameSegments[0] + "." + packageNameSegments[1];
        } else {
            selfPackagePrefix = packageName;
        }
        this.prefixSelf = KEYWORD_IMPORT_PREFIX + selfPackagePrefix;
    }

    public String sortImportsInFormattedCode(String formattedCode) {
        // 已经经过格式化后的代码, import语句肯定全部集中在一起,中间不会夹杂其他代码或者注释
        // 在import代码块之前,也不会有多行模式的注释的中间行是以import开头的
        int importPartStart;
        if (formattedCode.startsWith(KEYWORD_IMPORT_PREFIX)) {
            importPartStart = 0;
        } else {
            importPartStart = formattedCode.indexOf(LINE_SEP + KEYWORD_IMPORT_PREFIX);
        }
        if (importPartStart == -1) {
            // No imports
            return formattedCode;
        }
        int importPartEnd = formattedCode.lastIndexOf(LINE_SEP + KEYWORD_IMPORT_PREFIX)
            + LINE_SEP.length()
            + KEYWORD_IMPORT_PREFIX.length();
        while (formattedCode.charAt(importPartEnd) != '\n') {
            ++importPartEnd;
        }
        String importPart = formattedCode.substring(importPartStart, importPartEnd);
        String firstPart = importPartStart == 0 ? "" : formattedCode.substring(0, importPartStart) + LINE_SEP;
        return firstPart + sort(importPart) + formattedCode.substring(importPartEnd + 1);
    }

    private String sort(String rawAllImportLines) {
        String[] imports = rawAllImportLines.split(LINE_SEP);
        SortContext sortContext = new SortContext();
        for (String item : imports) {
            if (!item.startsWith(KEYWORD_IMPORT_PREFIX)) {
                continue;
            }
            sortContext.addImport(item);
        }
        return sortContext.getResult();
    }

    public String getImportOrderFIleContent() {
        return "0=java\n1=javax\n2=\n3=" + selfPackagePrefix + "\n4=\\#";
    }

    class SortContext {

        private final List<String> javaImports = new ArrayList<>();

        private final List<String> javaxImports = new ArrayList<>();

        private final List<String> otherImports = new ArrayList<>();

        private final List<String> selfCodeImports = new ArrayList<>();

        private final List<String> staticImports = new ArrayList<>();

        private final List<String> result = new LinkedList<>();

        public void addImport(String item) {
            if (item.startsWith(prefixSelf)) {
                selfCodeImports.add(item);
            } else if (item.startsWith(PREFIX_JAVAX)) {
                javaxImports.add(item);
            } else if (item.startsWith(PREFIX_JAVA)) {
                javaImports.add(item);
            } else if (item.startsWith(PREFIX_STATIC)) {
                staticImports.add(item);
            } else {
                otherImports.add(item);
            }
        }

        public String getResult() {
            handleGroup(javaImports);
            handleGroup(javaxImports);
            handleGroup(otherImports);
            handleGroup(selfCodeImports);
            handleGroup(staticImports);
            return String.join("\n", result);
        }

        private void handleGroup(List<String> importsGroup) {
            if (importsGroup.isEmpty()) {
                return;
            }
            importsGroup.sort(String::compareTo);
            result.addAll(importsGroup);
            result.add("");
        }

    }

}
