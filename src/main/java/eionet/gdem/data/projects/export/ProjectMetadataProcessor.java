package eionet.gdem.data.projects.export;

import java.io.IOException;

/**
 *
 *
 */
public interface ProjectMetadataProcessor<T> {
    T deserialize(String data) throws IOException;
}
