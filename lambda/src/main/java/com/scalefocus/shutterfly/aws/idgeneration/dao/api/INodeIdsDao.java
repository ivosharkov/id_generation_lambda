package com.scalefocus.shutterfly.aws.idgeneration.dao.api;

/**
 * <p>
 * A general implementation of an interface used to keep shared state of the node ids.
 * Different instances of this service will need to communicate with each-other in order
 * to establish unique ids. When a new instance joins the cluster of existing instances
 * it will need to: find out existing node ids, generate one that does not exist yet,
 * assign it to oneself and broadcast it to other nodes or centralised authority if needed.
 * </p>
 *
 * <p>
 * One option of implementing this is by using a shared database like Redis.
 * It can keep the list of all existing ids for existing nodes.
 * </p>
 *
 * <p>
 * Another option is to use a fully distributed system
 * that communicates with all nodes every time a new node
 * joins the cluster. Responses can be cached for speed.
 * </p>
 * @author nikola
 */
@SuppressWarnings("SameReturnValue")
public interface INodeIdsDao {

    /**
     * Safely returns the nodeId of this server node, unique within the server fleet.
     * @return A unique node id
     */
    Byte getId();
}
