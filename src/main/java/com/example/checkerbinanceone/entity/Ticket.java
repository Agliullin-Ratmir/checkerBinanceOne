package com.example.checkerbinanceone.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "ticket")
public class Ticket implements Serializable {

    private static final long serialVersionUID = 7458207673981219552L;

    @Id
    @Column(name = "ticket_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @Column(name = "ticket_title")
    private String ticketTitle;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "tickets")
//    @LazyToOne(LazyToOneOption.NO_PROXY)
//    @JoinTable(name = "tickets_user_price_ranges",
//            joinColumns = @JoinColumn(name = "ticket_fk"),
//            inverseJoinColumns = @JoinColumn(name = "user_price_range_fk"))
    private Set<UserPriceRange> userPriceRanges = new HashSet<>(0);

    @EqualsAndHashCode.Exclude
    @Column(name = "modify_date")
    private LocalDateTime modifyDate;
}
