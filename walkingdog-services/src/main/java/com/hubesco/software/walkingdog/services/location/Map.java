package com.hubesco.software.walkingdog.services.location;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * @author paoesco
 */
public class Map {

    private final Point2D topLeft;
    private final Point2D topRight;
    private final Point2D bottomRight;
    private final Point2D bottomLeft;

    public Map(Point2D topLeft, Point2D topRight, Point2D bottomRight, Point2D bottomLeft) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomRight = bottomRight;
        this.bottomLeft = bottomLeft;
    }

    private Path2D.Double polygon() {
        Path2D.Double polygon = new Path2D.Double();
        polygon.moveTo(topLeft.getX(), topLeft.getY());
        polygon.lineTo(topRight.getX(), topRight.getY());
        polygon.lineTo(bottomRight.getX(), bottomRight.getY());
        polygon.lineTo(bottomLeft.getX(), bottomLeft.getY());
        polygon.closePath();
        return polygon;
    }

    public boolean contains(Point2D point) {
        return polygon().contains(point.getX(), point.getY());
    }

    public Point2D getTopLeft() {
        return topLeft;
    }

    public Point2D getTopRight() {
        return topRight;
    }

    public Point2D getBottomRight() {
        return bottomRight;
    }

    public Point2D getBottomLeft() {
        return bottomLeft;
    }

}
