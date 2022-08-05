package com.example.WYSpace.sevice;

import com.example.WYSpace.dto.SatelliteResponse;

public interface SatelliteDownlinkService {
	public SatelliteResponse checkGroundStationBandwidth(int bandwidth);
}
