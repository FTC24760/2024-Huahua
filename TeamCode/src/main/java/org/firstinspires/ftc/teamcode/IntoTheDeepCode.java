package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.JavaUtil;
//import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


/*
TODO:
 -> make b button to make the robot arm go back down
 -> configure gamepad2
 -> make a button preset to have the robot in a specimen dropping position
 ->

Gamepad1 Button Usage Recap:
-> A - slide go up
-> B - slide go down
-> Right Trigger - slide move forward
-> Left trigger - slide move backward

Gamepad2 Button Usage Recap:
-> X - claw open/close
-> D Pad - wrist rotate

Still available buttons (gamepad1):
 -> Y -> PRESET -> move the slide forward almost to the ground
 -> left bumper
 -> right bumper
 -> X -> PRESET -> extend slide to the max and move slide forward almost to the ground
 -> dpad



*/
@TeleOp
public class IntoTheDeepCode extends LinearOpMode {
    // Mecanum Drive
    private DcMotor frontRight;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor backLeft;

    // Slide
    private DcMotor leftSlide;
    private DcMotor rightSlide;

    private DcMotor leftRotate;


    // Wrist Spin
    private Servo wrist;
    private Servo updown_wrist;

    // Claw
    private Servo clawLeft;
    private Servo clawRight;

//    private DistanceSensor distanceSensor1;
//    private DistanceSensor distanceSensor2;

    static final int SLIDE_MAX_POSITION = 2000;
    static final int SLIDE_NEUTRAL_POSITION = 0;


    private double wrist_position = 0.5;

    private double updown_wrist_position = 0.5;


    @Override
    public void runOpMode() {
        // Drive
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        float driveY;
        double driveX;
        float driveRX;
        double driveDenominator;

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        // Slide
        leftSlide = hardwareMap.get(DcMotor.class, "leftSlide");
        rightSlide = hardwareMap.get(DcMotor.class, "rightSlide");
        leftSlide.setDirection(DcMotorSimple.Direction.REVERSE);

        leftRotate = hardwareMap.get(DcMotor.class, "leftRotate");

//        distanceSensor1 = hardwareMap.get(DistanceSensor.class, "distanceSensor1");
//        distanceSensor2 = hardwareMap.get(DistanceSensor.class, "distanceSensor2");


        // Wrist & Claw
        wrist = hardwareMap.get(Servo.class, "wrist");
        updown_wrist = hardwareMap.get(Servo.class, "updown_wrist");
        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        boolean clawOpen = false;
        boolean xPressed = false;

        waitForStart();

        while (opModeIsActive()) {

            driveY = gamepad1.left_stick_y * 0.5f;
            driveX = -gamepad1.left_stick_x * 0.5f;
            driveRX = -gamepad1.right_stick_x * 0.5f;

            driveDenominator = JavaUtil.maxOfList(JavaUtil.createListWith(JavaUtil.sumOfList(JavaUtil.createListWith(Math.abs(driveY), Math.abs(driveX), Math.abs(driveRX))), 1));

            frontLeft.setPower((driveY + driveX + driveRX) / driveDenominator);
            backLeft.setPower(((driveY - driveX) + driveRX) / driveDenominator);
            frontRight.setPower(((driveY - driveX) - driveRX) / driveDenominator);
            backRight.setPower(((driveY + driveX) - driveRX) / driveDenominator);

            // Slide
            if (gamepad2.a) { //gampepad2.a makes the slide go up to max position

                leftSlide.setTargetPosition(SLIDE_MAX_POSITION);
                rightSlide.setTargetPosition(SLIDE_MAX_POSITION);

                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftSlide.setPower(-1);
                rightSlide.setPower(-1);

            }

            if (gamepad2.b) { //gamepad1.b will make the slide go back down into minimum position
                leftSlide.setTargetPosition(0);
                rightSlide.setTargetPosition(0);

                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);


                leftSlide.setPower(1);
                rightSlide.setPower(1);

            }

            telemetry.addData("Slide", "a/UP - b/DOWN");

            if (gamepad2.x && !xPressed) {
                clawOpen = !clawOpen;
                xPressed = true;
            } else if (!gamepad2.x) {
                xPressed = false;
            }

            //if statement determining whether or not the claw is open
            if (clawOpen) {
                clawLeft.setPosition(0.7);
                clawRight.setPosition(0.3);
            } else {
                clawLeft.setPosition(0.3);
                clawRight.setPosition(0.7);
            }

            telemetry.addData("Claw", "X toggle");

            if (gamepad1.a) { //to put specimens onto the bar, if have time use and map distance sensors
                int SLIDE_SPECIMEN_HANG_POSITION = -800;
                int SLIDE_EXTEND_SPECIMEN_HANG = 1000;

                updown_wrist.setPosition(0.576);
                wrist.setPosition(0.59);

                wrist_position = 0.59;
                updown_wrist_position = 0.576;

                leftRotate.setTargetPosition(SLIDE_SPECIMEN_HANG_POSITION);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(-1);

                leftSlide.setTargetPosition(SLIDE_EXTEND_SPECIMEN_HANG);
                rightSlide.setTargetPosition(SLIDE_EXTEND_SPECIMEN_HANG);
                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftSlide.setPower(-1);
                rightSlide.setPower(-1);

            }
//
//            if (gamepad1.b) {  //to hang the robot at the end
//                code
//            }

            if (gamepad1.right_bumper) { //slide will just be sent to the bottom

                updown_wrist.setPosition(0.576);
                wrist.setPosition(0.59);

                wrist_position = 0.59;
                updown_wrist_position = 0.576;


                leftRotate.setTargetPosition(-2275);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(1);

            }

            if (gamepad1.left_bumper) { //puts the robot in a neutral position

                if (Math.abs(leftSlide.getCurrentPosition()) > 0 || Math.abs(rightSlide.getCurrentPosition()) > 0){
                    leftSlide.setTargetPosition(0);
                    rightSlide.setTargetPosition(0);

                    leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                    leftSlide.setPower(1);
                    rightSlide.setPower(1);

                }

                leftRotate.setTargetPosition(0);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                if (leftRotate.getCurrentPosition() > 0) {
                    leftRotate.setPower(1);
                } else if (leftRotate.getCurrentPosition() < 0) {
                    leftRotate.setPower(-1);
                }
            }

            if (gamepad1.y) { //slide will be put in a position to grab a block from the pit

                int SLIDE_PICKUP_POSITION = -2025;

                updown_wrist.setPosition(0.116);
                wrist.setPosition(0.53);

                wrist_position = 0.53;
                updown_wrist_position = 0.116;


                leftRotate.setTargetPosition(SLIDE_PICKUP_POSITION);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(-1);


                leftSlide.setTargetPosition(1000);
                rightSlide.setTargetPosition(1000);
                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftSlide.setPower(-1);
                rightSlide.setPower(-1);


            }

            if (gamepad1.x) { //put slide into position to put something in the basket
                int SLIDE_BASKET_POSITION = 100;

                updown_wrist.setPosition(0.0);
                wrist.setPosition(0.565);

                wrist_position = 0.565;
                updown_wrist_position = 0.0;

                leftRotate.setTargetPosition(SLIDE_BASKET_POSITION);
                leftSlide.setTargetPosition(SLIDE_MAX_POSITION);
                rightSlide.setTargetPosition(SLIDE_MAX_POSITION);

                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftRotate.setPower(-1);

                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftSlide.setPower(-1);
                rightSlide.setPower(-1);
            }

            telemetry.addData("LeftRotate Position", leftRotate.getCurrentPosition());


            /*

            GAMEPAD NUMBER 2 CONTROLS

             */

            //controls the opening and closing of the claw
            //controls the servos that rotate the claw
            if (gamepad2.dpad_up) {
                if (updown_wrist_position > 0) {
                    updown_wrist_position -= 0.008;
                }
            } else if (gamepad2.dpad_down) {
                if (updown_wrist_position < 1) {
                    updown_wrist_position += 0.008;
                }
            }

            if (gamepad2.dpad_left) {
                if (wrist_position > 0) {
                    wrist_position -= 0.005;
                }
            } else if (gamepad2.dpad_right) {
                if (wrist_position < 1) {
                    wrist_position += 0.005;
                }
            }

            telemetry.addData("Wrist Left Right Position", wrist.getPosition());
            telemetry.addData("Wrist Up Down Position", updown_wrist.getPosition());
            telemetry.addData("Wrist Spin", "DPad Left/Right");

            if (gamepad2.left_trigger > 0.1) {
                leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftSlide.setPower(-0.66);
                rightSlide.setPower(-0.66);
            } else if (gamepad2.right_trigger > 0.1) {
                leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftSlide.setPower(0.66);
                rightSlide.setPower(0.66);
            } else if (leftSlide.getMode() != DcMotor.RunMode.RUN_TO_POSITION && rightSlide.getMode() != DcMotor.RunMode.RUN_TO_POSITION) {
                leftSlide.setPower(0.0);
                rightSlide.setPower(0.0);
            }

            if (gamepad2.right_bumper) {
                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftRotate.setPower(-0.7);

            } else if (gamepad2.left_bumper) {
                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftRotate.setPower(0.7);
            } else {
                if (leftRotate.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) {
                    leftRotate.setPower(0.0);
                }
            }

            telemetry.addData("LeftRotate Position", leftRotate.getCurrentPosition());

            // Add all telemetry
            telemetry.update();
        }
    }

}

