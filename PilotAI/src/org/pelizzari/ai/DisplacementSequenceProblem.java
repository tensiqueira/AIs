package org.pelizzari.ai;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.pelizzari.db.Miner;
import org.pelizzari.gis.Box;
import org.pelizzari.gis.Displacement;
import org.pelizzari.gis.DisplacementSequence;
import org.pelizzari.gis.Map;
import org.pelizzari.gis.Point;
import org.pelizzari.mine.Areas;
import org.pelizzari.ship.Ship;
import org.pelizzari.ship.ShipPosition;
import org.pelizzari.ship.ShipPositionList;
import org.pelizzari.ship.ShipTrack;
import org.pelizzari.ship.TrackError;
import org.pelizzari.time.Timestamp;

import ec.*;
import ec.simple.*;
import ec.util.*;
import ec.vector.*;

public class DisplacementSequenceProblem extends Problem implements
		SimpleProblemForm {

	static ShipPositionList trainingShipPositionList;

	static ShipPosition startPosition;
	
	static final String DATA_STORAGE = "DB";

	static final String FILE_DIR = "c:/master_data/";
	static final String FILE_PREFIX = "pos_";
	static final String FILE_EXT = ".csv";
	//static final String[] MMSIs = {"211394200", "212720000"};
	
	static final String YEAR_PERIOD = "WINTER";
	static final Box  DEPARTURE_AREA = Areas.CAPETOWN;
	static final Box  ARRIVAL_AREA = Areas.REUNION;
	
//	static final float[] TRACK_LAT = { 31f, 32f, 31f, 30f, 31f };
//	static final float[] TRACK_LON = { -12f, -11f, -10f, -11f, -12f };

	// init target track, map, etc.
	static {
		//List<ShipTrack> tracks = new ArrayList<ShipTrack>();
		ShipPositionList shipPositionList = new ShipPositionList();
		if (DATA_STORAGE.equals("VAR")) {
			System.err.println("VAR not supported");
//			Point p = null;
//			Point prevP = null;
//			for (int i = 0; i < TRACK_LON.length; i++) {
//				p = new Point(TRACK_LAT[i], TRACK_LON[i]);
//				int ts = 0;
//				if (i > 0) {
//					durationInSeconds = (int) (prevP.distanceInMiles(p) / SPEED * 3600f);
//				} else {
//					// set 
//				}
//				// this is WRONG, but I do not use it anymore
//				Timestamp ts = new Timestamp(100000 + i * durationInSeconds);
//				prevP = p;				
//				ShipPosition pos = new ShipPosition(p, ts);
//				pos.setIndex(i);
//				track.addPosition(pos);
//			}
			//tracks.add(track);
		} else if (DATA_STORAGE.equals("FILE")) {
			System.err.println("FILE not supported");
//			try {
//				for (int i = 0; i < MMSIs.length; i++) {
//					//ShipTrack track = new ShipTrack();
//					String fileName = FILE_DIR+FILE_PREFIX+MMSIs[i]+FILE_EXT;
//					FileReader fr = new FileReader(fileName);
//					track.loadTrack(fr);
//					fr.close();
//					//tracks.add(track);
//				}
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		} else if (DATA_STORAGE.equals("DB")) {
			Miner miner = new Miner();
//			List<Ship> ships = miner.getShipsWithTracks(YEAR_PERIOD, DEPARTURE_AREA, ARRIVAL_AREA);
//			for (Ship ship : ships) {
//				ShipTrack track = new ShipTrack();
//				track.setMmsi(ship.getMmsi());
//				track.loadTrack(YEAR_PERIOD, DEPARTURE_AREA, ARRIVAL_AREA);
//				tracks.add(track);
//			}			
			trainingShipPositionList = miner.getMergedShipTracksInPeriodAndBetweenBoxes(YEAR_PERIOD, DEPARTURE_AREA, ARRIVAL_AREA);			
		}
		
//		List<ShipPosition> allPos = new ArrayList<ShipPosition>();
//		// WARNING: overwrite timestamps!!! use fixed speed SPEED, then merge the tracks into a single one 
//		for (ShipTrack track : tracks) {
//			track.computeTrackSegments(SPEED);
//			allPos.addAll(track.getPosList());
//		}
//		ShipTrack mergedTrack = new ShipTrack();
//		mergedTrack.setPosList(allPos);
		
		//DisplacementSequence displSeq = mergedTrack.computeDisplacements();
		//displSeq = displSeq.increaseDisplacements(2);			
//		targetTrack = ShipTrack.reconstructShipTrack(track.getFirstPosition(),
//				displSeq, SPEED);
//		targetTrack = track;
		
		// set start position close to the first position of the track (0.1 deg North)
		// TBD: use the average of the first positions of the input tracks 
		Point startPoint = new Point(trainingShipPositionList.getFirstPosition().getPoint().lat+0.1f,
				trainingShipPositionList.getFirstPosition().getPoint().lon);
		startPosition = new ShipPosition(startPoint, trainingShipPositionList.getFirstPosition().getTs());
		System.out.println("Problem initialized; training position list: " + trainingShipPositionList);
	}

	// ind is the individual to be evaluated.
	// We're given state and threadnum primarily so we
	// have access to a random number generator
	// (in the form: state.random[threadnum] )
	// and to the output facility
	public void evaluate(final EvolutionState state, final Individual ind,
			final int subpopulation, final int threadnum) {
		if (ind.evaluated)
			return; // don't evaluate the individual if it's already evaluated
		if (!(ind instanceof GeneVectorIndividual))
			state.output.fatal("evaluate: not a GeneVectorIndividual", null);
		GeneVectorIndividual displSeqInd = (GeneVectorIndividual) ind;
		ShipTrack trackInd = makeTrack(state, displSeqInd);

		// compute fitness
		TrackError trackError = null;
		try {
			trackError = trackInd.computeTrackError(trainingShipPositionList);
		} catch (Exception e) {
			state.output.fatal("computeTrackError: "+e, null);
			e.printStackTrace();
		}

		//float totalSegmentError = trackError.totalSegmentError();
		// float meanLocErrorWithThreshold = trackError.meanLocErrorWithThreshold();
//		float headingError = trackError.headingError();
//		float destinationError = trackError.destinationError();
//		float distanceError = trackError.getAvgSquaredDistanceAllSegments();
//		float noCoverageError = trackError.getNoCoverageError();
		// int numberOfSegments = trackError.getTrackSize();

		float error =
		// trackError.headingError() +
		//trackError.destinationError() +
		//trackError.getAvgSquaredDistanceAllSegments() +
		trackError.getCoverageError() +
		//trackError.avgTotalSegmentError() +
		0f;

		if (!(displSeqInd.fitness instanceof SimpleFitness))
			state.output.fatal("evaluate: not a SimpleFitness", null);

		((SimpleFitness) displSeqInd.fitness).setFitness(state,
		// ...the fitness... (negative!)
				-error,
				// /... is the individual ideal? Indicate here...
				// /error < 1);
				// WARNING: overwrite. Never find ideal.
				false);
		displSeqInd.evaluated = true;
	}

	@Override
	public void closeContacts(EvolutionState state, int result) {
		// TODO Auto-generated method stub
		super.closeContacts(state, result);
		System.out.println("============= Found in generation: "
				+ state.generation + "\n");
		BestStatistics bestStats = (BestStatistics) state.statistics.children[0];
		// bestStats.showBestIndividual(state);
		Individual idealInd = bestStats.getBestIndividual(state);
		ShipTrack idealTrack = makeTrack(state, (GeneVectorIndividual) idealInd);
		System.out.println("Ideal Track: " + idealTrack);
		bestStats.drawOnMap(idealTrack, state, true);
	}

	public ShipTrack makeTrack(EvolutionState state,
			GeneVectorIndividual displSeqInd) {
		// build track corresponding to the individual (sequence of
		// displacements)
		ShipTrack track = new ShipTrack();
		DisplacementSequence displSeq = new DisplacementSequence();
		for (int i = 0; i < displSeqInd.genome.length; i++) {
			if (!(displSeqInd.genome[i] instanceof DisplacementGene))
				state.output.fatal("evaluate: not a DisplacementGene", null);
			Displacement displ = ((DisplacementGene) displSeqInd.genome[i])
					.getAllele();
			displSeq.add(displ);
		}
		track = ShipTrack.reconstructShipTrack(startPosition, displSeq, ShipTrack.REFERENCE_SPEED_IN_KNOTS);
		return track;
	}

	public static ShipPositionList getTrainingShipPositionList() {
		return trainingShipPositionList;
	}

}
