package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.robotcore.external.JavaUtil;

import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Into The Deep 2025")
public class IntoTheDeep extends OpMode {
    //ClawMotor
    private DcMotor clawmotor1;
    private DcMotor clawmotor2;

    //Gobilda Slide
    private DcMotor rightMotor;
    private DcMotor leftMotor;

    //LinearSlideMotor
    private DcMotor LSlideMotor;
    private DcMotor RSlideMotor;

    //MecanumDrive
    private DcMotor frontRight;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor backLeft;

    //NewRobotClawServo
    private Servo LServo;
    private Servo RServo;


    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");


    }


}
