import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Compy386BorderedPanel extends JPanel {
	private Color outerBezColor = new Color(0xcec39c);
	private Color outerBezOutlineColor = new Color(0xa5966b);
	private Color innerBezColor = new Color(0xcecfce);
	private Color innerBezShadowColor = new Color(0x313031);
	private Color screenColor = Color.BLACK;
	private Color angledSupportColor = new Color(0xded3b5);

	private int outerBezCornerWidth = 35;
	private int outerBezThickness = 10;
	private int innerBezThickness = 30;
	private int innerBezShadowCornerRadius = 20;
	private int innerBezShadowThickness = 10;
	private int outlineWidth = 3;

	private GeneralPath outerBezPath;
	private GeneralPath innerBezPath;
	private Rectangle innerBezShadowRect;
	private Rectangle screenRect;
	private Rectangle support1;
	private Rectangle supportBase;
	private GeneralPath angledSupport;
	private Arc2D wheelArcShape;

	public Compy386BorderedPanel() {
		setDefaultColors();
	}

	public Compy386BorderedPanel(LayoutManager layout) {
		super(layout);
		setDefaultColors();
	}

	private void setDefaultColors() {
		setBackground(screenColor);
		setForeground(Color.white);
	}

	@Override
	public Insets getInsets() {
		return getInsets(new Insets(0, 0, 0, 0));
	}

	@Override
	public Insets getInsets(Insets insets) {
		computeShapes();

		insets.left = screenRect.x;
		insets.top = screenRect.y;
		insets.right = (int) (getWidth() - screenRect.getMaxX());
		insets.bottom = (int) (getHeight() - screenRect.getMaxY());
		return insets;
	}

	private void computeShapes() {
		int xOfs = 0;
		int yOfs = 0;
		int supportHt = Math.max((int) (getHeight() * 0.1f), 30);
		int supportPartH = supportHt / 3;

		Rectangle outerBezRect = new Rectangle(xOfs + outlineWidth, yOfs + outlineWidth,
				getWidth() - xOfs * 2 - outlineWidth * 2, getHeight() - yOfs - supportHt - outlineWidth * 2);

		outerBezPath = makeCurvedRect(outerBezRect, outerBezCornerWidth);

		Rectangle innerBezRect = shrink(outerBezRect, outerBezThickness);
		innerBezPath = makeCurvedRect(innerBezRect, outerBezCornerWidth);

		innerBezShadowRect = shrink(innerBezRect, innerBezThickness);
		innerBezShadowRect.height -= 20;

		screenRect = shrink(innerBezShadowRect, innerBezShadowThickness);

		support1 = new Rectangle(screenRect.x, (int) outerBezRect.getMaxY() + outlineWidth, screenRect.width,
				supportPartH);

		supportBase = new Rectangle(innerBezRect.x, getHeight() - supportPartH - outlineWidth / 2, innerBezRect.width,
				supportPartH);

		angledSupport = new GeneralPath();
		angledSupport.moveTo(support1.x, support1.getMaxY());
		angledSupport.lineTo(supportBase.x, supportBase.y); // left angled line
		angledSupport.lineTo(supportBase.getMaxX(), supportBase.y); // horiz bottom line
		angledSupport.lineTo(support1.getMaxX(), support1.getMaxY()); // right angled line
		angledSupport.closePath();

		int wheelW = 50;
		wheelArcShape = new Arc2D.Float(outerBezRect.x + outerBezThickness + wheelW,
				(int) (outerBezRect.getMaxY() - wheelW * .8), wheelW, wheelW, 270 - 42, 42 * 2, Arc2D.OPEN);

	}

	@Override
	protected void paintBorder(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();

		Stroke outlineStroke = new BasicStroke(outlineWidth * 2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
		Stroke thinStroke = new BasicStroke(4);
		g2d.setStroke(outlineStroke);
		g2d.setColor(outerBezOutlineColor);
		g2d.draw(outerBezPath);
		g2d.setColor(outerBezColor);
		g2d.fill(outerBezPath);

		g2d.setColor(Color.black);
		g2d.setStroke(thinStroke);
		g2d.draw(innerBezPath);
		g2d.setColor(innerBezColor);
		g2d.fill(innerBezPath);

		g2d.setColor(innerBezShadowColor);
		g2d.fillRoundRect(innerBezShadowRect.x, innerBezShadowRect.y, innerBezShadowRect.width,
				innerBezShadowRect.height, innerBezShadowCornerRadius, innerBezShadowCornerRadius);

		g2d.setColor(screenColor);
		g2d.fill(screenRect);

		g2d.setColor(outerBezOutlineColor);
		g2d.setStroke(outlineStroke);
		g2d.draw(support1);
		g2d.setColor(outerBezColor);
		g2d.fill(support1);

		g2d.setColor(outerBezOutlineColor);
		g2d.draw(angledSupport);
		g2d.setColor(angledSupportColor);
		g2d.fill(angledSupport);

		g2d.setColor(outerBezOutlineColor);
		g2d.draw(supportBase);
		g2d.setColor(outerBezColor);
		g2d.fill(supportBase);

		g2d.setColor(outerBezOutlineColor);
		g2d.setStroke(thinStroke);
		g2d.draw(wheelArcShape);

		// font stuff
		int innerBezBottomHt = (int) (innerBezPath.getBounds().getMaxY() - innerBezShadowRect.getMaxY());
		int innerBezBottomCenterLine = (int) (innerBezPath.getBounds().getMaxY() - innerBezBottomHt / 2);

		int fontSize = (int) (innerBezBottomHt * 0.8);
		Font compyFont = new Font("Courier", Font.PLAIN, fontSize);
		Font compyItalicFont = new Font("Courier", Font.ITALIC, fontSize);

		int textBaseLine = innerBezBottomCenterLine + (g2d.getFontMetrics(compyFont).getAscent() / 3);

		g2d.setColor(Color.DARK_GRAY);
		g2d.setFont(compyFont);
		g2d.drawString("COMPY ", innerBezShadowRect.x, textBaseLine);
		int compyStrWidth = g2d.getFontMetrics(compyFont).stringWidth("COMPY ");
		g2d.setFont(compyItalicFont);
		g2d.drawString("386", innerBezShadowRect.x + compyStrWidth, textBaseLine);
	}

	private Rectangle shrink(Rectangle rect, int delta) {
		return new Rectangle(rect.x + delta, rect.y + delta, rect.width - delta * 2, rect.height - delta * 2);
	}

	private GeneralPath makeCurvedRect(Rectangle rect, int cornerWidth) {
		int hh = rect.height / 2;
		GeneralPath path = new GeneralPath();
		path.moveTo(rect.x + cornerWidth, rect.y);
		path.quadTo(rect.x, rect.y, rect.x, rect.y + hh);
		path.quadTo(rect.x, rect.y + rect.height, rect.x + cornerWidth, rect.y + rect.height);
		path.lineTo(rect.x + rect.width - cornerWidth, rect.y + rect.height);
		path.quadTo(rect.x + rect.width, rect.y + rect.height, rect.x + rect.width, rect.y + hh);
		path.quadTo(rect.x + rect.width, rect.y, rect.x + rect.width - cornerWidth, rect.y);
		path.closePath();
		return path;
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (g instanceof Graphics2D) {
			final int R = 240;
			final int G = 240;
			final int B = 240;

			//			Paint p = new GradientPaint(0.0f, 0.0f, new Color(R, G, B, 0), 0.0f, getHeight(), new Color(R, G, B, 255),
			//					true);
			//			Graphics2D g2d = (Graphics2D) g;
			//			g2d.setPaint(p);
			//			g2d.fillRect(0, 0, getWidth(), getHeight());
		}
	}

}
