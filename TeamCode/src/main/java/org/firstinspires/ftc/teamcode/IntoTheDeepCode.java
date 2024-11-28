package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.JavaUtil;


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
    private DcMotor rightRotate;


    // Wrist Spin
    private Servo wrist;
    private Servo updown_wrist;

    // Claw
    private Servo clawLeft;
    private Servo clawRight;

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
        rightRotate = hardwareMap.get(DcMotor.class, "rightRotate");
        rightRotate.setDirection(DcMotorSimple.Direction.REVERSE);

        // Wrist & Claw
        wrist = hardwareMap.get(Servo.class, "wrist");
        updown_wrist = hardwareMap.get(Servo.class, "updown_wrist");
        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRotate.setTargetPosition(0);
        rightRotate.setTargetPosition(0);
        leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        boolean clawOpen = false;
        boolean xPressed = false;

        waitForStart();

        while (opModeIsActive()) {
            // Drive
            float driveDampen = 0.4f;

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

            if (gamepad1.right_trigger > 0) {
                leftRotate.setPower(-0.66);
                rightRotate.setPower(-0.66);
                telemetry.addData("LeftRotate Position", leftRotate.getCurrentPosition());
                telemetry.addData("RightRotate Position", rightRotate.getCurrentPosition());

            } else if (gamepad1.left_trigger > 0) {
                leftRotate.setPower(0.66);
                rightRotate.setPower(0.66);
                telemetry.addData("LeftRotate Position", leftRotate.getCurrentPosition());
                telemetry.addData("RightRotate Position", rightRotate.getCurrentPosition());

            }
            else {
                leftRotate.setPower(0.0);
                rightRotate.setPower(0.0);
            }

            telemetry.addData("Slide Back and Forth", "Right Trigger and Left Trigger");



//
////                leftSlide.setTargetPosition(SLIDE_MAX_POSITION);
////                rightSlide.setTargetPosition(SLIDE_MAX_POSITION);
////                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
////                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
////
////                leftSlide.setPower(-0.75);
////                rightSlide.setPower(-0.75);
//
//                clawLeft.setPosition(0.7);
//                clawRight.setPosition(0.3);
//
//
//
            if (gamepad1.right_bumper) {

                int SLIDE_PICKUP_POSITION = leftRotate.getCurrentPosition() + 1000;

                rightRotate.setTargetPosition(SLIDE_PICKUP_POSITION);
                leftRotate.setTargetPosition(SLIDE_PICKUP_POSITION);

                rightRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                while (leftRotate.isBusy() || rightRotate.isBusy()) {
                }

                // Stop motors once the target is reached
                leftRotate.setPower(0.0);
                rightRotate.setPower(0.0);

                clawLeft.setPosition(0.7);
                clawRight.setPosition(0.3);
            }


            if (gamepad1.left_bumper) { //puts the robot in a neutral position
//                leftSlide.setTargetPosition(0);
//                rightSlide.setTargetPosition(0);
//
//                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//
//                leftSlide.setPower(1);
////                rightSlide.setPower(1);
//
//                while (leftSlide.isBusy() || rightSlide.isBusy()) {
//                    sleep(1);
//                }

                leftRotate.setTargetPosition(SLIDE_NEUTRAL_POSITION);
                rightRotate.setTargetPosition(SLIDE_NEUTRAL_POSITION);

                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftRotate.setPower(0.5);
                rightRotate.setPower(0.5);

                while (leftRotate.isBusy() || rightRotate.isBusy()) {
                }

                leftRotate.setPower(0);
                rightRotate.setPower(0);

            }


            if (gamepad1.x) {
                int SLIDE_PICKUP_POSITION = leftRotate.getCurrentPosition() + 1000;

                rightRotate.setTargetPosition(SLIDE_PICKUP_POSITION);
                leftRotate.setTargetPosition(SLIDE_PICKUP_POSITION);

                rightRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                while (leftRotate.isBusy() || rightRotate.isBusy()) {
                }


                rightSlide.setTargetPosition(SLIDE_MAX_POSITION);
                leftSlide.setTargetPosition(SLIDE_MAX_POSITION);

                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftSlide.setPower(-1);
                rightSlide.setPower(-1);

                while (leftSlide.isBusy() || rightSlide.isBusy()) {
                }

                clawLeft.setPosition(0.7);
                clawRight.setPosition(0.3);


                rightRotate.setPower(0);
                leftRotate.setPower(0);
                leftSlide.setPower(0);
                rightSlide.setPower(0);

            }

            //make left bumper essentially just an everything reset, so it checks if the slide is out, and it returns it

            /*

            GAMEPAD NUMBER 2 CONTROLS

             */

            //controls the opening and closing of the claws
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

            //controls the servos that rotate the claw
            if (gamepad2.dpad_up) {
                if (updown_wrist_position > 0) {
                    updown_wrist_position -= 0.001;
                }
            } else if (gamepad2.dpad_down) {
                if (updown_wrist_position < 1) {
                    updown_wrist_position += 0.001;
                }
            }

            if (gamepad2.dpad_left) {
                if (wrist_position > 0) {
                    wrist_position -= 0.001;
                }
            } else if (gamepad2.dpad_right) {
                if (wrist_position < 1) {
                    wrist_position += 0.001;
                }
            }

            wrist.setPosition(wrist_position);
            updown_wrist.setPosition(updown_wrist_position);

            telemetry.addData("Wrist Spin", "DPad Left/Right");


            // Add all telemetry
            telemetry.update();
        }
    }
}
