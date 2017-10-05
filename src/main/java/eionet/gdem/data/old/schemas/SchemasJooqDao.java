package eionet.gdem.data.old.schemas;

import eionet.gdem.web.spring.schemas.SchemaMySqlDao;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import static org.jooq.impl.DSL.*;

/**
 *
 *
 */
@Repository
public class SchemasJooqDao implements SchemasDao {

    private DSLContext create;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public SchemasJooqDao(DSLContext context, JdbcTemplate jdbcTemplate) {
        this.create = context;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public String schemaUrl(String schemaId) {
                 String schema = (String) create
                .select(field(SchemaMySqlDao.XML_SCHEMA_FLD))
                .from(table(SchemaMySqlDao.SCHEMA_TABLE))
                .where(field(SchemaMySqlDao.SCHEMA_ID_FLD).eq(schemaId))
                .fetchOne().value1();
                /*
        Schema schema = new Schema();
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                schema.setSchema(resultSet.getString(SchemaMySqlDao.UPL_SCHEMA_FLD));
            }
        });
        return schema.getSchema();*/
        return schema;
    }
}
