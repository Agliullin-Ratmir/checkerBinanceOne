package com.example.checkerbinanceone.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "price_range")
public class PricePair implements Serializable {

    private static final long serialVersionUID = 2427492555879271344L;

    @Id
    @Column(name = "price_range_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceRangeId;

    @Column(name = "lower_price")
    private double lowerPrice;

    @Column(name = "higher_price")
    private double higherPrice;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_fk")
    private Ticket ticket;

    @EqualsAndHashCode.Exclude
    @Column(name = "modify_date")
    @CreationTimestamp
    private LocalDateTime modifyDate;
}
