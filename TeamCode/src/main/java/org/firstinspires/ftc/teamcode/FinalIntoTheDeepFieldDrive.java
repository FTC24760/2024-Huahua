package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.kauailabs.NavxMicroNavigationSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.kauailabs.navx.ftc.AHRS;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.JavaUtil;

@TeleOp(name="FINAL 2024 INTO THE DEEP")
public class FinalIntoTheDeepFieldDrive extends LinearOpMode {
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


    static final int SLIDE_MAX_POSITION = 2150;

    private double wrist_position = 0.5;

    private double updown_wrist_position = 0.5;

    private double max_slide_when_rotated = 1000;


    private enum RobotState {
        IDLE,
        ROTATING,
        WAITING,
        SLIDING
    }

    private RobotState currentState = RobotState.IDLE;

    private AHRS navxDevice;


    @Override
    public void runOpMode() {
        navxDevice = AHRS.getInstance(hardwareMap.get(NavxMicroNavigationSensor.class, "navx"),
                AHRS.DeviceDataType.kProcessedData);
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

        boolean isRotatedDown = false;

        waitForStart();

        while (opModeIsActive()) {
            float gyroDegrees = navxDevice.getYaw();
            double gyroRadians = Math.toRadians(gyroDegrees);

            // DRIVING FUNCTIONS
            driveY = gamepad1.left_stick_y * 0.5f;
            driveX = -gamepad1.left_stick_x * 0.5f;
            driveRX = -gamepad1.right_stick_x * 0.5f;

            double temp = driveY * Math.cos(gyroRadians) + driveX * Math.sin(gyroRadians);
            driveX = -driveY * Math.sin(gyroRadians) + driveX * Math.cos(gyroRadians);
            driveY = (float) temp;

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
                isRotatedDown = true;

                wrist_position = 0.59;
                updown_wrist_position = 0.576;


                leftRotate.setTargetPosition(-2275);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(1);

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

                leftRotate.setTargetPosition(0);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                if (leftRotate.getCurrentPosition() > 0) {
                    leftRotate.setPower(1);
                } else if (leftRotate.getCurrentPosition() < 0) {
                    leftRotate.setPower(-1);
                }
            } else if (gamepad1.y && currentState == RobotState.IDLE) {
                slideDown = false;
                isRotatedDown = true;

                wrist_position = 0.53;
                updown_wrist_position = 0.75;

                clawOpen = true;

                leftRotate.setTargetPosition(-2100);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(-1);

                currentState = RobotState.ROTATING;

            } else if (gamepad1.x) {
                slideDown = false;
                isRotatedDown = false;

                wrist_position = 0.565;
                updown_wrist_position = 0.25;

                leftRotate.setTargetPosition(100);
                leftSlide.setTargetPosition(SLIDE_MAX_POSITION);
                rightSlide.setTargetPosition(SLIDE_MAX_POSITION);

                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftRotate.setPower(-1);

                leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                leftSlide.setPower(-1);
                rightSlide.setPower(-1);
            } else if (gamepad1.a) {
                isRotatedDown = true;
                wrist_position = 0.53;
                updown_wrist_position = 0.75;

                clawOpen = true;

                leftRotate.setTargetPosition(-2100);
                leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftRotate.setPower(-1);
            }


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
                    wrist_position -= 0.012;
                }
            } else if (gamepad2.right_stick_x > 0.1) {
                if (wrist_position < 1) {
                    wrist_position += 0.012;
                }
            }


            // Manual slide and rotation overrides
            if (gamepad2.left_trigger > 0.1) {
                leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftSlide.setPower(-0.75);
                rightSlide.setPower(-0.75);
            } else if (gamepad2.right_trigger > 0.1) {
                if (!isRotatedDown) {
                    leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    leftSlide.setPower(0.75);
                    rightSlide.setPower(0.75);
                } else {
                    if (leftSlide.getCurrentPosition() < max_slide_when_rotated) {
                        leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        leftSlide.setPower(0.75);
                        rightSlide.setPower(0.75);
                    }
                }
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


            switch (currentState) {
                case ROTATING:
                    // Check if the rotation is complete
                    if (!leftRotate.isBusy()) {
                        // Wait for a brief period before starting the slide
                        currentState = RobotState.WAITING;
                    }
                    break;

                case WAITING:
                        // Start the slide movement
                        leftSlide.setTargetPosition(1000);
                        rightSlide.setTargetPosition(1000);

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


            telemetry.update();
        }
    }
}