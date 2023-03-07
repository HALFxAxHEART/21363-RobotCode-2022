package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import com.qualcomm.robotcore.hardware.Servo;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

@Autonomous(name="AutoDriveColorEncoder_Left", group="Robot")
@Disabled
public class AutoDriveColorEncoder_Left extends LinearOpMode {

    ColorSensor sensorColor;
    DistanceSensor sensorDistance;
    
    private DcMotor         LeftBack   = null;
    private DcMotor         RightBack  = null;
    private DcMotor         LeftFront   = null;
    private DcMotor         RightFront  = null;
    private DcMotor         LiftMotor = null;
    private Servo RevServo;

    private ElapsedTime     runtime = new ElapsedTime();

    static final double     FORWARD_SPEED = 0.3;
    static final double     STRAFE_SPEED = 0.3;

    @Override
    public void runOpMode() {

        sensorColor = hardwareMap.get(ColorSensor.class, "sensor_color");
        sensorDistance = hardwareMap.get(DistanceSensor.class, "sensor_color");

        float hsvValues[] = {0F, 0F, 0F};
        final float values[] = hsvValues;
        final double SCALE_FACTOR = 255;
        
        LeftBack  = hardwareMap.get(DcMotor.class, "LeftBack");
        RightBack = hardwareMap.get(DcMotor.class, "RightBack");
        LeftFront  = hardwareMap.get(DcMotor.class, "LeftFront");
        RightFront = hardwareMap.get(DcMotor.class, "RightFront");
        LiftMotor = hardwareMap.get(DcMotor.class, "LiftMotor");
        RevServo    = hardwareMap.get(Servo.class, "RevServo");

        LeftBack.setDirection(DcMotor.Direction.REVERSE);
        RightBack.setDirection(DcMotor.Direction.FORWARD);
        LeftFront.setDirection(DcMotor.Direction.FORWARD);
        RightFront.setDirection(DcMotor.Direction.REVERSE);
        LiftMotor.setDirection(DcMotor.Direction.FORWARD);

        LeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RevServo.setPosition(1);     
                
        telemetry.addData("Status", "Ready to run");
            Color.RGBToHSV((int) (sensorColor.red() * SCALE_FACTOR),
            (int) (sensorColor.green() * SCALE_FACTOR),
            (int) (sensorColor.blue() * SCALE_FACTOR), hsvValues);
            telemetry.addData("Hue", hsvValues[0]);
            telemetry.update();
        telemetry.update();

        waitForStart();
        
        //move forward until reaching cone to read hue
   
        LeftBack.setTargetPosition(9000);
        LeftFront.setTargetPosition(9000);
        RightBack.setTargetPosition(9000);
        RightFront.setTargetPosition(9000);
        
        LiftMotor.setTargetPosition(0);
        LiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        LiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        LeftBack.setPower(FORWARD_SPEED);
        RightBack.setPower(FORWARD_SPEED);
        LeftFront.setPower(FORWARD_SPEED);
        RightFront.setPower(FORWARD_SPEED);
        LiftMotor.setPower(1);
        
        LeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        while (opModeIsActive() && (sensorDistance.getDistance(DistanceUnit.CM) > 3.0))
        {
            Color.RGBToHSV((int) (sensorColor.red() * SCALE_FACTOR),
            (int) (sensorColor.green() * SCALE_FACTOR),
            (int) (sensorColor.blue() * SCALE_FACTOR),
            hsvValues);
            telemetry.addData("Path", "Leg 1: %4.1f S Elapsed", runtime.seconds());
            telemetry.addData("Distance (cm)",
                String.format(Locale.US, "%.02f", sensorDistance.getDistance(DistanceUnit.CM)));
            telemetry.addData("Hue", hsvValues[0]);
            telemetry.update();
            idle();
        }
        LeftBack.setPower(0);
        LeftFront.setPower(0);
        RightBack.setPower(0);
        RightFront.setPower(0);
        
        sleep(500);
        
       // LeftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
      //  RightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);        
      //  LeftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);        
      //  RightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
        //read out hue value
        telemetry.addData("Final Hue Value Reading", hsvValues[0]);
        telemetry.update();      
        
        sleep(200);
                
        //based on hue value, read out what zone to move to
        if(hsvValues[0] <=110 && hsvValues[0] >=35)
        
        //MOVE TO ZONE 1
            {telemetry.addData("Move to zone 1", "now");
               ConeLiftDrop();
                //set power to move straight forward to zone 1
                LeftBack.setPower(STRAFE_SPEED);
                RightBack.setPower(STRAFE_SPEED);
                LeftFront.setPower(STRAFE_SPEED);
                RightFront.setPower(STRAFE_SPEED);
            
                //move forward for .6 seconds
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < .2)) {
                    telemetry.addData("Path to zone 1", "Leg 1: %4.1f S Elapsed", runtime.seconds());
                    telemetry.update();
                 }

                // move to the left for 2 second to get into zone 1
                    LeftBack.setPower(STRAFE_SPEED);
                    RightBack.setPower(-STRAFE_SPEED);
                    LeftFront.setPower(-STRAFE_SPEED);
                    RightFront.setPower(STRAFE_SPEED);
                    
                runtime.reset();                    
                while (opModeIsActive() && (runtime.seconds() <2)) {
                    telemetry.addData("Path", "Leg 1: %4.1f S Elapsed", runtime.seconds());
                    telemetry.update();
                 }
                 
                // Stop in Zone 1
                    LeftBack.setPower(0);
                    RightBack.setPower(0);
                    LeftFront.setPower(0);
                    RightFront.setPower(0);
                    
                    telemetry.addData("Path to Zone 1", "Complete");
                    telemetry.addData("Hue", hsvValues[0]);
                    telemetry.update();
                    sleep(500);                }
        else if(hsvValues[0] <= 180 && hsvValues[0] >= 140)
        
        //MOVE TO ZONE 3
            {telemetry.addData("Move to zone 3", "now");
               ConeLiftDrop();
                //set power to move straight forward to zone 3
                LeftBack.setPower(STRAFE_SPEED);
                RightBack.setPower(STRAFE_SPEED);
                LeftFront.setPower(STRAFE_SPEED);
                RightFront.setPower(STRAFE_SPEED);
            
                //move forward for .4 seconds
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < .4)) {
                    telemetry.addData("Path to zone 3", "Leg 1: %4.1f S Elapsed", runtime.seconds());
                    telemetry.update();
                 }

                // move to the right for 2 second to get into zone 3
                    LeftBack.setPower(-STRAFE_SPEED);
                    RightBack.setPower(STRAFE_SPEED);
                    LeftFront.setPower(STRAFE_SPEED);
                    RightFront.setPower(-STRAFE_SPEED);
                    
                runtime.reset();                    
                while (opModeIsActive() && (runtime.seconds() <2.25)) {
                    telemetry.addData("Path", "Leg 1: %4.1f S Elapsed", runtime.seconds());
                    telemetry.update();
                 }
                 
                // Stop in Zone 3
                    LeftBack.setPower(0);
                    RightBack.setPower(0);
                    LeftFront.setPower(0);
                    RightFront.setPower(0);
                    
                    telemetry.addData("Path to Zone 3", "Complete");
                    telemetry.addData("Hue", hsvValues[0]);
                    telemetry.update();
                    sleep(500);                }
        else if(hsvValues[0] <= 360 && hsvValues[0] >= 185)
        
        // Move to zone 2
            {telemetry.addData("Move to zone 2", "now");
                ConeLiftDrop();           
                //set power to move straight forward to zone 2
                LeftBack.setPower(STRAFE_SPEED);
                RightBack.setPower(STRAFE_SPEED);
                LeftFront.setPower(STRAFE_SPEED);
                RightFront.setPower(STRAFE_SPEED);
            
                runtime.reset();
                
                //move forward for .5 seconds to get into zone 2
                while (opModeIsActive() && (runtime.seconds() <.5)) {
                    telemetry.addData("Path", "Leg 1: %4.1f S Elapsed", runtime.seconds());
                    telemetry.update();
                 }
                // Stop in Zone 2
                    LeftBack.setPower(0);
                    RightBack.setPower(0);
                    LeftFront.setPower(0);
                    RightFront.setPower(0);
        
                    telemetry.addData("Path to Zone 2", "Complete");
                    telemetry.addData("Hue", hsvValues[0]);
                    telemetry.update();
                    sleep(500);
            }
    }
    private void ConeLiftDrop(){
        //Move forward to push cone out of the way
        LeftBack.setTargetPosition(LeftBack.getCurrentPosition()+650);
        LeftFront.setTargetPosition(LeftFront.getCurrentPosition()+650);
        RightBack.setTargetPosition(RightBack.getCurrentPosition()+650);
        RightFront.setTargetPosition(RightFront.getCurrentPosition()+650);
        
        LeftBack.setPower(FORWARD_SPEED);
        RightBack.setPower(FORWARD_SPEED);
        LeftFront.setPower(FORWARD_SPEED);
        RightFront.setPower(FORWARD_SPEED);
        
        LeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        while (opModeIsActive() && LeftFront.isBusy()){
        telemetry.addData("Step 1. Push", "cone out of the way");
        telemetry.addData("Left Front Position", LeftFront.getCurrentPosition());
        telemetry.addData("Right Front Position", RightFront.getCurrentPosition());
        telemetry.addData("Left Back Position", LeftBack.getCurrentPosition());
        telemetry.addData("Right Back Position", RightBack.getCurrentPosition());
        telemetry.update();
        idle();}
        
        sleep(500);
        
        //Move backward to get back to starting position
        LeftBack.setTargetPosition(LeftBack.getCurrentPosition()-500);
        LeftFront.setTargetPosition(LeftFront.getCurrentPosition()-500);
        RightBack.setTargetPosition(RightBack.getCurrentPosition()-500);
        RightFront.setTargetPosition(RightFront.getCurrentPosition()-500);

        LeftBack.setPower(FORWARD_SPEED);
        RightBack.setPower(FORWARD_SPEED);
        LeftFront.setPower(FORWARD_SPEED);
        RightFront.setPower(FORWARD_SPEED);
        
        LeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION); 
        
        while (opModeIsActive() && LeftFront.isBusy()){
        telemetry.addData("Step 2. Move", "back to start position");
        telemetry.addData("Left Front Position", LeftFront.getCurrentPosition());
        telemetry.addData("Right Front Position", RightFront.getCurrentPosition());
        telemetry.addData("Left Back Position", LeftBack.getCurrentPosition());
        telemetry.addData("Right Back Position", RightBack.getCurrentPosition());
        telemetry.update();
        idle();}
        
        sleep(500);
        
        //Turn toward medium stantion
        LeftBack.setTargetPosition(LeftBack.getCurrentPosition()+1850);
        LeftFront.setTargetPosition(LeftFront.getCurrentPosition()+1850);
        RightBack.setTargetPosition(RightBack.getCurrentPosition()-1850);
        RightFront.setTargetPosition(RightFront.getCurrentPosition()-1850);
 
        LeftBack.setPower(FORWARD_SPEED);
        RightBack.setPower(FORWARD_SPEED);
        LeftFront.setPower(FORWARD_SPEED);
        RightFront.setPower(FORWARD_SPEED);

        LeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        while (opModeIsActive() && LeftFront.isBusy()){
        telemetry.addData("Step 3. Turn", "toward medium stantion");
        telemetry.addData("Left Front Position", LeftFront.getCurrentPosition());
        telemetry.addData("Right Front Position", RightFront.getCurrentPosition());
        telemetry.addData("Left Back Position", LeftBack.getCurrentPosition());
        telemetry.addData("Right Back Position", RightBack.getCurrentPosition());
        telemetry.update();
        idle();}
        
        sleep(500);
        
        //Move forward toward medium stantion
        LeftBack.setTargetPosition(LeftBack.getCurrentPosition()-550);
        LeftFront.setTargetPosition(LeftFront.getCurrentPosition()-550);
        RightBack.setTargetPosition(RightBack.getCurrentPosition()-550);
        RightFront.setTargetPosition(RightFront.getCurrentPosition()-550);
        
        LeftBack.setPower(FORWARD_SPEED);
        RightBack.setPower(FORWARD_SPEED);
        LeftFront.setPower(FORWARD_SPEED);
        RightFront.setPower(FORWARD_SPEED);
        
        LeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        while (opModeIsActive() && LeftFront.isBusy()){
        telemetry.addData("Step 4. Move", "toward medium stantion");
        telemetry.addData("Left Front Position", LeftFront.getCurrentPosition());
        telemetry.addData("Right Front Position", RightFront.getCurrentPosition());
        telemetry.addData("Left Back Position", LeftBack.getCurrentPosition());
        telemetry.addData("Right Back Position", RightBack.getCurrentPosition());
        telemetry.update();
        idle();}
        
        sleep(500);
        
        //Lift motor to medium stantion height
        
        LiftMotor.setTargetPosition(-2175);
        
        sleep(1000);
        
        //Move slightly forward to position cone over stantion top
        LeftBack.setTargetPosition(LeftBack.getCurrentPosition()-240);
        LeftFront.setTargetPosition(LeftFront.getCurrentPosition()-240);
        RightBack.setTargetPosition(RightBack.getCurrentPosition()-240);
        RightFront.setTargetPosition(RightFront.getCurrentPosition()-240);
        
        LeftBack.setPower(FORWARD_SPEED*.75);
        RightBack.setPower(FORWARD_SPEED*.75);
        LeftFront.setPower(FORWARD_SPEED*.75);
        RightFront.setPower(FORWARD_SPEED*.75);
        
        LeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        while (opModeIsActive() && LeftFront.isBusy()){
        telemetry.addData("Step 5. Move", "slightly forward");
        telemetry.addData("Left Front Position", LeftFront.getCurrentPosition());
        telemetry.addData("Right Front Position", RightFront.getCurrentPosition());
        telemetry.addData("Left Back Position", LeftBack.getCurrentPosition());
        telemetry.addData("Right Back Position", RightBack.getCurrentPosition());
        telemetry.update();
        idle();}
        
        sleep(1000);
        
        //Drop cone
        RevServo.setPosition(0.35);
        
        sleep(500);
        
        //Move away from medium stantion
        LeftBack.setTargetPosition(LeftBack.getCurrentPosition()+780);
        LeftFront.setTargetPosition(LeftFront.getCurrentPosition()+780);
        RightBack.setTargetPosition(RightBack.getCurrentPosition()+780);
        RightFront.setTargetPosition(RightFront.getCurrentPosition()+780);
        
        LeftBack.setPower(FORWARD_SPEED);
        RightBack.setPower(FORWARD_SPEED);
        LeftFront.setPower(FORWARD_SPEED);
        RightFront.setPower(FORWARD_SPEED);
        
        LeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        while (opModeIsActive() && LeftFront.isBusy()){
        telemetry.addData("Step 6. Move", "away from medium stantion");
        telemetry.addData("Left Front Position", LeftFront.getCurrentPosition());
        telemetry.addData("Right Front Position", RightFront.getCurrentPosition());
        telemetry.addData("Left Back Position", LeftBack.getCurrentPosition());
        telemetry.addData("Right Back Position", RightBack.getCurrentPosition());
        telemetry.update();
        idle();}
        
        sleep(500);
        
        //Turn away from medium stantion and lower lift
        LiftMotor.setTargetPosition(0);
        RevServo.setPosition(1);
        
        LeftBack.setTargetPosition(LeftBack.getCurrentPosition()-1850);
        LeftFront.setTargetPosition(LeftFront.getCurrentPosition()-1850);
        RightBack.setTargetPosition(RightBack.getCurrentPosition()+1850);
        RightFront.setTargetPosition(RightFront.getCurrentPosition()+1850);
        
        LeftBack.setPower(FORWARD_SPEED);
        RightBack.setPower(FORWARD_SPEED);
        LeftFront.setPower(FORWARD_SPEED);
        RightFront.setPower(FORWARD_SPEED);
        
        LeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        while (opModeIsActive() && LeftFront.isBusy()){
        telemetry.addData("Step 7. Turn", "back toward beginning trajectory");
        telemetry.addData("Left Front Position", LeftFront.getCurrentPosition());
        telemetry.addData("Right Front Position", RightFront.getCurrentPosition());
        telemetry.addData("Left Back Position", LeftBack.getCurrentPosition());
        telemetry.addData("Right Back Position", RightBack.getCurrentPosition());
        telemetry.update();
        idle();}
        
        sleep(500);
        
        LeftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        RightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);        
        LeftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);        
        RightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
   }
}
