package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = {"parent_id", "slug"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String slug; // parte final de una URL que identifica de forma legible una pagina

    @Column(length = 500)
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    //Relacion hacia el padre (nullable para categorias raiz)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    //Coleccion de subcategorias
    @OneToMany(
            mappedBy = "parent",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    @Builder.Default
    private Set<Category> children = new HashSet<>();

    @PrePersist
    public void prePersist()
    {
        this.createdAt = LocalDateTime.now();

        if (this.active == null)
            this.active = true;
    }

    @PreUpdate
    public void preUpdate()
    {
        this.updateAt = LocalDateTime.now();
    }

    //Helpers para mantener la relacion bidireccional correctamente
    public void addChild(Category child)
    {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(Category child)
    {
        children.remove(child);
        child.setParent(null);
    }
}
