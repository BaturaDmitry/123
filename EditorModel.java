package sample;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class EditorModel {

    public void save(TypescriptFile file) throws IOException {
        FileWriter fw = new FileWriter(file.getPathToFile().toString(), false);
        for (String line : file.getContent()){
            fw.write(line);
            fw.write("\n");
        }
        fw.close();
    }

    public TypescriptFile load(Path pathToFile) throws IOException {
        List<String> lines = Files.readAllLines(pathToFile);
        return new TypescriptFile(pathToFile, lines);
    }

    public void close() {
        System.exit(0);
    }
}
