package com.example.checkerbinanceone.entity;

import com.example.checkerbinanceone.dto.FlowState;
import com.example.checkerbinanceone.dto.PostgreSQLEnumType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "state_log")
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)
public class StateLog implements Serializable {

    private static final long serialVersionUID = 2723352277883365303L;

    @Id
    @Column(name = "state_log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stateLogId;

    @Column(name = "chat_id")
    private String userChatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_state")
    @Type(type = "pgsql_enum")
    private FlowState flowState;

    @Column(name = "ticket_title")
    private String ticketTitle;

    @Column(name = "lower_price")
    private double lowerPrice;

    @EqualsAndHashCode.Exclude
    @CreationTimestamp
    @Column(name = "modify_date")
    private LocalDateTime modifyDate;

    @Column(name = "updating_ticket_id")
    private Long updatingTicketId;
}
