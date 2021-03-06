package eionet.gdem.data.projects;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import eionet.gdem.data.obligations.Obligation;
import eionet.gdem.data.schemata.Schema;
import eionet.gdem.data.scripts.Script;
import eionet.gdem.data.transformations.Transformation;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

/**
 *
 */
@Entity
@Table
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(min = 5, max = 50)
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Obligation> obligations;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private Set<Schema> schemata;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private Set<Script> scripts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private Set<Transformation> transformations;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Obligation> getObligations() {
        return this.obligations;
    }

    public void setObligations(Set<Obligation> obligations) {
        this.obligations = obligations;
    }

    public Set<Schema> getSchemata() {
        return schemata;
    }

    public void setSchemata(Set<Schema> schemata) {
        this.schemata = schemata;
    }

    public Set<Script> getScripts() {
        return scripts;
    }

    public void setScripts(Set<Script> scripts) {
        this.scripts = scripts;
    }

    public Set<Transformation> getTransformations() {
        return transformations;
    }

    public void setTransformations(Set<Transformation> transformations) {
        this.transformations = transformations;
    }
}
