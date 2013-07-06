import edu.ucla.sspace.text.Document;
import edu.ucla.sspace.text.StringDocument;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class DataCollectorImpl implements DataCollector {
    private final String DELIMITER = "</S>";
    private final String NOSPACE = "";
    private Log logger = LogFactory.getLog(FileAnalyserImpl.class);

    List<Document> files = new ArrayList<Document>();

    @SuppressWarnings("ConstantConditions")
    public Iterator<Document> readCompleteDirectory(File directory) throws IOException {

        logger.info("Reading files from directory: " + directory.getName());
        Collection<File> allFiles = FileUtils.listFiles(directory, null, true);
        double iterator = 0;
        for (File fileEntry : allFiles) {
            logger.info("Reading file: "+fileEntry.getName());
            files.add(readContentsOfFile(fileEntry));
            iterator++;
            logger.info("Read " + iterator + " files (" +iterator * 100 /allFiles.size() + "%)");
        }
        logger.info("Done reading files");
        return files.iterator();
    }

    private Document readContentsOfFile(File fileEntry) throws FileNotFoundException {
        Scanner in = new Scanner(fileEntry);
        String content = "";
        while(in.hasNext()) {
            //content += replaceTerminatingCharacters(in.nextLine()) + DELIMITER;
            content += in.nextLine();
        }
        return  new StringDocument(content);
    }

    public String getCollatedData(File directory) throws IOException {
        Iterator<Document> files = readCompleteDirectory(directory);
        String collatedText = "";
        while (files.hasNext()) {
            collatedText += replaceTerminatingCharacters(files.next().toString());
        }
        return collatedText;
    }

    @Override
    public boolean checkSSpaceFile() {
        logger.info("Checking output directory for SSpace File");

        File sSpaceDirectory = new File("output");
        try {
            for (final File fileEntry : sSpaceDirectory.listFiles()) {
                if (fileEntry.getName().contains(".sSpace")) {
                    return true;
                }
            }
        }catch (Exception e) {
            return false;
        }
        return false;
    }

    private String replaceTerminatingCharacters(String inputText)
    {
        inputText = replaceWithDelimiter(inputText);
        inputText = replaceWithNoSpace(inputText);
        
        return inputText;
    }

    private String replaceWithNoSpace(String inputText) {
        inputText = inputText.trim();
        inputText = inputText.replace("," , NOSPACE);
        inputText = inputText.replace("\n" , NOSPACE);
        inputText = inputText.replace("\"" , NOSPACE);
        inputText = inputText.replace("-" , NOSPACE);
        inputText = inputText.replace("<", NOSPACE);
        inputText = inputText.replace(">" , NOSPACE);
        inputText = inputText.replace("(" , NOSPACE);
        inputText = inputText.replace(")" , NOSPACE);
        inputText = inputText.replace("=" , NOSPACE);
        
        return inputText;
    }

    private String replaceWithDelimiter(String inputText) {
        inputText = inputText.replace("." , DELIMITER);
        inputText = inputText.replace("!" , DELIMITER);
        inputText = inputText.replace("?" , DELIMITER);
        
        return inputText;
    }

}
