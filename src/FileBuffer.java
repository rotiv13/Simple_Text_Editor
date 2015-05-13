import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileBuffer extends Buffer {
    //true = modificado;false=inalterado
    boolean modified = false;
    //null = caminho não definido
    private Path savePath;
    private List<Buffer> buffer;

    /**
     * Guarda o documento do path actual;
     *
     * @throws IOException
     */
    public void save() throws IOException {
        BufferedWriter writer;
        writer = Files.newBufferedWriter(savePath, StandardCharsets.UTF_8);
        writer.flush();
    }

    /**
     * Guardar um documento num certo path
     *
     * @param path
     * @throws IOException
     */
    public void saveAs(Path path) throws IOException {
        BufferedWriter writer;
        if (path == null) {
            throw new IllegalArgumentException("File.saveAs: Path == null");
        }
        writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        writer.flush();
    }

    /**
     * Abrir um documento
     *
     * @param path
     * @throws IOException
     */
    public void open(Path path) throws IOException {
        BufferedReader reader;
        if (path == null) {
            throw new IllegalArgumentException("FileBuffer.open: Path = null");
        } else {
            savePath = path;
            reader = Files.newBufferedReader(path);
        }
        buffer.add(new Buffer(reader.readLine()));
    }

    /**
     * Override do insertChar(c) do Buffer
     *
     * @param c
     */
    @Override
    public void insertChar(char c) {
        super.insertChar(c);
        modified = true;
    }
}
