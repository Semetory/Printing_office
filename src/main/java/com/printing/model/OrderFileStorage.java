package com.printing.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA-сущность для побайтового хранения бинарных данных файлов макетов непосредственно в СУБД.
 * <p>
 * Отображается на таблицу {@code order_files_storage}. Аннотирована Lombok-аннотациями
 * {@link Data}, {@link Getter}, {@link Setter} для автоматической генерации служебного кода.
 * Хранит файлы в виде массивов байт (BLOB/BYTEA).
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@Entity
@Data
@Table(name = "order_files_storage")
@Getter
@Setter
public class OrderFileStorage {

    /** Уникальный ID записи файла в хранилище. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Числовой идентификатор (ID) связанного заказа из таблицы {@code orders}. */
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    /** Оригинальное очищенное имя загруженного файла макета. */
    @Column(name = "file_name", nullable = false)
    private String fileName;

    /** Mime-тип файла (например, application/pdf, image/png). */
    @Column(name = "file_type", nullable = false)
    private String fileType;

    /** Бинарное содержимое файла (массив байт), отображаемое на соответствующий тип драйвера JDBC. */
    @Column(name = "data", nullable = false)
    @org.hibernate.annotations.JdbcType(org.hibernate.type.descriptor.jdbc.BinaryJdbcType.class)
    private byte[] data;
}