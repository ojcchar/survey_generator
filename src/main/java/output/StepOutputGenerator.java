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
import others.AppNamesMappings;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StepOutputGenerator extends HTMLOutputGenerator {
    protected String feedbackTemplate;
    protected String rowTableTemplate;
    protected String htmlTemplate;
    protected String missingTemplate;
    protected String rowMissingTemplate;
    protected String feedbackMissingTemplate;
    protected String feedbackMissingOverviewTemplate;
    protected String rowListTemplate;
    protected String TableListTemplate;

    protected String Q0Template;
    protected String Q1Template;
    protected String Q2Template;
    protected String QUsabilityTemplate;

    protected static String BugName;
    protected static String Path="/Users/mdipenta/euler-data/tool-output/";

    private static void generateStepReportForOneBugReport() {
        StepOutputGenerator generator = new StepOutputGenerator();
        try {
            File bugFolder = new File(Path+BugName);
            generator.generateOutput(bugFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void generateOutput(File bugFolder, BRQualityReport qualityReport) throws Exception {

        //File outputFile = Paths.get(bugFolder.getAbsolutePath(), bugFolder.getName() + "_STEPS.html").toFile();
        feedbackTemplate = FileUtils.readFileToString(new File("html_template/row_feedback.html"),
             Charset.defaultCharset());
        rowTableTemplate = FileUtils.readFileToString(new File("html_template/row_table.html"),
                Charset.defaultCharset());
        rowMissingTemplate = FileUtils.readFileToString(new File("html_template/row_table_missing.html"),
                Charset.defaultCharset());
        htmlTemplate = FileUtils.readFileToString(new File("html_template/step_template.html"),
                Charset.defaultCharset());
        missingTemplate = FileUtils.readFileToString(new File("html_template/missing_template.html"),
                Charset.defaultCharset());
        feedbackMissingTemplate = FileUtils.readFileToString(new File("html_template/row_feedback_missing.html"),
                Charset.defaultCharset());

        feedbackMissingOverviewTemplate = FileUtils.readFileToString(new File("html_template/row_feedback_missing_overview.html"),
                Charset.defaultCharset());

        rowListTemplate =FileUtils.readFileToString(new File("html_template/row_list.html"),Charset.defaultCharset());
        TableListTemplate =FileUtils.readFileToString(new File("html_template/table_list.html"),Charset.defaultCharset());

        Q0Template=FileUtils.readFileToString(new File("html_template/Q0_template.html"),Charset.defaultCharset());
        Q1Template=FileUtils.readFileToString(new File("html_template/Q1_template.html"),Charset.defaultCharset());
        Q2Template=FileUtils.readFileToString(new File("html_template/Q2_template.html"),Charset.defaultCharset());
        QUsabilityTemplate=FileUtils.readFileToString(new File("html_template/QUsability_template.html"),Charset.defaultCharset());



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

        generateQ0(bugFolder,qualityReport);
        generateQ1(bugFolder,qualityReport);
        generateQ2(bugFolder,qualityReport);
        generateQUsability(bugFolder,qualityReport);
        generateTableInfo(bugFolder, qualityReport.getS2RQualityFeedback());


        //String finalReport = GeneralUtils.replaceHTML(htmlTemplate, parameters);
        //FileUtils.write(outputFile, finalReport, Charset.defaultCharset());

    }


    protected void generateQ0(File bugFolder, BRQualityReport qualityReport ) throws IOException
    {
        List<String> parameters=new ArrayList<>();

        parameters.add(qualityReport.getBugReport().getId());
        parameters.add(qualityReport.getBugReport().getTitle());
        parameters.add(AppNamesMappings.normalizeAppName(qualityReport.getAppName())+" v. "+qualityReport.getAppVersion());


        String Report=GeneralUtils.replaceHTML(Q0Template, parameters);
        File outputFile = Paths.get(bugFolder.getAbsolutePath(), bugFolder.getName() + "_Q0.html").toFile();
        FileUtils.write(outputFile, Report, Charset.defaultCharset());
    }

    protected void generateQ1(File bugFolder, BRQualityReport qualityReport ) throws IOException
    {
        List<String> parameters=new ArrayList<>();

        String myBugName=BugName.replaceAll("\\#","%23");
        parameters.add(myBugName);

        String Report=GeneralUtils.replaceHTML(Q1Template, parameters);
        File outputFile = Paths.get(bugFolder.getAbsolutePath(), bugFolder.getName() + "_Q1.html").toFile();
        FileUtils.write(outputFile, Report, Charset.defaultCharset());
    }


    protected void generateQ2(File bugFolder, BRQualityReport qualityReport ) throws IOException
    {
        List<String> parameters=new ArrayList<>();

        String myBugName=BugName.replaceAll("\\#","%23");
        parameters.add(myBugName);

        String Report=GeneralUtils.replaceHTML(Q2Template, parameters);
        File outputFile = Paths.get(bugFolder.getAbsolutePath(), bugFolder.getName() + "_Q2.html").toFile();
        FileUtils.write(outputFile, Report, Charset.defaultCharset());
    }

    protected void generateQUsability(File bugFolder, BRQualityReport qualityReport ) throws IOException
    {
        List<String> parameters=new ArrayList<>();

        String myBugName=BugName.replaceAll("\\#","%23");
        parameters.add(myBugName);

        String Report=GeneralUtils.replaceHTML(QUsabilityTemplate, parameters);
        File outputFile = Paths.get(bugFolder.getAbsolutePath(), bugFolder.getName() + "_QUsability.html").toFile();
        FileUtils.write(outputFile, Report, Charset.defaultCharset());
    }

    private String getHTMLBugDescription(BRQualityReport qualityReport) {
        if (qualityReport.getBugReport().getDescription() == null) return "";
        return qualityReport.getBugReport().getDescription().trim()
                .replace("\n", "</br>");
    }

    private void generateTableInfo(File bugFolder,
                                List<S2RQualityFeedback> s2rQualityFeedback) throws IOException {
        //StringBuilder tableInfo = new StringBuilder();

        int sequence = 1;
        List<String> parameters;
        List<String> steps=new ArrayList<>();
        for (S2RQualityFeedback s2r : s2rQualityFeedback) {

            //parameters = new ArrayList<>();
            //parameters.add(sequence + "");
            steps.add(UtilReporter.getActionString(s2r.getAction(), false, false, false));
            generateFeedbackInfo(steps,bugFolder,
                    sequence,
                    UtilReporter.getActionString(s2r.getAction(), false, false, false),
                    s2r.getQualityAssessments(), s2r.getAction());

            generateMissingInfo(steps,bugFolder,
                    sequence,
                    UtilReporter.getActionString(s2r.getAction(), false, false, false),
                    s2r.getQualityAssessments(), s2r.getAction());

            //tableInfo.append(GeneralUtils.replaceHTML(rowTableTemplate, parameters));
            //tableInfo.append(myTab);
            generateSequence(steps,bugFolder,sequence);

            sequence++;
        }

        //return tableInfo.toString();
    }



    protected void generateSequence(List<String> steps, File bugFolder, int sequence) throws IOException
    {

        List<String> parameters;
        List<String> parametersTable;
        StringBuilder rowBuilder=new StringBuilder();
        String tableReport;

        parametersTable=new ArrayList<>();

        for(int x=1;x<=sequence;x++)
        {
            parameters=new ArrayList<>();
            if(x<sequence) {
                //parameters.add(""+x);
                parameters.add(""+x+". "+steps.get(x-1));
            }
            else
            {
                parameters.add("<b>"+x+". "+steps.get(x-1)+"</b>");
            }

            rowBuilder.append(GeneralUtils.replaceHTML(rowListTemplate, parameters));
        }
        parametersTable.add(""+sequence);
        parametersTable.add(rowBuilder.toString());
        String myBugName=BugName.replaceAll("\\#","%23");
        parametersTable.add(myBugName);
        tableReport=GeneralUtils.replaceHTML(TableListTemplate, parametersTable);

        File outputFile = Paths.get(bugFolder.getAbsolutePath(), bugFolder.getName() + "_List_" + sequence  + ".html").toFile();
        FileUtils.write(outputFile, tableReport, Charset.defaultCharset());


    }



    protected void generateFeedbackInfo(List<String> steps, File bugFolder,
                                     int sequence,
                                     String actionString, List<S2RQualityAssessment> qualityAssessments,
                                      NLAction action) throws IOException {


        /*if (qualityAssessments == null || qualityAssessments.isEmpty())
            return "The quality assessment for this S2R is not available.";*/

        List<String> parameters;
        List<String> parametersAll;
        List<String> parametersTable;


        int assess = 0;

        for (S2RQualityAssessment feedback : qualityAssessments) {


            if (feedback.getCategory() != S2RQualityCategory.MISSING) {
                //StringBuilder tableInfo = new StringBuilder();
                StringBuilder feedbackInfo = new StringBuilder();
                assess++;
                File outputFile = Paths.get(bugFolder.getAbsolutePath(), bugFolder.getName() + "_Action_" + sequence + "_Feed_" + assess + ".html").toFile();
                final String parentFolder = outputFile.getParent();
                final File imgsFolder = Paths.get(parentFolder, "html_imgs").toFile();
                if (!imgsFolder.exists()) {
                    final boolean folderCreated = imgsFolder.mkdirs();
                    if (!folderCreated) throw new IOException("Could not create the imgs folder: " + imgsFolder);
                }

//

                parametersTable = new ArrayList<>();
                parametersAll = new ArrayList<>();
                parameters = new ArrayList<>();

                final S2RQualityCategory category = feedback.getCategory();
                parameters.add(getFeedbackStyle(category));
                parameters.add(category == null ? "" : category.getCode());
                parameters.add(getCategoryStatement(feedback, action));
                parameters.add(getItemsFeedback(feedback, imgsFolder));
                //feedbackInfo.append());

                List<String> parametersPrevious;
                for (int i = 1; i < sequence; i++) {
                    parametersPrevious = new ArrayList<>();
                    parametersPrevious.add(i + " ");
                    parametersPrevious.add(steps.get(i - 1));
                    parametersPrevious.add("");
                    feedbackInfo.append(GeneralUtils.replaceHTML(rowTableTemplate, parametersPrevious));

                }
                parametersAll.add("<b>" + sequence + "</b> ");
                parametersAll.add("<b>" + actionString + "</b>");
                parametersAll.add("<b>" + GeneralUtils.replaceHTML(feedbackTemplate, parameters) + "</b>");

                feedbackInfo.append(GeneralUtils.replaceHTML(rowTableTemplate, parametersAll));


                //tableInfo.append(GeneralUtils.replaceHTML(rowTableTemplate, parameters));
                parametersTable.add("" + sequence);
                parametersTable.add(feedbackInfo.toString());
                String myBugName=BugName.replaceAll("\\#","%23");
                parametersTable.add(myBugName);

                String finalReport = GeneralUtils.replaceHTML(htmlTemplate, parametersTable);
                FileUtils.write(outputFile, finalReport, Charset.defaultCharset());
            }


            //return feedbackInfo.toString();
        }
    }



    protected void generateMissingInfo(List<String> steps, File bugFolder,
                                   int sequence,
                                   String actionString, List<S2RQualityAssessment> qualityAssessments,
                                   NLAction action) throws IOException {


        /*if (qualityAssessments == null || qualityAssessments.isEmpty())
            return "The quality assessment for this S2R is not available.";*/

        List<String> parameters;
        List<String> parametersOverview;
        List<String> parametersAll;
        List<String> parametersTable;
        List<String> parametersTableOverview;
        List<String> parametersAllOverview;


        int assess=0;
        int numMissing=0;

        StringBuilder feedbackInfo = new StringBuilder();
        StringBuilder feedbackInfoOverview = new StringBuilder();


        File outputFile=null;
        File outputFileOverview=null;



        parametersTable = new ArrayList<>();
        parametersTableOverview = new ArrayList<>();

        for (S2RQualityAssessment feedback : qualityAssessments) {

            assess++;


            if(feedback.getCategory()==S2RQualityCategory.MISSING) {
                //StringBuilder tableInfo = new StringBuilder();


                numMissing++;

                outputFile = Paths.get(bugFolder.getAbsolutePath(), bugFolder.getName() + "_Action_" + sequence + "_Missing_" + assess + ".html").toFile();
                outputFileOverview = Paths.get(bugFolder.getAbsolutePath(), bugFolder.getName() + "_Action_" + sequence + "_Feed_" + assess + ".html").toFile();

                final String parentFolder = outputFile.getParent();
                final File imgsFolder = Paths.get(parentFolder, "html_imgs").toFile();
                if (!imgsFolder.exists()) {
                    final boolean folderCreated = imgsFolder.mkdirs();
                    if (!folderCreated) throw new IOException("Could not create the imgs folder: " + imgsFolder);
                }


                parametersAll = new ArrayList<>();
                parameters = new ArrayList<>();
                parametersOverview=new ArrayList<>();
                parametersAllOverview=new ArrayList<>();

                final S2RQualityCategory category = feedback.getCategory();
                parametersOverview.add(getFeedbackStyle(category));
                parametersOverview.add(category == null ? "" : category.getCode());
                parametersOverview.add(getCategoryStatement(feedback, action));
                parameters.add(getItemsFeedback(feedback, imgsFolder));
                //feedbackInfo.append());

                List<String> parametersPrevious;
                for (int i = 1; i < sequence; i++) {
                    parametersPrevious=new ArrayList<>();
                    parametersPrevious.add(i + " ");
                    parametersPrevious.add(steps.get(i-1));
                    parametersPrevious.add("");
                    feedbackInfo.append(GeneralUtils.replaceHTML(rowTableTemplate, parametersPrevious));
                    feedbackInfoOverview.append(GeneralUtils.replaceHTML(rowTableTemplate, parametersPrevious));
                }
                parametersAll.add("<b>"+sequence + "</b> ");
                parametersAll.add("<b>"+actionString+"</b>");
                parametersAll.add("<b>"+GeneralUtils.replaceHTML(feedbackMissingTemplate, parameters)+"</b>");

                parametersAllOverview.add("<b>"+sequence+ "</b> ");
                parametersAllOverview.add("<b>"+actionString+"</b>");
                parametersAllOverview.add("<b>"+GeneralUtils.replaceHTML(feedbackMissingOverviewTemplate, parametersOverview)+"</b>");

                feedbackInfo.append(GeneralUtils.replaceHTML(rowMissingTemplate, parametersAll));
                feedbackInfoOverview.append(GeneralUtils.replaceHTML(rowMissingTemplate, parametersAllOverview));

                //tableInfo.append(GeneralUtils.replaceHTML(rowTableTemplate, parameters));
                parametersTable.add(feedbackInfo.toString());
                String myBugName=BugName.replaceAll("\\#","%23");
                parametersTable.add(myBugName);
                parametersTableOverview.add(""+sequence);
                parametersTableOverview.add(feedbackInfoOverview.toString());
                parametersTableOverview.add(myBugName);

            }

        }


        if(numMissing>0) {
            String finalReport = GeneralUtils.replaceHTML(missingTemplate, parametersTable);
            String OverviewReport = GeneralUtils.replaceHTML(htmlTemplate, parametersTableOverview);

            FileUtils.write(outputFile, finalReport, Charset.defaultCharset());
            FileUtils.write(outputFileOverview, OverviewReport, Charset.defaultCharset());
        }

        //return feedbackInfo.toString();
    }


    private static void generatQualityReportForAllBugReports() throws Exception {
        String outFolder = Path;
        StepOutputGenerator generator = new StepOutputGenerator();

        final File[] subFolders = new File(outFolder).listFiles(File::isDirectory);
        for (File bugFolder : subFolders) {
            File jsonReportFile = Paths.get(bugFolder.getAbsolutePath(),
                    bugFolder.getName() + ".json").toFile();
            if (jsonReportFile.exists()) {
                LOGGER.debug(jsonReportFile.getAbsolutePath());
                String line=bugFolder.toString();
                String pattern ="([^\\/\\\\]+)$";
                Pattern r=Pattern.compile(pattern);
                Matcher m = r.matcher(line);
                if (m.find( )) {
                    BugName=m.group(0);
                    generator.generateOutput(bugFolder);
                }

                //System.out.println(bugFolder.toString());
                //generator.generateOutput(bugFolder);


            }
        }
    }




    public static void main(String[] args) throws Exception {

            generatQualityReportForAllBugReports();

    }

}
