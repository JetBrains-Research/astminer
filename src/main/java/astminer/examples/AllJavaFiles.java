package astminer.examples;

import astminer.common.model.*;
import astminer.parse.java.GumTreeJavaParser;
import astminer.paths.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.stream.Collectors;

//Retrieve paths from Java files, using a GumTree parser.
public class AllJavaFiles {
    private static final String INPUT_FOLDER = "src/test/resources/gumTreeMethodSplitter";
    private static final String OUTPUT_FOLDER = "out_examples/allJavaFiles_GumTree_java";

    public static void runExample() {
        final PathMiner miner = new PathMiner(new PathRetrievalSettings(5,5));
        final CountingPathStorage<String> pathStorage = new CsvPathStorage(OUTPUT_FOLDER, Long.MAX_VALUE, Long.MAX_VALUE);

        final Path inputFolder = Paths.get(INPUT_FOLDER);

        FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                Node fileTree = new GumTreeJavaParser().parseInputStream(new FileInputStream(file.toFile()));
                if (fileTree == null) {
                    return FileVisitResult.CONTINUE;
                }
                final Collection<ASTPath> paths = miner.retrievePaths(fileTree);
                final Collection<PathContext> pathContexts = paths
                        .stream()
                        .map(node ->
                                PathUtilKt.toPathContext(node, (Node::getToken))
                        ).collect(Collectors.toList());

                pathStorage.store(new LabeledPathContexts<>(file.toAbsolutePath().toString(), pathContexts));

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
