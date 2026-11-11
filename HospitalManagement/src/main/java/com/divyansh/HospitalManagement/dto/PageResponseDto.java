package com.divyansh.HospitalManagement.dto;

import lombok.*;

import java.util.List;

// Generic Paginated Response DTO: Wraps list of items along with pagination and sorting metadata
// Next Flow: Formed in PatientService using Spring Data Page<T> -> Sent by PatientController as API response
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponseDto<T> {

    // List of records belonging to the current requested page
    private List<T> content;

    // Current page number (0-indexed)
    private int pageNumber;

    // Number of records per page
    private int pageSize;

    // Total number of records across all pages in the database matching criteria
    private long totalElements;

    // Total number of pages available
    private int totalPages;

    // Indicates if the current page is the final page
    private boolean isLast;

    // Indicates if the current page is the first page
    private boolean isFirst;
}
