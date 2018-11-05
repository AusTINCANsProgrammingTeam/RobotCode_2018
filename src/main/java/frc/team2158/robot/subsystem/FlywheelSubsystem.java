package frc.team2158.robot.subsystem;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2158.robot.command.stopFlywheel;
import frc.team2158.robot.subsystem.intake.IntakeSubsystem.IntakeDirection;
/**
 * @author William Blount
 * @version 0.0.1
 * This subsystem manages the cube intake motors and solenoids.
 */
public class FlywheelSubsystem extends Subsystem {

    private static double flywheel_speed = 1.0;

    private SpeedController speedController;

    public FlywheelSubsystem(SpeedController speedController) {
        this.speedController = speedController;
    }

    public void runFlywheel(double speed) {
            speedController.set(speed);
    }

    /**
     * Stops the intake by setting the speed controllers to 0.0
     */
    public void stopFlywheel() {
        double intake_modified_speed = flywheel_speed;
        for(double i = 0; i < 1.0; i = i+ .02){
            speedController.set(intake_modified_speed-i);
        }
        speedController.set(0);
    }

    public void estopFlywheel() {
        speedController.set(0);
    }

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new stopFlywheel());
    }
}
