package ch.swisssmp.livemap;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.GenericMarker;
import org.dynmap.markers.MarkerDescription;

import ch.swisssmp.livemap.markers.AreaMarker;
import ch.swisssmp.livemap.markers.CircleMarker;
import ch.swisssmp.livemap.markers.Marker;
import ch.swisssmp.livemap.markers.MarkerIcon;
import ch.swisssmp.livemap.markers.PolyLineMarker;
import ch.swisssmp.livemap.markers.PositionMarker;

public class DynmapHandler extends Livemap {

	private final DynmapAPI dynmap;
	
	private DynmapHandler(DynmapAPI dynmap){
		this.dynmap = dynmap;
	}
	
	@Override
	public PositionMarker _getPositionMarker(World world, String group_id, String marker_id) {
		org.dynmap.markers.MarkerSet set = dynmap.getMarkerAPI().getMarkerSet(group_id);
		if(set==null) return null;
		org.dynmap.markers.Marker dynmapMarker = set.findMarker(marker_id);
		if(dynmapMarker==null) return null;
		PositionMarker result = new PositionMarker(null, set.getMarkerSetID(), dynmapMarker.getMarkerID(), null);
		copyMarkerSettings((GenericMarker) dynmapMarker, (Marker) result);
		copyMarkerSettings(dynmapMarker, result);
		return result;
	}

	@Override
	public AreaMarker _getAreaMarker(World world, String group_id, String marker_id) {
		org.dynmap.markers.MarkerSet set = dynmap.getMarkerAPI().getMarkerSet(group_id);
		if(set==null) return null;
		org.dynmap.markers.AreaMarker dynmapMarker = set.findAreaMarker(marker_id);
		if(dynmapMarker==null) return null;
		AreaMarker result = new AreaMarker(world, set.getMarkerSetID(), dynmapMarker.getMarkerID(), null, null);
		copyMarkerSettings((GenericMarker) dynmapMarker, (Marker) result);
		copyMarkerSettings(dynmapMarker, result);
		return result;
	}

	@Override
	public PolyLineMarker _getPolyLineMarker(World world, String group_id, String marker_id) {
		org.dynmap.markers.MarkerSet set = dynmap.getMarkerAPI().getMarkerSet(group_id);
		if(set==null) return null;
		org.dynmap.markers.PolyLineMarker dynmapMarker = set.findPolyLineMarker(marker_id);
		if(dynmapMarker==null) return null;
		PolyLineMarker result = new PolyLineMarker(world, set.getMarkerSetID(), dynmapMarker.getMarkerID(), null);
		copyMarkerSettings((GenericMarker) dynmapMarker, (Marker) result);
		copyMarkerSettings(dynmapMarker, result);
		return result;
	}

	@Override
	public CircleMarker _getCircleMarker(World world, String group_id, String marker_id) {
		org.dynmap.markers.MarkerSet set = dynmap.getMarkerAPI().getMarkerSet(group_id);
		if(set==null) return null;
		org.dynmap.markers.CircleMarker dynmapMarker = set.findCircleMarker(marker_id);
		if(dynmapMarker==null) return null;
		CircleMarker result = new CircleMarker(world, set.getMarkerSetID(), dynmapMarker.getMarkerID(), null, 0, 0);
		copyMarkerSettings((GenericMarker) dynmapMarker, (Marker) result);
		copyMarkerSettings(dynmapMarker, result);
		return result;
	}
	
	protected static DynmapHandler create(Plugin dynmapPlugin){
		if(!(dynmapPlugin instanceof DynmapAPI)) return null;
		return new DynmapHandler((DynmapAPI) dynmapPlugin);
	}

	@Override
	protected void _saveMarker(PositionMarker marker) {
		org.dynmap.markers.MarkerSet set = dynmap.getMarkerAPI().getMarkerSet(marker.getGroup());
		if(set==null) return;
		Location location = marker.getLocation();
		String worldName = location.getWorld().getName();
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		org.dynmap.markers.MarkerIcon icon = dynmap.getMarkerAPI().getMarkerIcon(marker.getIcon());
		org.dynmap.markers.Marker dynmapMarker = set.findMarker(marker.getId());
		if(dynmapMarker==null){
			dynmapMarker = set.createMarker(marker.getId(), marker.getLabel(), worldName, x, y, z, icon, marker.isPersistent());
		}
		copyMarkerSettings((Marker) marker, (GenericMarker) dynmapMarker);
		copyMarkerSettings(marker, dynmapMarker);
	}

	@Override
	protected void _saveMarker(AreaMarker marker) {
		org.dynmap.markers.MarkerSet set = dynmap.getMarkerAPI().getMarkerSet(marker.getGroup());
		if(set==null) return;
		String worldName = marker.getWorld().getName();
		org.dynmap.markers.AreaMarker dynmapMarker = set.findAreaMarker(marker.getId());
		double[] x = marker.getX();
		double[] z = marker.getZ();
		if(dynmapMarker==null){
			dynmapMarker = set.createAreaMarker(marker.getId(), marker.getLabel(), marker.getBoostFlag(), worldName, x, z, marker.isPersistent());
		}
		copyMarkerSettings((Marker) marker, (GenericMarker) dynmapMarker);
		copyMarkerSettings(marker, dynmapMarker);
	}

	@Override
	protected void _saveMarker(PolyLineMarker marker) {
		org.dynmap.markers.MarkerSet set = dynmap.getMarkerAPI().getMarkerSet(marker.getGroup());
		if(set==null) return;
		String worldName = marker.getWorld().getName();
		Vector[] points = marker.getPoints();
		double[] x = new double[points.length];
		double[] y = new double[points.length];
		double[] z = new double[points.length];
		for(int i = 0; i < points.length; i++){
			x[i] = points[i].getX();
			y[i] = points[i].getY();
			z[i] = points[i].getZ();
		}
		org.dynmap.markers.PolyLineMarker dynmapMarker = set.findPolyLineMarker(marker.getId());
		if(dynmapMarker==null){
			dynmapMarker = set.createPolyLineMarker(marker.getId(), marker.getLabel(), false, worldName, x, y, z, marker.isPersistent());
		}
		copyMarkerSettings((Marker) marker, (GenericMarker) dynmapMarker);
		copyMarkerSettings(marker, dynmapMarker);
	}

	@Override
	protected void _saveMarker(CircleMarker marker) {
		org.dynmap.markers.MarkerSet set = dynmap.getMarkerAPI().getMarkerSet(marker.getGroup());
		if(set==null) return;
		Location center = marker.getCenter();
		String worldName = center.getWorld().getName();
		double x = center.getX();
		double y = center.getY();
		double z = center.getZ();
		double radiusX = marker.getRadiusX();
		double radiusZ = marker.getRadiusZ();
		org.dynmap.markers.CircleMarker dynmapMarker = set.findCircleMarker(marker.getId());
		if(dynmapMarker==null){
			dynmapMarker = set.createCircleMarker(marker.getId(), marker.getLabel(), marker.getBoostFlag(), worldName, x, y, z, radiusX, radiusZ, marker.isPersistent());
		}
		copyMarkerSettings((Marker) marker, (GenericMarker) dynmapMarker);
		copyMarkerSettings(marker, dynmapMarker);
	}

	@Override
	protected MarkerIcon _getIcon(String icon_id) {
		org.dynmap.markers.MarkerIcon dynmapIcon = dynmap.getMarkerAPI().getMarkerIcon(icon_id);
		if(dynmapIcon==null) return null;
		MarkerIcon result = new MarkerIcon(dynmapIcon.getMarkerIconID());
		copyIconSettings(dynmapIcon, result);
		return result;
	}

	@Override
	protected void _saveIcon(MarkerIcon icon) {
		org.dynmap.markers.MarkerIcon dynmapIcon = dynmap.getMarkerAPI().getMarkerIcon(icon.getId());
		URL url;
		InputStream inputStream = null;
		if(icon.getFileUrl()!=null){
			try {
				url = new URL(icon.getFileUrl());
				inputStream = url.openStream();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		if(dynmapIcon==null){
			if(inputStream==null) return;
			dynmapIcon = dynmap.getMarkerAPI().createMarkerIcon(icon.getId(), icon.getLabel(), inputStream);
		}
		else if(inputStream!=null){
			dynmapIcon.setMarkerIconImage(inputStream);
		}
		copyIconSettings(icon, dynmapIcon);
	}
	
	private void copyIconSettings(org.dynmap.markers.MarkerIcon template, MarkerIcon icon){
		icon.setLabel(template.getMarkerIconLabel());
		icon.setSize(getMarkerSize(template.getMarkerIconSize()));
	}
	
	private void copyIconSettings(MarkerIcon template, org.dynmap.markers.MarkerIcon icon){
		icon.setMarkerIconLabel(template.getLabel());
	}
	
	private void copyMarkerSettings(org.dynmap.markers.GenericMarker template, Marker marker){
		marker.setLabel(template.getLabel());
		marker.setPersistent(template.isPersistentMarker());
		if(template instanceof MarkerDescription){
			marker.setDescription(((MarkerDescription)template).getDescription());
		}
	}
	
	private void copyMarkerSettings(Marker template, org.dynmap.markers.GenericMarker marker){
		marker.setLabel(template.getLabel());
		if(marker instanceof MarkerDescription){
			((MarkerDescription)marker).setDescription(template.getDescription());
		}
	}
	
	private void copyMarkerSettings(org.dynmap.markers.Marker template, PositionMarker marker){
		World world = Bukkit.getWorld(template.getWorld());
		org.dynmap.markers.MarkerIcon icon = template.getMarkerIcon();
		marker.setLocation(new Location(world, template.getX(), template.getY(), template.getZ()));
		marker.setIcon(icon!=null ? icon.getMarkerIconID() : null);
	}
	
	private void copyMarkerSettings(PositionMarker template, org.dynmap.markers.Marker marker){
		Location location = template.getLocation();
		org.dynmap.markers.MarkerIcon icon = dynmap.getMarkerAPI().getMarkerIcon(template.getIcon());
		marker.setLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
		marker.setMarkerIcon(icon);
	}
	
	private void copyMarkerSettings(org.dynmap.markers.AreaMarker template, AreaMarker marker){
		double[] x = new double[template.getCornerCount()];
		double[] z = new double[template.getCornerCount()];
		for(int i = 0; i < template.getCornerCount(); i++){
			x[i] = template.getCornerX(i);
			z[i] = template.getCornerZ(i);
		}
		marker.setCorners(x, z);
		marker.setRangeY(template.getBottomY(), template.getTopY());
		marker.setBoostFlag(template.getBoostFlag());
		marker.setFillColor(template.getFillColor());
		marker.setFillOpacity(template.getFillOpacity());
		marker.setLineColor(template.getLineColor());
		marker.setLineOpacity(template.getLineOpacity());
		marker.setLineWeight(template.getLineWeight());
	}
	
	private void copyMarkerSettings(AreaMarker template, org.dynmap.markers.AreaMarker marker){
		marker.setCornerLocations(template.getX(), template.getZ());
		marker.setRangeY(template.getBottomY(), template.getTopY());
		marker.setBoostFlag(template.getBoostFlag());
		marker.setFillStyle(template.getFillOpacity(), template.getFillColor());
		marker.setLineStyle(template.getLineWeight(), template.getLineOpacity(), template.getLineColor());
	}
	
	private void copyMarkerSettings(org.dynmap.markers.PolyLineMarker template, PolyLineMarker marker){
		Vector[] points = new Vector[template.getCornerCount()];
		for(int i = 0; i < template.getCornerCount(); i++){
			double x = template.getCornerX(i);
			double y = template.getCornerY(i);
			double z = template.getCornerZ(i);
			points[i] = new Vector(x,y,z);
		}
		marker.setPoints(points);
		marker.setLineColor(template.getLineColor());
		marker.setLineOpacity(template.getLineOpacity());
		marker.setLineWeight(template.getLineWeight());
	}
	
	private void copyMarkerSettings(PolyLineMarker template, org.dynmap.markers.PolyLineMarker marker){
		Vector[] points = template.getPoints();
		double[] x = new double[points.length];
		double[] y = new double[points.length];
		double[] z = new double[points.length];
		for(int i = 0; i < points.length; i++){
			x[i] = points[i].getX();
			y[i] = points[i].getY();
			z[i] = points[i].getZ();
		}
		marker.setCornerLocations(x, y, z);
		marker.setLineStyle(template.getLineWeight(), template.getLineOpacity(), template.getLineColor());
	}
	
	private void copyMarkerSettings(org.dynmap.markers.CircleMarker template, CircleMarker marker){
		World world = Bukkit.getWorld(template.getWorld());
		marker.setCenter(new Location(world, template.getCenterX(), template.getCenterY(), template.getCenterZ()));
		marker.setRadius(template.getRadiusX(), template.getRadiusZ());
		marker.setBoostFlag(template.getBoostFlag());
		marker.setFillColor(template.getFillColor());
		marker.setFillOpacity(template.getFillOpacity());
		marker.setLineColor(template.getLineColor());
		marker.setLineOpacity(template.getLineOpacity());
		marker.setLineWeight(template.getLineWeight());
	}
	
	private void copyMarkerSettings(CircleMarker template, org.dynmap.markers.CircleMarker marker){
		Location location = template.getCenter();
		double radiusX = template.getRadiusX();
		double radiusZ = template.getRadiusZ();
		marker.setCenter(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
		marker.setRadius(radiusX, radiusZ);
		marker.setBoostFlag(template.getBoostFlag());
		marker.setFillStyle(template.getFillOpacity(), template.getFillColor());
		marker.setLineStyle(template.getLineWeight(), template.getLineOpacity(), template.getLineColor());
	}
	
	private int getMarkerSize(org.dynmap.markers.MarkerIcon.MarkerSize size){
		switch(size){
		case MARKER_16x16: return 16;
		case MARKER_32x32: return 32;
		case MARKER_8x8: return 8;
		default: return 1;
		}
	}
}
