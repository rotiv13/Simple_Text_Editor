public class Edit {
    private EditOp op;
    //posi��o do cursor
    private int cursorRow, cursorCol;
    //
    private char c;

    enum EditOp {INSERT, DELETE}
}