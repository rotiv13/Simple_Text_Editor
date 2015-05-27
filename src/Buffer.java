import java.util.ArrayList;
import java.util.Stack;

public class Buffer {
    private final ArrayList<StringBuilder> lines;
    private final Cursor cursor;
    //texto a ser copiado
    public StringBuilder clipBoard = new StringBuilder();
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
                StringBuilder nLine = getNLine(cursor_line);
                if (cursor_col < markCol) {
                    clipBoard.append(nLine.subSequence(cursor_col, markCol));
                }
                if (cursor_col > markCol) {
                    clipBoard.append(nLine.subSequence(markCol, cursor_col));
                }
            }
            if (cursor_line > markRow) {
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
            if (cursor_line < markRow) {
                for (int i = cursor_line; i <= markRow; i++) {
                    int length = getNLine(i).length();
                    if (i != markRow && i == cursor_line) {
                        clipBoard.append(getNLine(i).subSequence(cursor_col, length));
                    } else if (i != cursor_line && i != markRow) {
                        clipBoard.append(getNLine(i).subSequence(0, length));
                    }
                    if (i == markRow) {
                        clipBoard.append(getNLine(i).subSequence(0, markCol));
                    }
                    if (i != markRow) {
                        clipBoard.append('\n');
                    }
                }
            }
        }
    }

    public void cut() {
        int cursor_col = getCursor().getColumn();
        int cursor_line = getCursor().getLine();
        System.out.println("col: " + cursor_col + "\n line: " + cursor_line);
        if (isMarked()) {
            if (cursor_line == markRow) {
                StringBuilder nLine = getNLine(cursor_line);
                if (cursor_col < markCol) {
                    clipBoard.append(nLine.subSequence(cursor_col, markCol));
                    nLine.delete(cursor_col, markCol);
                    setCursor(cursor_col, cursor_line);
                }
                if (cursor_col > markCol) {
                    clipBoard.append(nLine.subSequence(markCol, cursor_col));
                    nLine.delete(markCol, cursor_col);
                    setCursor(markCol, cursor_line);
                }
            }
            if (cursor_line > markRow) {
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
            if (cursor_line < markRow) {
                for (int i = cursor_line; i <= markRow; i++) {
                    StringBuilder nLine = getNLine(i);
                    int length = nLine.length();
                    if (i != markRow && i == cursor_line) {
                        clipBoard.append(nLine.subSequence(cursor_col, length));
                        nLine.delete(cursor_col, length);

                    }
                    if (i != cursor_line && i != markRow) {
                        clipBoard.append(nLine.subSequence(0, length));
                        nLine.delete(0, length);
                    }
                    if (i == markRow) {
                        clipBoard.append(nLine.subSequence(0, markCol));
                        nLine.delete(0, markCol);
                    }
                    if (i != markRow) {
                        clipBoard.append('\n');
                    }
                }
                int remove_lines = cursor_line - markRow;
                for (int i = 0; i < remove_lines; i++)
                    deleteCharInFront();

            }
            System.out.println(clipBoard.toString());
        }

    }

    /**
     * Pastes the content of the clipBoard into the buffer
     */
    public void paste() {
        insertStrWithLn();
        markCol = 0;
        markRow = 0;
        marked = false;
        clipBoard = new StringBuilder();
    }

    /**
     * Like insertStr(String) but if the String has '\n' it doesnt return an error
     */
    private void insertStrWithLn() {
        String temp = clipBoard.toString();
        for (char c : temp.toCharArray()) {
            insertChar(c);
        }
    }

    /**
     * @param edit
     */
    private void undo(Edit edit) {
        switch (edit.getOp()) {
            case INSERT:
                break;
            case DELETE:
                break;
            case PASTE:
                break;
            case COPY:
                break;
            case CUT:
                break;
            default:
                break;
        }
    }

    public void undo() {
        if (!undoList.isEmpty())
            undo(undoList.pop());

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
        return col >= 0 && col <= col_lines && lines >= 0 && lines <= linesCount;
    }

    public void insertStr(String txt) {
        if (txt.contains("\n")) {
            throw new IllegalArgumentException("Buffer.insert: text has new line!");
        } else {
            final int column = getCursor().getColumn();
            final int line = getCursor().getLine();
            getNLine(line).insert(column, txt);
            setCursor(column + txt.length(), line);
        }
    }


    public void insertChar(char c) {
        if (c == '\n') {
            insertLn();
        } else {
            this.insertStr("" + c);
        }
    }

    private void insertLn() {
        final int column = getCursor().getColumn();
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
        if (cursor_col == 0 && temp != null)
            deleteLn();
        else {
            lines.get(cur_line).deleteCharAt(cursor_col - 1);
            setCursor(cursor_col - 1, cur_line);
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
        if (cursor_col < size) {
            setCursor(cursor_col + 1, cursor_l);
        } else {
            if (cursor_l + 1 < getLinesCount()) {
                this.setCursor(0, cursor_l + 1);
            }
        }
    }

    public void movePrev() {
        int cursor_col = getCursor().getColumn();
        int cursor_line = getCursor().getLine();
        if (cursor_col > 0) {
            setCursor(cursor_col - 1, cursor_line);
        } else if (cursor_line > 0) {
            this.setCursor(getNLine(cursor_line - 1).length(), cursor_line - 1);
        }
    }

    public void movePrevLine() {
        int size;
        int cursor_line = getCursor().getLine();
        if (cursor_line > 0) {
            size = getNLine(cursor_line - 1).length();
            int cursor_col = getCursor().getColumn();
            int min = Math.min(size, cursor_col);
            setCursor(min, cursor_line - 1);
        }
    }

    public void moveNextLine() {
        int cursor_line = getCursor().getLine();
        int size;
        if (cursor_line + 1 < getLinesCount()) {
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