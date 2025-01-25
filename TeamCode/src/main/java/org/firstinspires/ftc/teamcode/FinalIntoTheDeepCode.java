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


package org.firstinspires.ftc.teamcode;

import android.text.method.Touch;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

//MOTORS & SERVOS
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

//REV LED
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;

//TOUCH SENSOR
import com.qualcomm.robotcore.hardware.TouchSensor;

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
    private DcMotor rightRotate;
    RevBlinkinLedDriver blinkinLedDriver;
    RevBlinkinLedDriver.BlinkinPattern pattern;

    private TouchSensor touchSensor;

    private Servo wrist;
    private Servo updown_wrist;

    // Claw
    private Servo clawLeft;
    private Servo clawRight;


    private double wrist_position = 0.07;

    private double updown_wrist_position = 0.915;

//    private boolean isBPressed = false;
//    private boolean toggleSpecimen = false;


    private enum RobotState {
        IDLE,
        ROTATING,
        WAITING,
        SLIDING,
        MAX_SLIDE,
        SPECIMEN,
        STARTING,
        ENABLE_TOUCH
    }

    private RobotState currentState = RobotState.IDLE;

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

        // Wrist and Claw
        wrist = hardwareMap.get(Servo.class, "wrist");
        updown_wrist = hardwareMap.get(Servo.class, "updown_wrist");
        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        touchSensor = hardwareMap.get(TouchSensor.class, "touchSensor");

        blinkinLedDriver = hardwareMap.get(RevBlinkinLedDriver.class, "blinkin");


        boolean clawOpen = false;
        boolean xPressed = false;

        boolean slideDown = true;

        boolean touchSensorEnabled = true;


        waitForStart();

        while (opModeIsActive()) {
            if (touchSensorEnabled) {
                if (touchSensor.isPressed()) {
                    telemetry.addData("Touch sensor", "is pressed");

                    leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

                    leftRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    rightRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

                } else {
                    telemetry.addData("touch sensor", "is not pressed");
                }

            } else {
                telemetry.addData("touch sensor", "is being disabled temporarily");
            }

            pattern = RevBlinkinLedDriver.BlinkinPattern.TWINKLES_FOREST_PALETTE;

            // DRIVING FUNCTIONS FOR FIELD DRIVE
            driveY = -gamepad1.left_stick_y * 0.5f;
            driveX = gamepad1.left_stick_x * 0.5f;
            driveRX = -gamepad1.right_stick_x * 0.5f;

            driveDenominator = JavaUtil.maxOfList(JavaUtil.createListWith(JavaUtil.sumOfList(JavaUtil.createListWith(Math.abs(driveY), Math.abs(driveX), Math.abs(driveRX))), 1));

            frontLeft.setPower((driveY + driveX + driveRX) / driveDenominator);
            backLeft.setPower(((driveY - driveX) + driveRX) / driveDenominator);
            frontRight.setPower(((driveY - driveX) - driveRX) / driveDenominator);
            backRight.setPower(((driveY + driveX) - driveRX) / driveDenominator);

            /*
            -----------------
            SCORING PROCESSES
            -----------------
             */

            if (gamepad1.x) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.RED;

                slideDown = false;

                wrist_position = 0.64;

                leftRotate.setTargetPosition(2400);
                rightRotate.setTargetPosition(-2400);

                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                rightRotate.setPower(-1);
                leftRotate.setPower(1);


                currentState = RobotState.MAX_SLIDE;
            }

//            if (gamepad1.b && !isBPressed) {
//                toggleSpecimen = !toggleSpecimen;
//                isBPressed = true;
//
//                if (toggleSpecimen) {
//                    pattern = RevBlinkinLedDriver.BlinkinPattern.RED;
//
//                    wrist_position = 0.05;
//                    updown_wrist_position = 0.615;
//
//                    leftRotate.setTargetPosition(-1707);
//                    leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                    leftRotate.setPower(-1);
//
//                    currentState = RobotState.SPECIMEN;
//
//                } else {
//                    pattern = RevBlinkinLedDriver.BlinkinPattern.RED;
//
//                    updown_wrist_position = 0.200;
//
//                    leftRotate.setTargetPosition(-1200);
//                    leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                    leftRotate.setPower(-1);
//
//                    currentState = RobotState.IDLE;
//                }
//            } else if (!gamepad1.b){
//                isBPressed = false;
//            }

            /*
            -----------
            NEUTRAL POS.
            -----------
             */

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

            } else if (gamepad1.right_trigger > 0.5) {
                touchSensorEnabled = true;
//                isRotatedDown = false;
                pattern = RevBlinkinLedDriver.BlinkinPattern.ORANGE;

                updown_wrist_position = 0.69;
                wrist_position = 0.05;

                slideDown = true;

                leftRotate.setTargetPosition(0);
                rightRotate.setTargetPosition(0);

                rightRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                rightRotate.setPower(1);
                leftRotate.setPower(-1);
            }

            /*
            ----------------
            INTAKE PROCESSES
            ----------------
             */


            if (gamepad1.right_bumper) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.YELLOW;

                wrist_position = 0.04;
                updown_wrist_position = 0.435;

                slideDown = true;

            } else if (gamepad1.y) {
                touchSensorEnabled = false;
                pattern = RevBlinkinLedDriver.BlinkinPattern.YELLOW;

                slideDown = true;

                updown_wrist_position = 0.69;
                wrist_position = 0.05;

                leftSlide.setTargetPosition(1000);
                rightSlide.setTargetPosition(1000);

                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftSlide.setPower(1);
                rightSlide.setPower(1);


            }

//            if (gamepad1.left_bumper) {
//                updown_wrist_position = 0.37;
//                wrist_position = 0.452;
//            }


            /*
            ------------------
            HANGING IN END GAME
            ------------------
             */

            if (gamepad1.a) {
                slideDown = false;

                leftRotate.setTargetPosition(-3140);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(-1);

                currentState = RobotState.ROTATING;

            }

            /*
            --------------
            CLAW MOVEMENTS
            --------------
             */

            if (gamepad2.y && !xPressed) {
                clawOpen = !clawOpen;
                xPressed = true;
            } else if (!gamepad2.y) {
                xPressed = false;
            }

            if (clawOpen) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.AQUA;

                clawLeft.setPosition(0.4);
                clawRight.setPosition(0.5);
            } else {
                pattern = RevBlinkinLedDriver.BlinkinPattern.AQUA;

                clawLeft.setPosition(0.7);
                clawRight.setPosition(0.2);

            }

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

            /*
            -------------
            ARM MOVEMENTS
            -------------
             */

            if (gamepad2.left_trigger > 0.1) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.VIOLET;

                //limitation so slide is unable to extend further than 42"
                if (leftSlide.getCurrentPosition() < -1250 && slideDown && leftRotate.getCurrentPosition() < 800) {
                    leftSlide.setPower(0);
                    rightSlide.setPower(0);
                } else {
                    leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    leftSlide.setPower(-0.9);
                    rightSlide.setPower(-0.9);
                }

            } else if (gamepad2.right_trigger > 0.1) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.VIOLET;

                leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftSlide.setPower(0.9);
                rightSlide.setPower(0.9);

            } else if ((leftSlide.getMode() == DcMotor.RunMode.RUN_USING_ENCODER && rightSlide.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) || (!leftSlide.isBusy() && !rightSlide.isBusy() && slideDown)) {
                leftSlide.setPower(0.0);
                rightSlide.setPower(0.0);

                currentState = RobotState.IDLE;
            }

            //rb brings the slide backward; lb brings the slide backward
            if (gamepad2.right_bumper) {

                pattern = RevBlinkinLedDriver.BlinkinPattern.VIOLET;

                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                rightRotate.setPower(-0.95);
                leftRotate.setPower(0.95);
            } else if (gamepad2.left_bumper) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.VIOLET;

                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                rightRotate.setPower(0.95);
                leftRotate.setPower(-0.95);
            } else if (leftRotate.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) {
                leftRotate.setPower(0.0);
                rightRotate.setPower(0.0);

                currentState = RobotState.IDLE;
            }

            /*
            --------------------
            SEMI-NEUTRAL POSITION
            --------------------
             */


            wrist.setPosition(wrist_position);
            updown_wrist.setPosition(updown_wrist_position);


            switch (currentState) {
                case ROTATING:
                    if (!leftRotate.isBusy()) {
                        currentState = RobotState.WAITING;
                    }
                    break;

                case SPECIMEN:
                    if (!leftRotate.isBusy()) {
                        leftSlide.setTargetPosition(-850);
                        rightSlide.setTargetPosition(-850);

                        leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                        leftSlide.setPower(-1);
                        rightSlide.setPower(-1);
                    }
                    break;


                case MAX_SLIDE:
                    if (!leftRotate.isBusy() && !rightRotate.isBusy()) {
                        updown_wrist_position = 1;

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

            blinkinLedDriver.setPattern(pattern);

            telemetry.addData("State", currentState);

            telemetry.addData("Left Rotate pos", leftRotate.getCurrentPosition());
            telemetry.addData("Right Rotate pos, ", rightRotate.getCurrentPosition());

            telemetry.addData("Updown wrist pos", updown_wrist.getPosition());
            telemetry.addData("Side to side wrist pos", wrist.getPosition());

            telemetry.addData("claw right pos", clawRight.getPosition());
            telemetry.addData("claw left pos", clawLeft.getPosition());

            telemetry.addData("left slide pos: ", leftSlide.getCurrentPosition());
            telemetry.addData("right slide pos: ", rightSlide.getCurrentPosition());

            telemetry.update();
        }
    }
}