package crosschain.threshPaillier.actordata;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import crosschain.threshPaillier.protocol.BGWParameters.BGWPrivateParameters;
import akka.actor.ActorRef;

/** Represents the state data of the Biprimality test protocol actor's FSM.
 * <p>
 * This is an immutable object type in order to comply to the Akka good practices regarding FSMs.
 * @author Christian Mouchet
 */
@SuppressWarnings("unchecked")
public class BiprimalityTestData extends Data{

	private final Map<Integer, BigInteger>[] Qs;
	/** Current candidate to RSA modulus*/
	public final BigInteger N;
	/** Round number in the Biprimality test*/
	public final int round;
	/** BGW private parameters associated with the current candidate to RSA modulus*/
	public final BGWPrivateParameters bgwPrivateParameters;
	

	private BiprimalityTestData(Map<ActorRef, Integer> participants,
			BigInteger N,
			BGWPrivateParameters bgwPrivateParameters,
			Map<Integer, BigInteger>[] Qs,
			int round) {
		super(participants);
		this.Qs = new HashMap[2];
		
		for (int i=0; i<Qs.length; i++)
			this.Qs[i] = new HashMap<Integer, BigInteger>(Qs[i]);
		
		this.N = N;
		this.bgwPrivateParameters = bgwPrivateParameters;
		this.round = round;
	}
	
	public static BiprimalityTestData init() {
		Map<Integer, BigInteger>[] Qs = new HashMap[2];
		Qs[0] = new HashMap<Integer, BigInteger>();
		Qs[1] = new HashMap<Integer, BigInteger>();
		return new BiprimalityTestData(null,
				null,
				null,
				Qs,
				0);
	}
	
	public boolean hasQiOf(Collection<Integer> is, int round) {
		return is.stream().allMatch(i->Qs[round%2].containsKey(i));
	}
	
	public Stream<Map.Entry<Integer, BigInteger>> qis(int round) {
		return this.Qs[round%2].entrySet().stream();
	}
	
	public Map<Integer,BigInteger> qiss(int round) {
		return this.Qs[round%2];
	}
	
	public BiprimalityTestData withNewCandidateN(BigInteger N, BGWPrivateParameters bgwPrivateParameters) {
		return new BiprimalityTestData(participants, N,bgwPrivateParameters ,Qs, round);
	}
	
	public BiprimalityTestData withNewQi(BigInteger Qi, int fromId, int round) {
		if(Qs[round%2].containsKey(fromId))
			return this;
		
		Map<Integer, BigInteger> newMap = new HashMap<Integer, BigInteger>(Qs[round%2]);
		newMap.put(fromId, Qi);
		
		Map<Integer, BigInteger>[] newQs = new HashMap[2];
		newQs[round%2] = newMap;
		newQs[(round+1)%2] = Qs[(round+1)%2];
		
		return new BiprimalityTestData(participants, N, bgwPrivateParameters,newQs,round);
	}

	public BiprimalityTestData withParticipants(Map<ActorRef,Integer> participants) { 
		return new BiprimalityTestData(participants, N, bgwPrivateParameters,Qs, round);
	}
	
	public BiprimalityTestData forNextRound() {
		Map<Integer, BigInteger>[] newQs = new Map[2];
		newQs [round%2] = new HashMap<Integer, BigInteger>();
		newQs[(round+1)%2] = new HashMap<Integer,BigInteger>( Qs[(round+1)%2]);
		return new BiprimalityTestData(participants, N, bgwPrivateParameters, newQs, round+1);
	}

	public BiprimalityTestData forNextCandidate() {
		return init().withParticipants(participants);
	}
}
