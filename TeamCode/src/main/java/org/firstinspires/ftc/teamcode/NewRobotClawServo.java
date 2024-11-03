package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="NewRobotClawServo")
public class NewRobotClawServo extends OpMode {
    private Servo LServo;
    private Servo RServo;

    @Override
    public void init() {
        LServo = hardwareMap.get(Servo.class, "LServo");
        RServo = hardwareMap.get(Servo.class, "RServo");
    }

    public void init_loop() {

    }

    public void start() {

    }

    public void loop() {
        if (gamepad1.left_trigger>0.1) {
            LServo.setPosition(0.78); //works
            RServo.setPosition(0.22);

        } else if (gamepad1.right_trigger>0.1) {
            LServo.setPosition(1); //works
            RServo.setPosition(0);
        }
    }

    public void stop() {
    }
}