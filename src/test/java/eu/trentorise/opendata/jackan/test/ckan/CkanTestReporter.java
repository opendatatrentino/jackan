package eu.trentorise.opendata.jackan.test.ckan;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.trentorise.opendata.jackan.ckan.CkanClient;
import eu.trentorise.opendata.jackan.test.TestConfig;
import eu.trentorise.opendata.traceprov.TraceProvUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.href;
import org.rendersnake.HtmlCanvas;

/**
 * Little app to produce a list of all catalogs listed in
 *
 * @author David Leoni
 */
public class CkanTestReporter {

    private static final Logger logger = Logger.getLogger(CkanTestReporter.class.getName());

    public static final List<String> testNames = ImmutableList.of("getDatasetList");

    private static String ERROR_CLASS = "jackan-error";

    public static Map<String, String> readCatalogsList() {
        InputStream is = CkanTestReporter.class.getClassLoader().getResourceAsStream("ckan-instances-small.txt");

        if (is == null) {
            throw new RuntimeException("Couldn't find file!");
        }

        // catalog url, name
        ImmutableMap.Builder<String, String> catalogsBuilder = ImmutableMap.builder();

        String str;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            boolean readingName = true;
            String name = "";
            while ((str = reader.readLine()) != null) {
                if (readingName) {
                    name = str;
                } else {
                    catalogsBuilder.put(TraceProvUtils.removeTrailingSlash(str), name);
                }

                readingName = !readingName;
            }

        }
        catch (IOException ex) {
            Logger.getLogger(CkanTestReporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                is.close();
            }
            catch (Throwable ignore) {
            }
        }

        return catalogsBuilder.build();
    }

    public static final String REPORT_SUFFIX = "jackan-scan";

    public static void main(String[] args) {

        TestConfig.initLogger();
        TestConfig.initProperties();

        Map<String, String> catalogsNames = readCatalogsList();

        new File("reports").mkdirs();

        List<TestResult> testResults = runTests(catalogsNames);

        String content = renderResults(catalogsNames, testResults);

        saveToFile(new File("reports/" + REPORT_SUFFIX + "-" + new DateTime().toString(ISODateTimeFormat.basicDateTimeNoMillis()) + ".html"), content);
        saveToFile(new File("reports/" + REPORT_SUFFIX + "-latest.html"), content);

    }

    public static List<TestResult> runTests(Map<String, String> catalogNames) {
        Map<String, CkanClient> clients = new HashMap();

        for (Entry<String, String> e : catalogNames.entrySet()) {
            clients.put(e.getKey(), new CkanClient(e.getKey()));
        }

        ImmutableList.Builder<TestResult> results = ImmutableList.builder();

        CkanClientIT ckanTests = new CkanClientIT();

        for (String url : catalogNames.keySet()) {
            Throwable error;
            try {
                ckanTests.testDatasetList(clients.get(url));
                error = null;
            }
            catch (Throwable t) {
                error = t;
            }
            results.add(new TestResult("testDatasetList", catalogNames.get(url), url, error));
        }

        return results.build();
    }

    private static String removeProtocol(URL url) {
        return url.toString().substring(url.getProtocol().length() + 3);
    }

    public static String renderResults(Map<String, String> catalogs, List<TestResult> results) {
        String outputFileContent;
        try {

            HtmlCanvas html = new HtmlCanvas();

            html
                    .html()
                    .head()
                    .title().content("Jackan Test Analysis")
                    //.meta(name("description").add("content","Jackan test anal",false))
                    //.macros().stylesheet("htdocs/style-01.css"))
                    //.render(JQueryLibrary.core("1.4.3"))
                    //.render(JQueryLibrary.ui("1.8.6"))
                    //.render(JQueryLibrary.baseTheme("1.8"))
                    .style()
                    .write("." + ERROR_CLASS + " {color:red}")
                    ._style()
                    ._head()
                    .body()
                    .h1().content("Jackan Report")
                    .table()
                    .tr()
                    .th().write("Catalog name")._th()
                    .th().write("Catalog URL")._th();

            for (String testName : testNames) {
                html.th().write(testName)._th();
            }
            html._tr();

            Iterator<TestResult> resultIterator = results.iterator();

            for (String catalogURL : catalogs.keySet()) {
                URL catURL;
                try {
                    catURL = new URL(catalogURL);
                }
                catch (MalformedURLException ex) {
                    html.tr()
                            .td(class_(ERROR_CLASS)) // todo we are skipping columns....
                            .write("Bad catalog URL: " + catalogURL + " for catalog " + catalogs.get(catalogURL))
                            ._td()
                            ._tr();
                    continue;
                }
                html.tr();
                html.td()
                        .write(catalogs.get(catalogURL))
                        ._td()
                        .td()
                        .a(href(catalogURL))
                        .write(removeProtocol(catURL))
                        ._a()
                        ._td();
                for (String testName : testNames) {
                    TestResult result = resultIterator.next();
                    if (result.passed()) {
                        html.td().write("PASSED")._td();
                    } else {
                        html.td(class_(ERROR_CLASS)).write("ERROR")._td();
                    }
                }
                html._tr();
            }

            html
                    ._table()
                    .br()
                    .b().write("Finished: ")._b()
                    .span().write(new DateTime().toString(DateTimeFormat.shortDateTime()))._span()
                    ._body()
                    ._html();

            outputFileContent = html.toHtml();
        }
        catch (IOException ex) {
            outputFileContent = "HTML generation problem!" + ex;
        }
        return outputFileContent;
    }

    public static void saveToFile(File outputFile, String content) {
        PrintWriter out;
        try {
            out = new PrintWriter(outputFile);
            out.write(content);
            out.close();
            logger.info("Report is now available at " + outputFile.getAbsolutePath());
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(CkanTestReporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
