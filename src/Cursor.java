
public class Cursor {
	private Pos pos;
	
	public Cursor(int column, int row) {
		pos=new Pos(column, row);
	}
	
	
	public int getColumn(){
		return pos.getX();
	}
	
	public int getLine(){
		return pos.getY();
	}
	
	public void setColumn(int c){
		pos.setX(c);
	}
	
	public void setLine(int l){
		pos.setY(l);
	}

	public void setCursor(int c,int l){ pos.setX(c);pos.setY(l); }
	
	
	
}
