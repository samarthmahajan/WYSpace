package com.example.WYSpace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.WYSpace.dto.SatelliteRequestData;
import com.example.WYSpace.dto.SatelliteResponse;
import com.example.WYSpace.sevice.SatelliteDownlinkService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class SatelliteDownlinkController {

	@Autowired
	public SatelliteDownlinkService satelliteDownlingService;
	  /**
	   * API to check if the ground station is able to handle the max bandwidth
	   * 
	   * it is a get call with max capacity of the substation as its parameter.
     */
    @RequestMapping(value = "/validate/GroundLevelBandwidthSupport", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<SatelliteResponse>  getSatelliteResponse(@Validated @RequestBody(required = true) SatelliteRequestData satelliteRequestData) {
    	log.info("Request recieved to validate if the ground station can process the bandwidth of : {} ", satelliteRequestData.getMaxBandwidth());
    	SatelliteResponse response =satelliteDownlingService.checkGroundStationBandwidth(satelliteRequestData.getMaxBandwidth());
    	log.info("Request completed, response : {}", response);
    	return ResponseEntity.ok(response);
    	
    }

}
