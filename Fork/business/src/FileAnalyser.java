
import java.io.File;
import java.io.IOException;
public interface FileAnalyser {

    void generateSSpace(File directory) throws IOException, InterruptedException;

    boolean checkIfSSpaceGenerated();
}
