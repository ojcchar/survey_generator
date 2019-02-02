package output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EulerParameters {


    private static final Logger LOGGER = LoggerFactory.getLogger(EulerParameters.class);

    //general parameters
    public static String SCREENSHOT_PATH = null;
    public static String CRASHSCOPE_SCREENSHOTS_PATH = "C:/Users/ojcch/Documents/Projects/Amadeus/study-data/CS-Data/screenshots";

    //algorithm (default) parameters
    public static Boolean RANDOM_EXPLORATION;
    public static Integer ITERATIONS_THRESHOLD_RANDOM_EXPLORATION;
    public static Integer NUMBER_RANDOM_STEPS;
    public static Integer GRAPH_MAX_DEPTH_CHECK;

    //device server (default) parameters
    public static Boolean VALIDATE_TEXT_INPUTS;
    public static Boolean VALIDATE_CHANGED_SCREEN_ON_LAST_COMMAND_ONLY;
    public static Boolean CAPTURE_SCREENSHOTS;
    public static Long WAIT_MILLIS;
    public static Boolean CHECK_ACTIVITY_AFTER_EVENT;

}
