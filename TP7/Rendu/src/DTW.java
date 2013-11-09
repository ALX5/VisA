import java.awt.Point;
import java.util.Vector;


public class DTW {
	
	Vector<Point> X, Y;	

	Vector<Couple> list;
	
	public DTW(Vector<Point> rStroke, Vector<Point> tStroke) {
		this.X=rStroke;
		this.Y=tStroke;
		list = new Vector<Couple>();
		if(X.size()!=0 && Y.size()!=0) computeMatrix(this.X.size(), this.Y.size());
	}

	public Matrix computeMatrix(int n, int m) {
		Matrix dtw = new Matrix(n, m);		
		dtw.items[0][0] = 0;
		dtw.couple[0][0] = new Couple();
		for (int i = 1; i < n; i++) {
			dtw.items[i][0] = dtw.items[i-1][0] + this.c(X.get(i), Y.get(0));
			dtw.couple[i][0] = new Couple(i-1, 0);
		}
		for (int i = 1; i < m; i++) {
			dtw.items[0][i] = dtw.items[0][i-1] + this.c(X.get(0), Y.get(i));
			dtw.couple[0][i] = new Couple(0, i-1);
		}
		for(int i=1; i<n; i++) {
			for(int j=1; j<m; j++) {
				dtw.items[i][j] = this.c(X.get(i), Y.get(j)) + itemMin3(dtw, i,j);
			}
		}
		computelist(dtw.couple);
		return dtw;
	}
	
	private double c(Point point, Point point2) {
		return Math.sqrt(Math.pow(point2.x-point.x, 2)+Math.pow(point2.y-point.y, 2));		
	}
	 
	private double itemMin3(Matrix dtw, int i, int j) {
		if (dtw.items[i-1][j] < dtw.items[i][j-1]) {
			if (dtw.items[i-1][j] < dtw.items[i-1][j-1]) {
				dtw.couple[i][j] = new Couple(i-1, j);
				return dtw.items[i][j];
			}
		} else {
			if (dtw.items[i][j-1] < dtw.items[i-1][j-1]) {
				dtw.couple[i][j] = new Couple(i, j-1);
				return dtw.items[i][j];
			}
		}
		dtw.couple[i][j] = new Couple(i-1, j-1);
		return dtw.items[i][j];
	}	
	
	private void computelist(Couple[][] couples) {		
		int x, y;
		x = couples.length-1;
		y = couples[0].length-1;		
		list.add(new Couple(x,y));		
		do {
			x = couples[x][y].x;
			y = couples[x][y].y;
			list.add(couples[x][y]);
		} while (x != 0 && y != 0);		
	}
}
