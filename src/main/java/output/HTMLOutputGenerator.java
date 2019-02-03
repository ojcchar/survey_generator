package output;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import graph.AppGuiComponent;
import graph.AppStep;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import others.AppNamesMappings;
import others.DeviceUtils;
import others.GeneralUtils;
import others.UtilReporter;
import s2rquality.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HTMLOutputGenerator extends EulerOutputGenerator {

    public final Gson gson = new GsonBuilder().setPrettyPrinting()
            .addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    final Expose annotation = f.getAnnotation(Expose.class);
                    if (annotation == null) return false;

                    return !annotation.serialize();
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .create();

    @Override
    public void generateOutput(File bugFolder) throws Exception {
        File jsonReportFile = Paths.get(bugFolder.getAbsolutePath(),
                bugFolder.getName() + ".json").toFile();
        BRQualityReport report = gson.fromJson(new FileReader(jsonReportFile),
                BRQualityReport.class);
        generateOutput(bugFolder, report);
    }

    @Override
    public void generateOutput(File bugFolder, BRQualityReport qualityReport) throws Exception {

        File outputFile = Paths.get(bugFolder.getAbsolutePath(), bugFolder.getName() + ".html").toFile();

        String feedbackTemplate = FileUtils.readFileToString(new File("html_template/row_feedback.html"),
                Charset.defaultCharset());
        String rowTableTemplate = FileUtils.readFileToString(new File("html_template/row_table.html"),
                Charset.defaultCharset());
        String htmlTemplate = FileUtils.readFileToString(new File("html_template/template.html"),
                Charset.defaultCharset());

        final String parentFolder = outputFile.getParent();
        final File imgsFolder = Paths.get(parentFolder, "html_imgs").toFile();
        if (imgsFolder.exists()) {
            FileUtils.forceDelete(imgsFolder);
        }
        final boolean folderCreated = imgsFolder.mkdirs();
        if (!folderCreated) throw new Exception("Could not create the imgs folder: " + imgsFolder);

        List<String> parameters = new ArrayList<>();
        parameters.add(getApplicationName(qualityReport));
        parameters.add(qualityReport.getAppVersion());
        parameters.add(qualityReport.getBugReport().getId());
        parameters.add(qualityReport.getBugReport().getTitle());
        parameters.add(getHTMLBugDescription(qualityReport));
        parameters.add(getTableInfo(qualityReport.getS2RQualityFeedback(), rowTableTemplate, feedbackTemplate,
                imgsFolder));


        String finalReport = GeneralUtils.replaceHTML(htmlTemplate, parameters);
        FileUtils.write(outputFile, finalReport, Charset.defaultCharset());

    }

    private String getApplicationName(BRQualityReport qualityReport) {
        return AppNamesMappings.normalizeAppName(qualityReport.getAppName());
    }

    private String getHTMLBugDescription(BRQualityReport qualityReport) {
        if (qualityReport.getBugReport().getDescription() == null) return "";
        return qualityReport.getBugReport().getDescription().trim()
                .replace("\n", "</br>");
    }

    private String getTableInfo(List<S2RQualityFeedback> s2rQualityFeedback, String rowTableTemplate,
                                String feedbackTemplate, File imgsFolder) throws IOException {
        StringBuilder tableInfo = new StringBuilder();

        int sequence = 1;
        List<String> parameters;
        for (S2RQualityFeedback s2r : s2rQualityFeedback) {
            parameters = new ArrayList<>();
            parameters.add(sequence + "");
            parameters.add(UtilReporter.getActionString(s2r.getAction(), false, false, false));
            parameters.add(getFeedbackInfo(s2r.getQualityAssessments(), feedbackTemplate, imgsFolder, s2r.getAction()));

            tableInfo.append(GeneralUtils.replaceHTML(rowTableTemplate, parameters));

            sequence++;
        }

        return tableInfo.toString();
    }

    protected String getFeedbackInfo(List<S2RQualityAssessment> qualityAssessments,
                                     String feedbackTemplate,
                                     File imgsFolder, NLAction action) throws IOException {
        StringBuilder feedbackInfo = new StringBuilder();

        if (qualityAssessments == null || qualityAssessments.isEmpty())
            return "The quality assessment for this S2R is not available.";

        List<String> parameters;
        for (S2RQualityAssessment feedback : qualityAssessments) {
            parameters = new ArrayList<>();

            final S2RQualityCategory category = feedback.getCategory();
            parameters.add(getFeedbackStyle(category));
            parameters.add(category == null ? "" : category.getCode());
            parameters.add(getCategoryStatement(feedback, action));
            parameters.add(getItemsFeedback(feedback, imgsFolder));
            feedbackInfo.append(GeneralUtils.replaceHTML(feedbackTemplate, parameters));
        }

        return feedbackInfo.toString();
    }

    protected String getCategoryStatement(S2RQualityAssessment feedback, NLAction action) {
        final S2RQualityCategory category = feedback.getCategory();
        if (category == null) return "";
        switch (feedback.getCategory()) {
            case LOW_Q_AMBIGUOUS: {
                final List<AppGuiComponent> components = feedback.getAmbiguousComponents();
                final List<String> actions = feedback.getAmbiguousActions();

                final String preFix = "This S2R matches multiple ";

                if (components != null && actions != null) {
                    final String assessmentTemplate = preFix + "GUI components (e.g., %s) and multiple actions " +
                            "(e.g., %s)";
                    return String.format(assessmentTemplate, getComponentsString(components),
                            getActionsString(actions));
                } else if (components != null) {
                    final String assessmentTemplate = preFix + "GUI components (e.g., %s)";
                    return String.format(assessmentTemplate, getComponentsString(components));
                } else {
                    final String assessmentTemplate = preFix + "actions (e.g., %s)";
                    return String.format(assessmentTemplate, getActionsString(actions));
                }

            }
            case LOW_Q_VOCAB_MISMATCH: {

                if (feedback.isObjsVocabMismatch() && feedback.isVerbVocabMismatch())
                    return "The vocabulary of this S2R does not match the vocabulary of the app.";
                else if (feedback.isVerbVocabMismatch())
                    return String.format("The term \"%s\" does not match an action performed on the app.",
                            action.getAction());
                else
                    return String.format("The term \"%s\" does not match a GUI component from the" +
                                    " app.",
                            getObjs(action));
            }
            default:
                return category.getDescription();
        }

    }

    private String getActionsString(List<String> actions) {
        return actions.stream().map(s -> "\"" + s.trim() + "\"").collect(Collectors.joining(" or "));
    }

    private String getComponentsString(List<AppGuiComponent> components) {
        int limit = 3;
        if (components.size() < limit)
            limit = components.size();
        return components.subList(0, limit).stream()
                .map(c -> "the " + UtilReporter.getComponentDescription(c))
                .collect(Collectors.joining(" or "));
    }

    protected String getObjs(NLAction action) {
        StringBuilder builder = new StringBuilder();
        builder.append(StringUtils.isEmpty(action.getObject()) ? "" : action.getObject());
        builder.append(" ");
        builder.append(StringUtils.isEmpty(action.getPreposition()) ? "" : action.getPreposition());
        builder.append(" ");
        builder.append(StringUtils.isEmpty(action.getObject2()) ? "" : action.getObject2());
        return builder.toString().trim();
    }

    protected String getItemsFeedback(S2RQualityAssessment feedback, File imgsFolder) throws IOException {
        StringBuilder items = new StringBuilder();

        if (feedback.getCategory() == null) return items.toString();

        switch (feedback.getCategory()) {
            case HIGH_QUALITY:
                addSteps(feedback.getMatchedSteps(), items, imgsFolder);
                break;
            /*case LOW_Q_AMBIGUOUS:
                addAmbiguousCases(feedback.getAmbiguousCases(), items);
                break;*/
         /*   case LOW_Q_COMPOSITE:
                addSteps(feedback.getMatchedSteps(), items, imgsFolder);
                addSteps(feedback.getInferredSteps(), items, imgsFolder);
                break;*/
            case LOW_Q_INCORRECT_INPUT:
                items.append("<li>").append(feedback.getInputValue()).append("</li>");
                break;
            case LOW_Q_VOCAB_MISMATCH:
                break;
            case MISSING:
                final List<AppStep> inferredSteps = feedback.getInferredSteps();
                final List<AppStep> cleanedInferredSteps = cleanSteps(inferredSteps);
                addSteps(cleanedInferredSteps, items, imgsFolder);
                break;
            default:
                break;
        }
        return items.toString();
    }

    private List<AppStep> cleanSteps(List<AppStep> steps) {

        if (steps == null) return null;
        if (steps.isEmpty()) return steps;

        List<AppStep> cleanedSteps = new ArrayList<>();
        for (int i = 0; i < steps.size(); ) {
            final AppStep appStep = steps.get(i);
            AppStep nextStep = null;
            if ((i + 1) < steps.size()) {
                nextStep = steps.get(i + 1);
            }

            if (nextStep != null) {

                AppStep nextNextStep = null;
                if ((i + 2) < steps.size()) {
                    nextNextStep = steps.get(i + 2);
                }

                if (nextNextStep != null) {
                    if (
                            (DeviceUtils.isType(nextStep.getAction()) &&
                                    DeviceUtils.isClick(appStep.getAction()) &&
                                    DeviceUtils.isClick(nextNextStep.getAction())
                            ) &&
                                    (appStep.getComponent().getDbId().equals(nextStep.getComponent().getDbId())
                                            && nextStep.getComponent().getDbId().equals(nextNextStep.getComponent().getDbId()))
                    ) {

                        cleanedSteps.add(nextStep);
                        i = i+3;
                        continue;
                    }
                }
            }

            cleanedSteps.add(appStep);

            i++;

        }
        return cleanedSteps;
    }

    private void addAmbiguousCases(List<String> cases, StringBuilder items) {
        if (cases == null) {
            return;
        }

        for (String caseElement : cases) {
            items.append("<li>");
            items.append(caseElement);
            items.append("</li>");
        }
    }

    private void addSteps(List<AppStep> steps, StringBuilder items, File imgsFolder) throws IOException {
        if (steps == null) {
            return;
        }

        int stepId = 1;
        for (AppStep step : steps) {
            final String screenshotFilePath = step.getScreenshotFile();
            items.append("<li>");
            if (StringUtils.isEmpty(screenshotFilePath) || DeviceUtils.isClickMenuButton(step.getAction()) ||
                    DeviceUtils.isClickBackButton(step.getAction()) || DeviceUtils.isChangeRotation(step.getAction())) {
                items.append(stepId + ". " + UtilReporter.getNLStep(step, false));
            } else {
                File screenshotFile = new File(screenshotFilePath);
                if (!screenshotFile.exists()) {
                    screenshotFile = Paths.get(EulerParameters.CRASHSCOPE_SCREENSHOTS_PATH,
                            screenshotFilePath).toFile();
                }

                if (!screenshotFile.exists()) {
                    LOGGER.warn("Screenshot file does not exist: " + screenshotFile);
                    items.append(stepId + ". " + UtilReporter.getNLStep(step, false));
                } else {
                    File destFile = Paths.get(imgsFolder.getAbsolutePath(), screenshotFile.getName()).toFile();
                    FileUtils.copyFile(screenshotFile, destFile);

                    items.append("<div class=\"pop step\" href=\"\" step=\"")
                            .append(step.getSequence())
                            .append("\" data =\"")
                            .append(Paths.get(imgsFolder.getName(), destFile.getName()).toString())
                            .append("\">")
                            .append(stepId + ". " + UtilReporter.getNLStep(step, false))
                            .append("</div>");
                }
            }
            items.append("</li>");
            stepId++;
        }
    }

    /**
     * @param category
     * @return
     */
    protected String getFeedbackStyle(S2RQualityCategory category) {
        String style = "";

        if (category == null) return style;

        switch (category) {
            case HIGH_QUALITY:
                style = "success";
                break;
            case LOW_Q_AMBIGUOUS:
                style = "primary";
                break;
           /* case LOW_Q_COMPOSITE:
                style = "danger";
                break;*/
            case LOW_Q_INCORRECT_INPUT:
                style = "grey";
                break;
            case LOW_Q_VOCAB_MISMATCH:
                style = "info";
                break;
            case MISSING:
                style = "warning";
                break;
            default:
                style = "";
                break;
        }
        return style;
    }

    public static void main(String[] args) throws Exception {

        //generatQualityReportForAllBugReports();

        generateQualityReportForOneBugReport();
    }

    private static void generatQualityReportForAllBugReports() throws Exception {
        String outFolder = "/Users/mdipenta/euler-data/tool-output/";
        HTMLOutputGenerator generator = new HTMLOutputGenerator();

        final File[] subFolders = new File(outFolder).listFiles(File::isDirectory);
        for (File bugFolder : subFolders) {
            File jsonReportFile = Paths.get(bugFolder.getAbsolutePath(),
                    bugFolder.getName() + ".json").toFile();
            if (jsonReportFile.exists()) {
                LOGGER.debug(jsonReportFile.getAbsolutePath());

                System.out.println("ciao");
                generator.generateOutput(bugFolder);
            }
        }
    }

    private static void generateQualityReportForOneBugReport() {
        HTMLOutputGenerator generator = new HTMLOutputGenerator();
        try {
//            File bugFolder = new File("/Users/mdipenta/euler-data/ATimeTracker#0.20_46");
            File bugFolder = new File("/Users/mdipenta/euler-data/ATimeTracker#0.20_46");
            generator.generateOutput(bugFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
