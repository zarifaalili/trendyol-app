package org.example.trendyolfinalproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.model.enums.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerResponse {


    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String companyName;
    private Integer taxId;
    private String contactEmail;
    private Status status;

}
