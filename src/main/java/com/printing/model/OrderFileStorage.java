package com.printing.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_files_storage")
@Getter
@Setter
public class OrderFileStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "data", nullable = false)
    @org.hibernate.annotations.JdbcType(org.hibernate.type.descriptor.jdbc.BinaryJdbcType.class)
    private byte[] data;
}