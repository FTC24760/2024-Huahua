package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Spinning claw servo")
public class SpinningClaw extends OpMode {
    private Servo spinServo;

    @Override
    public void init() {
    }

    @Override
    public void start() {
        spinServo = hardwareMap.get(Servo.class, "spinServo");
    }

    @Override
    public void loop() {
        if (gamepad1.a) {
            spinServo.setPosition(0.0);
        } else if (gamepad1.b) {
            spinServo.setPosition(0.5);
        } else if (gamepad1.x) {
            spinServo.setPosition(1.0);
        }

    }

    public void stop() {
    }
}