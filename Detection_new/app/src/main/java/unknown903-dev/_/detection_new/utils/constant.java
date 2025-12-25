package alex_j.lab3.detection_new.utils;


public class constant {
    public static final String BROADCAST_DETECTED_ACTIVITY = "activity_intent";

    // The desired time between activity detections. Larger values will result in fewer activity
    // detections while improving battery life. A value of 0 will result in activity detections
    // at the fastest possible rate.
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 1000; // every N seconds
}