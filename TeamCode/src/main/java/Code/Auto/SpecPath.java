package Code.Auto;

import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;

public class SpecPath {

    public static PathBuilder builder0 = new PathBuilder();
    public static PathBuilder builder1 = new PathBuilder();

    public static PathChain line1 = builder0
            .addPath(
                    new BezierLine(
                            new Point(7.500, 72.000, Point.CARTESIAN),
                            new Point(24.459, 72.000, Point.CARTESIAN)
                    )
            )
            .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
            .build();

    public static PathChain line2 = builder1
            .addPath(
                    new BezierLine(
                            new Point(24.459, 72.000, Point.CARTESIAN),
                            new Point(39.750, 72.000, Point.CARTESIAN)
                    )
            )
            .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
            .build();
}

