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

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

//MOTORS & SERVOS
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

//REV LED
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;

//DISTANCE SENSOR
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.robot.Robot;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

//FIELD DRIVE GYROSCOPE
//import com.qualcomm.hardware.kauailabs.NavxMicroNavigationSensor;
//import com.kauailabs.navx.ftc.AHRS;

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
    RevBlinkinLedDriver blinkinLedDriver;
    RevBlinkinLedDriver.BlinkinPattern pattern;

//    private AHRS navxDevice;

    private Servo wrist;
    private Servo updown_wrist;

    // Claw
    private Servo clawLeft;
    private Servo clawRight;

    static final int SLIDE_MAX_POSITION = -2300;

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
        SUBMERSIBLE,
        STARTING,
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

        // Wrist and Claw
        wrist = hardwareMap.get(Servo.class, "wrist");
        updown_wrist = hardwareMap.get(Servo.class, "updown_wrist");
        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        blinkinLedDriver = hardwareMap.get(RevBlinkinLedDriver.class, "blinkin");

//        navxDevice = AHRS.getInstance(hardwareMap.get(NavxMicroNavigationSensor.class, "navx"),
//                AHRS.DeviceDataType.kProcessedData);

        boolean clawOpen = false;
        boolean xPressed = false;

        boolean slideDown = true;

//        boolean isRotatedDown = false;


        waitForStart();

        while (opModeIsActive()) {

            pattern = RevBlinkinLedDriver.BlinkinPattern.TWINKLES_FOREST_PALETTE;

            // DRIVING FUNCTIONS FOR FIELD DRIVE
            driveY = -gamepad1.left_stick_y * 0.5f;
            driveX = gamepad1.left_stick_x * 0.5f;
            driveRX = -gamepad1.right_stick_x * 0.5f;

//            float gyroDegrees = navxDevice.getYaw();
//            double gyroRadians = Math.toRadians(gyroDegrees);
//
//            double temp = driveY * Math.cos(gyroRadians) + driveX * Math.sin(gyroRadians);
//            driveX = -driveY * Math.sin(gyroRadians) + driveX * Math.cos(gyroRadians);
//            driveY = (float) temp;

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
//                isRotatedDown = false;

                wrist_position = 0.64;

                leftRotate.setTargetPosition(-2265);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(-1);

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

                leftSlide.setPower(1);
                rightSlide.setPower(1);

                if (!leftSlide.isBusy() && !rightSlide.isBusy()) {
                    currentState = RobotState.IDLE;
                }

            } else if (gamepad1.right_trigger > 0.5) {
//                isRotatedDown = false;
                pattern = RevBlinkinLedDriver.BlinkinPattern.ORANGE;

                updown_wrist_position = 0.69;
                wrist_position = 0.05;

                slideDown = true;

                leftRotate.setTargetPosition(0);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftRotate.setPower(1);
            }

            /*
            ----------------
            INTAKE PROCESSES
            ----------------
             */


            if (gamepad1.right_bumper) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.YELLOW;
//                isRotatedDown = true;

                wrist_position = 0.04;
                updown_wrist_position = 0.495;

                slideDown = true;

            } else if (gamepad1.y && currentState == RobotState.IDLE) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.YELLOW;

                slideDown = true;
//                isRotatedDown = true;

                updown_wrist_position = 0.69;
                wrist_position = 0.05;

                leftSlide.setTargetPosition(-1000);
                rightSlide.setTargetPosition(-1000);

                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftSlide.setPower(-1);
                rightSlide.setPower(-1);

                currentState = RobotState.SUBMERSIBLE;

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
//                isRotatedDown = false;

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
                    leftSlide.setPower(-0.75);
                    rightSlide.setPower(-0.75);
                }

            } else if (gamepad2.right_trigger > 0.1) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.VIOLET;

                leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftSlide.setPower(0.75);
                rightSlide.setPower(0.75);

            } else if ((leftSlide.getMode() == DcMotor.RunMode.RUN_USING_ENCODER && rightSlide.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) || (!leftSlide.isBusy() && !rightSlide.isBusy() && slideDown)) {
                leftSlide.setPower(0.0);
                rightSlide.setPower(0.0);

                currentState = RobotState.IDLE;
            }

            //rb brings the slide backward; lb brings the slide backward
            if (gamepad2.right_bumper) {

                pattern = RevBlinkinLedDriver.BlinkinPattern.VIOLET;

                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftRotate.setPower(0.95);
            } else if (gamepad2.left_bumper) {
                pattern = RevBlinkinLedDriver.BlinkinPattern.VIOLET;

                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftRotate.setPower(-0.95);
            } else if (leftRotate.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) {
                leftRotate.setPower(0.0);
            }

            /*
            --------------------
            SEMI-NEUTRAL POSITION
            --------------------
             */

//            if (gamepad2.x) {
//                pattern = RevBlinkinLedDriver.BlinkinPattern.ORANGE;
//
//                clawOpen = false;
//
//                updown_wrist_position = 0.69;
//                wrist_position = 0.05;
//
//
//                if (Math.abs(leftSlide.getCurrentPosition()) > 0 || Math.abs(rightSlide.getCurrentPosition()) > 0){
//                    slideDown = true;
//                    leftSlide.setTargetPosition(0);
//                    rightSlide.setTargetPosition(0);
//
//                    leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                    rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//                    leftSlide.setPower(1);
//                    rightSlide.setPower(1);
//                }
//            }

            if (gamepad2.dpad_up) {
                leftRotate.setTargetPosition(1650);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(1);

                slideDown = true;

                currentState = RobotState.STARTING;
            }


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
                    if (!leftRotate.isBusy()) {
                        updown_wrist_position = 0.855;

                        leftSlide.setTargetPosition(SLIDE_MAX_POSITION);
                        rightSlide.setTargetPosition(SLIDE_MAX_POSITION);

                        leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                        leftSlide.setPower(-1);
                        rightSlide.setPower(-1);

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