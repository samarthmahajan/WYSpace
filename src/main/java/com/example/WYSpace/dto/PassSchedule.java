package com.example.WYSpace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassSchedule {
	
	private String satelliteName;
	private int bandwidth;
	private int passStartTime;
	private int passEndTime;

}
