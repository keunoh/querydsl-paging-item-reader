package com.myreader.querydslreader.setting.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class ManufactureBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long originId;
    private String name;
    private long price;
    private LocalDate createDate;

    @Builder
    public ManufactureBackup(Manufacture product) {
        this.originId = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.createDate = product.getCreateDate();
    }
}
