package repast.simphony.visualization.network;

import java.awt.Paint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.piccolo2d.PNode;
import org.piccolo2d.extras.util.PBoundsLocator;
import org.piccolo2d.extras.util.PNodeLocator;
import org.piccolo2d.nodes.PPath;


import repast.simphony.visualization.visualization2D.style.EdgeStyle2D;

@Deprecated
public class PEdge extends PPath.Double {

	PNode source;
	PNode target;
	PNodeLocator sourceLocator;
	PNodeLocator targetLocator;
	Line2D line = new Line2D.Float();
	Point2D sourcePoint;
	Point2D targetPoint;
	Paint sourceEndPaint;
	Paint targetEndPaint;
	
	// The end icon classes depend on the Phoebe library which itself has depedencies
	//   on older version of Piccolo and is therefore not compatible with Piccolo 3+.
	//   It appears that the Phoebe code may have been updated as part of the 
	//   "hypergraphdb" project, but we have not updated it here since PEdge is
	//   already deprecated.
	
//	PEdgeEndIcon targetEnd;
//	PEdgeEndIcon sourceEnd;

	public final static int STRAIGHT_LINES = 1;
	public final static int CURVED_LINES = 2;

	int lineType = STRAIGHT_LINES;

	int targetEndType;
	int sourceEndType;

	public PEdge(PNode source, PNode target) {
		this.source = source;
		this.target = target;
		if (source == target) {
			sourceLocator = PBoundsLocator.createSouthLocator(source);
			targetLocator = PBoundsLocator.createSouthLocator(target);
		} else {
			sourceLocator = new PNodeLocator(source);
			targetLocator = new PNodeLocator(target);
		}
		sourcePoint = sourceLocator.locatePoint(sourcePoint);
		targetPoint = targetLocator.locatePoint(targetPoint);

		targetPoint = ((PNode) target).localToGlobal(targetPoint);
		sourcePoint = ((PNode) source).localToGlobal(sourcePoint);
	}

	public void update() {
		targetPoint = targetLocator.locatePoint(targetPoint);
		sourcePoint = sourceLocator.locatePoint(sourcePoint);

		targetPoint = ((PNode) target).localToGlobal(targetPoint);
		sourcePoint = ((PNode) source).localToGlobal(sourcePoint);

		if (source == target) {
			float width = (float) ((PNode) target).getWidth();
			float height = (float) ((PNode) target).getHeight();
			
			this.reset();
			this.append(new Ellipse2D.Double( (sourcePoint.getX() - .25 * width),
					 (sourcePoint.getY() - .25 * height), width * 2,
					height * 2), false);
			
			return;
		}
		updatePoint(sourcePoint, targetPoint, source);
		updatePoint(targetPoint, sourcePoint, target);
		line.setLine(sourcePoint, targetPoint);
//		if (targetEnd == null) {
//			targetEnd = createEndIcon(targetEndType);
//			addChild(targetEnd);
//		}
//		if (sourceEnd == null) {
//			sourceEnd = createEndIcon(sourceEndType);
//			addChild(sourceEnd);
//		}
//		updateEndPoints();
		this.reset();
		this.append(line,false);
	}

//	private PEdgeEndIcon createEndIcon(int type) {
//		switch (type) {
//		case EdgeStyle2D.ARROW_HEAD:
//			return new PArrowIcon(sourcePoint, targetPoint, 10);
//		case EdgeStyle2D.DIAMOND_HEAD:
//			return new PDiamondIcon(sourcePoint, targetPoint, 10);
//		case EdgeStyle2D.CIRCLE_HEAD:
//			return new PCircleIcon(sourcePoint, targetPoint, 10);
//		case EdgeStyle2D.DELTA_HEAD:
//			return new PDeltaIcon(sourcePoint, targetPoint, 10);
//		case EdgeStyle2D.T_HEAD:
//			return new PTIcon(sourcePoint, targetPoint, 10);
//		default:
//			return new PNullIcon(sourcePoint, targetPoint, 10);
//		}
//	}

//	private void updateEndPoints() {
//		targetEnd.setPaint(targetEndPaint);
//		targetEnd.drawIcon(sourcePoint, targetPoint);
//		targetPoint.setLocation(targetEnd.getNewX(), targetEnd.getNewY());
//		
//		sourceEnd.setPaint(sourceEndPaint);
//		sourceEnd.drawIcon(targetPoint, sourcePoint);
//		sourcePoint.setLocation(sourceEnd.getNewX(), sourceEnd.getNewY());
//	}

	public void setTargetEndType(int type) {
		targetEndType = type;
	}

	public void setTargetEndPaint(Paint p) {
		this.targetEndPaint = p;
	}
	
	public void setSourceEndType(int type) {
		sourceEndType = type;
	}

	public void setSourceEndPaint(Paint p) {
		this.sourceEndPaint = p;
	}

	private void updatePoint(Point2D updating, Point2D other, PNode node) {
		double deltaX = updating.getX() - other.getX();
		double deltaY = updating.getY() - other.getY();
		double nodeWidth = node.getWidth() / 2;
		double nodeHeight = node.getHeight() / 2;
		if (deltaX == 0) {
			if (deltaY > 0) {
				updating.setLocation(updating.getX(), updating.getY()
						- nodeWidth);
			} else {
				updating.setLocation(updating.getX(), updating.getY()
						+ nodeHeight);
			}
		} else {
			double theta = Math.atan(deltaY / deltaX);
			if (other.getX() < updating.getX()) {
				theta += Math.PI;
			}
			updating.setLocation(updating.getX() + nodeWidth * Math.cos(theta),
					updating.getY() + nodeHeight * Math.sin(theta));
		}
	}

	public int getLineType() {
		return lineType;
	}
}