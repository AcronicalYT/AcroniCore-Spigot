package uk.acronical.particle;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for calculating spatial coordinates for geometric shapes.
 * <p>
 * This class provides mathematical algorithms to generate lists of {@link Location}
 * objects, which can be utilised for particle effects, hologram placement,
 * or hitbox visualisation.
 *
 * @author Acronical
 * @since 1.0.2
 */
public class ShapeGenerator {

    /**
     * Generates a circular set of coordinates on the XZ plane.
     *
     * @param centre The centre of the circle.
     * @param radius The distance from the centre to the edge.
     * @param points The number of locations to generate.
     * @return A list of locations forming a circle.
     */
    @NotNull
    public static List<Location> circle(@NotNull Location centre, double radius, int points) {
        List<Location> locations = new ArrayList<>();

        double increment = (2 * Math.PI) / points;

        for (int i = 0; i < points; i++) {
            double angle = i * increment;
            double x = centre.getX() + radius * Math.cos(angle);
            double z = centre.getZ() + radius * Math.sin(angle);
            locations.add(new Location(centre.getWorld(), x, centre.getY(), z));
        }

        return locations;
    }

    /**
     * Generates a uniform sphere of coordinates using the Fibonacci Spiral algorithm.
     *
     * @param centre The centre of the sphere.
     * @param radius The radius of the sphere.
     * @param points The total number of locations to distribute across the surface.
     * @return A list of locations forming a sphere.
     */
    @NotNull
    public static List<Location> sphere(@NotNull Location centre, double radius, int points) {
        List<Location> locations = new ArrayList<>();

        double increment = Math.PI * (3 - Math.sqrt(5));

        for (int i = 0; i < points; i++) {
            double y = 1 - (i / (double) points) * 2;
            double radiusAtY = Math.sqrt(1 - y * y);

            double angle = i * increment;

            double x = centre.getX() + radius * radiusAtY * Math.cos(angle);
            double z = centre.getZ() + radius * radiusAtY * Math.sin(angle);
            double finalY = centre.getY() + radius * y;

            locations.add(new Location(centre.getWorld(), x, finalY, z));
        }

        return locations;
    }

    /**
     * Generates a helical (spiral) set of coordinates rising upwards.
     *
     * @param centre The starting base centre.
     * @param radius The horizontal radius of the spiral.
     * @param height The vertical height of the helix.
     * @param points The number of locations to generate.
     * @return A list of locations forming a helix.
     */
    @NotNull
    public static List<Location> helix(@NotNull Location centre, double radius, double height, int points) {
        List<Location> locations = new ArrayList<>();

        double increment = (2 * Math.PI) / points;

        for (int i = 0; i < points; i++) {
            double angle = i * increment;
            double x = centre.getX() + radius * Math.cos(angle);
            double z = centre.getZ() + radius * Math.sin(angle);
            double y = centre.getY() + (height / points) * i;
            locations.add(new Location(centre.getWorld(), x, y, z));
        }

        return locations;
    }

    /**
     * Generates a straight line of coordinates between two points.
     *
     * @param start  The starting location.
     * @param end    The ending location.
     * @param points The number of segments to divide the line into.
     * @return A list of locations forming a line.
     */
    @NotNull
    public static List<Location> line(@NotNull Location start, @NotNull Location end, int points) {
        List<Location> locations = new ArrayList<>();

        double incrementX = (end.getX() - start.getX()) / (points - 1);
        double incrementY = (end.getY() - start.getY()) / (points - 1);
        double incrementZ = (end.getZ() - start.getZ()) / (points - 1);

        for (int i = 0; i < points; i++) {
            locations.add(new Location(start.getWorld(), start.getX() + incrementX * i, start.getY() + incrementY * i, start.getZ() + incrementZ * i));
        }

        return locations;
    }

    /**
     * Generates the perimeter of a rectangle on the XY plane.
     *
     * @param centre The centre of the rectangle.
     * @param width  The total width (X-axis).
     * @param height The total height (Y-axis).
     * @param points The number of locations per side.
     * @return A list of locations forming the rectangle boundary.
     */
    @NotNull
    public static List<Location> rectangle(@NotNull Location centre, double width, double height, int points) {
        List<Location> locations = new ArrayList<>();

        double halfWidth = width / 2;
        double halfHeight = height / 2;

        for (int i = 0; i < points; i++) {
            double x = centre.getX() + (i % 2 == 0 ? -halfWidth : halfWidth);
            double y = centre.getY() + (i / 2 == 0 ? -halfHeight : halfHeight);
            locations.add(new Location(centre.getWorld(), x, y, centre.getZ()));
        }

        return locations;
    }
}