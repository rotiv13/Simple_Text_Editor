import java.util.ArrayList;
import java.util.List;

public class Buffer {
	private final ArrayList<StringBuilder> lines;
	private final Cursor cursor;
	//posicao da marca do cursor
	private int markRow, markCol;
	//true=temos uma posição marcada; false= nao tem posiçao marcada
	private boolean marked;
	//texto a ser copiado
	private StringBuilder clipBoard;
	//ultimas operações
	private List<Edit> undoList;

	public Buffer() {
		lines = new ArrayList<StringBuilder>();
		StringBuilder initBuilder = new StringBuilder();
		lines.add(initBuilder);
		cursor = new Cursor(0, 0);
	}

	public Buffer(String str) {
		lines = new ArrayList<StringBuilder>();
		StringBuilder initBuilder = new StringBuilder();
		lines.add(initBuilder);
		cursor = new Cursor(0, 0);
		String[] temp;
		if (str.contains("\n")) {
			temp = str.split("\n");
			for (String i : temp) {
				insertStr(i);
				insertChar('\n');
			}
		} else insertStr(str);

	}

	public void setMark(int line, int col) {

	}

	public void unsetMark() {

	}

	public void copy() {

	}

	public void cut() {

	}

	public void paste() {

	}

	private void undo(Edit edit) {

	}

	public void undo() {

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
		int cursor_line = getCursor().getLine();
		int col_lines = getNLine(lines).length();
		return col >= 0 && col <= col_lines && lines >= 0 && lines <= getLinesCount();
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
		if (c == '\n')
			insertLn();
		else {
			this.insertStr("" + c);
		}
	}

	private void insertLn() {
		final int column = getCursor().getColumn();
		int line = getCursor().getLine();
		//se o cursor estiver na ultima coluna da linha
		if (column == getNLine(line).length()) {
			line += 1;
			lines.add(line, new StringBuilder());
			setCursor(0, line);
		}
		//se o cursor estiver a meio da linha
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