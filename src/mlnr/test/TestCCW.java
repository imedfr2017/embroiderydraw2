/*
 * TestCCW.java
 *
 * Created on September 22, 2006, 1:06 PM
 *
 */

package mlnr.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;

/**
 *
 * @author Robert Molnar II
 */
public class TestCCW extends JFrame implements MouseListener, MouseMotionListener {
    Point2D.Float fPt1 = new Point2D.Float(0, 100);
    Point2D.Float fPt2 = new Point2D.Float(100, 100);
    Point2D.Float fPt3 = new Point2D.Float(100, 0);
    Point2D.Float fPt4 = new Point2D.Float(0, 0);
    Line2D.Float fTop;
    Line2D.Float fBottom;
    Line2D.Float fLeft;
    Line2D.Float fRight;
    Point2D.Float fptCalculate = new Point2D.Float(50, 50);    
    BasicStroke bStroke = new BasicStroke(2.0f);
    
    /** Creates a new instance of TestCCW */
    public TestCCW() {
        addMouseListener(this);
        addMouseMotionListener(this);
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        fPt1.x += (640 / 2) - 50;
        fPt1.y += (480 / 2) - 50;
        
        fPt2.x += (640 / 2) - 50;
        fPt2.y += (480 / 2) - 50;
        
        fPt3.x += (640 / 2) - 50;
        fPt3.y += (480 / 2) - 50;
        
        fPt4.x += (640 / 2) - 50;
        fPt4.y += (480 / 2) - 50;
        
        fTop = new Line2D.Float(fPt1.x, fPt1.y + 10, fPt2.x, fPt2.y + 10);
        fBottom = new Line2D.Float(fPt4.x, fPt4.y - 10, fPt3.x, fPt3.y - 10);
        fLeft = new Line2D.Float(fPt4, fPt1);
        fRight = new Line2D.Float(fPt2, fPt3);
    }        
    
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(bStroke);
        g2d.setColor(Color.BLUE);
        g2d.draw(fTop);
        g2d.draw(fBottom);
        g2d.draw(fLeft);
        g2d.draw(fRight);       
        g2d.draw(new Rectangle2D.Float(fptCalculate.x - 3, fptCalculate.y - 3, 6, 6));
        
        String msg = "bottom: " + fTop.relativeCCW(fptCalculate);        
        g2d.drawString(msg, 10, 50);
        
        msg = "top: " + fBottom.relativeCCW(fptCalculate);        
        g2d.drawString(msg, 10, 70);
        
        msg = "right: " + fRight.relativeCCW(fptCalculate);        
        g2d.drawString(msg, 10, 90);
        
        msg = "lef: " + fLeft.relativeCCW(fptCalculate);        
        g2d.drawString(msg, 10, 110);
        
        msg = "-1: 2nd point, 1: 1st point";        
        g2d.drawString(msg, 10, 110);
        
    }
    
    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e){
        fptCalculate.x = e.getPoint().x;
        fptCalculate.y = e.getPoint().y;
        repaint();
        
    }

    public void mouseReleased(MouseEvent e){}

    public void mouseEntered(MouseEvent e){}

    public void mouseExited(MouseEvent e){}
    
    public void mouseDragged(MouseEvent e) {
        fptCalculate.x = e.getPoint().x;
        fptCalculate.y = e.getPoint().y;
        repaint();
        
    }
    
    public void mouseMoved(MouseEvent e) {}
        
    public static void main(String []args) {
        TestCCW test = new TestCCW();
        test.setVisible(true);
        
    }
}
