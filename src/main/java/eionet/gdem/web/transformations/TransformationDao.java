package eionet.gdem.web.transformations;

import java.util.List;

/**
 *
 */
public interface TransformationDao {

    Transformation insert(Transformation transformation);
    Transformation findById(Integer id);
    Transformation update(Transformation transformation);
    void delete(Transformation transformation);
    List<Transformation> findAll();
}
