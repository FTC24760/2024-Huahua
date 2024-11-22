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

Button Usage Recap:
-> A - slide go up
-> B - slide go down
-> X - claw open/close
-> Right Trigger - slide move forward
-> Left trigger - slide move backward
-> D Pad - wrist rotate

Still available buttons:
 -> Y
 -> left bumper
 -> right bumpter



*/
@TeleOp(name = "Revamped Into the Deep")
public class OldIntoTheDeepRevamped extends LinearOpMode {
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

    static final int SLIDE_MAX_POSITION = 2000;
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

        // Wrist & Claw
        wrist = hardwareMap.get(Servo.class, "wrist");
        updown_wrist = hardwareMap.get(Servo.class, "updown_wrist");
        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


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

                while (opModeIsActive() && (leftSlide.isBusy() || rightSlide.isBusy())) {
                    telemetry.addData("Left Slide Position", leftSlide.getCurrentPosition());
                    telemetry.addData("Right Slide Position", rightSlide.getCurrentPosition());
                }

                leftSlide.setPower(0);
                rightSlide.setPower(0);

            }

            telemetry.addData("Slide", "a/UP - b/DOWN");

            if (gamepad1.right_trigger > 0) {
                leftRotate.setPower(1);
            }
            else {
                leftRotate.setPower(0.0);
            }

            if (gamepad1.left_trigger > 0) {
                leftRotate.setPower(-1);
            }
            else {
                leftRotate.setPower(0.0);
            }

            telemetry.addData("Slide Back and Forth", "Right Trigger and Left Trigger");

            // Wrist Spinning
            if (gamepad1.dpad_left) {
                if (wrist_position < 1) {
                    wrist_position -= 0.001;
                }
            } else if (gamepad1.dpad_right) {
                if (wrist_position > 0) {
                    wrist_position += 0.001;
                }
            }

            if (gamepad1.dpad_up) {
                if (updown_wrist_position < 1) {
                    updown_wrist_position -= 0.001;
                }
            } else if (gamepad1.dpad_down) {
                if (updown_wrist_position > 0) {
                    updown_wrist_position += 0.001;
                }
            }

            wrist.setPosition(wrist_position);
            updown_wrist.setPosition(updown_wrist_position);

            telemetry.addData("Wrist Spin", "DPad Left/Right");

            //claw
            if (gamepad1.x && !xPressed) {
                clawOpen = !clawOpen;
                xPressed = true;
            } else if (!gamepad1.x) {
                xPressed = false;
            }

            // Claw
            if (clawOpen) {
                clawLeft.setPosition(0.7);
                clawRight.setPosition(0.3);
            } else {
                clawLeft.setPosition(0.3);
                clawRight.setPosition(0.7);
            }

            telemetry.addData("Claw", "X toggle");


            if (gamepad1.y) {
                leftRotate.setTargetPosition(500); //change
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftRotate.setPower(0.75);

                leftSlide.setTargetPosition(SLIDE_MAX_POSITION);
                rightSlide.setTargetPosition(SLIDE_MAX_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftSlide.setPower(0.75);
                rightSlide.setPower(0.75);

                while (leftRotate.isBusy() || rightSlide.isBusy() || leftSlide.isBusy()) {
                    telemetry.addData("Left Rotate Pos", leftRotate.getCurrentPosition());
                    telemetry.addData("Right Slide Pos", rightSlide.getCurrentPosition());
                    telemetry.addData("Left Slide Pos", leftSlide.getCurrentPosition());
                }

                leftRotate.setPower(0);
                leftSlide.setPower(0.3);
                rightSlide.setPower(0.3);

                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }

            // Add all telemetry
            telemetry.update();
        }
    }
}
