package output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import s2rquality.BRQualityReport;

import java.io.File;

public abstract class EulerOutputGenerator {

    protected static final Logger LOGGER = LoggerFactory.getLogger(EulerOutputGenerator.class);

    public abstract void generateOutput(File bugFolder) throws Exception;

    public abstract void generateOutput(File bugFolder, BRQualityReport qualityReport) throws Exception;
}
