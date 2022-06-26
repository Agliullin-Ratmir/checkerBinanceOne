package com.example.checkerbinanceone.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "user_price_range")
public class UserPriceRange implements Serializable {

    private static final long serialVersionUID = -210458819609757772L;

    @Id
    @Column(name = "user_price_range_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userPriceRangeId;

    @Column(name = "chat_id")
    private String userChatId;

    @OneToOne(fetch = FetchType.LAZY,
            cascade =  CascadeType.ALL,
            mappedBy = "userPriceRange")
    private PricePair pricePair;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @JoinTable(name = "tickets_user_price_ranges",
            joinColumns = @JoinColumn(name = "user_price_range_fk"),
            inverseJoinColumns = @JoinColumn(name = "ticket_fk"))
    private Set<Ticket> tickets = new HashSet<>(0);

    @EqualsAndHashCode.Exclude
    @Column(name = "modify_date")
    private LocalDateTime modifyDate;
}
