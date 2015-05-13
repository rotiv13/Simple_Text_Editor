public class Edit {
    private EditOp op;
    //posição do cursor
    private int cursorRow, cursorCol;
    //
    private char c;

    enum EditOp {INSERT, DELETE}
}