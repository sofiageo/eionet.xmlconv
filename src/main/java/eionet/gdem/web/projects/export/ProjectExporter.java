package eionet.gdem.web.projects.export;

import eionet.gdem.web.projects.Project;

import java.io.File;

/**
 *
 *
 */
public interface ProjectExporter {

    File export(Project project);
}
