//package org.firstinspires.ftc.teamcode;
//
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode; // This example is for a LinearOpMode, though a similar idea applies to regular OpModes.
//import org.firstinspires.ftc.teamcode.controllers.PIDFController; // This may vary depending on what implementation you are using.
//
//@TeleOp
//public class PIFINTOTHEDEEPTEST extends LinearOpMode {
//
//    // This line creates a PIDF controller named examplePIDF that has coefficients of:
//    // kP = 0
//    // kI = 0
//    // kD = 0
//    // kF = 0
//    private PIDFController examplePIDF = new PIDFController(0, 0, 0, 0);
//
//    // This line creates a PID controller named examplePID that has coefficients of:
//    // kP = 0
//    // kI = 0
//    // kD = 0
//
//    @Override
//    public void runOpMode() {
//        // Put all of your initialization here.
//        DcMotor slides = hardwareMap.dcMotor.get("slides");
//        waitForStart();
//
//        int targetPosition = 500;
//
//        // We will use this variable to determine if we want the PIDF to run.
//        boolean usePIDF = false;
//
//        Gamepad lastGamepad1 = new Gamepad();
//        Gamepad lastGamepad2 = new Gamepad();
//
//        while (opModeIsActive()) {
//
//            // This is a rising-edge detector that runs if and only if "a" was pressed this loop.
//            if (gamepad1.a && !lastGamepad1.a) {
//                usePIDF = true;
//            }
//
//
//            if (gamepad1.left_trigger > 0) {
//                slides.setPower(gamepad1.left_trigger);
//
//                // If we get any sort of manual input, turn PIDF off.
//                usePIDF = false;
//            } else if (gamepad1.right_trigger > 0) {
//                slides.setPower(gamepad1.right_trigger);
//
//                // If we get any sort of manual input, turn PIDF off.
//                usePIDF = false;
//            } else if (usePIDF) {
//                // Sets the slide motor power according to the PIDF output.
//                slides.setPower(examplePIDF.calculate(slides.getCurrentPosition(), targetPosition));
//            }
//        }
//    }
//}
//
