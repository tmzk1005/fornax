package zk.fornax.gradle.plugin;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;

public class JavaFormatterPlugin implements Plugin<Project> {

    private static final String JAVA_FILE_SUFFIX = ".java";

    private static final String FORMATTER_RULES_FILE = "buildSrc/qa/format/java-format.xml";

    private static final String IMPORT_ORDER_FILE = "buildSrc/qa/format/java.importorder";

    private static final String TASK_FORMAT = "fmtFormat";

    private static final String TASK_CHECK = "fmtCheck";

    private static final String TASK_CREATE_RULES_FILE = "fmtCreateRulesFile";

    private final JavaCodeFormatter javaCodeFormatter = new JavaCodeFormatter();

    @Override
    public void apply(Project project) {
        project.task(TASK_FORMAT).doLast(task -> doFormat(project.getProjectDir().toPath(), false, task));
        project.task(TASK_CHECK).doLast(task -> doFormat(project.getProjectDir().toPath(), true, task));
        Project rootProject = project.getRootProject();
        Set<Task> tasksByName = rootProject.getTasksByName(TASK_CREATE_RULES_FILE, false);
        if (tasksByName.isEmpty()) {
            Path rulesFilePath = rootProject.getProjectDir().toPath().resolve(FORMATTER_RULES_FILE);
            Path importOrderFilePath = rootProject.getProjectDir().toPath().resolve(IMPORT_ORDER_FILE);
            rootProject.task(TASK_CREATE_RULES_FILE).doLast(
                task -> createRulesFile(rulesFilePath, importOrderFilePath)
            );
        }
    }

    public void doFormat(Path srcDir, boolean dryRun, Task task) {
        Set<Path> allJavaFiles;
        try {
            allJavaFiles = getFiles(srcDir);
        } catch (IOException ioException) {
            return;
        }
        final FormatResults formatResults = new FormatResults();
        for (Path javaFile : allJavaFiles) {
            String filePathName = javaFile.toString();
            FormatResult fr = formatFile(javaFile, dryRun);
            switch (fr) {
                case UNCHANGED -> formatResults.addUnchangedFile(filePathName);
                case SUCCEED -> formatResults.addSucceed(filePathName);
                case FAILED -> formatResults.addFailed(filePathName, fr.getMessage());
            }
        }
        Logger logger = task.getLogger();
        if (dryRun) {
            showCheckResults(formatResults, logger);
        } else {
            showFormatResults(formatResults, logger);
        }
    }

    private Set<Path> getFiles(Path path) throws IOException {
        final Set<Path> javaFiles = new HashSet<>();
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Objects.requireNonNull(file);
                Objects.requireNonNull(attrs);
                if (file.getFileName().toString().endsWith(JAVA_FILE_SUFFIX)) {
                    javaFiles.add(file);
                }
                return super.visitFile(file, attrs);
            }
        });
        return javaFiles;
    }

    private void showCheckResults(FormatResults formatResults, Logger logger) {
        if (formatResults.getFailed().isEmpty() && formatResults.getSucceed().isEmpty()) {
            logger.lifecycle("All files are pretty formatted!");
            return;
        }
        if (!formatResults.getSucceed().isEmpty()) {
            String fileListStr = String.join("\n", formatResults.getSucceed());
            logger.lifecycle(
                "There is {} files was not pretty formatted: \n{}",
                formatResults.getSucceed().size(),
                fileListStr
            );
        }
        if (!formatResults.getFailed().isEmpty()) {
            for (Map.Entry<String, String> entry : formatResults.getFailed().entrySet()) {
                logger.error("Failed to check if file {} is formatted : {}", entry.getKey(), entry.getValue());
            }
        }
    }

    private void showFormatResults(FormatResults formatResults, Logger logger) {
        if (formatResults.getSucceed().isEmpty() && formatResults.getFailed().isEmpty()) {
            logger.lifecycle("No files need to reformat.");
            return;
        }
        if (!formatResults.getSucceed().isEmpty()) {
            String fileListStr = String.join("\n", formatResults.getSucceed());
            logger.lifecycle(
                "{} files formatted: \n{}",
                formatResults.getSucceed().size(),
                fileListStr
            );
        }
        if (!formatResults.getFailed().isEmpty()) {
            for (Map.Entry<String, String> entry : formatResults.getFailed().entrySet()) {
                logger.error("Failed to format file {} : {}", entry.getKey(), entry.getValue());
            }
        }
    }

    private FormatResult formatFile(Path file, boolean dryRun) {
        return javaCodeFormatter.formatFile(file, dryRun);
    }

    private void createRulesFile(Path formatRulesFile, Path importOrderFile) {
        try {
            javaCodeFormatter.writeOptionsToXml(formatRulesFile);
            javaCodeFormatter.writeImportOrderFile(importOrderFile);
        } catch (IOException ioException) {
            throw new GradleException(ioException.getMessage(), ioException);
        }
    }

}
