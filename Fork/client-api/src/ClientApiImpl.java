import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

public class ClientApiImpl implements ClientApi{

    private SSpaceClientApi sSpaceClientApi = new SSpaceClientApi();
    private DataCollector dataCollector = new DataCollectorImpl();
    FileAnalyser fileAnalyser = new FileAnalyserImpl(sSpaceClientApi, dataCollector);
    SentenceGenerator sentenceGenerator = new SentenceGeneratorImpl(sSpaceClientApi, dataCollector);
    private Log logger = LogFactory.getLog(ClientApiImpl.class);

    @Override
    public void loadSSpaceFile() throws IOException, InterruptedException {
        if(!isSSpaceIsLoaded()) {
            System.out.println("Loading Semantic Space, please wait");
            analyseTrainingData();
        }
        loadFile();
    }

    private void loadFile() throws IOException {
        File outputDirectory = new File("output");
        for(File file : outputDirectory.listFiles()) {
            if(file.getName().contains(".sSpace")) {
                logger.info("Reading sSpace file:" + file.getName());
                sSpaceClientApi.readSpace(file);
                break;
            }
        }
    }

    public void analyseTrainingData() throws IOException, InterruptedException {
        File trainingFileDirectory = new File("trainingData");
        fileAnalyser.generateSSpace(trainingFileDirectory);
    }


    private boolean isSSpaceIsLoaded() {
        return fileAnalyser.checkIfSSpaceGenerated();
    }

    public File loadUserData(String directory) throws Exception {
        File userFileDirectory = new File(directory);
        if(!userFileDirectory.isDirectory()) {
            throw new Exception("The specified directory does not exist");
            }
        return userFileDirectory;
    }

    @Override
    public String generateSentence(String queryTerm) throws Exception {
        return sentenceGenerator.generateSentence(queryTerm);

    }

    @Override
    public void readCollatedData(String userDirectory) throws Exception {
        File userDirectoryFile = loadUserData(userDirectory);
        sentenceGenerator.setCollatedData(userDirectoryFile);
    }
}
