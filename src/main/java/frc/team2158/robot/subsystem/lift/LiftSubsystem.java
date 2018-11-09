package frc.team2158.robot.subsystem.lift;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2158.robot.command.lift.StopLift;

import java.util.logging.Logger;
/**
 * @author William Blount
 * @version 0.0.1
 * This class initializes and runs our Lift speed and direction.  
 */
public class LiftSubsystem extends Subsystem {
    private static final Logger LOGGER = Logger.getLogger(LiftSubsystem.class.getName());

    public enum Direction {UP, DOWN}

    private SpeedController liftSpeedController;
    public static double DEFAULT_LIFT_UP_SPEED = 1.0;
    public static double DEFAULT_LIFT_DOWN_SPEED = 0.75;
    /**
     * Initializes our Lift subsystem.
     * @param liftSpeedController controller to be initialized.
     * @param inverted If the lift is inverted or not
     */
    public LiftSubsystem(SpeedController liftSpeedController, boolean inverted) {
        this.liftSpeedController = liftSpeedController;
        liftSpeedController.setInverted(inverted);
        LOGGER.info("Lift subsystem initialization complete!");
    }
    /**
     * Moves the lift.
     * @param direction Direction specified.
     * @param speed Speed specified.
     */
    public void moveLift(Direction direction, double speed) {
        switch(direction) {
            case UP:
                liftSpeedController.set(speed);
                break;
            case DOWN:
                liftSpeedController.set(-speed);
                break;
        }
    }
    /**
     * Stops the lift by setting the speed to zero.
     */
    public void stopLift() {
        for(double i = 0; i < 1.0; i = i+ .02){
            liftSpeedController.set(DEFAULT_LIFT_UP_SPEED-i);
            Timer.delay(.0025);
        }
        liftSpeedController.set(0.0);
    }
    
    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new StopLift());
    }
}
