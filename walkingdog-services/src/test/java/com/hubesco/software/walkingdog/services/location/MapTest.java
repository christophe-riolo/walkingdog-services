package com.hubesco.software.walkingdog.services.location;

import com.hubesco.software.walkingdog.services.location.Map;
import java.awt.Point;
import java.awt.geom.Point2D;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author paoesco
 */
public class MapTest {
    
    private Map map;
    
    @Before
    public void before() {
        Point topLeft = new Point(-1, 1);
        Point topRight = new Point(1, 1);
        Point bottomRight = new Point(1, -1);
        Point bottomLeft = new Point(-1, -1);
        map = new Map(topLeft, topRight, bottomRight, bottomLeft);
    }
    
    @Test
    public void testContainsTrue() {
        Point point1 = new Point(0, 0);
        Assert.assertTrue(map.contains(point1));
        
        Point2D point2 = new Point2D.Double(0.5, 0.5);
        Assert.assertTrue(map.contains(point2));
    }
    
    @Test
    public void testContainsFalse() {
        Point point1 = new Point(2, 2);
        Assert.assertFalse(map.contains(point1));
    }
    
}
