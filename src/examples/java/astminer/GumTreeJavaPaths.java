package astminer;

import astminer.common.model.LabeledResult;
import astminer.parse.gumtree.GumTreeNode;
import astminer.parse.gumtree.java.GumTreeJavaParser;
import astminer.storage.path.Code2VecPathStorage;
import astminer.storage.path.PathBasedStorage;
import astminer.storage.path.PathBasedStorageConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

// Retrieve paths from Java files, using a GumTree parser.
public class GumTreeJavaPaths {
    private static final String INPUT_FOLDER = "src/test/resources/examples";
    private static final String OUTPUT_FOLDER = "examples_output/gumtree_java_paths_java_api";

    public static void runExample() {
        final PathBasedStorageConfig config = new PathBasedStorageConfig(5, 5, null, null, null);
        final PathBasedStorage code2vecStorage = new Code2VecPathStorage(OUTPUT_FOLDER, config);

        final Path inputFolder = Paths.get(INPUT_FOLDER);

        FileVisitor<Path> fileVisitor = new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                if (!file.getFileName().toString().endsWith(".java")) {
                    return FileVisitResult.CONTINUE;
                }
                GumTreeNode fileTree = new GumTreeJavaParser().parseInputStream(new FileInputStream(file.toFile()));
                String filePath = file.toAbsolutePath().toString();

                LabeledResult<GumTreeNode> labeledResult = new LabeledResult<>(fileTree, filePath, filePath);
                code2vecStorage.store(labeledResult);

                return FileVisitResult.CONTINUE;
            }
        };

        try {
            Files.walkFileTree(inputFolder, fileVisitor);
        } catch (IOException e) {
            System.out.println("Error while processing files: " + e.getMessage());
        } finally {
            code2vecStorage.close();
        }
    }

    public static void main(String[] args) {
        runExample();
    }
}
