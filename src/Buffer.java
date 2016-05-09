import java.util.ArrayList;
import java.util.Stack;

public class Buffer {
    private final ArrayList<StringBuilder> lines;
    private final Cursor cursor;
    //texto a ser copiado
    public StringBuilder clipBoard = new StringBuilder();
    boolean undoing = false;
    //posicao da marca do cursor
    private int markRow, markCol;
    //true=temos uma posição marcada; false= nao tem posiçao marcada
    private boolean marked;
    //ultimas operações
    private Stack<Edit> undoList;

    public Buffer() {
        lines = new ArrayList<StringBuilder>();
        StringBuilder initBuilder = new StringBuilder();
        lines.add(initBuilder);
        cursor = new Cursor(0, 0);
        undoList = new Stack<>();
    }

    public Buffer(String str) {
        lines = new ArrayList<StringBuilder>();
        StringBuilder initBuilder = new StringBuilder();
        lines.add(initBuilder);
        cursor = new Cursor(0, 0);
        undoList = new Stack<>();
        String[] temp;
        if (str.contains("\n")) {
            temp = str.split("\n");
            for (String i : temp) {
                insertStr(i);
                insertChar('\n');
            }
        } else insertStr(str);

    }

    public int getMarkCol() {
        return markCol;
    }

    public int getMarkRow() {
        return markRow;
    }

    public void setMark(int col, int line) {
        markRow = line;
        markCol = col;
        marked = true;

    }

    public boolean isMarked() {
        return marked;
    }

    public void unsetMark() {
        markCol = 0;
        markRow = 0;
        marked = false;
        clipBoard = new StringBuilder();
    }

    public void copy() {
        int cursor_col = getCursor().getColumn();
        int cursor_line = getCursor().getLine();
        if (isMarked()) {
            if (cursor_line == markRow) {
                if (cursor_col < markCol) {
                    copying(cursor_col, markRow, cursor_line);
                }
                if (cursor_col > markCol) {
                    copying(cursor_col, cursor_line, markRow);
                }
            }
            if (cursor_line > markRow) {
                copying(cursor_col, cursor_line, markRow);
            }
            if (cursor_line < markRow) {
                copying(cursor_col, markRow, cursor_line);
            }
        }
    }

    private void copying(int cursor_col, int cursor_line, int markRow) {
        for (int i = markRow; i <= cursor_line; i++) {
            StringBuilder nLine = getNLine(i);
            int length = nLine.length();
            if (i != cursor_line && i == markRow) {
                clipBoard.append(nLine.subSequence(markCol, length));
            } else if (i != cursor_line && i != markRow) {
                clipBoard.append(nLine.subSequence(0, length));
            }
            if (i == cursor_line) {
                clipBoard.append(nLine.subSequence(0, cursor_col));
            }
            if (i != cursor_line) {
                clipBoard.append('\n');
            }
        }
    }

    public void cut() {
        int cursor_col = getCursor().getColumn();
        int cursor_line = getCursor().getLine();
        if (isMarked()) {
            if (cursor_line == markRow) {
                if (cursor_col < markCol) {
                    cuting(cursor_col, markRow, cursor_line);
                }
                if (cursor_col > markCol) {
                    cuting(cursor_col, cursor_line, markRow);
                }
            }
            if (cursor_line > markRow) {
                cuting(cursor_col, cursor_line, markRow);
            }
            if (cursor_line < markRow) {
                cuting(cursor_col, markRow, cursor_line);
            }
        }
        if (!undoing) {
            undoList.push(new Edit(Edit.EditOp.CUT, markCol, markRow, clipBoard));
        }
    }

    private void cuting(int cursor_col, int cursor_line, int markRow) {
        for (int i = markRow; i <= cursor_line; i++) {
            StringBuilder nLine = getNLine(i);
            int length = nLine.length();
            if (i != cursor_line && i == markRow) {
                clipBoard.append(nLine.subSequence(markCol, length));
                nLine.delete(markCol, length);
            }
            if (i != cursor_line && i != markRow) {
                clipBoard.append(nLine.subSequence(0, length));
                nLine.delete(0, length);
            }
            if (i == cursor_line) {
                clipBoard.append(nLine.subSequence(0, cursor_col));
                nLine.delete(0, cursor_col);
            }
            if (i != cursor_line) {
                clipBoard.append('\n');
            }
        }
        int remove_lines = cursor_line - markRow;
        setCursor(markCol, markRow);
        for (int i = 0; i < remove_lines; i++)
            deleteCharInFront();
    }

    /**
     * Pastes the content of the clipBoard into the buffer
     */
    public void paste() {
        int line = getCursor().getLine();
        int column = getCursor().getColumn();
        if (!undoing)
            undoList.push(new Edit(Edit.EditOp.PASTE, column, line, clipBoard));
        insertStrWithLn();
        unsetMark();
    }



    /**
     * @param edit
     */
    private void undo(Edit edit) {
        switch (edit.getOp()) {
            case INSERT:
                setCursor(edit.getCursorCol(), edit.getCursorRow());
                deleteChar();
                break;
            case DELETE:
                if(edit.getCursorCol()==0)
                    setCursor(0, edit.getCursorRow());
                else
                    setCursor(edit.getCursorCol()-1,edit.getCursorRow());
                insertChar(edit.getObject().toString().charAt(0));
                break;
            case PASTE:
                markRow = edit.getCursorRow();
                markCol = edit.getCursorCol();
                marked = true;
                cut();
                unsetMark();
                break;
            case CUT:
                setCursor(edit.getCursorCol(), edit.getCursorRow());
                clipBoard = new StringBuilder(edit.getObject().toString());
                paste();
                clipBoard = new StringBuilder();
                break;
            default:
                return;
        }
    }

    public void undo() {
        if (!undoList.isEmpty()) {
            undoing = true;
            undo(undoList.pop());
        } else
            System.out.println("Stack is empty");
        undoing = false;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public ArrayList<StringBuilder> getAllLines() {
        return lines;
    }

    public StringBuilder getNLine(int n) {
        return lines.get(n);
    }

    public int getLinesCount() {
        return lines.size();
    }

    public void setCursor(int col, int lines) {
        if (validPos(col, lines)) {
            getCursor().setCursor(col, lines);
        } else {
            throw new IllegalArgumentException("Buffer.setCursor: invalid position (" + col + "," + lines + ")");
        }
    }

    private boolean validPos(int col, int lines) {
        int col_lines = getNLine(lines).length();
        int linesCount = getLinesCount();
        return col >= 0 && col <= col_lines && lines >= 0 && lines < linesCount;
    }

    public void insertStr(String txt) {
        if (txt.contains("\n")) {
            throw new IllegalArgumentException("Buffer.insert: text has new line!");
        } else {
            int column = getCursor().getColumn();
            int line = getCursor().getLine();
            getNLine(line).insert(column, txt);
            setCursor(column + txt.length(), line);
        }
    }


    public void insertChar(char c) {
        if (c == '\n') {
            insertLn();
        } else {
            insertStr("" + c);
        }
        if (!undoing) {
            Edit e = new Edit(Edit.EditOp.INSERT, getCursor().getColumn(), getCursor().getLine(), c);
            undoList.push(e);
        }
    }

    /**
     * Like insertStr(String) but if the String has '\n' it doesnt return an error
     */
    private void insertStrWithLn() {
        String temp = clipBoard.toString();
        undoing = true;
        for (char c : temp.toCharArray()) {
            insertChar(c);
        }
        undoing = false;
    }

    private void insertLn() {
        int column = getCursor().getColumn();
        int line = getCursor().getLine();
        //se o cursor estiver na ultima coluna da linha
        int length = getNLine(line).length();
        if (column == length) {
            line += 1;
            lines.add(line, new StringBuilder(""));
            setCursor(0, line);
        }
        //se o cursor estiver no meio/inicio da linha
        else {
            line += 1;
            StringBuilder temp = getNLine(line - 1);
            String temp1 = temp.subSequence(column, temp.length()).toString();
            lines.add(line, new StringBuilder(temp1));
            lines.get(line - 1).delete(column, getNLine(line - 1).length());
            setCursor(0, line);
        }
    }

    private void deleteLn() {
        int cursor_line = getCursor().getLine();
        String temp = lines.get(cursor_line).toString();
        int size;
        if (cursor_line > 0) {
            size = getNLine(cursor_line - 1).length();
            setCursor(size, cursor_line - 1);
            insertStr(temp);
            setCursor(lines.get(cursor_line - 1).length() - temp.length(), cursor_line - 1);
            lines.remove(cursor_line);
        }
    }

    public void deleteChar() {
        int cursor_col = getCursor().getColumn();
        int cur_line = getCursor().getLine();
        StringBuilder temp = getNLine(cur_line);
        Edit e = null;
        if (cursor_col == 0 && temp != null) {
            e = new Edit(Edit.EditOp.DELETE, cursor_col, cur_line, '\n');
            deleteLn();
        } else {
            e = new Edit(Edit.EditOp.DELETE, cursor_col, cur_line, temp.charAt(cursor_col - 1));
            lines.get(cur_line).deleteCharAt(cursor_col - 1);
            setCursor(cursor_col - 1, cur_line);
        }
        if (!undoing) {
            undoList.push(e);
        }
    }

    public void deleteCharInFront() {
        int cursor_col = getCursor().getColumn();
        int cur_line = getCursor().getLine();
        int size = getNLine(cur_line).length();
        if (cursor_col == size) {
            deleteLnInFront();
        } else {
            String temp = lines.get(cur_line).deleteCharAt(cursor_col).toString();
            lines.set(cur_line, new StringBuilder(temp));
        }
    }

    public void deleteLnInFront() {
        int cursor_col = getCursor().getColumn();
        int cur_line = getCursor().getLine();
        int size = getNLine(cur_line).length();
        StringBuilder temp;
        if (cur_line + 1 < getLinesCount() && cursor_col == size) {
            temp = getNLine(cur_line + 1);
            lines.get(cur_line).append(temp);
            lines.remove(cur_line + 1);
        }
    }

    public void moveNext() {
        int cursor_l = getCursor().getLine();
        int size = getNLine(cursor_l).length();
        int cursor_col = getCursor().getColumn();
        //cursor no meio da linha
        if (cursor_col < size) {
            setCursor(cursor_col + 1, cursor_l);
        }
//        cursor no fim da linha
        else {
//            existem mais linha depois da atual
            if (cursor_l + 1 < getLinesCount()) {
                this.setCursor(0, cursor_l + 1);
            }
        }
    }

    public void movePrev() {
        int cursor_col = getCursor().getColumn();
        int cursor_line = getCursor().getLine();
        //cursor no meio da linha
        if (cursor_col > 0) {
            setCursor(cursor_col - 1, cursor_line);
        }
        //move o cursor para a linha anterior se esta existir
        else if (cursor_line > 0) {
            int length = getNLine(cursor_line - 1).length();
            setCursor(length, cursor_line - 1);
        }
    }

    public void movePrevLine() {
        int size;
        int cursor_line = getCursor().getLine();
        //move o cursor se não estiver na linha 0
        if (cursor_line > 0) {
            //tamanho da linha anterior
            size = getNLine(cursor_line - 1).length();
            //coluna do cursor
            int cursor_col = getCursor().getColumn();
            int min = Math.min(size, cursor_col);
            setCursor(min, cursor_line - 1);
        }
    }

    public void moveNextLine() {
        //linha atual do cursor
        int cursor_line = getCursor().getLine();
        int size;
        //testa se exite uma linha aseguir
        if (cursor_line + 1 < getLinesCount()) {
            //tamanho da linha seguinte
            size = getNLine(cursor_line + 1).length();
            int min = Math.min(size, getCursor().getColumn());
            setCursor(min, cursor_line + 1);
        }
    }

    public void moveEnd() {
        int line = getCursor().getLine();
        int size = getNLine(line).length();
        setCursor(size, line);
    }

    public void moveHome() {
        int line = getCursor().getLine();
        setCursor(0, line);
    }

}