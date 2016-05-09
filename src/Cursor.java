
class Cursor {
	private Pos pos;

	Cursor(int column, int row) {
		pos=new Pos(column, row);
	}


	int getLine(){
		return pos.getY();
	}

	int getColumn(){
		return pos.getX();
	}

	private void setColumn(int c){
		pos.setX(c);
	}

	private void setLine(int l){
		pos.setY(l);
	}

	void setCursor(int c, int l){
		setColumn(c);
		setLine(l);
	}



}
