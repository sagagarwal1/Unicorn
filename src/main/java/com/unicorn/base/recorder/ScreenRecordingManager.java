package com.unicorn.base.recorder;

import com.unicorn.base.logger.Logger;
import com.unicorn.utils.CommonUtils;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

/**
 * ScreenRecordingManager provides the functioning to start, save and stop the screen recording per test case.
 *
 */

public class ScreenRecordingManager {
    public static String testName = "";
    private static String recordingName = null;
    public static boolean isTestFailed;
    private static File file;
    private ScreenRecorder screenRecorder;

    /**
     * This will start screen recording before test case execution start
     *
     * @throws IOException
     * @throws AWTException
     * @throws Exception
     */
    public void startRecording() throws IOException, AWTException {
        Logger.info("Starting recording");
            String screenRecorderPath = "target" + File.separator + "screenrecordings";

            file = new File(screenRecorderPath + File.separator);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int width = screenSize.width;
            int height = screenSize.height;
            Rectangle captureSize = new Rectangle(0, 0, width, height);
            GraphicsConfiguration gc = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();


            this.screenRecorder = new SpecializedScreenRecorder(gc, captureSize,
                    new org.monte.media.Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                    new org.monte.media.Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                            QualityKey, 1.0f,
                            KeyFrameIntervalKey, 15 * 60),
                    new org.monte.media.Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black",
                            FrameRateKey, Rational.valueOf(30)),
                    null, file, recordingName);
            this.screenRecorder.start();
    }

    /**
     * Stop screen recorder once test case execution done
     */
    public void stopRecording() {
        testName = testName.replaceAll("\\s+", "_") + "_" + CommonUtils.getCurrentTimeStamp();
            Logger.info("Stopping recording");
            try {
                if (this.screenRecorder != null)
                    this.screenRecorder.stop();
                else
                    Logger.debug("ScreenRecorder instance is null !!. Not stopping it");

                File createdRecordingFile = new File(file + File.separator + recordingName + ".avi");
                File recordedFileClassDirectory = new File(file.getParentFile().toString());

                if (!isTestFailed) {
                    if (createdRecordingFile.exists()) {
                        if (!createdRecordingFile.delete())
                            Logger.error("Failed to delete recording file: " + file + File.separator + recordingName);
                    }

                    if (recordedFileClassDirectory.exists() && CommonUtils.isDirEmpty(recordedFileClassDirectory.toPath())) {
                        if (!recordedFileClassDirectory.delete())
                            Logger.error("Failed to delete directory: " + recordedFileClassDirectory.getPath());
                    }
                } else {
                    Logger.info("Saving recording file...");
                    File recordingFile = new File("target" + File.separator + "screenrecordings" + File.separator + testName + ".avi");
                    if (!createdRecordingFile.renameTo(recordingFile)) {
                        Logger.error("Failed to rename recording file: " + file.getName() + " To " + testName + ".avi");
                    } else
                        Logger.info("Recording saved to: " + recordingFile);
                }
                testName = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
