import java.io.IOException;

public interface ClientApi {

    public void loadSSpaceFile() throws IOException, InterruptedException;

    public String generateSentence(String queryTerm) throws Exception;

    public void readCollatedData(String userDirectory) throws Exception;
}
