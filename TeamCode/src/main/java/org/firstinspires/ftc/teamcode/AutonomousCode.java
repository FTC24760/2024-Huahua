package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "Into The Deep 2025 AUTONOMOUS", group = "Autonomous")
public class AutonomousCode extends LinearOpMode {

    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;

    private DcMotor leftSlide;
    private DcMotor rightSlide;

    private DcMotor topArm;
    private DcMotor bottomArm;

    private Servo clawLeft;
    private Servo clawRight;

    private DistanceSensor distanceSensor1; //the distance sensor in the front
    private DistanceSensor distanceSensor2; //the distance sensor in the back

    private static final int SLIDE_MAX_POSITION = 1000;
    private static final int TOP_ARM_POSITION = 250;
    private static final int BOTTOM_ARM_POSITION = 250;

    @Override
    public void runOpMode() {
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        leftSlide = hardwareMap.get(DcMotor.class, "leftSlide");
        rightSlide = hardwareMap.get(DcMotor.class, "rightSlide");
        leftSlide.setDirection(DcMotorSimple.Direction.REVERSE);

        topArm = hardwareMap.get(DcMotor.class, "topArm");
        bottomArm = hardwareMap.get(DcMotor.class, "bottomArm");

        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

        distanceSensor1 = hardwareMap.get(DistanceSensor.class, "distanceSensor1");
        distanceSensor2 = hardwareMap.get(DistanceSensor.class, "distanceSensor2");

        double targetdistance_forward = 50.0;
        double targetdistance_backward = 50.0;

        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        topArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        topArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        bottomArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bottomArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();
        while (opModeIsActive() && distanceSensor1.getDistance(DistanceUnit.CM) > targetdistance_forward) {
            // -> robot will go forwards until the distance sensors senses 50cm
            frontRight.setPower(0.3);
            frontLeft.setPower(0.3);
            backRight.setPower(0.3);
            backLeft.setPower(0.3);

            telemetry.addData("Distance (cm)", distanceSensor1.getDistance(DistanceUnit.CM));
            telemetry.update();
        }

        //-> once the robot calculates 50cm, the robot will stop
        frontRight.setPower(0);
        frontLeft.setPower(0);
        backRight.setPower(0);
        backLeft.setPower(0);

        telemetry.addData("Status", "Distance Reached. Yay! Robot will now stop.");
        telemetry.update();

        //-> the robot will extend the slides to maximum position
        if (leftSlide.getCurrentPosition() < SLIDE_MAX_POSITION && rightSlide.getCurrentPosition() < SLIDE_MAX_POSITION) {
            leftSlide.setPower(0.7);
            rightSlide.setPower(0.7);
        }

        // -> the slide will stop at the top
        leftSlide.setPower(0);
        rightSlide.setPower(0);

        // -> top arm will move forwards to a certain position and then stop
        topArm.setTargetPosition(TOP_ARM_POSITION);
        topArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        topArm.setPower(0.5);

        // -> bottom arm will move to a certain position then stop
        bottomArm.setTargetPosition(BOTTOM_ARM_POSITION);
        bottomArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bottomArm.setPower(0.5);

        // -> claw will release specimen
        clawLeft.setPosition(0.3);
        clawRight.setPosition(0.7);

        // -> robot will move backwards
        frontLeft.setPower(-0.3);
        frontRight.setPower(-0.3);
        backLeft.setPower(-0.3);
        backRight.setPower(-0.3);

        while (distanceSensor2.getDistance(DistanceUnit.CM) > targetdistance_backward) {
            telemetry.addData("Distance (cm)", distanceSensor2.getDistance(DistanceUnit.CM));
            telemetry.update();
        }

        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);

    }
}
