public class Edit {
    private EditOp op;
    //posição do cursor
    private int cursorRow, cursorCol;
    //
    private char c;

    Edit(EditOp op, int col, int line, char c) {
        this.c = c;
        cursorCol = col;
        cursorRow = line;
        this.op = op;
    }

    public EditOp getOp() {
        return op;
    }

    public int getCursorRow() {
        return cursorRow;
    }

    public int getCursorCol() {
        return cursorCol;
    }

    public char getC() {
        return c;
    }

    enum EditOp {INSERT, DELETE, PASTE, CUT, COPY}
}