package com.shadril.email_api_demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class EmailDto extends BaseEmailDto {

    @NotNull(message = "Reference ID is required")
    @Length(max = 100, message = "Reference ID cannot exceed 100")
    private String referenceId;

    private Integer priority = 0;

    @JsonProperty("scheduled")
    private boolean isScheduled;

    @Valid
    private SchedulingDetailsDto schedulingDetails;
}
