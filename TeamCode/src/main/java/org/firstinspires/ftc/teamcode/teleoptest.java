package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.JavaUtil;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/*
TODO:
-> map distanceSensor1, distanceSensor2 (they are on 2 and 3 port on the control hub)
 */

@TeleOp(name = "BETTER TELEOP")
public class teleoptest extends LinearOpMode {
    private DcMotor frontRight, backRight, frontLeft, backLeft;

    private DcMotor leftSlide, rightSlide, leftRotate;

    private Servo wrist, updown_wrist, clawLeft, clawRight;

    private DistanceSensor distanceSensor1, distanceSensor2;

    private double wrist_position = 0.5, updown_wrist_position = 0.5;
    private boolean clawOpen = false, xPressed = false;

    static final int SLIDE_MAX_POSITION = 2000;
    static final int SLIDE_NEUTRAL_POSITION = 0;

    @Override
    public void runOpMode() {
        // Hardware Initialization
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        leftSlide = hardwareMap.get(DcMotor.class, "leftSlide");
        rightSlide = hardwareMap.get(DcMotor.class, "rightSlide");
        leftRotate = hardwareMap.get(DcMotor.class, "leftRotate");
        leftSlide.setDirection(DcMotorSimple.Direction.REVERSE);

        wrist = hardwareMap.get(Servo.class, "wrist");
        updown_wrist = hardwareMap.get(Servo.class, "updown_wrist");
        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

        distanceSensor1 = hardwareMap.get(DistanceSensor.class, "distanceSensor1");
        distanceSensor2 = hardwareMap.get(DistanceSensor.class, "distanceSensor2");

        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        waitForStart();

        while (opModeIsActive()) {
            // Drive Controls
            float driveY = gamepad1.left_stick_y * 0.7f;
            float driveX = -gamepad1.left_stick_x * 0.7f;
            float driveRX = -gamepad1.right_stick_x * 0.7f;

            double driveDenominator = JavaUtil.maxOfList(JavaUtil.createListWith(JavaUtil.sumOfList(JavaUtil.createListWith(Math.abs(driveY), Math.abs(driveX), Math.abs(driveRX))), 1));

            frontLeft.setPower((driveY + driveX + driveRX) / driveDenominator);
            backLeft.setPower(((driveY - driveX) + driveRX) / driveDenominator);
            frontRight.setPower(((driveY - driveX) - driveRX) / driveDenominator);
            backRight.setPower(((driveY + driveX) - driveRX) / driveDenominator);


            // Claw Toggle
            if (gamepad2.x && !xPressed) {
                clawOpen = !clawOpen;
                xPressed = true;
            } else if (!gamepad2.x) {
                xPressed = false;
            }

            clawLeft.setPosition(clawOpen ? 0.7 : 0.3);
            clawRight.setPosition(clawOpen ? 0.3 : 0.7);

            // Execute Presets in Threads
            if (gamepad1.x) {
                new Thread(() -> BasketPreset()).start();
            } else if (gamepad1.y) {
                new Thread(() -> GrabPreset()).start();
            } else if (gamepad1.right_bumper) {
                new Thread(() -> SlidePickup()).start();
            } else if (gamepad1.left_bumper) {
                new Thread(() -> NeutralPosition()).start();
            } else if (gamepad2.y) {
                new Thread(() -> SpecimenHangPreset()).start();
            }



            if (gamepad2.left_trigger > 0.1) {
                leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftSlide.setPower(-1);
                rightSlide.setPower(-1);
            } else if (gamepad2.right_trigger > 0.1) {
                leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftSlide.setPower(1);
                rightSlide.setPower(1);
            } else if (leftSlide.getMode() != DcMotor.RunMode.RUN_TO_POSITION && rightSlide.getMode() != DcMotor.RunMode.RUN_TO_POSITION) {
                leftSlide.setPower(0.0);
                rightSlide.setPower(0.0);
            }

            if (gamepad2.right_bumper) {
                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftRotate.setPower(-1);

            } else if (gamepad2.left_bumper) {
                leftRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftRotate.setPower(1);
                telemetry.addData("LeftRotate Position", leftRotate.getCurrentPosition());

            }
            else {
                leftRotate.setPower(0.0);
            }

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

            telemetry.addData("Wrist Left-Right Position", wrist.getPosition());
            telemetry.addData("Wrist Up-Down Position", updown_wrist.getPosition());
            telemetry.addData("Left Rotate Position", leftRotate.getCurrentPosition());
            telemetry.addData("Left Slide Position", leftSlide.getCurrentPosition());
            telemetry.addData("Right Slide Position", rightSlide.getCurrentPosition());

            telemetry.update();
        }
    }

    private void BasketPreset() { //gamepad1.x
        int SLIDE_BASKET_POSITION = 100;
        wrist.setPosition(0.565);
        updown_wrist.setPosition(0.0);

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

    private void GrabPreset() { //gamepad1.y
        int SLIDE_PICKUP_POSITION = -2280;

        leftRotate.setTargetPosition(SLIDE_PICKUP_POSITION);
        leftSlide.setTargetPosition(1000);
        rightSlide.setTargetPosition(1000);

        leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftRotate.setPower(-1);

        while (leftRotate.isBusy()) {
        }

        leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftSlide.setPower(-1);
        rightSlide.setPower(-1);
    }

    private void SlidePickup() { //gamepad1.right_bumper
        int SLIDE_PICKUP_POSITION = -2275;
        updown_wrist.setPosition(0.696);
        wrist.setPosition(0.59);

        leftRotate.setTargetPosition(SLIDE_PICKUP_POSITION);
        leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftRotate.setPower(-1);
    }

    private void NeutralPosition() { //gamepad1.left_bumper
        leftSlide.setTargetPosition(0);
        rightSlide.setTargetPosition(0);

        leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftSlide.setPower(1);
        rightSlide.setPower(1);

        leftRotate.setTargetPosition(0);
        leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftRotate.setPower(1);
    }

    private void SpecimenHangPreset() { //gamepad.y
        while (distanceSensor1.getDistance(DistanceUnit.CM) > 50 && distanceSensor2.getDistance(DistanceUnit.CM)  > 50) {
            frontRight.setPower(0.3);
            frontLeft.setPower(0.3);
            backRight.setPower(0.3);
            backLeft.setPower(0.3);
        }

        leftRotate.setTargetPosition(100); //change
        leftRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftRotate.setPower(1);

        while (leftRotate.isBusy()) {
        }

        leftSlide.setTargetPosition(1500); //change
        rightSlide.setTargetPosition(1500); //change
        leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftSlide.setPower(1);
        rightSlide.setPower(1);


        }
}