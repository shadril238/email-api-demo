package com.shadril.email_api_demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class EmailRequest implements Serializable {

    @NotBlank(message = "Reference ID is required")
    @NotEmpty(message = "Reference ID is required")
    @Length(max = 100, message = "Reference ID cannot exceed 100")
    private String referenceId;

    private Integer priority = 0;

    @JsonProperty("scheduled")
    private boolean isScheduled;

    @Valid
    private SchedulingDetailsDto schedulingDetails;

    @NotEmpty(message = " Email request cannot be empty.")
    @Valid
    private List<BaseEmailDto> emailRequests;
}
