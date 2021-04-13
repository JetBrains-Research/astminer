package astminer.examples;

import astminer.cli.LabeledResult;
import astminer.common.model.*;
import astminer.parse.java.GumTreeJavaParser;
import astminer.storage.*;
import astminer.storage.path.CsvPathStorage;
import astminer.storage.path.PathBasedStorage;
import astminer.storage.path.PathBasedStorageConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

//Retrieve paths from Java files, using a GumTree parser.
public class AllJavaFiles {
    private static final String INPUT_FOLDER = "src/test/resources/gumTreeMethodSplitter";
    private static final String OUTPUT_FOLDER = "out_examples/allJavaFiles_GumTree_java";

    public static void runExample() {
        final PathBasedStorageConfig config = new PathBasedStorageConfig(5, 5, Long.MAX_VALUE, Long.MAX_VALUE, Integer.MAX_VALUE);
        final PathBasedStorage pathStorage = new CsvPathStorage(OUTPUT_FOLDER, config, TokenProcessor.LeaveOriginal);

        final Path inputFolder = Paths.get(INPUT_FOLDER);

        FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                Node fileTree = new GumTreeJavaParser().parseInputStream(new FileInputStream(file.toFile()));
                if (fileTree == null) {
                    return FileVisitResult.CONTINUE;
                }

                String filePath = file.toAbsolutePath().toString();
                pathStorage.store(new LabeledResult<>(fileTree, filePath, filePath));

                return FileVisitResult.CONTINUE;
            }
        };

        try {
            Files.walkFileTree(inputFolder, fileVisitor);
            pathStorage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
