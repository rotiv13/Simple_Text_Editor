class Edit {
    private EditOp op;
    //posi��o do cursor
    private int cursorRow, cursorCol;

    private Object o;

    Edit(EditOp op, int col, int line, Object o) {
        this.o = o;
        cursorCol = col;
        cursorRow = line;
        this.op = op;
    }

    EditOp getOp() {
        return op;
    }

    int getCursorRow() {
        return cursorRow;
    }

    int getCursorCol() {
        return cursorCol;
    }

    Object getObject() {
        return o;
    }

    enum EditOp {INSERT, DELETE, PASTE, CUT}
}