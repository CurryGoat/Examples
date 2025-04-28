import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class Glossary {
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        System.out.print("enter the C:path to the input file: ");
        String inputFilePath = console.nextLine();
        System.out.print("enter the path to the C: output folder: ");
        String outputFolderPath = console.nextLine();
        Map<String, String> glossary = new TreeMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            String term = null;
            StringBuilder definition = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (term != null && definition.length() > 0) {
                        glossary.put(term, definition.toString().trim());
                        term = null;
                        definition.setLength(0);
                    }
                } else if (term == null) {
                    term = line.trim();
                } else {
                    if (definition.length() > 0) {
                        definition.append(" ");
                    }
                    definition.append(line.trim());
                }
            }
            if (term != null && definition.length() > 0) {
                glossary.put(term, definition.toString().trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }
        generateIndexFile(glossary, outputFolderPath);
        generateTermFiles(glossary, outputFolderPath);
        System.out.println("Glossary HTML files have been generated successfully.");
    }

    /**
     * Generates the index.html file.
     *
     * @param glossary
     *            Map containing terms and definitions.
     * @param outputFolderPath
     *            Path to the output folder.
     */
    private static void generateIndexFile(Map<String, String> glossary,
            String outputFolderPath) {
        File indexFile = new File(outputFolderPath, "index.html");
        try (PrintWriter writer = new PrintWriter(new FileWriter(indexFile))) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head>");
            writer.println("<title>Glossary Index</title>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("<h1>Glossary Index</h1>");
            writer.println("<ul>");
            for (String term : glossary.keySet()) {
                writer.printf("<li><a href=\"%s.html\">%s</a></li>%n", term, term);
            }
            writer.println("</ul>");
            writer.println("</body>");
            writer.println("</html>");
        } catch (IOException e) {
            System.err.println("Error writing index.html: " + e.getMessage());
        }
    }

    /**
     * Generates individual HTML files for each term.
     *
     * @param glossary
     *            Map containing terms and definitions.
     * @param outputFolderPath
     *            Path to the output folder.
     */
    private static void generateTermFiles(Map<String, String> glossary,
            String outputFolderPath) {
        for (Map.Entry<String, String> entry : glossary.entrySet()) {
            String term = entry.getKey();
            String definition = entry.getValue();
            File termFile = new File(outputFolderPath, term + ".html");
            try (PrintWriter writer = new PrintWriter(new FileWriter(termFile))) {
                writer.println("<!DOCTYPE html>");
                writer.println("<html>");
                writer.println("<head>");
                writer.printf("<title>%s</title>%n", term);
                writer.println("</head>");
                writer.println("<body>");
                writer.printf("<h1><i><b style=\"color:red;\">%s</b></i></h1>%n", term);

                String linkedDefinition = createLinkedDefinition(definition,
                        glossary.keySet());
                writer.printf("<p>%s</p>%n", linkedDefinition);

                writer.println("<p><a href=\"index.html\">Back to Index</a></p>");
                writer.println("</body>");
                writer.println("</html>");
            } catch (IOException e) {
                System.err.println("Error writing " + term + ".html: " + e.getMessage());
            }
        }
    }

    /**
     * Converts glossary terms in a definition to hyperlinks.
     *
     * @param definition
     *            The definition to process.
     * @param terms
     *            The set of glossary terms.
     * @return The definition with hyperlinks added.
     */
    private static String createLinkedDefinition(String definition, Set<String> terms) {
        for (String term : terms) {
            String link = String.format("<a href=\"%s.html\">%s</a>", term, term);
            definition = definition.replace(term, link);
        }
        return definition;
    }
}
