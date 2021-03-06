package org.pelizzari.mine;

import java.awt.Color;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.pelizzari.db.Miner;
import org.pelizzari.gis.Box;
import org.pelizzari.gis.Map;
import org.pelizzari.gis.Point;
import org.pelizzari.kml.KMLGenerator;
import org.pelizzari.ship.Ship;
import org.pelizzari.ship.ShipTrack;
import org.pelizzari.time.TimeInterval;
import org.pelizzari.time.Timestamp;

public class ShowShipTrack {

	final static int MMSI = 209616000; //235068861;
	final static String START_DT = "2019-10-01 00:00:00";
	final static int ANALYSIS_PERIOD_IN_DAYS = 7;

	final static String OUTPUT_FILE = "D:\\Google Drive MB\\Casnav\\Evo-SisTram\\master_data\\ShipTrack";
	
	public static void main(String[] args) {

		KMLGenerator kmlGenerator = null;
		try {
			kmlGenerator = new KMLGenerator();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		kmlGenerator.addIconStyle("targetStyle",
				//"http://maps.google.com/mapfiles/kml/shapes/target.png");
				"http://maps.google.com/mapfiles/kml/shapes/placemark_circle.png");
			
		
		Timestamp startTS = null;
		TimeInterval analysisInterval = null;
		try {
			startTS = new Timestamp(START_DT);
			analysisInterval = new TimeInterval(startTS, ANALYSIS_PERIOD_IN_DAYS);
		} catch (Exception e) {
			System.err.println("error parsing times");
			e.printStackTrace();
		}
		
		/// Let's mine
		
		Miner miner = new Miner();
		
		ShipTrack track = miner.getShipTrackInIntervalAndBetweenBoxes(
				new Ship(""+MMSI), analysisInterval, null, null);		
		
		// make segment to change timestamps based on speed (10 knots)
		//track.computeTrackSegments(10f);
		
		kmlGenerator.addTrack(track, null);
		
		kmlGenerator.saveKMLFile(OUTPUT_FILE+"_"+MMSI+".kml");
		
		System.out.println("Done\n");
		
	}

}
