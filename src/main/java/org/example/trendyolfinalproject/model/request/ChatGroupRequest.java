package org.example.trendyolfinalproject.model.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.trendyolfinalproject.model.enums.GroupVisibility;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatGroupRequest {
    @NotNull(message = "Title cannot be null")
    private String title;
    @NotNull(message = "Description cannot be null")
    private String description;
    @NotNull(message = "Visibility cannot be null")
    private GroupVisibility visibility;
    private List<Long> memberIds;

}