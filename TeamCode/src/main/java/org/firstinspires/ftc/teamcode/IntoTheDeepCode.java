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
@TeleOp(name = "2024 INTO THE DEEP CODE")
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

//    private boolean presetActive = false;

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
        leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        boolean clawOpen = false;
        boolean xPressed = false;

        waitForStart();

        while (opModeIsActive()) {
            // Drive
            float driveDampen = 0.7f;

            driveY = gamepad1.left_stick_y * driveDampen;
            driveX = -gamepad1.left_stick_x * driveDampen;
            driveRX = -gamepad1.right_stick_x * driveDampen;

            driveDenominator = JavaUtil.maxOfList(JavaUtil.createListWith(JavaUtil.sumOfList(JavaUtil.createListWith(Math.abs(driveY), Math.abs(driveX), Math.abs(driveRX))), 1));

            frontLeft.setPower((driveY + driveX + driveRX) / driveDenominator);
            backLeft.setPower(((driveY - driveX) + driveRX) / driveDenominator);
            frontRight.setPower(((driveY - driveX) - driveRX) / driveDenominator);
            backRight.setPower(((driveY + driveX) - driveRX) / driveDenominator);

            telemetry.addData("Drive Controls", "Left and Right Joysticks");


            // Slide
            if (gamepad1.a) { //gampepad1.a makes the slide go up to max position

                leftSlide.setTargetPosition(SLIDE_MAX_POSITION);
                rightSlide.setTargetPosition(SLIDE_MAX_POSITION);

                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);


                leftSlide.setPower(-1);
                rightSlide.setPower(-1);

            }

            if (gamepad1.b) { //gamepad1.b will make the slide go back down into minimum position
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




            if (gamepad2.right_bumper) {
                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftRotate.setPower(-0.8);

            } else if (gamepad2.left_bumper) {
                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftRotate.setPower(0.8);
                telemetry.addData("LeftRotate Position", leftRotate.getCurrentPosition());

            }
            else {
                leftRotate.setPower(0.0);
            }



            if (gamepad1.right_bumper) { //slide will just be sent to the bottom
//                presetActive = true;
                int SLIDE_PICKUP_POSITION = -2275;

                updown_wrist.setPosition(0.696);
                wrist.setPosition(0.59);

                wrist_position = 0.59;
                updown_wrist_position = 0.696;


                leftRotate.setTargetPosition(SLIDE_PICKUP_POSITION);

                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftRotate.setPower(-1);

                while (leftRotate.isBusy()) {
                }

            }


            if (gamepad1.left_bumper) { //puts the robot in a neutral position
//                presetActive = true;

                if (Math.abs(leftSlide.getCurrentPosition()) > 0 || Math.abs(rightSlide.getCurrentPosition()) > 0){
                    leftSlide.setTargetPosition(0);
                    rightSlide.setTargetPosition(0);

                    leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                    leftSlide.setPower(1);
                    rightSlide.setPower(1);

                    while (leftSlide.isBusy() || rightSlide.isBusy()) {

                    }

                }

                leftRotate.setTargetPosition(0);

                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftRotate.setPower(1);


            }

            if (gamepad1.y) { //slide will be put in a position to grab a block from the pit
//                presetActive = true;

                int SLIDE_PICKUP_POSITION = -2280;

                leftRotate.setTargetPosition(SLIDE_PICKUP_POSITION);
                leftSlide.setTargetPosition(1000);
                rightSlide.setTargetPosition(1000);

                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftRotate.setPower(-1);


                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftSlide.setPower(-1);
                rightSlide.setPower(-1);

                while (leftSlide.isBusy() || leftSlide.isBusy()) {
                }
//
            }

            if (gamepad1.x) { //put slide into position to put something in the basket
//                presetActive = true;
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

//                while (leftRotate.isBusy() || rightRotate.isBusy()) {
//                }


                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftSlide.setPower(-1);
                rightSlide.setPower(-1);

//                while (leftSlide.isBusy() || leftSlide.isBusy()) {
//                }
//


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

            wrist.setPosition(wrist_position);
            updown_wrist.setPosition(updown_wrist_position);

                // Reset presetActive once manual control resumes
//            if (!gamepad1.x || !gamepad1.right_bumper || !gamepad1.y || !gamepad1.left_bumper) {
//                presetActive = false;
//            }


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

//            if (gamepad2.x) {
//                while (distanceSensor1.getDistance(DistanceUnit.CM) > 50 && distanceSensor2.getDistance(DistanceUnit.CM)  > 50) {
//                    frontRight.setPower(0.3);
//                    frontLeft.setPower(0.3);
//                    backRight.setPower(0.3);
//                    backLeft.setPower(0.3);
//                }
//
//                leftRotate.setTargetPosition(100); //change
//                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                leftRotate.setPower(1);
//
//                while (leftRotate.isBusy()) {
//                }
//
//                leftSlide.setTargetPosition(1500); //change
//                rightSlide.setTargetPosition(1500); //change
//                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                leftSlide.setPower(1);
//                rightSlide.setPower(1);
//
//            }


            // Add all telemetry
            telemetry.update();
        }
    }
}
