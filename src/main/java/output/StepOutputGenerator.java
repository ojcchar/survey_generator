package output;

import graph.AppGuiComponent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import others.GeneralUtils;
import others.UtilReporter;
import s2rquality.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StepOutputGenerator extends HTMLOutputGenerator {

    private static void generateStepReportForOneBugReport() {
        StepOutputGenerator generator = new StepOutputGenerator();
        try {
            File bugFolder = new File("/Users/mdipenta/euler-data/ATimeTracker#0.20_46");
            generator.generateOutput(bugFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void generateOutput(File bugFolder, BRQualityReport qualityReport) throws Exception {

        //File outputFile = Paths.get(bugFolder.getAbsolutePath(), bugFolder.getName() + "_STEPS.html").toFile();

        String feedbackTemplate = FileUtils.readFileToString(new File("html_template/row_feedback.html"),
                Charset.defaultCharset());
        String rowTableTemplate = FileUtils.readFileToString(new File("html_template/row_table.html"),
                Charset.defaultCharset());
        String htmlTemplate = FileUtils.readFileToString(new File("html_template/step_template.html"),
                Charset.defaultCharset());

//        final String parentFolder = outputFile.getParent();
//        final File imgsFolder = Paths.get(parentFolder, "html_imgs").toFile();
//        if (!imgsFolder.exists()) {
//            final boolean folderCreated = imgsFolder.mkdirs();
//            if (!folderCreated) throw new Exception("Could not create the imgs folder: " + imgsFolder);
//        }

        List<String> parameters = new ArrayList<>();
        //parameters.add(getApplicationName(qualityReport));
        //parameters.add(qualityReport.getAppVersion());
        //parameters.add(qualityReport.getBugReport().getId());
        //parameters.add(qualityReport.getBugReport().getTitle());
        //parameters.add(getHTMLBugDescription(qualityReport));
        getTableInfo(bugFolder, qualityReport.getS2RQualityFeedback(), htmlTemplate, rowTableTemplate, feedbackTemplate);


        //String finalReport = GeneralUtils.replaceHTML(htmlTemplate, parameters);
        //FileUtils.write(outputFile, finalReport, Charset.defaultCharset());

    }

    private String getHTMLBugDescription(BRQualityReport qualityReport) {
        if (qualityReport.getBugReport().getDescription() == null) return "";
        return qualityReport.getBugReport().getDescription().trim()
                .replace("\n", "</br>");
    }

    private void getTableInfo(File bugFolder,
                                List<S2RQualityFeedback> s2rQualityFeedback, String htmlTemplate, String rowTableTemplate,
                                String feedbackTemplate) throws IOException {
        //StringBuilder tableInfo = new StringBuilder();

        int sequence = 1;
        List<String> parameters;
        for (S2RQualityFeedback s2r : s2rQualityFeedback) {

            parameters = new ArrayList<>();
            //parameters.add(sequence + "");
            getFeedbackInfo(bugFolder,
                    sequence,
                    UtilReporter.getActionString(s2r.getAction(), false, false, false),
                    s2r.getQualityAssessments(), htmlTemplate, feedbackTemplate, rowTableTemplate, s2r.getAction());

            //tableInfo.append(GeneralUtils.replaceHTML(rowTableTemplate, parameters));
            //tableInfo.append(myTab);

            sequence++;
        }

        //return tableInfo.toString();
    }


    protected void getFeedbackInfo(File bugFolder,
                                     int sequence,
                                     String actionString, List<S2RQualityAssessment> qualityAssessments,
                                     String htmlTemplate, String feedbackTemplate, String rowTableTemplate,
                                      NLAction action) throws IOException {


        /*if (qualityAssessments == null || qualityAssessments.isEmpty())
            return "The quality assessment for this S2R is not available.";*/

        List<String> parameters;
        List<String> parametersAll;
        List<String> parametersTable;


        int assess=0;
        for (S2RQualityAssessment feedback : qualityAssessments) {
            //StringBuilder tableInfo = new StringBuilder();
            StringBuilder feedbackInfo = new StringBuilder();
            assess++;
            File outputFile = Paths.get(bugFolder.getAbsolutePath(), bugFolder.getName() + "_Action_"+sequence+"_Feed_"+assess+".html").toFile();
            final String parentFolder = outputFile.getParent();
            final File imgsFolder = Paths.get(parentFolder, "html_imgs").toFile();
            if (!imgsFolder.exists()) {
                final boolean folderCreated = imgsFolder.mkdirs();
                if (!folderCreated) throw new IOException("Could not create the imgs folder: " + imgsFolder);
            }



            parametersTable=new ArrayList<>();
            parametersAll=new ArrayList<>();
            parameters = new ArrayList<>();

            final S2RQualityCategory category = feedback.getCategory();
            parameters.add(getFeedbackStyle(category));
            parameters.add(category == null ? "" : category.getCode());
            parameters.add(getCategoryStatement(feedback, action));
            parameters.add(getItemsFeedback(feedback, imgsFolder));
            //feedbackInfo.append());

            parametersAll.add(sequence+"");
            parametersAll.add(actionString);
            parametersAll.add(GeneralUtils.replaceHTML(feedbackTemplate, parameters));
           
            feedbackInfo.append(GeneralUtils.replaceHTML(rowTableTemplate, parametersAll));


            //tableInfo.append(GeneralUtils.replaceHTML(rowTableTemplate, parameters));
            parametersTable.add(feedbackInfo.toString());

            String finalReport = GeneralUtils.replaceHTML(htmlTemplate, parametersTable);
            FileUtils.write(outputFile, finalReport, Charset.defaultCharset());

        }

        //return feedbackInfo.toString();
    }





    public static void main(String[] args) throws Exception {

        //generatQualityReportForAllBugReports();

        generateStepReportForOneBugReport();
    }

}
