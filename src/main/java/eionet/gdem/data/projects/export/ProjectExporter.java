package eionet.gdem.data.projects.export;

import eionet.gdem.data.projects.Project;

import java.io.File;

/**
 *
 *
 */
public interface ProjectExporter {

    File export(Project project);
}
