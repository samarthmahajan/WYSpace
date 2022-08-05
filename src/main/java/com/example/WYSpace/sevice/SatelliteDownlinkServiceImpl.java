package com.example.WYSpace.sevice;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.WYSpace.dto.PassSchedule;
import com.example.WYSpace.dto.SatelliteResponse;
import com.example.WYSpace.dto.SatelliteResponse.SatelliteResponseBuilder;
import com.example.WYSpace.utils.ConversionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SatelliteDownlinkServiceImpl implements SatelliteDownlinkService {
	
	@Value("${satellite.pass.schedule.file.name}")
	private String FILE_NAME ;
	
	@Value("${satellite.pass.period}")
	private int PASS_PERIOD;
	
	private static final int MIN_IN_A_DAY = 24 * 60;
	
	@Autowired
	public ConversionUtils conversion;
	
	@Override
	public SatelliteResponse checkGroundStationBandwidth(int ground_level_max_bandwidth) {
		URL url =getClass().getClassLoader().getResource(FILE_NAME);
		if(url == null) {
			log.error("File not found ");
			return SatelliteResponse.builder()
					.error("No Value Found")
					.can_ground_station_support_max_bandwidth(false).build();
		}
		File file = new File(getClass().getClassLoader().getResource(FILE_NAME).getFile());
		
		//create a list of passSchedule from the file
		List<PassSchedule> psList = conversion.getAllPassesforFile(file);
		return processPassData(psList, ground_level_max_bandwidth);
	}

	
	
	//process passes to get the solution
	private SatelliteResponse processPassData(List<PassSchedule> psList, int ground_level_max_bandwidth) {
		HashMap<Integer, Integer> timeBandwidthMap = new HashMap<Integer, Integer>();
		psList.stream().forEach(pass ->{
			int startTimeInMin = pass.getPassStartTime();
			int endTimeInMin = pass.getPassEndTime();
			int bandwidth = pass.getBandwidth();
			int counter =0 ;
			int satelliteBandwidth = 0;
			
			if(startTimeInMin > endTimeInMin) 
				endTimeInMin +=  MIN_IN_A_DAY; // add 1 day to end time
				
			int satelliteDuration = endTimeInMin - startTimeInMin;
			
			while (counter < satelliteDuration) {
				if (timeBandwidthMap.containsKey(startTimeInMin)) 
					satelliteBandwidth = timeBandwidthMap.get(startTimeInMin) + bandwidth;
				else 
					satelliteBandwidth = bandwidth;
				
				timeBandwidthMap.put(startTimeInMin, satelliteBandwidth);

				startTimeInMin += PASS_PERIOD; // updating the start time by 30 min pass
				counter += PASS_PERIOD; // min 30 min passes as per problem statement
			}
		});
		
		return populateSatelliteResponse(timeBandwidthMap, ground_level_max_bandwidth);

	
	}



	private SatelliteResponse populateSatelliteResponse(HashMap<Integer, Integer> timeBandwidthMap, int ground_level_max_bandwidth) {
		
		SatelliteResponseBuilder res = SatelliteResponse.builder();
		int maxBandwidth = (Collections.max(timeBandwidthMap.values()));

		Optional<Entry<Integer,Integer>> maxValueData=timeBandwidthMap.entrySet().stream().filter(entry -> entry.getValue() == maxBandwidth).findAny();
		
		if(maxValueData.isPresent()) {
			res.max_downlink_bandwidth(maxValueData.get().getValue());
			res.max_downlink_bandwidth_duation(conversion.covertMinToHHmm(maxValueData.get().getKey()) + " - " + conversion.covertMinToHHmm(maxValueData.get().getKey()+PASS_PERIOD));
			if (maxValueData.get().getValue() > ground_level_max_bandwidth) 
				res.can_ground_station_support_max_bandwidth(false);
			else
				res.can_ground_station_support_max_bandwidth(true);
		}
		
		else {

			res.error("No Value Found");
			res.can_ground_station_support_max_bandwidth(false);
		}
		
		return res.build();
		
	}

}
