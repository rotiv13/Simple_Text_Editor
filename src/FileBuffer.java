import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBuffer extends Buffer {
    //true = modificado;false=inalterado
    boolean modified = false;
    //null = caminho não definido
    private Path savePath;

    FileBuffer() {
        super();
    }


    /**
     * Guarda o documento do path actual;
     */
    public void save() {
        BufferedWriter writer;
        modified = false;
        boolean first = true;
        try {
            writer = Files.newBufferedWriter(savePath, StandardCharsets.UTF_8);
            for (StringBuilder sb : getAllLines()) {
                if (first) {
                    writer.write(sb.toString());
                    first = false;
                } else {
                    writer.newLine();
                    writer.write(sb.toString());
                }
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Guardar um documento num certo path
     *
     * @param path
     */
    public void saveAs(Path path) {
        BufferedWriter writer;
        modified = false;
        boolean first = true;
        if (path == null) {
            throw new IllegalArgumentException("File.saveAs: Path == null");
        }
        try {
            writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
            for (StringBuilder sb : getAllLines()) {
                if (first) {
                    writer.write(sb.toString());
                    first = false;
                } else {
                    writer.newLine();
                    writer.write(sb.toString());
                }
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abrir um documento
     *
     * @param path
     */
    public void open(Path path) {
        BufferedReader reader = null;
        try {
            savePath = path;
            reader = Files.newBufferedReader(path);
            for (String x = reader.readLine(); x != null; x = reader.readLine()) {
                insertStr(x);
                insertChar('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void insertStr(String txt) {
        super.insertStr(txt);
        modified = true;
    }


    @Override
    public void insertChar(char c) {
        super.insertChar(c);
        modified = true;
    }


    @Override
    public void deleteChar() {
        super.deleteChar();
        modified = true;
    }

    @Override
    public void deleteCharInFront() {
        super.deleteCharInFront();
        modified = true;
    }


}
