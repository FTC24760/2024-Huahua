/*

COLOUR CODING LEGEND

====================

 Preset Movements (gamepad1)
 -> Scoring Processes - Red
 -> Neutral - Orange
 -> Intake Processes - Yellow
 -> Hanging & End Game - Hot Pink

 Auxiliary Movements (gamepad2)
 -> Claw - AQUA
 -> ARM - VIOLET


====================

 */


package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;

import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.JavaUtil;

@TeleOp(name="Into The Deep 2024-2025")
public class IntoTheDeep extends LinearOpMode {
    // ------ DEFINE MOTORS AND SERVOS ------

    // Mecanum Drive
    private DcMotor frontRight;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor backLeft;

    // Slide
    private DcMotor leftSlide;
    private DcMotor rightSlide;

    // Rotate
    private DcMotor leftRotate;

    // Blinkin
    RevBlinkinLedDriver blinkinLedDriver;
    RevBlinkinLedDriver.BlinkinPattern pattern;

    // Touch Sensor
    private TouchSensor touchSensor;

    // Wrist
    private Servo wrist;
    private Servo updown_wrist;

    // Claw
    private Servo clawLeft;
    private Servo clawRight;


    // ------ Robot State Enum ------
    private enum RobotState {
        IDLE,
        ROTATING,
        WAITING,
        SLIDING,
        MAX_SLIDE,
        STARTING,
    }



    // ------ INIT VALUES ------

    // Wrist position
    private double wrist_position = 0.07;
    private double updown_wrist_position = 0.515;


    // Robot state
    private RobotState currentState = RobotState.IDLE;


    // ------ MAIN FUNCTION ------
    @Override
    public void runOpMode() {
        // --- Set Motors ---

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

        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Rotate
        leftRotate = hardwareMap.get(DcMotor.class, "leftRotate");

        leftRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Make sure rotaton motor BRAKES
        leftRotate.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Wrist
        wrist = hardwareMap.get(Servo.class, "wrist");
        updown_wrist = hardwareMap.get(Servo.class, "updown_wrist");

        // Claw
        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

        // Touch Sensor
        touchSensor = hardwareMap.get(TouchSensor.class, "touchSensor");

        // Blinkin
        blinkinLedDriver = hardwareMap.get(RevBlinkinLedDriver.class, "blinkin");

        // Some other variables
        boolean clawOpen = false;
        boolean xPressed = false;

        boolean slideDown = true;
        boolean touchSensorEnabled = true;


        // --- Starting Main Loop ---
        waitForStart();

        // --- Main Loop ---
        while (opModeIsActive()) {
            // Touch Sensor - Reset Rotation Encoder
            if (touchSensorEnabled) {
                if (touchSensor.isPressed()) {
                    telemetry.addData("Touch Sensor", "Pressed");

                    // Reset rotation encoder
                    leftRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                } else {
                    telemetry.addData("Touch Sensor", "Not Pressed");
                }
            } else {
                telemetry.addData("Touch Sensor", "Disabled");
            }


            // Set LED pattern initially, ooh ahh!
            pattern = RevBlinkinLedDriver.BlinkinPattern.TWINKLES_FOREST_PALETTE;

            // --- Driving ---
            driveY = -gamepad1.left_stick_y * 0.5f;
            driveX = gamepad1.left_stick_x * 0.5f;
            driveRX = -gamepad1.right_stick_x * 0.5f;

            driveDenominator = JavaUtil.maxOfList(JavaUtil.createListWith(JavaUtil.sumOfList(JavaUtil.createListWith(Math.abs(driveY), Math.abs(driveX), Math.abs(driveRX))), 1));

            frontLeft.setPower((driveY + driveX + driveRX) / driveDenominator);
            backLeft.setPower(((driveY - driveX) + driveRX) / driveDenominator);
            frontRight.setPower(((driveY - driveX) - driveRX) / driveDenominator);
            backRight.setPower(((driveY + driveX) - driveRX) / driveDenominator);


            // --- Presets ---

            // Bring rotation up and slide all the way up
            if (gamepad1.x) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.RED;

                slideDown = false;

                updown_wrist_position = 0.500;

                leftRotate.setTargetPosition(-2650);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(-1);

                currentState = RobotState.MAX_SLIDE;
            }

            // ONLY bring rotation up
            if (gamepad1.b) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.RED;

                slideDown = false;

                updown_wrist_position = 0.500;

                leftRotate.setTargetPosition(-2650);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(-1);

                currentState = RobotState.IDLE;
            }

            // Bring slide down
            if (gamepad1.left_trigger > 0.5) {

                pattern = RevBlinkinLedDriver.BlinkinPattern.ORANGE;

                updown_wrist_position = 0.69;
                wrist_position = 0.05;

                leftSlide.setTargetPosition(0);
                rightSlide.setTargetPosition(0);

                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftSlide.setPower(-1);
                rightSlide.setPower(-1);

                if (!leftSlide.isBusy() && !rightSlide.isBusy()) {
                    currentState = RobotState.IDLE;
                }
            }
            // Bring rotation down | ENSURE that the right and the left slides are down before the rotation as well.
            else if (gamepad1.right_trigger > 0.5 && rightSlide.getCurrentPosition() > -1200 && leftSlide.getCurrentPosition() > -1200) {
                touchSensorEnabled = true;

                pattern = RevBlinkinLedDriver.BlinkinPattern.ORANGE;

                updown_wrist_position = 0.69;
                wrist_position = 0.05;

                slideDown = true;

                leftRotate.setTargetPosition(0);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(-1);
            }


            // Move wrist into position
            if (gamepad1.right_bumper) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.YELLOW;

                wrist_position = 0.04;
                updown_wrist_position = 0.415;
            }
            // Intake - move slide out
            else if (gamepad1.y) {
                touchSensorEnabled = false;
                pattern = RevBlinkinLedDriver.BlinkinPattern.YELLOW;

                slideDown = true;

                wrist_position = 0.04;
                updown_wrist_position = 0.415;

                leftSlide.setTargetPosition(1000);
                rightSlide.setTargetPosition(1000);

                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftSlide.setPower(1);
                rightSlide.setPower(1);
            }


            // TODO Hang preset
            // Level 2 ascent (WIP)
            if (gamepad1.a) {
                slideDown = false;

                leftRotate.setTargetPosition(-3140);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(-1);

                currentState = RobotState.ROTATING;
            }

            // Claw opening/closing
            if (gamepad2.y && !xPressed) {
                clawOpen = !clawOpen;
                xPressed = true;
            } else if (!gamepad2.y) {
                xPressed = false;
            }


            // --- Manual Override ---

            // Wrist up/down
            if (gamepad2.left_stick_y < -0.1) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.AQUA;

                if (updown_wrist_position > 0) {
                    updown_wrist_position -= 0.01;
                }
            } else if (gamepad2.left_stick_y > 0.1) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.AQUA;

                if (updown_wrist_position < 1) {
                    updown_wrist_position += 0.01;
                }
            }

            // Wrist side-to-side
            if (gamepad2.right_stick_x < -0.1) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.AQUA;

                if (wrist_position > 0) {
                    wrist_position -= 0.01;
                }
            } else if (gamepad2.right_stick_x > 0.1) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.AQUA;

                if (wrist_position < 1) {
                    wrist_position += 0.01;
                }
            }

            // Slide
            if (gamepad2.left_trigger > 0.1) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.VIOLET;

                // Slide Limit
                if (leftSlide.getCurrentPosition() > 1250 && slideDown && leftRotate.getCurrentPosition() > -1000) {
                    leftSlide.setPower(0);
                    rightSlide.setPower(0);
                } else {
                    leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    leftSlide.setPower(1);
                    rightSlide.setPower(1);
                }

            } else if (gamepad2.right_trigger > 0.1) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.VIOLET;

                leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftSlide.setPower(-1);
                rightSlide.setPower(-1);

            } else if ((leftSlide.getMode() == DcMotor.RunMode.RUN_USING_ENCODER && rightSlide.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) || (!leftSlide.isBusy() && !rightSlide.isBusy() && slideDown)) {
                leftSlide.setPower(0.0);
                rightSlide.setPower(0.0);

                currentState = RobotState.IDLE;
            }

            // Rotation
            if (gamepad2.right_bumper) {

                pattern = RevBlinkinLedDriver.BlinkinPattern.VIOLET;

                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                leftRotate.setPower(1);
            } else if (gamepad2.left_bumper) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.VIOLET;

                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                leftRotate.setPower(-1);
            } else if (leftRotate.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) {
                leftRotate.setPower(0.0);

                currentState = RobotState.IDLE;
            }


            // --- Set Servo Positions ---

            // Wrists
            wrist.setPosition(wrist_position);
            updown_wrist.setPosition(updown_wrist_position);

            // Claw
            if (clawOpen) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.AQUA;

                clawLeft.setPosition(0.4);
                clawRight.setPosition(0.5);
            } else {
                pattern = RevBlinkinLedDriver.BlinkinPattern.AQUA;

                clawLeft.setPosition(0.7);
                clawRight.setPosition(0.2);
            }



            // State management
            switch (currentState) {
                case ROTATING:
                    if (!leftRotate.isBusy()) {
                        currentState = RobotState.WAITING;
                    }
                    break;

                case MAX_SLIDE:
                    if (!leftRotate.isBusy()) {
                        updown_wrist_position = 1;
                        wrist_position = 0.64;

                        leftSlide.setTargetPosition(4150);
                        rightSlide.setTargetPosition(4150);

                        leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                        leftSlide.setPower(1);
                        rightSlide.setPower(1);

                        currentState = RobotState.SLIDING;
                    }
                    break;

                case SLIDING:
                    if (!leftSlide.isBusy() && !rightSlide.isBusy()) {
                        currentState = RobotState.IDLE;
                    }
                    break;


                case STARTING:
                    if (!leftRotate.isBusy()) {
                        leftRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        currentState = RobotState.IDLE;
                    }
                    break;

                case IDLE:
                    //do nothing and wait for input
                    break;


            }

            // Blinkin
            blinkinLedDriver.setPattern(pattern);


            // Telemetry
            telemetry.addData("State", currentState);

            telemetry.addData("Left Rotate Position", leftRotate.getCurrentPosition());

            telemetry.addData("Up/Down Wrist Position", updown_wrist.getPosition());
            telemetry.addData("Left/Right Wrist Position", wrist.getPosition());

            telemetry.addData("Left Claw Position", clawLeft.getPosition());
            telemetry.addData("Right Claw Position", clawRight.getPosition());

            telemetry.addData("Left Slide Position", leftSlide.getCurrentPosition());
            telemetry.addData("Right Slide Position", rightSlide.getCurrentPosition());

            telemetry.update();
        }
    }

}
