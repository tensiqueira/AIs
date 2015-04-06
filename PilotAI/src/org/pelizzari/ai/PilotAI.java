package org.pelizzari.ai;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.pelizzari.gis.*;
import org.pelizzari.ship.*;

public class PilotAI {

	static final boolean FROM_FILE = false;

	public static void main(String[] args) {

		// final float AVERAGE_SPEED = 5f; // speed parameter

//		final float[] TRACK_LAT = { 32f, 33f, 32f, 33f };
//		final float[] TRACK_LON = { -10f, -11f, -13f, -15f };
		 final float[] TRACK_LAT = {31f, 32f, 31f, 30f, 31f};
		 final float[] TRACK_LON = {-12f, -11f, -10f, -11f, -12f};

		ShipTrack track = new ShipTrack();
		if (!FROM_FILE) {
			for (int i = 0; i < TRACK_LON.length; i++) {
				Point p = new Point(TRACK_LAT[i], TRACK_LON[i]);
				Timestamp ts = new Timestamp(100000 + i * 3600);
				ShipPosition pos = new ShipPosition(p, ts);
				pos.setIndex(i);
				track.addPosition(pos);
			}
		} else {
			try {
				FileReader fr = new FileReader("C:\\master_data\\pos.csv");
				track.loadTrack(fr);
				fr.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Map map = new Map();

		System.out.println(track);
		map.plotTrack(track, Color.BLACK);
		ChangeOfCourseSequence cocSeq = track.computeChangeOfCourseSequence();
		System.out.println(cocSeq);

		ShipTrack interpolatedTrack = track.getInterpolatedTrack(3000);
		System.out.println(interpolatedTrack);
		map.plotTrack(interpolatedTrack, Color.BLUE);
		cocSeq = interpolatedTrack.computeChangeOfCourseSequence();
		System.out.println(cocSeq);

		ShipTrack reconstructedTrack = track.reconstructShipTrack(track
				.getPosList().get(0), cocSeq, track.getAverageSpeed());
		System.out.println(reconstructedTrack);
		map.plotTrack(reconstructedTrack, Color.RED);

		// System.out.println("Reducing...");
		// track.reducePositions();
		// System.out.println(track);
		// map.plotTrack(track, Color.BLUE);
		// cocSeq = track.computeChangeOfCourseSequence();
		// System.out.println(cocSeq);

		map.setVisible(true);

	}

}