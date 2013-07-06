import edu.ucla.sspace.text.Document;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public interface DataCollector {

    public Iterator<Document> readCompleteDirectory(File directory) throws IOException;

    public String getCollatedData(File directory) throws IOException;

    public boolean checkSSpaceFile();
}
