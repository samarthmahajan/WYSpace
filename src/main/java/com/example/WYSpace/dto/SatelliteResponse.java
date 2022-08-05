package com.example.WYSpace.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SatelliteResponse {
	
	private Integer max_downlink_bandwidth;
	private String max_downlink_bandwidth_duation;
	private boolean can_ground_station_support_max_bandwidth;
	private String error;

}
