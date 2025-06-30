package com.shadril.email_api_demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "History_Log", schema = "EMAIL")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Subject")
    private String subject;

    @Column(name = "To_Recipients")
    private String toRecipients;

    @Column(name = "Cc_Recipients")
    private String ccRecipients;

    @Column(name = "Bcc_Recipients")
    private String bccRecipients;

    @Column(name = "Is_Scheduled")
    private boolean isScheduled;

    @Column(name = "Schedule_Start_Time")
    private LocalDateTime scheduleStartTime;

    @Column(name = "Schedule_End_Time")
    private LocalDateTime scheduleEndTime;

    @Column(name = "Time_Stamp")
    private LocalDateTime timeStamp;

    @Column(name = "Reference_Id")
    private String referenceId;

    @Column(name = "Msg_Body")
    private String msgBody;

}

