import java.io.File;
import java.io.IOException;

public interface SentenceGenerator {

    public String generateSentence(String query) throws IOException;

    public void setCollatedData(File fileDirectory) throws IOException;
}
