package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

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
//    private DistanceSensor distancesensor1;


    static final int SLIDE_MAX_POSITION = -2150;

    private double wrist_position = 0.07;

    private double updown_wrist_position = 0.915;

    private double max_slide_when_rotated = 1000;


    private enum RobotState {
        IDLE,
        ROTATING,
        WAITING,
        SLIDING,
        TRANSITION,
        MAX,
        NEUTRAL_TRANSITION,
        NEUTRAL,
        NEUTRAL_ROTATE
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

//        distancesensor1 = hardwareMap.get(DistanceSensor.class, "distancesensor1");

        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Define claw variables
        boolean clawOpen = false;
        boolean xPressed = false;

        // Define slide variable
        boolean slideDown = true;

        boolean isRotatedDown = false;

        waitForStart();

        while (opModeIsActive()) {

            // DRIVING FUNCTIONS
            driveY = -gamepad1.left_stick_y * 0.5f;
            driveX = -gamepad1.left_stick_x * 0.5f;
            driveRX = -gamepad1.right_stick_x * 0.5f;

            driveDenominator = JavaUtil.maxOfList(JavaUtil.createListWith(JavaUtil.sumOfList(JavaUtil.createListWith(Math.abs(driveY), Math.abs(driveX), Math.abs(driveRX))), 1));

            frontLeft.setPower((driveY + driveX + driveRX) / driveDenominator);
            backLeft.setPower(((driveY - driveX) + driveRX) / driveDenominator);
            frontRight.setPower(((driveY - driveX) - driveRX) / driveDenominator);
            backRight.setPower(((driveY + driveX) - driveRX) / driveDenominator);

            // Opens and closes the claw
            if (gamepad2.y && !xPressed) {
                clawOpen = !clawOpen;
                xPressed = true;
            } else if (!gamepad2.y) {
                xPressed = false;
            }

            if (clawOpen) {
                clawLeft.setPosition(0.7);
                clawRight.setPosition(0.2);
            } else {
                clawLeft.setPosition(0.4);
                clawRight.setPosition(0.5);
            }

            // PRESET - picks up sample that is right in front of the robot
            if (gamepad1.right_bumper) {
                isRotatedDown = true;

                wrist_position = 0.07;
                updown_wrist_position = 0.465;


                leftRotate.setTargetPosition(0);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(1);

            // PRESET - returns the robot to neutral position, including slides and rotate pos
            } else if (gamepad1.left_bumper) {
                isRotatedDown = false;
                if (Math.abs(leftSlide.getCurrentPosition()) > 0 || Math.abs(rightSlide.getCurrentPosition()) > 0){
                    slideDown = true;
                    leftSlide.setTargetPosition(0);
                    rightSlide.setTargetPosition(0);

                    leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                    leftSlide.setPower(1);
                    rightSlide.setPower(1);

                }

                currentState = RobotState.NEUTRAL_TRANSITION;

                //PRESET - grabs a specimen from the inside thingymabob
            } else if (gamepad1.y && currentState == RobotState.IDLE) {
                slideDown = false;
                isRotatedDown = true;

                wrist_position = 0.07;
                updown_wrist_position = 0.465;

                clawOpen = false;

                leftRotate.setTargetPosition(0);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(-1);

                currentState = RobotState.ROTATING;

            // PRESET - scores sample into high basket
            } else if (gamepad1.x) {
                slideDown = false;
                isRotatedDown = false;

//                wrist_position = 0.445;
//                updown_wrist_position = 0.246;

                leftRotate.setTargetPosition(-2980);

                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftRotate.setPower(-1);

                currentState = RobotState.TRANSITION;

            //PRESET - lines up the robot with the submersible to be ready to hang using distance sensors, and then uses cases to line up and extend the slides to be hung
            }

//            else if (gamepad1.a) {
//                int thresholdDistance = 50;
//
//                if (distancesensor1.getDistance(DistanceUnit.CM) > thresholdDistance) {
//                    frontRight.setPower(0.4);
//                    frontLeft.setPower(0.4);
//                    backRight.setPower(0.4);
//                    backLeft.setPower(0.4);
//                } else {
//                    frontRight.setPower(0);
//                    frontLeft.setPower(0);
//                    backRight.setPower(0);
//                    backLeft.setPower(0);
//                }
//            }

            // Rotate of the claw
            if (gamepad2.left_stick_y < -0.1) {
                if (updown_wrist_position > 0) {
                    updown_wrist_position -= 0.01;
                }
            } else if (gamepad2.left_stick_y > 0.1) {
                if (updown_wrist_position < 1) {
                    updown_wrist_position += 0.01;
                }
            }

            if (gamepad2.right_stick_x < -0.1) {
                if (wrist_position > 0) {
                    wrist_position -= 0.01;
                }
            } else if (gamepad2.right_stick_x > 0.1) {
                if (wrist_position < 1) {
                    wrist_position += 0.01;
                }
            }


            // Manual slide and rotation overrides, lt brings the slide up; rt brings the slide down
            if (gamepad2.left_trigger > 0.1) {
                leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftSlide.setPower(-0.75);
                rightSlide.setPower(-0.75);
            } else if (gamepad2.right_trigger > 0.1) {
//                if (!isRotatedDown) {
                leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftSlide.setPower(0.75);
                rightSlide.setPower(0.75);
//                } else {
//                    if (leftSlide.getCurrentPosition() < max_slide_when_rotated) {
//                        leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//                        rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//                        leftSlide.setPower(0.66);
//                        righ  tSlide.setPower(0.66);
//                    }
//                }
            } else if ((leftSlide.getMode() == DcMotor.RunMode.RUN_USING_ENCODER && rightSlide.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) || (!leftSlide.isBusy() && !rightSlide.isBusy() && slideDown)) {
                leftSlide.setPower(0.0);
                rightSlide.setPower(0.0);
            }

            //rb brings the slide backward; lb brings the slide backward
            if (gamepad2.right_bumper) {
                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftRotate.setPower(-0.95);
            } else if (gamepad2.left_bumper) {
                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftRotate.setPower(0.95);
            } else if (leftRotate.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) {
                leftRotate.setPower(0.0);
            }


            //PRESET - SPECIMEN

            //grabs the specimen straight from the wall at a short distance
            if (gamepad2.a) {
                updown_wrist_position = 0.37;
                wrist_position = 0.452;
            }

            if (gamepad2.x) {
                clawOpen = true;

                if (Math.abs(leftSlide.getCurrentPosition()) > 0 || Math.abs(rightSlide.getCurrentPosition()) > 0){
                    slideDown = true;
                    leftSlide.setTargetPosition(0);
                    rightSlide.setTargetPosition(0);

                    leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                    leftSlide.setPower(1);
                    rightSlide.setPower(1);

                }

                currentState = RobotState.NEUTRAL_TRANSITION;

            }

            //puts the slide at the correct position to place a specimen onto the bar
//            else if (gamepad2.b) {
//
//
//
//            }


            wrist.setPosition(wrist_position);
            updown_wrist.setPosition(updown_wrist_position);


            switch (currentState) {
                case ROTATING:
                    // Check if the rotation is complete
                    if (!leftRotate.isBusy()) {
                        // Wait for a brief period before starting the slide
                        currentState = RobotState.WAITING;
                    }
                    break;
                case NEUTRAL_TRANSITION:
                    if (!leftSlide.isBusy() && !rightSlide.isBusy()) {
                        currentState = RobotState.NEUTRAL;
                    }
                case NEUTRAL:
                        wrist_position = 0.07;
                        updown_wrist_position = 0.685;

                        leftRotate.setTargetPosition(0);
                        leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                        if (leftRotate.getCurrentPosition() > 0) {
                            leftRotate.setPower(1);
                        } else if (leftRotate.getCurrentPosition() < 0) {
                            leftRotate.setPower(-1);
                        }

                        currentState = RobotState.NEUTRAL_ROTATE;

                    break;

                case NEUTRAL_ROTATE:
                    if (!leftRotate.isBusy()) {
                        currentState = RobotState.IDLE;
                    }
                    break;

                case TRANSITION:
                    if (!leftRotate.isBusy()) {
                        currentState = RobotState.MAX;
                    }
                    break;

                case MAX:
                        leftSlide.setTargetPosition(SLIDE_MAX_POSITION);
                        rightSlide.setTargetPosition(SLIDE_MAX_POSITION);

                        leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                        leftSlide.setPower(-1);
                        rightSlide.setPower(-1);

                        currentState = RobotState.SLIDING;

                    break;
                case WAITING:
                        // Start the slide movement
                        leftSlide.setTargetPosition(-1000);
                        rightSlide.setTargetPosition(-1000);

                        leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                        leftSlide.setPower(-1);
                        rightSlide.setPower(-1);

                        // Transition to SLIDING state
                        currentState = RobotState.SLIDING;

                    break;

                case SLIDING:
                    // Check if the slide is complete
                    if (!leftSlide.isBusy() && !rightSlide.isBusy()) {
                        // Return to IDLE state
                        currentState = RobotState.IDLE;
                    }
                    break;

                case IDLE:
                    // Nothing to do, waiting for input
                    break;
            }

            telemetry.addData("State", currentState);
            telemetry.addData("Left Rotate pos", leftRotate.getCurrentPosition());
            telemetry.addData("Updown wrist pos", updown_wrist.getPosition());
            telemetry.addData("Side to side wrist pos", wrist.getPosition());

            telemetry.addData("claw right pos", clawRight.getPosition());
            telemetry.addData("claw left pos", clawLeft.getPosition());

            telemetry.addData("left slide pos: ", leftSlide.getCurrentPosition());
            telemetry.addData("right slide pos: ", rightSlide.getCurrentPosition());

//            telemetry.addData("distance sensor in cm: ", distancesensor1.getDistance(DistanceUnit.CM));
            telemetry.update();
        }
    }
}