package eionet.gdem.data.projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 */
@Service
@Transactional
public class ProjectService {

    private final ProjectDao dao;

    /**
     * DI constructor
     * @param dao Project DAO
     */
    @Autowired
    ProjectService(ProjectDao dao) {
        this.dao = dao;
    }

    /**
     * Get All Projects and their dependencies
     * @return Projects
     */
    public List<Project> getAllProjects() {
        return dao.getProjectList();
    }

    public void deleteById(Integer id) {
        Project p = dao.read(id);
        dao.delete(p);
    }

    public Project insert(Project p) {
        Project pr = dao.insert(p);
        return pr;
    }

    public Project update(Project p) {
        Project pr = dao.update(p);
        return pr;
    }

    public Project findById(Integer id) {
        return dao.read(id);
    }
}
