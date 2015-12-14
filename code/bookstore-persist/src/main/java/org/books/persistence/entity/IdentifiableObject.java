package org.books.persistence.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;

@MappedSuperclass
public class IdentifiableObject implements Serializable {

    @TableGenerator(name = "general_sequence",
            table = "GENERAL_SEQUENCE",
            initialValue = 10_000,
            allocationSize = 10
    )

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "general_sequence")
    private Long id;

    public Long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other instanceof IdentifiableObject && Objects.equals(id, ((IdentifiableObject) other).id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "id=" + id + '}';
    }
}
