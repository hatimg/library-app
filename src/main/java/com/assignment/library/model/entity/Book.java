package com.assignment.library.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @Column(nullable = false, updatable = false)
    private String isbn;
    @Column(nullable = false, updatable = false)
    private String title;
    @Column(nullable = false, updatable = false)
    private String author;
    @Column(nullable = false, updatable = false)
    private int publicationYear;
    @Column(nullable = false)
    private int availableCopies;

    /** This field is used for handling concurrency */
    @Version
    @Column(nullable = false)
    @ColumnDefault("0")
    private Long version;
}
