# Proyecto: Tienda Online

Imagina que te encargan construir la web de una tienda online tipo Amazon o Zalando (pero mucho más sencilla): un catálogo de productos organizados por categorías, donde los clientes pueden comprar y dejar reseñas. Es lo que vamos a construir entre todo el grupo.

La aplicación tiene **4 entidades principales**. Cada persona del grupo se encarga de una. A continuación se explica qué representa cada una, por qué la necesitamos y qué información guarda.


## La entidad User

`User` ya existe en el proyecto base (no hay que crearla). Representa a cualquier persona que usa la aplicación:

- Un **usuario normal** (rol USER) es un cliente que navega por el catálogo, compra productos y deja reseñas.
- Un **administrador** (rol ADMIN) es un empleado de la tienda que gestiona el catálogo: crea productos, organiza categorías, revisa pedidos.

Cuando una de vuestras entidades necesite saber "quién hizo esto" (por ejemplo, quién compró un producto o quién escribió una reseña), se vincula con `User` mediante una asociación `@ManyToOne`. No tenéis que programar nada en `User`: solo la usáis como referencia.

---

## Fase 1 — Entidades

Cada alumno crea **una sola entidad** en `model/`. Como cada alumno toca archivos distintos, no hay conflictos de Git.

### Product

Un **producto** es cualquier artículo que la tienda vende. Es la entidad central de toda la aplicación: sin productos no hay tienda. Piensa en lo que ves al navegar por Amazon: cada tarjeta es un Product.

Ejemplos reales: "Camiseta básica algodón — 19,99 €", "Auriculares Bluetooth — 29,99 €", "Mochila urbana 30L — 45,00 €".

```
- id: Long               → identificador único, lo genera la base de datos
- name: String            → nombre del producto ("Camiseta básica")
- description: String     → descripción corta ("Algodón 100%, disponible en 5 colores")
- price: Double           → precio de venta (19.99)
- stock: Integer          → cuántas unidades quedan en almacén (50)
```

### Category

Una **categoría** agrupa productos del mismo tipo para que el cliente pueda filtrar y encontrar lo que busca más fácilmente. Sin categorías, todos los productos aparecerían mezclados en una lista interminable.

Ejemplos reales: "Electrónica", "Ropa", "Hogar", "Deportes", "Libros".

```
- id: Long               → identificador único
- name: String            → nombre de la categoría ("Electrónica")
- description: String     → descripción de qué incluye ("Móviles, portátiles, auriculares...")
```

### Purchase

Una **compra** es el registro de que un cliente ha comprado un producto concreto. Cada Purchase representa **un producto comprado**: si un cliente compra 3 productos distintos, se crean 3 registros de Purchase (uno por cada producto). Esto simplifica el modelo y evita tablas intermedias complejas.

Ejemplos reales: "Juan compró 2 unidades de Camiseta básica el 15/04/2026 a 19,99 € la unidad, total 39,98 €".

```
- id: Long               → identificador único
- purchaseDate: LocalDateTime → cuándo se hizo la compra (2026-04-15T10:30)
- quantity: Integer       → cuántas unidades del producto (2)
- unitPrice: Double       → precio por unidad en el momento de la compra (19.99)
- total: Double           → importe total de esta línea (39.98)
```

### Review

Una **reseña** es la opinión que un cliente deja sobre un producto después de comprarlo. Sirve para que otros clientes sepan si el producto merece la pena. Piensa en las estrellas y comentarios que ves en Amazon debajo de cada producto.

Ejemplos reales: "★★★★★ — Muy buena calidad, la tela es suave y no encoge al lavar".

```
- id: Long               → identificador único
- rating: Integer         → puntuación de 1 a 5 (5)
- comment: String         → texto de la opinión ("Muy buena calidad...")
- createdAt: LocalDateTime → cuándo se escribió la reseña (2026-04-16T14:00)
```

---


## Fase 2 — Repositorios y datos

Una vez creada la entidad, cada alumno crea dos cosas más:

**El repositorio** (`repository/`) permite guardar y consultar datos en la base de datos sin escribir SQL. Spring Data JPA genera las operaciones automáticamente.

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
}
```

**El DataInitializer** (`config/`) carga datos de ejemplo automáticamente al arrancar la aplicación para que la base de datos no esté vacía. Así al abrir h2-console ya se ven filas con datos reales.

```java
@Component
@Profile("!test")
public class ProductDataInitializer implements CommandLineRunner {
    private final ProductRepository repository;

    public ProductDataInitializer(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;
        repository.save(new Product("Camiseta básica", "Algodón 100%", 19.99, 50));
        repository.save(new Product("Auriculares BT", "Bluetooth 5.0", 29.99, 30));
        repository.save(new Product("Mochila urban", "30 litros", 45.00, 20));
    }
}
```

- `@Profile("!test")` hace que estos datos **no se carguen cuando se ejecutan los tests**, para no interferir con las aserciones.
- `if (repository.count() > 0) return;` evita duplicar datos si reinicias la aplicación.

Verificar en h2-console (`localhost:8080/h2-console`) que cada tabla tiene datos: `SELECT * FROM products;`

---

## Fase 3 — Asociaciones @ManyToOne

Hasta ahora cada entidad vive aislada. En la realidad están conectadas: un producto pertenece a una categoría, una compra se refiere a un producto concreto hecha por un cliente concreto, y una reseña la escribe un cliente sobre un producto. Estas conexiones se representan con `@ManyToOne`.

- **Product → Category**: cada producto pertenece a **una** categoría (pero una categoría agrupa muchos productos)
- **Purchase → Product**: cada compra es de **un** producto (pero un producto puede tener muchas compras)
- **Purchase → User**: cada compra la hace **un** cliente (pero un cliente puede hacer muchas compras)
- **Review → Product**: cada reseña es sobre **un** producto (pero un producto puede tener muchas reseñas)
- **Review → User**: cada reseña la escribe **un** cliente (pero un cliente puede escribir muchas reseñas)

```
Category ←── Product ←── Review ──→ User
               ↑                     ↑
               │                     │
            Purchase ────────────────┘
```

Después de añadir las asociaciones, hay que actualizar los DataInitializers para que creen datos relacionados (por ejemplo, crear primero las Categories, luego los Products que apunten a ellas).

---

## Fase 4 — Controladores y HTML

Cada alumno crea un **controlador** y una **vista Thymeleaf** para que su entidad se pueda ver en el navegador, no solo en h2-console.

- `GET /products` → catálogo de productos mostrando nombre, precio, stock y categoría
- `GET /categories` → listado de categorías con nombre y descripción
- `GET /purchases` → historial de compras mostrando producto, cantidad, total y cliente
- `GET /reviews` → reseñas mostrando producto, puntuación (estrellas), comentario y cliente

Más adelante: formularios de creación, detalle (`GET /products/{id}`), edición y borrado.

---

## Fase 5 — Ampliar el modelo

Cuando todo lo anterior funcione, se puede ampliar el proyecto con más campos, más entidades y más relaciones:

### Campos adicionales
- `Product`: sku (String, código único de producto), imageUrl, active (Boolean)
- `Category`: slug (String, versión URL-friendly del nombre: "electronica")
- `Purchase`: status (String: "pending", "shipped", "delivered")
- `Review`: title (String, título corto de la reseña)

### Entidades nuevas
- **Brand**: la marca del producto (Apple, Nike, Samsung). Campos: name, country, website.
- **Address**: dirección de envío del cliente (un cliente puede tener varias). Campos: street, city, postalCode, country.
- **Promotion**: descuentos temporales sobre productos. Campos: name, discountPercent, validFrom, validUntil.

### Asociaciones nuevas
- `Product → Brand` (`@ManyToOne`): cada producto pertenece a una marca
- `Address → User` (`@ManyToOne`): cada dirección pertenece a un cliente
- `Purchase → Address` (`@ManyToOne`): cada compra se envía a una dirección

### Queries derivadas
```java
List<Product> findByPriceLessThan(Double price);
List<Product> findByNameContainingIgnoreCase(String text);
List<Purchase> findByUserId(Long userId);
List<Review> findByRatingGreaterThanEqual(Integer rating);
```
