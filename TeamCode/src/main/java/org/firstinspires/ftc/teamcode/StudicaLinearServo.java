package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "LinearServo", group = "Concept")

public class StudicaLinearServo extends LinearOpMode {
    Servo linearServo;

    @Override
    public void runOpMode() {

        // Connect to servo (Assume Robot Left Hand)
        // Change the text in quotes to match any servo name on your robot.
        linearServo = hardwareMap.get(Servo.class, "linearServo");

        // Wait for the start button
        waitForStart();

        // Scan servo till stop pressed.
        while(opModeIsActive()){
            if (gamepad1.x) {
                linearServo.setPosition(1);
            } else if (gamepad1.y) {
                linearServo.setPosition(0);
            }
        }
    }
}
