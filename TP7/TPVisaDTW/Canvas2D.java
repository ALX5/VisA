import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

/**
 * Canvas2D.java
 *
 * @author <a href="mailto:gery.casiez@lifl.fr">Gery Casiez</a>
 * @version
 */


public class Canvas2D extends Canvas implements MouseMotionListener, MouseListener {
	// Stroke of reference
	private Vector<Point> RStroke;
	
	// Stroke to test
	private Vector<Point> TStroke;
	
	private int shiftModifier = 17;
	
	//
	private DTW dtw;

	
	Canvas2D () {
		RStroke = new Vector<Point>();
		TStroke = new Vector<Point>();
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		setBackground(new Color(255,255,255));
	}
	
	
	public void paint (Graphics g){ 
		g.drawString("Drag avec le bouton gauche ou droit de la souris + Shift : création d'une courbe de référence",10,15); 
		g.drawString("Drag avec le bouton gauche ou droit de la souris : création d'une courbe de test",10,30); 
		int r = 5;  
		
		if (!RStroke.isEmpty()) {
			g.setColor(Color.black);
			for (int i = 1; i < RStroke.size(); i++) {
				g.drawLine(RStroke.elementAt(i-1).x, RStroke.elementAt(i-1).y,
						RStroke.elementAt(i).x, RStroke.elementAt(i).y);		
				g.drawArc(RStroke.elementAt(i-1).x - r, RStroke.elementAt(i-1).y - r, 2*r, 2*r, 0, 360);
			}
			g.drawArc(RStroke.elementAt(RStroke.size()-1).x - r, RStroke.elementAt(RStroke.size()-1).y - r, 2*r, 2*r, 0, 360);
		}
 
		if (!TStroke.isEmpty()) {
			g.setColor(Color.orange);
			for (int i = 1; i < TStroke.size(); i++) {
				g.drawLine(TStroke.elementAt(i-1).x, TStroke.elementAt(i-1).y,
						TStroke.elementAt(i).x, TStroke.elementAt(i).y);
				g.drawArc(TStroke.elementAt(i-1).x - r, TStroke.elementAt(i-1).y - r, 2*r, 2*r, 0, 360);
			}
			g.drawArc(TStroke.elementAt(TStroke.size()-1).x - r, TStroke.elementAt(TStroke.size()-1).y - r, 2*r, 2*r, 0, 360);
		}
				
	}

	public void mouseMoved(MouseEvent e) {

	}

	public void mouseDragged(MouseEvent e) {
		if ((e.getModifiers() == shiftModifier) || RStroke.isEmpty())
			RStroke.add(e.getPoint());
		else
			TStroke.add(e.getPoint());
		repaint();
		dtw=new DTW(RStroke,TStroke);
	}	
	
	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		if ((e.getModifiers() == shiftModifier) || RStroke.isEmpty())
			RStroke.clear();
		else
			TStroke.clear();
	}

	public void mouseReleased(MouseEvent e) {

	}  
}
