/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
 
package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Robot extends TimedRobot {
  NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
  NetworkTableEntry tx = table.getEntry("tx");
  NetworkTableEntry ty = table.getEntry("ty");
  NetworkTableEntry ta = table.getEntry("ta");
  NetworkTableEntry thor = table.getEntry("thor");
  NetworkTableEntry tvert = table.getEntry("tvert");
  NetworkTableEntry ts1 = table.getEntry("ts1");
  NetworkTableEntry ts0 = table.getEntry("ts0");
  NetworkTableEntry tv = table.getEntry("tv");
  NetworkTableEntry camTran = table.getEntry("camtran");


  Joystick _joystick = new Joystick(0);
  

  WPI_TalonSRX _rghtFront = new WPI_TalonSRX(10); // Masters are single digits
  WPI_TalonSRX _rghtFollower = new WPI_TalonSRX(11); // Followers are the same id as the master but with a 0 added
  WPI_TalonSRX _leftFront = new WPI_TalonSRX(20);
  WPI_TalonSRX _leftFollower = new WPI_TalonSRX(21);
  
  DifferentialDrive _diffDrive = new DifferentialDrive(_leftFront, _rghtFront);
  double distance = 0;
  double x = 0;
  double isThereATarget = 0;
  double driveSpeed = 0;
  static double maxDriveSpeed = 0.5;
  static double minDriveSpeed = 0.3;
  double driveTurn = 0;
  static double minTurnSpeed = 0.5;
  double[] helpme = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

  //LiftController _lift = new LiftController(false, _joystick);
  

  @Override
  public void robotInit() {
    _rghtFront.configFactoryDefault();
   _rghtFollower.configFactoryDefault();
   _leftFront.configFactoryDefault();
   _leftFollower.configFactoryDefault();
   _rghtFollower.follow(_rghtFront);
    _leftFollower.follow(_leftFront);
  }

  @Override
  public void robotPeriodic() {
  //read values periodically
  x = tx.getDouble(0.0); //angle from crosshair (-27° to 27°) in the x direction
  double y = ty.getDouble(0.0); //angle from crosshair (-27° to 27°) in the y direction
  double area = ta.getDouble(0.0);
  double width = thor.getDouble(0.0);
  double height = tvert.getDouble(0.0);
  double skew1 = ts1.getDouble(0.0);
  double skew0 = ts0.getDouble(0.0);
  isThereATarget = tv.getDouble(0.0);
  distance = (272.695621739*5.75/height + 264*14/width)/2;
  helpme = camTran.getDoubleArray(helpme);

  


  
  //post to smart dashboard periodically
  SmartDashboard.putNumber("LimelightX", x);
  SmartDashboard.putNumber("LimelightY", y);
  SmartDashboard.putNumber("LimelightArea", area);
  SmartDashboard.putNumber("LimelightWidth", width);
  SmartDashboard.putNumber("LimelightHeight", height);
  SmartDashboard.putNumber("LimelightDistance",distance);
  SmartDashboard.putNumber("LimelightSkew1", skew1);
  SmartDashboard.putNumber("LimelightSkew0", skew0);
  //SmartDashboard.putNumberArray("DOES THIS WORK", helpme);
  SmartDashboard.putNumber("DOES THIS WORK0", helpme[0]);
  SmartDashboard.putNumber("DOES THIS WORK1", helpme[1]);
  SmartDashboard.putNumber("DOES THIS WORK2", helpme[2]);
  SmartDashboard.putNumber("DOES THIS WORK3", helpme[3]);
  SmartDashboard.putNumber("DOES THIS WORK4", helpme[4]);
  SmartDashboard.putNumber("DOES THIS WORK5", helpme[5]);
  
  //System.out.println(test);


  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
    //_lift.run();
    _diffDrive.arcadeDrive(_joystick.getY()/2, _joystick.getZ()/1.5);
  }

  @Override
  public void teleopInit() {
  }

  @Override
  public void teleopPeriodic() {
    if (distance > 12){
      _diffDrive.arcadeDrive(_joystick.getY()/(-2), _joystick.getZ()/1.5);
    }
    if(_joystick.getRawButton(5)){
      //table.getEntry("ledMode").setNumber(3); //LEDs on
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(3);
      if (distance <12){
        _diffDrive.arcadeDrive(-0.3, driveTurn);
        //drop ball in cargo ship, or put on hatch panel, i'll figure this out later
      }else{
        driveSpeed = 0.05*(distance-12);
        if (driveSpeed > maxDriveSpeed){
          driveSpeed = maxDriveSpeed;
        } else if(driveSpeed < minDriveSpeed){
          driveSpeed = minDriveSpeed;
        }
        driveTurn = x/27;
        _diffDrive.arcadeDrive(driveSpeed, driveTurn);
      }
    }else if(_joystick.getRawButton(6)){
      //table.getEntry("ledMode").setNumber(1); //LEDs off
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(1);
    }else if(_joystick.getRawButton(4)){
      table.getEntry("ledMode").setNumber(2); //LEDs blind everybody that come in their path
    }else if(_joystick.getRawButton(3)){
      //table.getEntry("ledMode").setNumber(3);
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(3);
      //this only turn on LEDs, no driving
    }
  }

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
  }

}
