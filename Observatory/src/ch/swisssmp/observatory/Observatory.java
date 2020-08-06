package ch.swisssmp.observatory;

import ch.swisssmp.city.City;
import org.bukkit.Location;
import org.bukkit.World;

public class Observatory {
    public static final int OBSERVATORY_RADIUS = 50;

    private City city;
    private final World world;
    private final int id;
    private final Location center;

    public Observatory(World world, int id, int city_id, Location center) {
        this.world = world;
        this.id = id;
        this.city = ch.swisssmp.city.Cities.getCity(city_id);
        this.center = center;
    }

    public City getCity(){
        return city;
    }

    public World getWorld(){
        return world;
    }

    public int getId(){
        return id;
    }

    public Location getCenter(){
        return center;
    }
}
