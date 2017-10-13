package eionet.gdem.web.obligations;

import java.util.List;

/**
 *
 *
 */
public interface ObligationDao {

    Obligation insert(Obligation project);
    Obligation findById(Integer id);
    Obligation update(Obligation project);
    void delete(Obligation project);
    List<Obligation> findAll();
}
