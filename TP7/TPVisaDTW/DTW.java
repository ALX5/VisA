import java.awt.Point;
import java.util.Vector;


public class DTW {
	
	Vector<Point> X, Y;
	
	public DTW(Vector<Point> rStroke, Vector<Point> tStroke) {
		this.X=rStroke;
		this.Y=tStroke;
		if(X.size()!=0 && Y.size()!=0) computeMatrix(this.X.size(), this.Y.size());
	}

	public Matrix computeMatrix(int n, int m) {
		Matrix dtw = new Matrix(n, m, false);
		dtw.items[0][0]=0;
		for(int i=1; i<n; i++) {
			dtw.items[i][0]=dtw.items[i-1][0]+c(this.X.get(i),this.Y.get(0));
			dtw.couple[i][0]=new Couple(this.X.get(i-1).x,this.Y.get(0).y);
		}
		for(int j=1; j<m; j++) {
			dtw.items[0][j]=dtw.items[0][j-1]+c(this.X.get(0),this.Y.get(j));
			dtw.couple[0][j]=new Couple(this.X.get(0).x,this.Y.get(j-1).y);
		}
		for(int i=1; i<n; i++) {
			for(int j=1; j<m; j++) {
				dtw.items[i][j]=c(this.X.get(i),this.Y.get(j))+min3(dtw.items[i-1][j],dtw.items[i][j-1],dtw.items[i-1][j-1]);
				dtw.couple[i][j]=indexMin3(dtw,i,j);
			}
		}
		return dtw;
	}
	
	private double c(Point point, Point point2) {
		return Math.sqrt(Math.pow(point2.x-point.x, 2)+Math.pow(point2.y-point.y, 2));		
	}
	
	 private double min3(double item, double item2, double item3) {
		double tmp = Math.min(item, item2);
		return Math.min(tmp, item3);
	}
	 
	private Couple indexMin3(Matrix dtw, int i, int j) {
		if(dtw.items[i-1][j]<dtw.items[i][j-1]) {
			if(dtw.items[i-1][j]<dtw.items[i-1][j-1])
				return new Couple(this.X.get(i-1).x,this.Y.get(j).y);
			else 
				return new Couple(this.X.get(i-1).x,this.Y.get(j-1).y);
		}
		else {
			if(dtw.items[i][j-1]<dtw.items[i-1][j-1]) 
				return new Couple(this.X.get(i).x,this.Y.get(j-1).y);
			else 
				return new Couple(this.X.get(i-1).x,this.Y.get(j-1).y);
		}
	}
}
