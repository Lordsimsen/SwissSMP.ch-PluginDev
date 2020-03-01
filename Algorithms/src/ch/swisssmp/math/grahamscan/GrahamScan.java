package ch.swisssmp.math.grahamscan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.bukkit.util.Vector;

import ch.swisssmp.math.util.MapUtil;

public class GrahamScan {
	public static List<Vector> calculate(List<Vector> points){
		if(points.size()<3) return points;
		Vector p0 = getLowest(points);
		List<Vector> sorted = sortByAngle(points, p0);
		Stack<Vector> result = new Stack<Vector>();
		result.add(sorted.get(0));
		result.add(sorted.get(1));
		int k = 2;
		while(k < sorted.size()){
			Vector pk = sorted.get(k);
			Vector pt1 = result.peek();
			Vector pt2 = result.get(result.size()-2);
			if(result.size()<=2 || !isPositiveTriangle(pk,pt1,pt2)){
				result.push(pk);
				k++;
			}
			else{
				result.pop();
			}
		}
		return result;
	}
	
	/**
	 * Findet den Punkt mit dem kleinsten Z-Wert
	 * @param points - Eine Sammlung von Punkten
	 * @return Der Punkt mit dem kleinsten Z-Wert
	 */
	private static Vector getLowest(List<Vector> points){
		final double tolerance = 0.01;
		Vector lowest = points.get(0);
		for(Vector vector : points){
			double difference = vector.getZ()-lowest.getZ();
			//skip if z is greater than lowest
			if(difference>tolerance) continue;
			//skip if z is equal but x is greater
			if(Math.abs(difference)<tolerance && vector.getX()>lowest.getX()) continue;
			lowest = vector;
		}
		return lowest;
	}
	
	/**
	 * Sortiert die Vektoren nach ihrem Winkel zu P0
	 * @param points
	 * @param p0 - Der Punkt mit der tiefsten Ordinate (Y-Wert in 2D-Graphen, hier der Z-Wert)
	 * @return Die sortierte Liste
	 */
	private static List<Vector> sortByAngle(List<Vector> points, Vector p0){
		HashMap<Vector,Double> angles = new HashMap<Vector,Double>();
		Vector xAxis = new Vector(1,0,0);
		for(Vector point : points){
			Vector delta = point.clone().subtract(p0);
			if(delta.lengthSquared()==0) continue;
			double angle = xAxis.angle(new Vector(delta.getX(),0,delta.getZ()));
			angles.put(point, angle);
		}
		Map<Vector,Double> sorted = MapUtil.sortByValue(angles, false);
		List<Vector> result = new ArrayList<Vector>();
		for(Vector v : sorted.keySet()){
			result.add(v);
		}
		return result;
	}
	
	/**
	 * Prüft, ob sie Punktreihenfolge dem Uhrzeigersinn folgt
	 * @param a
	 * @param b
	 * @param c
	 * @return <code>true</code>, wenn die Reihenfolge dem Uhrzeigersinn folgt; ansonsten <code>false</code>
	 */
	private static boolean isPositiveTriangle(Vector a, Vector b, Vector c){
		Vector ab = new Vector(b.getX()-a.getX(), b.getY()-a.getY(), b.getZ() - a.getZ());
		Vector ac = new Vector(c.getX() - a.getX(), c.getY() - a.getY(), c.getZ() - a.getZ());
		double A = ab.getX();
		double B = ab.getZ();
		double C = ac.getX();
		double D = ac.getY();
		return A*D-B*C > 0;
	}
}
