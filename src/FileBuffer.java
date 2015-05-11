public class FileBuffer extends Buffer {
    boolean modified = false;

    @Override
    public void insertChar(char c) {
        super.insertChar(c);
        modified = true;
    }
}
