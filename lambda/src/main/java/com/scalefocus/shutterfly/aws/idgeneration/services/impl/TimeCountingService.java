/*
 * Copyright (c) Shutterfly.com, Inc. 2015-2017. All Rights Reserved.
 */
package com.scalefocus.shutterfly.aws.idgeneration.services.impl;

import com.scalefocus.shutterfly.aws.idgeneration.dao.api.INodeIdsDao;
import com.scalefocus.shutterfly.aws.idgeneration.dao.impl.NodeIdsRandom;
import com.scalefocus.shutterfly.aws.idgeneration.services.api.IUniqueIdService;

import java.util.BitSet;

/**
 * <p>
 * Implementation of the Unique ID service, using the time counting methodology for generating unique IDs.
 * Basically the design is as follows:
 * </p>
 *
 * <ul>
 * <li>
 * Here is a not-so complicated scheme for generating 64-bit unique ids – the id consists of 3 components
 *  – an instance id (8 bits), time in seconds (34 bits) and a counter (22 bits).
 *  The order by bits will be as described below in the code comments.
 *  </li>
 *
 * <li>
 * If we go with a service based implementation, then each service instance will get a unique instance id.
 * The time component represents the current time in seconds from epoch in UTC. This is usually 32-bits long.
 * However, with 32-bits we will start wrapping around in 2038. Making this 34-bits will ensure this scheme can last for 100+ years.
 * The counter is roughly 4 million in size and just keeps incrementing and wrapping around.
 * This also means that a single service instance can allocate 4 million ids in a second before we start to see duplicates.
 * This can start with zero initially.
 * </li>
 *
 * <li>
 * This can easily be implemented using multiple stateless and independent services behind a load-balancer giving us reliability.
 * The main thing we need to solve is assigning an instance id to each service. Since, the instance id is 8 bits wide,
 * we can have up to 256 services behind the LB. This should be more than enough for us. We can track the id assignment
 * in a common service such as etcd/consul/redis to solve the instance id assignment problem.
 * A given instance doesn’t need to get the same id when it starts up. It just needs to get a unique id.
 * We also need to be careful about assigning an id that was just vacated by one instance to an instance that is just starting up.
 * These issues can be solved by just delaying the startup by a few seconds.
 * </li>
 *
 * <li>
 * A few things that we need to think about in a little more detail – time adjustments for daylight savings and leap second adjustments.
 * UTC is impacted by daylight savings. However, we will need to make sure the APIs we are using to obtain UTC behave in the same way.
 * UTS is still impacted by leap second adjustments. We will be ok as long as time jumps forward (which is usually the case).
 * This will be an issue only if time jumps back. In general, we should have defensive mechanisms so that a service will not generate ids
 * that are lower than what has already been generated.
 * </li>
 *
 * <li>
 * Another issue will be keeping the state of the 2 additional counter bits (that augment the UNIX time into 34 bits).
 * One way we could do this is to have a counting mechanism in place, and store the counters in Redis. This will however
 * be very unstable w.r.t. server moves and Redis restarts.
 * A much better way is to use the full current time (no UNIX time), and, through division, get the correct value of the counter
 * for the current date and time. This can cheaply be done on initialization and then at the end of every UNIX era,
 * if a server should ever live that long. </br>
 * This will not be implemented for now and we will simply append two '0' bits at the beginning of the counter.
 * </li>
 * </ul>
 *
 * NOTE: This application will not be self-aware if it is generating more than 4 million IDs per second. This is because incorporating such
 * a check will inescapably slow down every id generation request with an unnecessary boolean check or worse. Ideally people that monitor the
 * traffic should be aware of that limitation and have a metric for it on their dashboards.
 */
public class TimeCountingService implements IUniqueIdService {

	private final Byte nodeId;
	private int counter;

	public TimeCountingService() {
		INodeIdsDao nodeIdsDao = new NodeIdsRandom();
		nodeId = nodeIdsDao.getId();
	}

	public synchronized Long getUniqueId() {
		int systemTime = (int) (System.currentTimeMillis())/1000;
		BitSet id = new BitSet(64);

		// Set 2 bits for the counter extended for unix time
		// xx000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000
		id.clear(0);
		id.clear(1);

		// Set 32 bits for unix epoch time in seconds integer.
		// eexxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xx000000 00000000 00000000 00000000
		BitSet unixTimeBits = BitSet.valueOf(new byte[] {
			(byte)(systemTime >>> 24),
			(byte)(systemTime >>> 16),
			(byte)(systemTime >>> 8),
			(byte) systemTime
		});
		for (int i = 2; i < 34 ; i++) {
			if (unixTimeBits.get(i-2))
				id.set(i);
		}

		// Set 22 bits with counter value
		// eetttttt tttttttt tttttttt tttttttt ttxxxxxx xxxxxxxx xxxxxxxx 00000000
		BitSet counterBits = BitSet.valueOf(new byte[] {
				(byte) counter,
				(byte) (counter >>> 8),
				(byte) (counter >>> 16)
		});

		for (int i = 34; i < 56 ; i++) {
			if (counterBits.get(i-34))
				id.set(i);
		}

		// Set last 8 bits for instance (node) id
		// eetttttt tttttttt tttttttt tttttttt ttcccccc cccccccc cccccccc xxxxxxxx
		// NOTE: We disallow a node id of '00000000'. This will guarantee that the
		// generated ids will be by absolute value larger than or equal to
		// 2^56 = 72057594037927936 > 10^16 = 10000000000000000
		// (this should ensure no collisions occur between the new ids and the legacy Oracle ones)
		BitSet nodeIdBits = BitSet.valueOf(new byte[] {nodeId});
		for (int i = 56; i < 64 ; i++) {
			if (nodeIdBits.get(i-56))
				id.set(i);
		}

		// Increment counter after successful id generation
		counter++;

		// We should now have the complete id
		// eetttttt tttttttt tttttttt tttttttt ttcccccc cccccccc cccccccc nnnnnnnn
		// n - node id
		// e - extended (counter for unix time)
		// t - unix epoch time in seconds
		// c - counter within every instance
		return id.toLongArray()[0];
	}

}
