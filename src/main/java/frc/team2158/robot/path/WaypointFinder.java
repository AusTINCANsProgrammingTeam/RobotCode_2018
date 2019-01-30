package frc.team2158.robot.path;

import frc.team2158.robot.subsystem.drive.TalonSRXGroup;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;
import frc.team2158.robot.subsystem.drive.DriveSubsystem;

import java.beans.Encoder;

public class WaypointFinder {
    private double way1Px = 0;
    private double way1Py = 0;
    private double way1Pangle;
    private double way2Px;
    private double way2Py;
    private double way2Pangle;

    public void setWaypoints() {
        Waypoint[] points = new Waypoint[]{
                new Waypoint(way1Px, way1Py, Pathfinder.d2r(way1Pangle)),
                new Waypoint(way2Px, way2Py, Pathfinder.d2r(way2Pangle))
        };

        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.05, 1.7, 2.0, 60.0);
        Trajectory trajectory = Pathfinder.generate(points, config);
        TankModifier modifier = new TankModifier(trajectory).modify(0.5);

        EncoderFollower left = new EncoderFollower(modifier.getLeftTrajectory());
        EncoderFollower right = new EncoderFollower(modifier.getRightTrajectory());

        left.configureEncoder(DriveSubsystem);
    }
}
