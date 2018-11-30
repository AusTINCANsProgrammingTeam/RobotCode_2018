package frc.team2158.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2158.robot.command.drive.DriveMode;
import frc.team2158.robot.command.drive.OperatorControl;
import frc.team2158.robot.command.drive.ToggleGearMode;
import frc.team2158.robot.command.intake.*;
import frc.team2158.robot.command.lift.MoveLift;
import frc.team2158.robot.subsystem.drive.DriveSubsystem;
import frc.team2158.robot.subsystem.drive.GearMode;
import frc.team2158.robot.subsystem.drive.TalonSRXGroup;
import frc.team2158.robot.subsystem.intake.IntakeSubsystem;
import frc.team2158.robot.subsystem.lift.LiftSubsystem;

import java.util.logging.Logger;
//TODO Rename some classes <- Billy's job.
//TODO Lua macros

/**
 * @author William Blount
 * @version 0.0.1
 * The main class of our code.
 * Initializes the teleOperated code.
 */
public class Robot extends TimedRobot {
    private SendableChooser<Double> autoChooser;

    private static final Logger LOGGER = Logger.getLogger(Robot.class.getName());
    private static final LoggingSystem LOGGING_SYSTEM = LoggingSystem.getInstance();

    private static DriveSubsystem driveSubsystem;
    private static LiftSubsystem liftSubsystem;
    private static IntakeSubsystem intakeSubsystem;

    private static OperatorInterface operatorInterface;
    private Spark blinkin = new Spark(6);

    /**
     *Initializes the TalonSRX Groups, the Solenoid, Operator Interface, Lift Motors, and Intake Motors.
     */
    @Override
    public void robotInit() {
        // Initialize the auto chooser system
        autoChooser = new SendableChooser<>();
        autoChooser.addObject("0.0", 0.0);
        autoChooser.addObject("0.25", 0.25);
        autoChooser.addObject("0.50", 0.5);
        autoChooser.addObject("0.75", 0.75);
        autoChooser.addDefault("1.0", 1.0);
        autoChooser.addObject("1.25", 1.25);
        autoChooser.addObject("1.50", 1.5);
        autoChooser.addObject("1.75", 1.75);
        autoChooser.addObject("2.0", 2.0);
        autoChooser.addObject("2.25", 2.25);
        autoChooser.addObject("2.50", 2.50);
        autoChooser.addObject("2.75", 2.75);
        autoChooser.addObject("3.0", 3.5);

        /*

            autoChooser.addObject("#.#", #.#);
         */
        SmartDashboard.putData("Time to run forward in auto!", autoChooser);
        TalonSRXGroup leftGroup =  new TalonSRXGroup(
                SmartDashboard.getNumber("pLeft", 0),
                SmartDashboard.getNumber("iLeft", 0),
                SmartDashboard.getNumber("dLeft", 0),
                new WPI_TalonSRX(RobotMap.LEFT_MOTOR_1), // This motor is the master for the left side.
                new WPI_TalonSRX(RobotMap.LEFT_MOTOR_2),
                new WPI_TalonSRX(RobotMap.LEFT_MOTOR_3)
        );

        TalonSRXGroup rightGroup = new TalonSRXGroup(
                SmartDashboard.getNumber("pRight", 0),
                SmartDashboard.getNumber("iRight", 0),
                SmartDashboard.getNumber("dRight", 0),
                new WPI_TalonSRX(RobotMap.RIGHT_MOTOR_1), // This motor is the master for the right side.
                new WPI_TalonSRX(RobotMap.RIGHT_MOTOR_2),
                new WPI_TalonSRX(RobotMap.RIGHT_MOTOR_3)
        );

        // Initialize the drive subsystem.
        driveSubsystem = new DriveSubsystem(leftGroup, rightGroup
              ,
                new DoubleSolenoid(RobotMap.PCM_ADDRESS, RobotMap.GEARBOX_FORWARD_CHANNEL,
                        RobotMap.GEARBOX_REVERSE_CHANNEL)
        );

        LOGGER.info("Drive Subsystem Initialized properly!");
        // Initialize the lift subsystem.
        liftSubsystem = new LiftSubsystem(
                new SpeedControllerGroup(
                        new Spark(RobotMap.LIFT_MOTOR_1),
                        new Spark(RobotMap.LIFT_MOTOR_2),
                        new Spark(RobotMap.LIFT_MOTOR_3)
                ),
                true 
        );
        LOGGER.info("Lift Subsystem Initialized properly!");
        // Initialize the intake subsystem.
        intakeSubsystem = new IntakeSubsystem(
                new Spark(RobotMap.LEFT_INTAKE_MOTOR),
                new Spark(RobotMap.RIGHT_INTAKE_MOTOR),
                new Spark(RobotMap.PIVOT_INTAKE_MOTOR),
                new DoubleSolenoid(RobotMap.PCM_ADDRESS, RobotMap.INTAKE_SOLENOID_1, RobotMap.INTAKE_SOLENOID_2)
        );
        LOGGER.info("Intake Subsystem Initialized properly!");
        // Initialize the operator interface.
        operatorInterface = new OperatorInterface();

        LOGGER.info("Robot initialization completed.");
    }

    /**
     * Returns the instance of the drive subsystem.
     * @return the instance of the drive subsystem.
     */
    @Override
    public void autonomousInit() {
        timer.reset();
        timer.start();
    }

    @Override
    public void autonomousPeriodic() {
        if(timer.get() < 2){
            getDriveSubsystem().arcadeDrive(-0.75, 0);
        } else {
            getDriveSubsystem().arcadeDrive(0, 0);
        }
        if(timer.get() < 0.5){
            //getIntakeSubsystem().pivotIntake(IntakeSubsystem.PivotDirection.DOWN);
        } else {

            //getIntakeSubsystem().stopPivot();
        }
    }

    public static DriveSubsystem getDriveSubsystem() {
        if(driveSubsystem != null) {
            return driveSubsystem;
        }
        throw new RuntimeException("Drive subsystem has not yet been initialized!");
    }

    /**
     * Returns the instance of the lift subsystem.
     * @return the instance of the lift subsystem.
     */
    public static LiftSubsystem getLiftSubsystem() {
        if(liftSubsystem != null) {
            return liftSubsystem;
        }
        throw new RuntimeException("Lift subsystem has not yet been initialized!");
    }

    /**
     * Returns the instance of the intake subsystem.
     * @return the instance of the intake subsystem.
     */
    public static IntakeSubsystem getIntakeSubsystem() {
        if(intakeSubsystem != null) {
            return intakeSubsystem;
        }
        throw new RuntimeException("Intake subsystem has not yet been initialized!");
    }

    /**
     * Returns the instance of the Operator Interface.
     * @return the instance of the Operator Interface.
     */
    public static OperatorInterface getOperatorInterface() {
        return operatorInterface;
    }

    /**
     * Initializes the TeleOp Robot State and binds the buttons that will be used in the controller.
     */
    @Override
    public void teleopInit() {

        LOGGER.info("Teleop Init!");
        operatorInterface.bindButton("buttonLB", OperatorInterface.ButtonMode.WHILE_HELD, new Intake());
        operatorInterface.bindButton("buttonLT", OperatorInterface.ButtonMode.WHILE_HELD, new Outtake());
        operatorInterface.bindButton("buttonY", OperatorInterface.ButtonMode.WHEN_PRESSED, new ToggleIntakeSolenoid());
        operatorInterface.bindButton("buttonRB", OperatorInterface.ButtonMode.WHILE_HELD, new MoveLift(LiftSubsystem.Direction.UP, LiftSubsystem.DEFAULT_LIFT_UP_SPEED));
        operatorInterface.bindButton("buttonRT", OperatorInterface.ButtonMode.WHILE_HELD, new MoveLift(LiftSubsystem.Direction.DOWN, LiftSubsystem.DEFAULT_LIFT_DOWN_SPEED));
        operatorInterface.bindButton("buttonX", OperatorInterface.ButtonMode.WHILE_HELD, new CounterClockwise());
        operatorInterface.bindButton("buttonB", OperatorInterface.ButtonMode.WHILE_HELD, new Clockwise());
        operatorInterface.bindButton("buttonA", OperatorInterface.ButtonMode.WHEN_PRESSED, new ToggleGearMode());
        operatorInterface.bindButton("buttonBack", OperatorInterface.ButtonMode.WHILE_HELD, new PivotDown());
        operatorInterface.bindButton("buttonStart", OperatorInterface.ButtonMode.WHILE_HELD, new PivotUp());
        Scheduler.getInstance().add(new OperatorControl(DriveMode.ARCADE));
    // Stretch Goal: Make the button bindings come from an xml/json config.
    //how would we implement such a system?
}
    /**
     * Runs the TeleOp Periodic code.
     */
    private static double solid_red = 0.61;
    private static double solid_blue = 0.87;
    private static double breath_red = -0.17;
    private static double breath_blue = -0.15;

    @Override
    public void teleopPeriodic() {
        SmartDashboard.putNumber("Left Sensor Position", getDriveSubsystem().getLeftController().getMaster()
                .getSelectedSensorPosition(0));
        SmartDashboard.putNumber("Left Sensor Velocity", getDriveSubsystem().getLeftController().getMaster()
                .getSelectedSensorVelocity(0));

        SmartDashboard.putNumber("Right Sensor Position", getDriveSubsystem().getRightController().getMaster()
                .getSelectedSensorPosition(0));
        SmartDashboard.putNumber("Right Sensor Velocity", getDriveSubsystem().getRightController().getMaster()
                .getSelectedSensorVelocity(0));

        Scheduler.getInstance().run();
        GearMode gearMode = getDriveSubsystem().getGearMode();

        switch(getIntakeSubsystem().getSolenoidState()) {
            case kForward: //intake is closed - dont strobe
                if(gearMode.equals(GearMode.HIGH)){
                    blinkin.set(solid_red);
                } else if(gearMode.equals(GearMode.LOW)) {
                    blinkin.set(solid_blue);
                } else {
                    blinkin.set(0.9);
                }
                break;
            case kReverse: //intake is open - strobe
                if(gearMode.equals(GearMode.HIGH)){
                    blinkin.set(breath_red);
                } else if(gearMode.equals(GearMode.LOW)) {
                    blinkin.set(breath_blue);
                } else {
                    blinkin.set(0.9);
                }
                break;
            case kOff:

                break;
        }


    }
    private static Timer timer = new Timer();

}
