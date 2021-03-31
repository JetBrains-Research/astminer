package astminer.examples;

import astminer.common.model.*;
import astminer.parse.java.GumTreeJavaParser;
import astminer.storage.CountingPathStorage;
import astminer.storage.CountingPathStorageConfig;
import astminer.storage.CsvPathStorage;
import astminer.storage.LabellingResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

//Retrieve paths from Java files, using a GumTree parser.
public class AllJavaFiles {
    private static final String INPUT_FOLDER = "src/test/resources/gumTreeMethodSplitter";
    private static final String OUTPUT_FOLDER = "out_examples/allJavaFiles_GumTree_java";

    public static void runExample() {
        final CountingPathStorageConfig config = new CountingPathStorageConfig(5, 5, false, Long.MAX_VALUE, Long.MAX_VALUE, Integer.MAX_VALUE);
        final CountingPathStorage pathStorage = new CsvPathStorage(OUTPUT_FOLDER, config);

        final Path inputFolder = Paths.get(INPUT_FOLDER);

        FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                Node fileTree = new GumTreeJavaParser().parseInputStream(new FileInputStream(file.toFile()));
                if (fileTree == null) {
                    return FileVisitResult.CONTINUE;
                }

                String filePath = file.toAbsolutePath().toString();
                pathStorage.store(new LabellingResult<>(fileTree, filePath, filePath));

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
