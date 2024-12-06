package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.JavaUtil;

@TeleOp(name="FINAL 2024 INTO THE DEEP")
public class FinalIntoTheDeepCode extends LinearOpMode {
    // * DEFINE MOTORS AND SERVOS
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

        // Wrist and Claw
        wrist = hardwareMap.get(Servo.class, "wrist");
        updown_wrist = hardwareMap.get(Servo.class, "updown_wrist");
        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Define claw variables
        boolean clawOpen = false;
        boolean xPressed = false;

        // Define slide variable
        boolean slideDown = true;

        waitForStart();

        while (opModeIsActive()) {
            // DRIVING FUNCTIONS
            driveY = gamepad1.left_stick_y * 0.5f;
            driveX = -gamepad1.left_stick_x * 0.5f;
            driveRX = -gamepad1.right_stick_x * 0.5f;

            driveDenominator = JavaUtil.maxOfList(JavaUtil.createListWith(JavaUtil.sumOfList(JavaUtil.createListWith(Math.abs(driveY), Math.abs(driveX), Math.abs(driveRX))), 1));

            frontLeft.setPower((driveY + driveX + driveRX) / driveDenominator);
            backLeft.setPower(((driveY - driveX) + driveRX) / driveDenominator);
            frontRight.setPower(((driveY - driveX) - driveRX) / driveDenominator);
            backRight.setPower(((driveY + driveX) - driveRX) / driveDenominator);

            // Slide
            if (gamepad2.a) {
                slideDown = false;
                leftSlide.setTargetPosition(SLIDE_MAX_POSITION);
                rightSlide.setTargetPosition(SLIDE_MAX_POSITION);

                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftSlide.setPower(-1);
                rightSlide.setPower(-1);
            } else if (gamepad2.b) {
                slideDown = true;
                leftSlide.setTargetPosition(0);
                rightSlide.setTargetPosition(0);

                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);


                leftSlide.setPower(1);
                rightSlide.setPower(1);
            }

            telemetry.addData("Slide", "2A/UP - 2B/DOWN");

            // Claw
            if (gamepad2.x && !xPressed) {
                clawOpen = !clawOpen;
                xPressed = true;
            } else if (!gamepad2.x) {
                xPressed = false;
            }

            if (clawOpen) {
                clawLeft.setPosition(0.7);
                clawRight.setPosition(0.3);
            } else {
                clawLeft.setPosition(0.3);
                clawRight.setPosition(0.7);
            }

            telemetry.addData("Claw", "2X toggle");

            // PRESET - Right bumper
            if (gamepad1.right_bumper) {

                wrist_position = 0.59;
                updown_wrist_position = 0.576;


                leftRotate.setTargetPosition(-2275);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(1);

            } else if (gamepad1.left_bumper) {
                if (Math.abs(leftSlide.getCurrentPosition()) > 0 || Math.abs(rightSlide.getCurrentPosition()) > 0){
                    slideDown = true;
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
            } else if (gamepad1.y) {
                slideDown = false;

                wrist_position = 0.53;
                updown_wrist_position = 0.116;


                leftRotate.setTargetPosition(-2025);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(-1);


                leftSlide.setTargetPosition(1000);
                rightSlide.setTargetPosition(1000);
                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftSlide.setPower(-1);
                rightSlide.setPower(-1);
            } else if (gamepad1.x) {
                slideDown = false;

                wrist_position = 0.565;
                updown_wrist_position = 0.0;

                leftRotate.setTargetPosition(100);
                leftSlide.setTargetPosition(SLIDE_MAX_POSITION);
                rightSlide.setTargetPosition(SLIDE_MAX_POSITION);

                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftRotate.setPower(-1);

                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftSlide.setPower(-1);
                rightSlide.setPower(-1);
            }


            // Rotate of the claw
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


            // Manual slide and rotation overrides
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
            } else if ((leftSlide.getMode() == DcMotor.RunMode.RUN_USING_ENCODER && rightSlide.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) || (!leftSlide.isBusy() && !rightSlide.isBusy() && slideDown)) {
                leftSlide.setPower(0.0);
                rightSlide.setPower(0.0);
            }

            if (gamepad2.right_bumper) {
                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftRotate.setPower(-0.7);

            } else if (gamepad2.left_bumper) {
                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftRotate.setPower(0.7);
            } else if (leftRotate.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) {
                leftRotate.setPower(0.0);
            }

            wrist.setPosition(wrist_position);
            updown_wrist.setPosition(updown_wrist_position);


            telemetry.update();
        }
    }
}