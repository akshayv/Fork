import edu.ucla.sspace.text.Document;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Iterator;

public class FileAnalyserImpl implements FileAnalyser {

    private Log logger = LogFactory.getLog(FileAnalyserImpl.class);
    private SSpaceClientApi sSpaceClientApi;
    private DataCollector dataCollector;

    public FileAnalyserImpl(SSpaceClientApi sSpaceClientApi, DataCollector dataCollector) {
        this.sSpaceClientApi = sSpaceClientApi;
        this.dataCollector = dataCollector;
    }

    @Override
    public void generateSSpace(File directory) throws IOException, InterruptedException {
        Iterator<Document> files = dataCollector.readCompleteDirectory(directory);
        sSpaceClientApi.performRandomIndexing(files, new File("output/"+directory.getName()));
    }

    @Override
    public boolean checkIfSSpaceGenerated() {
        return dataCollector.checkSSpaceFile();
    }
}
