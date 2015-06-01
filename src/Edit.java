public class Edit {
    private EditOp op;
    //posição do cursor
    private int cursorRow, cursorCol;

    private Object o;

    Edit(EditOp op, int col, int line, Object o) {
        this.o = o;
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

    public Object getObject() {
        return o;
    }

    enum EditOp {INSERT, DELETE, PASTE, CUT}
}